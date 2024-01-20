package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankAdv;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.lang.reflect.Field;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.IFactoryLaserTarget;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiChunkLoader;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerChunkLoader;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.net.ConnectionHandler;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityChunkLoader extends TileEntityFactoryPowered implements ITankContainerBucketable, IFactoryLaserTarget {

	private static void bypassLimit(Ticket tick) {

		try {
			Field f = Ticket.class.getDeclaredField("maxDepth");
			f.setAccessible(true);
			f.setInt(tick, Short.MAX_VALUE);
		} catch (Throwable ignored) {
		}
	}

	protected static TObjectIntHashMap<String> fluidConsumptionRate = new TObjectIntHashMap<String>();
	static {
		fluidConsumptionRate.put("mobessence", 10);
		fluidConsumptionRate.put("liquidessence", 20);
		fluidConsumptionRate.put("ender", 40);
	}

	protected short _radius;
	protected boolean activated, unableToRequestTicket;
	public boolean useAltPower;
	protected Ticket _ticket;
	protected int consumptionTicks;
	protected int emptyTicks, prevEmpty;
	protected int unactivatedTicks;

	public TileEntityChunkLoader() {

		super(Machine.ChunkLoader);
		_radius = 0;
		useAltPower = MFRConfig.enableConfigurableCLEnergy.getBoolean(false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiChunkLoader(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer) {

		if (unableToRequestTicket &&
				inventoryPlayer.player.getCommandSenderName().equals(_owner)) {
			inventoryPlayer.player.addChatMessage(
					new ChatComponentTranslation("chat.info.mfr.chunkloader.noticket"));
		}
		return new ContainerChunkLoader(this, inventoryPlayer);
	}

	@Override
	protected FluidTankAdv[] createTanks() {

		return new FluidTankAdv[] { new FluidTankAdv(BUCKET_VOLUME * 10) };
	}

	@Override
	public void invalidate() {

		super.invalidate();
		if (_ticket != null) {
			unforceChunks();
			ForgeChunkManager.releaseTicket(_ticket);
		}
	}

	public void setRadius(short r) {

		int maxR = 38;
		if (_ticket != null)
			maxR = Math.min((int) Math.sqrt(_ticket.getChunkListDepth() / Math.PI), maxR);
		if (r < 0 | r > maxR | r == _radius)
			return;
		_radius = r;
		markDirty();
		if (worldObj != null && !worldObj.isRemote)
			forceChunks();
	}

	@Override
	protected boolean activateMachine() {

		activated = true;
		unactivatedTicks = 0;
		if (consumptionTicks > 0)
			--consumptionTicks;
		else {
			emptyTicks = Math.min(65535, emptyTicks + 1);
			FluidStack s = _tanks[0].getFluid();
			if (drain(_tanks[0], 1, true) == 1) {
				consumptionTicks = fluidConsumptionRate.get(getFluidName(s));
				emptyTicks = Math.max(-65535, emptyTicks - 2);
			}
		}
		return true;
	}

	@Override
	public void updateEntity() {

		if (_owner.isEmpty())
			return;
		if (unableToRequestTicket) {
			setIdleTicks(getIdleTicksMax());
			super.setIsActive(false);
			return;
		}
		activated = false;
		if (!worldObj.isRemote && MFRConfig.enableChunkLoaderRequiresOwner.getBoolean(false) &&
				!ConnectionHandler.onlinePlayerMap.containsKey(_owner)) {
			setIdleTicks(getIdleTicksMax());
		}
		super.updateEntity();
		if (worldObj.isRemote)
			return;
		if (getIdleTicks() > 0) {
			if (_ticket != null)
				unforceChunks();
			return;
		}

		if (!activated)
			l: {
				if (_ticket != null) {
					Set<ChunkCoordIntPair> chunks = _ticket.getChunkList();
					if (chunks.size() == 0)
						break l;

					unactivatedTicks = Math.min(_tanks[0].getCapacity() + 10, unactivatedTicks + 1);
					if (consumptionTicks > 0)
						consumptionTicks /= 10;
					else {
						emptyTicks = Math.min(65535, emptyTicks + 1);
						FluidStack s = _tanks[0].getFluid();
						if (drain(_tanks[0], Math.min(unactivatedTicks,
							_tanks[0].getFluidAmount()), true) == unactivatedTicks) {
							consumptionTicks = fluidConsumptionRate.get(getFluidName(s));
							consumptionTicks = Math.max(0, consumptionTicks - unactivatedTicks);
							activated = emptyTicks == 1 && unactivatedTicks < _tanks[0].getCapacity();
							emptyTicks = Math.max(-65535, emptyTicks - 2);
							if (activated)
								break l;
						}
					}

					for (ChunkCoordIntPair c : chunks)
						ForgeChunkManager.unforceChunk(_ticket, c);
				}
			}
		else if (activated & !isActive()) {
			if (_ticket == null) {
				_ticket = ForgeChunkManager.
						requestPlayerTicket(MineFactoryReloadedCore.instance(),
							_owner, worldObj, Type.NORMAL);
				if (_ticket == null) {
					unableToRequestTicket = true;
					return;
				}
				_ticket.getModData().setInteger("X", xCoord);
				_ticket.getModData().setInteger("Y", yCoord);
				_ticket.getModData().setInteger("Z", zCoord);

			}
			forceChunks();
		}

		if (prevEmpty != emptyTicks) {
			prevEmpty = emptyTicks;
			onFactoryInventoryChanged();
		}

		super.setIsActive(activated);
	}

	protected void unforceChunks() {

		Set<ChunkCoordIntPair> chunks = _ticket.getChunkList();
		if (chunks.size() == 0)
			return;

		for (ChunkCoordIntPair c : chunks)
			ForgeChunkManager.unforceChunk(_ticket, c);
	}

	protected void forceChunks() {

		if (_ticket == null)
			return;
		if (MFRConfig.enableChunkLimitBypassing.getBoolean(false))
			bypassLimit(_ticket);
		Set<ChunkCoordIntPair> chunks = _ticket.getChunkList();
		int x = xCoord >> 4;
		int z = zCoord >> 4;
		int r = _radius * _radius;
		for (ChunkCoordIntPair c : chunks) {
			int xS = c.chunkXPos - x;
			int zS = c.chunkZPos - z;
			if ((xS * xS + zS * zS) > r)
				ForgeChunkManager.unforceChunk(_ticket, c);
		}
		for (int xO = -_radius; xO <= _radius; ++xO) {
			int xS = xO * xO;
			for (int zO = -_radius; zO <= _radius; ++zO)
				if (xS + zO * zO <= r) {
					ChunkCoordIntPair p = new ChunkCoordIntPair(x + xO, z + zO);
					if (!chunks.contains(p))
						ForgeChunkManager.forceChunk(_ticket, p);
				}
		}
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		if (isInvalid())
			return;
		int r = _radius + 1, c, r2 = _radius * _radius;
		if (_ticket == null) {
			// {int t = _radius * _radius; c = (int)(t * (float)Math.PI);}
			// this is the actual math for calculating the radius of a circle
			// and it is inaccurate for calculating the area of the loaded
			// circle of square chunks, so simulate the number of loaded chunks
			c = 0;
			for (int xO = -_radius; xO <= _radius; ++xO) {
				int xS = xO * xO;
				for (int zO = -_radius; zO <= _radius; ++zO)
					if (xS + zO * zO <= r2)
						++c;
			}
		}
		else
			c = _ticket.getChunkList().size();
		int energy;
		if (useAltPower) {
			int a = (r2 + 1) * c * 16 * _machine.getActivationEnergy();
			a &= ~a >> 31;
			energy = a;
			c *= 16;
		}
		else {
			double a = (r * r * 32 - 17 + r * r * r);
			for (int i = r / 10; i-- > 0;)
				a *= r / 6d;
			energy = (int) (a * 10);
		}
		energy += (int) (StrictMath.cbrt(emptyTicks) * c);
		energy &= ~energy >> 31;
		if (energy == 0)
			energy = 1;
		setActivationEnergy(energy);
	}

	public boolean receiveTicket(Ticket ticket) {

		if (!MFRConfig.enableChunkLoaderRequiresOwner.getBoolean(false) ||
				ConnectionHandler.onlinePlayerMap.containsKey(_owner)) {
			if (_ticket == null) {
				ForgeChunkManager.releaseTicket(ticket);
				_ticket = ForgeChunkManager.
						requestPlayerTicket(MineFactoryReloadedCore.instance(),
							_owner, worldObj, Type.NORMAL);
				if (_ticket == null) {
					unableToRequestTicket = true;
					return true;
				}
				_ticket.getModData().setInteger("X", xCoord);
				_ticket.getModData().setInteger("Y", yCoord);
				_ticket.getModData().setInteger("Z", zCoord);
			}
			return true;
		}
		else {
			_ticket = ticket;
			unforceChunks();
			_ticket = null;
		}
		return false;
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setShort("radius", _radius);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		setRadius(tag.getShort("radius"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);

		tag.setShort("radius", _radius);
		tag.setInteger("empty", emptyTicks);
		tag.setInteger("inactive", unactivatedTicks);
		tag.setInteger("consumed", consumptionTicks);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);

		setRadius(tag.getShort("radius"));
		emptyTicks = tag.getInteger("empty");
		unactivatedTicks = tag.getInteger("inactive");
		consumptionTicks = tag.getInteger("consumed");
		onFactoryInventoryChanged();
	}

	protected boolean isFluidFuel(FluidStack fuel) {

		String name = getFluidName(fuel);
		if (name == null)
			return false;
		return fluidConsumptionRate.containsKey(name);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (!unableToRequestTicket & resource != null && isFluidFuel(resource))
			for (FluidTankAdv _tank : getTanks())
				if (_tank.getFluidAmount() == 0 || resource.isFluidEqual(_tank.getFluid()))
					return _tank.fill(resource, doFill);
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
	public boolean allowBucketFill(ItemStack stack) {

		return !unableToRequestTicket;
	}

	@Override
	public boolean allowBucketDrain(ItemStack stack) {

		return true;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return !unableToRequestTicket;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return unableToRequestTicket;
	}

	protected String getFluidName(FluidStack fluid) {

		if (fluid == null || fluid.getFluid() == null)
			return null;
		String name = fluid.getFluid().getName();
		if (name == null)
			return null;
		return name;
	}

	@Override
	public int getSizeInventory() {

		return 0;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 40;
	}

	public short getRadius() {

		return _radius;
	}

	public boolean getUnableToWork() {

		return unableToRequestTicket;
	}

	@SideOnly(Side.CLIENT)
	public void setEmpty(int r) {

		emptyTicks = r;
		onFactoryInventoryChanged();
	}

	public short getEmpty() {

		return (short) emptyTicks;
	}

	@Override
	public void setIsActive(boolean a) {

	}

	@Override
	public boolean canFormBeamWith(ForgeDirection from) {

		return true;
	}

	@Override
	public int addEnergy(ForgeDirection from, int energy, boolean simulate) {

		return storeEnergy(energy, !simulate);
	}
}
