package powercrystals.minefactoryreloaded.tile.base;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.EnergyHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class TileEntityFactoryGenerator extends TileEntityFactoryInventory
										implements IEnergyProvider {
	private boolean deadCache;
	private IEnergyReceiver[] receiverCache;

	private int _ticksBetweenConsumption;
	private int _outputPulseSize;

	private int _ticksSinceLastConsumption = 0;
	private int _energyMax;
	private int _energy;

	protected TileEntityFactoryGenerator(Machine machine, int ticksBetweenConsumption) {
		super(machine);
		if (machine.getActivationEnergy() <= 0)
			throw new IllegalStateException("Generators cannot produce 0 energy.");
		_ticksBetweenConsumption = ticksBetweenConsumption;
		_outputPulseSize = machine.getActivationEnergy();
		_energyMax = machine.getMaxEnergyStorage();
	}

	@Override
	public void validate() {
		super.validate();
		deadCache = true;
		receiverCache = null;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (deadCache) reCache();

			boolean skipConsumption = ++_ticksSinceLastConsumption < _ticksBetweenConsumption;

			if (_rednetState != 0 || CoreUtils.isRedstonePowered(this)) {
				setIsActive(false);
				return;
			}

			setIsActive(hasFuel());

			int pulse = Math.min(_energy, _outputPulseSize);
			_energy -= pulse - transmitEnergy(pulse);

			if (skipConsumption || !canConsumeFuel(_energyMax - _energy))
				return;

			if (consumeFuel()) {
				_energy += produceEnergy();
				_ticksSinceLastConsumption = 0;
			}
		}
	}

	protected abstract boolean canConsumeFuel(int space);
	protected abstract boolean consumeFuel();
	protected abstract boolean hasFuel();
	protected abstract int produceEnergy();

	protected final int transmitEnergy(int energy) {
		if (_inventory[0] != null)
			energy -= EnergyHelper.insertEnergyIntoContainer(_inventory[0], energy, false);
		if (energy <= 0)
			return 0;

		if (receiverCache != null)
			for (int i = receiverCache.length; i --> 0; ) {
				IEnergyReceiver tile = receiverCache[i];
				if (tile == null)
					continue;

				ForgeDirection from = ForgeDirection.VALID_DIRECTIONS[i];
				if (tile.receiveEnergy(from, energy, true) > 0)
					energy -= tile.receiveEnergy(from, energy, false);
				if (energy <= 0)
					return 0;
			}

		return energy;
	}

	public int getBuffer() {
		return _energy;
	}

	public void setBuffer(int buffer) {
		_energy = buffer;
	}

	public int getBufferMax() {
		return _energyMax;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		return EnergyHelper.isEnergyContainerItem(itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		return _inventory[0] != null && EnergyHelper.insertEnergyIntoContainer(_inventory[0], 2, true) < 2;
	}

	private void reCache() {
		if (deadCache) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				onNeighborTileChange(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ);
			deadCache = false;
		}
	}

	@Override
	public void onNeighborTileChange(int x, int y, int z) {
		TileEntity tile = worldObj.getTileEntity(x, y, z);

		if (x < xCoord)
			addCache(tile, 5);
		else if (x > xCoord)
			addCache(tile, 4);
		else if (z < zCoord)
			addCache(tile, 3);
		else if (z > zCoord)
			addCache(tile, 2);
		else if (y < yCoord)
			addCache(tile, 1);
		else if (y > yCoord)
			addCache(tile, 0);
	}

	private void addCache(TileEntity tile, int side) {
		if (receiverCache != null)
			receiverCache[side] = null;

		if (tile instanceof IEnergyReceiver) {
			if (((IEnergyReceiver)tile).canConnectEnergy(ForgeDirection.VALID_DIRECTIONS[side])) {
				if (receiverCache == null) receiverCache = new IEnergyReceiver[6];
				receiverCache[side] = (IEnergyReceiver)tile;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (_ticksSinceLastConsumption > 0)
			tag.setInteger("ticksSinceLastConsumption", _ticksSinceLastConsumption);
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {
		super.writeItemNBT(tag);

		if (_energy > 0)
			tag.setInteger("energyStored", _energy);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		_ticksSinceLastConsumption = tag.getInteger("ticksSinceLastConsumption");
		_energy = tag.getInteger(tag.hasKey("energyStored") ? "energyStored" : "buffer");
	}

	// TE methods

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		maxExtract = Math.min(_energy, Math.min(_outputPulseSize, maxExtract));
		if (maxExtract <= 0) return 0;

		if (!simulate)
			_energy -= maxExtract;
		return maxExtract;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return _energy;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return _energyMax;
	}
}
