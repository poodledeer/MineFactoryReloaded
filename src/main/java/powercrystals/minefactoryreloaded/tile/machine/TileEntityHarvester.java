package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.position.Area;
import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.core.HarvestMode;
import powercrystals.minefactoryreloaded.core.IHarvestManager;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.SideOffset;
import powercrystals.minefactoryreloaded.core.TreeHarvestManager;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiHarvester;
import powercrystals.minefactoryreloaded.gui.container.ContainerHarvester;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityHarvester extends TileEntityFactoryPowered implements ITankContainerBucketable {

	private static boolean skip = false;
	private static Map<String, Boolean> DEFAULT_SETTINGS;
	static {

		HashMap<String, Boolean> _settings = new HashMap<String, Boolean>();
		_settings.put("silkTouch", false);
		_settings.put("harvestSmallMushrooms", false);
		_settings.put("playSounds", MFRConfig.playSounds.getBoolean(true));
		_settings.put("isHarvestingTree", false);
		DEFAULT_SETTINGS = java.util.Collections.unmodifiableMap(_settings);
	}

	private Map<String, Boolean> _settings;
	private Map<String, Boolean> _immutableSettings;

	private Random _rand;

	private IHarvestManager _treeManager;
	private BlockPosition _lastTree;

	public TileEntityHarvester() {

		super(Machine.Harvester);
		createHAM(this, 1);
		setManageSolids(true);

		_settings = new HashMap<String, Boolean>();
		_settings.putAll(DEFAULT_SETTINGS);
		_immutableSettings = java.util.Collections.unmodifiableMap(_settings);

		_rand = new Random();
		setCanRotate(true);

		skip = MFRConfig.harvesterSkip.getBoolean(false);
	}

	@Override
	public void onChunkUnload() {

		super.onChunkUnload();
		if (_treeManager != null)
			_treeManager.free();
		_lastTree = null;
	}

	@Override
	public void validate() {

		super.validate();
		if (!worldObj.isRemote) {
			createHAM(this, 1);
			onFactoryInventoryChanged();
			if (_treeManager != null && _areaManager.getHarvestArea().contains(_treeManager.getOrigin())) {
				_treeManager.setWorld(worldObj);
			} else {
				_treeManager = new TreeHarvestManager(worldObj,
						new Area(new BlockPosition(this), 0, 0, 0),
						HarvestMode.FruitTree, _immutableSettings);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiHarvester(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerHarvester getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerHarvester(this, inventoryPlayer);
	}

	public Map<String, Boolean> getSettings() {

		return _settings;
	}

	public Map<String, Boolean> getImmutableSettings() {

		return _immutableSettings;
	}

	@Override
	protected boolean shouldPumpLiquid() {

		return true;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 5 + getExtraIdleTime(10);
	}

	protected int getExtraIdleTime(int additionalDelay) {

		return (_tanks[0].getFluidAmount() * additionalDelay / _tanks[0].getCapacity());
	}

	@Override
	public boolean activateMachine() {

		BlockPosition target = getNextHarvest();

		if (target == null) {
			setIdleTicks(getIdleTicksMax());
			return false;
		}

		Block harvestedBlock = worldObj.getBlock(target.x, target.y, target.z);
		int harvestedBlockMetadata = worldObj.getBlockMetadata(target.x, target.y, target.z);

		IFactoryHarvestable harvestable = MFRRegistry.getHarvestables().get(harvestedBlock);

		List<ItemStack> drops = harvestable.getDrops(worldObj, _rand, _immutableSettings, target.x, target.y, target.z);

		harvestable.preHarvest(worldObj, target.x, target.y, target.z);

		if (drops instanceof ArrayList) {
			ForgeEventFactory.fireBlockHarvesting((ArrayList<ItemStack>) drops, worldObj, harvestedBlock,
				target.x, target.y, target.z, harvestedBlockMetadata, 0,
				1f, _settings.get("silkTouch") == Boolean.TRUE, null);
		}

		if (harvestable.breakBlock()) {
			if (!worldObj.setBlock(target.x, target.y, target.z, Blocks.air, 0, 2))
				return false;
			if (_settings.get("playSounds") == Boolean.TRUE) {
				worldObj.playAuxSFXAtEntity(null, 2001, target.x, target.y, target.z,
					Block.getIdFromBlock(harvestedBlock) + (harvestedBlockMetadata << 12));
			}
		}

		setIdleTicks(getExtraIdleTime(10));

		doDrop(drops);
		_tanks[0].fill(FluidRegistry.getFluidStack("sludge", 10), true);

		harvestable.postHarvest(worldObj, target.x, target.y, target.z);

		return true;
	}

	private BlockPosition getNextHarvest() {

		if (!_treeManager.getIsDone())
			return getNextTreeSegment(_lastTree, false);
		BlockPosition bp = _areaManager.getNextBlock();
		_lastTree = null;
		if (skip) {
			int extra = getExtraIdleTime(10);
			if (extra > 0 && extra > _rand.nextInt(15))
				return null;
		}
		if (!worldObj.blockExists(bp.x, bp.y, bp.z)) {
			return null;
		}

		Block search = worldObj.getBlock(bp.x, bp.y, bp.z);

		if (!MFRRegistry.getHarvestables().containsKey(search)) {
			_lastTree = null;
			return null;
		}

		_settings.put("isHarvestingTree", false);

		IFactoryHarvestable harvestable = MFRRegistry.getHarvestables().get(search);
		HarvestType type = harvestable.getHarvestType();
		if (type == HarvestType.Gourd || harvestable.canBeHarvested(worldObj, _immutableSettings, bp.x, bp.y, bp.z)) {
			switch (type) {
			case Gourd:
				return getNextAdjacent(bp.x, bp.y, bp.z, harvestable);
			case Column:
			case LeaveBottom:
				return getNextVertical(bp.x, bp.y, bp.z, type == HarvestType.Column ? 0 : 1, harvestable);
			case Tree:
			case TreeFlipped:
			case TreeLeaf:
				return getNextTreeSegment(bp, type == HarvestType.TreeFlipped);
			case TreeFruit:
			case Normal:
				return bp;
			}
		}
		return null;
	}

	private BlockPosition getNextAdjacent(int x, int y, int z, IFactoryHarvestable harvestable) {

		for (SideOffset side : SideOffset.SIDES) {
			int X = x + side.offsetX, Y = y + side.offsetY, Z = z + side.offsetX;
			if (worldObj.blockExists(X, Y, Z) && harvestable.canBeHarvested(worldObj, _immutableSettings, X, Y, Z))
				return new BlockPosition(X, Y, Z);
		}
		return null;
	}

	private BlockPosition getNextVertical(int x, int y, int z, int startOffset, IFactoryHarvestable harvestable) {

		int highestBlockOffset = -1;
		int maxBlockOffset = MFRConfig.verticalHarvestSearchMaxVertical.getInt();

		Block plant = harvestable.getPlant();
		for (int currentYoffset = startOffset; currentYoffset < maxBlockOffset; ++currentYoffset) {
			Block block = worldObj.getBlock(x, y + currentYoffset, z);
			if (!block.equals(plant) ||
					!harvestable.canBeHarvested(worldObj, _immutableSettings, x, y + currentYoffset, z))
				break;

			highestBlockOffset = currentYoffset;
		}

		if (highestBlockOffset >= 0)
			return new BlockPosition(x, y + highestBlockOffset, z);

		return null;
	}

	private BlockPosition getNextTreeSegment(BlockPosition pos, boolean treeFlipped) {

		Block block;
		_settings.put("isHarvestingTree", true);

		if (!pos.equals(_lastTree) || _treeManager.getIsDone()) {
			int lowerBound = 0;
			int upperBound = MFRConfig.treeSearchMaxVertical.getInt();
			if (treeFlipped) {
				lowerBound = upperBound;
				upperBound = 0;
			}

			_lastTree = new BlockPosition(pos);

			Area a = new Area(_lastTree, MFRConfig.treeSearchMaxHorizontal.getInt(), lowerBound, upperBound);

			_treeManager.reset(worldObj, a,
				treeFlipped ? HarvestMode.HarvestTreeInverted : HarvestMode.HarvestTree,
				_immutableSettings);
		}

		Map<Block, IFactoryHarvestable> harvestables = MFRRegistry.getHarvestables();
		while (!_treeManager.getIsDone()) {
			BlockPosition bp = _treeManager.getNextBlock();
			_treeManager.moveNext();
			if (!worldObj.blockExists(bp.x, bp.y, bp.z)) {
				return null;
			}
			block = worldObj.getBlock(bp.x, bp.y, bp.z);

			if (harvestables.containsKey(block)) {
				IFactoryHarvestable obj = harvestables.get(block);
				HarvestType t = obj.getHarvestType();
				if (t == HarvestType.Tree | t == HarvestType.TreeFlipped |
						t == HarvestType.TreeLeaf | t == HarvestType.TreeFruit)
					if (obj.canBeHarvested(worldObj, _immutableSettings, bp.x, bp.y, bp.z))
						return bp;
			}
		}
		return null;
	}

	@Override
	public boolean allowBucketDrain(ItemStack stack) {

		return true;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		return drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		return drain(resource, doDrain);
	}

	@Override
	protected FluidTankAdv[] createTanks() {

		return new FluidTankAdv[] { new FluidTankAdv(4 * BUCKET_VOLUME) };
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		NBTTagCompound list = new NBTTagCompound();
		for (Entry<String, Boolean> setting : _settings.entrySet()) {
			String key = setting.getKey();
			if ("playSounds" == key || "isHarvestingTree" == key)
				continue;
			list.setBoolean(key, setting.getValue() == Boolean.TRUE);
		}
		tag.setTag("harvesterSettings", list);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		NBTTagCompound list = (NBTTagCompound) tag.getTag("harvesterSettings");
		if (list != null) {
			for (String s : _settings.keySet()) {
				if ("playSounds".equals(s))
					continue;
				boolean b = list.getBoolean(s);
				_settings.put(s.intern(), b);
			}
		}
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		NBTTagCompound list = new NBTTagCompound();
		for (Entry<String, Boolean> setting : _settings.entrySet()) {
			String key = setting.getKey();
			if ("playSounds" == key | "isHarvestingTree" == key
					|| DEFAULT_SETTINGS.get(key) == setting.getValue())
				continue;
			list.setBoolean(key, setting.getValue() == Boolean.TRUE);
		}
		if (!list.hasNoTags())
			tag.setTag("harvesterSettings", list);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);

		_treeManager.writeToNBT(tag);
		tag.setInteger("bpos", _areaManager.getPosition());
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		NBTTagCompound list = (NBTTagCompound) tag.getTag("harvesterSettings");
		if (list != null) {
			for (String s : _settings.keySet()) {
				if ("playSounds".equals(s))
					continue;
				boolean b = list.getBoolean(s);
				_settings.put(s.intern(), b);
			}
		}
		if (_treeManager != null)
			_treeManager.free();
		_treeManager = new TreeHarvestManager(tag, _immutableSettings);
		if (!_treeManager.getIsDone())
			_lastTree = _treeManager.getOrigin();
		_areaManager.getHarvestArea();
		_areaManager.setPosition(tag.getInteger("bpos"));
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public int getStartInventorySide(ForgeDirection side) {

		return 0;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {

		return 0;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public int getUpgradeSlot() {

		return 0;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {

		return slot == 0 && isUsableAugment(itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {

		return false;
	}
}
