package powercrystals.minefactoryreloaded.tile.base;

import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;

import cofh.api.inventory.IInventoryConnection;
import cofh.api.tileentity.IPortableData;
import cofh.asm.relauncher.Strippable;
import cofh.lib.util.position.IRotateableTile;
import com.google.common.base.Strings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.core.HarvestAreaManager;
import powercrystals.minefactoryreloaded.core.IHarvestAreaContainer;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.setup.Machine;

@Strippable("buildcraft.api.transport.IPipeConnection")
public abstract class TileEntityFactory extends TileEntityBase
																implements IRotateableTile, IInventoryConnection, IPortableData,
																IHarvestAreaContainer, IPipeConnection {

	// first index is rotation, second is side
	private static final int[][] _textureSelection = new int[][]
	{
			{ 0, 1, 2, 3, 4, 5 }, // 0 D (unused)
			{ 0, 1, 2, 3, 4, 5 }, // 1 U (unused)
			{ 0, 1, 2, 3, 4, 5 }, // 2 N
			{ 0, 1, 3, 2, 5, 4 }, // 3 S
			{ 0, 1, 5, 4, 2, 3 }, // 4 W
			{ 0, 1, 4, 5, 3, 2 }, // 5 E
	};

	protected static class FactoryAreaManager extends HarvestAreaManager<TileEntityFactory> {

		public FactoryAreaManager(TileEntityFactory owner, int harvestRadius,
				int harvestAreaUp, int harvestAreaDown, float upgradeModifier, boolean usesBlocks) {

			super(owner, harvestRadius, harvestAreaUp, harvestAreaDown, upgradeModifier, usesBlocks);
		}
	}

	private ForgeDirection _forwardDirection;
	private boolean _canRotate = false;

	private boolean _manageFluids = false;
	private boolean _manageSolids = false;

	private boolean _isActive = false, _prevActive;
	protected byte _activeSyncTimeout = 101;
	private long _lastActive = -100;
	private int _lastUpgrade = 0;

	protected int _rednetState;

	protected HarvestAreaManager<TileEntityFactory> _areaManager;
	protected Machine _machine;

	protected String _owner = "";

	protected TileEntityFactory(Machine machine) {

		this._machine = machine;
		_forwardDirection = ForgeDirection.NORTH;
	}

	@Override
	public void cofh_validate() {

		super.cofh_validate();
		onRotate();
		if (worldObj.isRemote && hasHAM()) {
			MineFactoryReloadedClient.addTileToAreaList(this);
		}
	}

	@Override
	public void invalidate() {

		if (worldObj != null && worldObj.isRemote && hasHAM()) {
			MineFactoryReloadedClient.removeTileFromAreaList(this);
		}
		super.invalidate();
	}

	/**
	 * Used to create HarvestAreas for entity-interacting machines.
	 */
	protected static void createEntityHAM(TileEntityFactory owner) {

		createHAM(owner, 2, 2, 1, 1.0f, false);
	}

	/**
	 * Used to create HarvestAreas for block-interacting machines
	 */
	protected static void createHAM(TileEntityFactory owner, int harvestRadius) {

		createHAM(owner, harvestRadius, 0, 0, 1.0f, true);
	}

	protected static void createHAM(TileEntityFactory owner, int harvestRadius, int harvestAreaUp, int harvestAreaDown,
			boolean usesBlocks) {

		createHAM(owner, harvestRadius, harvestAreaUp, harvestAreaDown, 1.0f, usesBlocks);
	}

	protected static void createHAM(TileEntityFactory owner, int harvestRadius, int harvestAreaUp, int harvestAreaDown,
			float upgradeModifier, boolean usesBlocks) {

		owner._areaManager = new FactoryAreaManager(owner, harvestRadius, harvestAreaUp, harvestAreaDown,
				upgradeModifier, usesBlocks);
	}

	@Override
	public boolean hasHAM() {

		return getHAM() != null;
	}

	@Override
	public HarvestAreaManager<TileEntityFactory> getHAM() {

		return _areaManager;
	}

	public World getWorld() {

		return worldObj;
	}

	@Override
	public ForgeDirection getDirectionFacing() {

		return _forwardDirection;
	}

	@Override
	public boolean canRotate() {

		return _canRotate;
	}

	@Override
	public boolean canRotate(ForgeDirection axis) {

		return _canRotate;
	}

	protected void setCanRotate(boolean canRotate) {

		_canRotate = canRotate;
	}

	@Override
	public void rotate(ForgeDirection axis) {

		if (canRotate())
			rotate(false);
	}

	public void rotate(boolean reverse) {

		if (worldObj != null && !worldObj.isRemote) {
			switch ((reverse ? _forwardDirection.getOpposite() : _forwardDirection).ordinal()) {
			case 2://NORTH:
				_forwardDirection = ForgeDirection.EAST;
				break;
			case 5://EAST:
				_forwardDirection = ForgeDirection.SOUTH;
				break;
			case 3://SOUTH:
				_forwardDirection = ForgeDirection.WEST;
				break;
			case 4://WEST:
				_forwardDirection = ForgeDirection.NORTH;
				break;
			default:
				_forwardDirection = ForgeDirection.NORTH;
			}

			onRotate();
		}
	}

	@Override
	public void rotateDirectlyTo(int rotation) {

		ForgeDirection p = _forwardDirection;
		_forwardDirection = ForgeDirection.getOrientation(rotation);
		if (worldObj != null && p != _forwardDirection) {
			onRotate();
		}
	}

	protected void onRotate() {

		if (!isInvalid() && worldObj.blockExists(xCoord, yCoord, zCoord)) {
			markForUpdate();
			MFRUtil.notifyNearbyBlocks(worldObj, xCoord, yCoord, zCoord, getBlockType());
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	public int getRotatedSide(int side) {

		return _textureSelection[_forwardDirection.ordinal()][side];
	}

	public ForgeDirection getDropDirection() {

		if (canRotate())
			return getDirectionFacing().getOpposite();
		return ForgeDirection.UP;
	}

	public ForgeDirection[] getDropDirections() {

		return ForgeDirection.VALID_DIRECTIONS;
	}

	public boolean isActive() {

		return _isActive;
	}

	public void setIsActive(boolean isActive) {

		if (_isActive != isActive & worldObj != null &&
				!worldObj.isRemote && _lastActive < worldObj.getTotalWorldTime()) {
			_lastActive = worldObj.getTotalWorldTime() + _activeSyncTimeout;
			_prevActive = _isActive;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		_isActive = isActive;
	}

	@Override
	public void updateEntity() {

		super.updateEntity();

		if (!worldObj.isRemote && _prevActive != _isActive && _lastActive < worldObj.getTotalWorldTime()) {
			_prevActive = _isActive;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	public void setOwner(String owner) {

		if (owner == null)
			owner = "";
		if (_owner == null || _owner.isEmpty())
			_owner = owner;
	}

	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return null;
	}

	public ContainerFactoryInventory getContainer(InventoryPlayer inventoryPlayer) {

		return null;
	}

	public String getGuiBackground() {

		if (_machine == null)
			return null;
		return _machine.getName().toLowerCase(Locale.US);
	}

	@Override
	public void markDirty() {

		if (worldObj != null && !worldObj.isRemote && hasHAM()) {
			HarvestAreaManager<TileEntityFactory> ham = getHAM();
			int u = ham.getUpgradeLevel();
			if (_lastUpgrade != u)
				Packets.sendToAllPlayersWatching(worldObj, xCoord, yCoord, zCoord, ham.getUpgradePacket());
			_lastUpgrade = u;
		}
		super.markDirty();
	}

	protected void writePacketData(NBTTagCompound tag) {

	}

	protected void readPacketData(NBTTagCompound tag) {

	}

	public void markForUpdate() {

		_lastActive = 0;
	}

	@Override
	public Packet getDescriptionPacket() {

		if (worldObj != null && _lastActive < worldObj.getTotalWorldTime()) {
			NBTTagCompound data = new NBTTagCompound();
			data.setByte("r", (byte) _forwardDirection.ordinal());
			data.setBoolean("a", _isActive);
			writePacketData(data);
			S35PacketUpdateTileEntity packet = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, data);
			return packet;
		}
		return null;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {

		NBTTagCompound data = pkt.func_148857_g();
		switch (pkt.func_148853_f()) {
		case 0:
			rotateDirectlyTo(data.getByte("r"));
			_prevActive = _isActive;
			_isActive = data.getBoolean("a");
			readPacketData(data);
			if (_prevActive != _isActive)
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			if (_lastActive < 0 && hasHAM()) {
				Packets.sendToServer(Packets.HAMUpdate, this);
			}
			_lastActive = 5;
			break;
		case 255:
			if (hasHAM()) {
				getHAM().setUpgradeLevel(data.getInteger("_upgradeLevel"));
			}
			break;
		}
	}

	@Override
	public String getDataType() {

		return _machine.getInternalName() + ".name";
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);
		tag.setInteger("rotation", getDirectionFacing().ordinal());
		if (!Strings.isNullOrEmpty(_owner))
			tag.setString("owner", _owner);
		tag.setBoolean("a", _isActive);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		if (tag.hasKey("rotation"))
			rotateDirectlyTo(tag.getInteger("rotation"));
		if (tag.hasKey("owner"))
			_owner = tag.getString("owner");
		_isActive = tag.getBoolean("a");
	}

	public void onRedNetChanged(ForgeDirection side, int value) {

		_rednetState = value;
	}

	public int getRedNetOutput(ForgeDirection side) {

		return 0;
	}

	// hoisted IMachine methods

	public void setManageFluids(boolean manageFluids) {

		_manageFluids = manageFluids;
	}

	public boolean manageFluids() {

		return _manageFluids;
	}

	public void setManageSolids(boolean manageSolids) {

		_manageSolids = manageSolids;
	}

	public boolean manageSolids() {

		return _manageSolids;
	}

	@Override
	public ConnectionType canConnectInventory(ForgeDirection from) {

		return manageSolids() ? ConnectionType.FORCE : ConnectionType.DENY;
	}

	@Override
	@Strippable("buildcraft.api.transport.IPipeConnection")
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {

		if (type == PipeType.FLUID)
			return manageFluids() ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
		if (type == PipeType.ITEM)
			return canConnectInventory(with).canConnect ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
		if (type == PipeType.STRUCTURE)
			return ConnectOverride.CONNECT;
		return ConnectOverride.DEFAULT;
	}
}
