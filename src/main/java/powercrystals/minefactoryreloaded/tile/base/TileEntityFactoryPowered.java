package powercrystals.minefactoryreloaded.tile.base;

import appeng.api.implementations.tiles.ICrankable;

import cofh.api.energy.IEnergyReceiver;
import cofh.api.item.IAugmentItem;
import cofh.asm.relauncher.Strippable;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.AugmentHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.setup.Machine;

/*
 * There are three pieces of information tracked - energy, work, and idle ticks.
 * Energy is stored and used when the _machine activates. The energy stored must be >= energyActivation for the activateMachine() method to be called.
 * If activateMachine() returns true, energy will be drained.
 * Work is built up and then when at 100% something happens. This is tracked/used entirely by the derived class. If not used (f.ex. harvester), return max 1.
 * Idle ticks cause an artificial delay before activateMachine() is called again. Max should be the highest value the _machine will use, to draw the
 * progress bar correctly.
 */
@Strippable("appeng.api.implementations.tiles.ICrankable")
public abstract class TileEntityFactoryPowered extends TileEntityFactoryInventory
																					implements IEnergyReceiver, ICrankable {

	public static final int energyPerAE = 2;
	public static final int energyPerEU = 4;
	public static final int energyPerMJ = 10;

	private static final int energyFudge = 80;

	private int _energyStored;
	private int _maxEnergyStored;
	private int _maxEnergyTick;
	private int _energyRequiredThisTick = 0;

	protected int _energyActivation;

	private int _workTicks = 1;
	private int _workDone;

	private int _idleTicks;

	// constructors

	protected TileEntityFactoryPowered(Machine machine) {

		this(machine, machine.getActivationEnergy());
	}

	protected TileEntityFactoryPowered(Machine machine, int activationCost) {

		super(machine);
		_maxEnergyStored = machine.getMaxEnergyStorage();
		setActivationEnergy(activationCost);
		setIsActive(false);
	}

	// local methods

	protected void setActivationEnergy(int activationCost) {

		_energyActivation = activationCost;
		_maxEnergyTick = Math.min(activationCost * 4, _maxEnergyStored);
	}

	@Override
	public void updateEntity() {

		super.updateEntity();

		_energyStored = Math.min(_energyStored, getEnergyStoredMax());

		if (worldObj.isRemote) {
			machineDisplayTick();
			return;
		}

		markChunkDirty();

		int energyRequired = Math.min(getEnergyStoredMax() - getEnergyStored(), getActivationEnergy() + energyFudge);

		_energyRequiredThisTick = Math.max(_energyRequiredThisTick + energyRequired, getMaxEnergyPerTick());

		setIsActive(updateIsActive(failedDrops != null));

		if (failedDrops != null) {
			setIdleTicks(getIdleTicksMax());
			return;
		}

		if (_rednetState > 0 || CoreUtils.isRedstonePowered(this)) {
			setIdleTicks(getIdleTicksMax());
		} else if (_idleTicks > 0) {
			_idleTicks--;
		} else if (_energyStored >= _energyActivation) {
			int i = 0;
			for (; i < _workTicks && activateMachine(); ++i);
			if (i > 0) {
				_energyStored -= _energyActivation;
			}
		}
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		int i = getUpgradeSlot();
		if (i < 0) {
			return;
		}
		ItemStack stack = getStackInSlot(i);
		if (AugmentHelper.isAugmentItem(stack)) {
			IAugmentItem item = (IAugmentItem) stack.getItem();
			int level = item.getAugmentLevel(stack, "machineSpeed");
			if (level >= 0) {
				_workTicks = (int) Math.pow(2, level);
				int e = _machine.getActivationEnergy();
				setActivationEnergy(_workTicks * (e + ((e * level) >> 1)));
			}
		} else {
			if (_workTicks > 1) {
				setActivationEnergy(_machine.getActivationEnergy());
			}
			_workTicks = 1;
		}
	}

	@Override
	protected boolean canUseUpgrade(ItemStack stack, IAugmentItem item) {

		return super.canUseUpgrade(stack, item) || item.getAugmentLevel(stack, "machineSpeed") > 0;
	}

	protected boolean updateIsActive(boolean failedDrops) {

		return !failedDrops && hasSufficientPower();
	}

	protected abstract boolean activateMachine();

	@SideOnly(Side.CLIENT)
	protected void machineDisplayTick() {

	}

	public final boolean hasSufficientPower() {

		return _energyStored >= _energyActivation * 2;
	}

	public int getActivationEnergy() {

		return _energyActivation;
	}

	public int getMaxEnergyPerTick() {

		return _maxEnergyTick;
	}

	public int getEnergyStored() {

		return _energyStored;
	}

	public int getEnergyStoredMax() {

		return _maxEnergyStored;
	}

	public void setEnergyStored(int energy) {

		_energyStored = energy;
	}

	public void drainEnergy(int drainAmount) {

		_energyStored -= drainAmount;
	}

	public int getWorkDone() {

		return _workDone;
	}

	public abstract int getWorkMax();

	public void setWorkDone(int work) {

		_workDone = work;
	}

	protected boolean incrementWorkDone() {

		setWorkDone(getWorkDone() + 1);
		return true;
	}

	public int getIdleTicks() {

		return _idleTicks;
	}

	public abstract int getIdleTicksMax();

	public void setIdleTicks(int ticks) {

		_idleTicks = ticks;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);

		if (_workDone > 0)
			tag.setInteger("workDone", _workDone);

		if (_idleTicks > 0)
			tag.setInteger("idleDone", _idleTicks);
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);

		if (_energyStored > 0)
			tag.setInteger("energyStored", _energyStored);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);

		_energyStored = Math.min(tag.getInteger("energyStored"), getEnergyStoredMax());
		_idleTicks = Math.min(tag.getInteger("idleDone"), getIdleTicksMax());
		_workDone = Math.min(tag.getInteger("workDone"), getWorkMax());
	}

	public int getEnergyRequired() {

		return Math.min(getEnergyStoredMax() - getEnergyStored(), _energyRequiredThisTick);
	}

	public int storeEnergy(int energy, boolean doStore) {

		int energyInjected = Math.max(Math.min(energy, getEnergyRequired()), 0);
		if (doStore) {
			_energyStored += energyInjected;
			_energyRequiredThisTick -= energyInjected;
		}
		return energyInjected;
	}

	public int storeEnergy(int energy) {

		return storeEnergy(energy, true);
	}

	// TE methods
	/*
		@Override
		public int getInfoEnergyPerTick() {
			return getEnergyRequired();
		}

		@Override
		public int getInfoMaxEnergyPerTick() {
			return getMaxEnergyPerTick();
		}

		@Override
		public int getInfoEnergyStored() {
			return getEnergyStored();
		}

		@Override
		public int getInfoMaxEnergyStored() {
			return getEnergyStoredMax();
		}//*/

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		return storeEnergy(maxReceive, !simulate);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {

		return true;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {

		return getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {

		return getEnergyStoredMax();
	}

	// AE methods

	@Override
	public boolean canTurn() {

		return getEnergyStored() < getEnergyStoredMax();
	}

	@Override
	public void applyTurn() {

		storeEnergy(90, true);
	}

	@Override
	public boolean canCrankAttach(ForgeDirection directionToCrank) {

		return true;
	}
}
