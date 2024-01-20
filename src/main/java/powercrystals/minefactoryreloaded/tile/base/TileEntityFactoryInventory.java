package powercrystals.minefactoryreloaded.tile.base;

import cofh.api.item.IAugmentItem;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.FluidHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import powercrystals.minefactoryreloaded.core.MFRLiquidMover;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class TileEntityFactoryInventory extends TileEntityFactory implements ISidedInventory {

	protected final static FluidTankAdv[] emptyIFluidTank = new FluidTankAdv[] { };
	protected final static FluidTankInfo[] emptyFluidTankInfo = FluidHelper.NULL_TANK_INFO;
	protected final static int BUCKET_VOLUME = FluidContainerRegistry.BUCKET_VOLUME;

	protected List<ItemStack> failedDrops = null;
	private List<ItemStack> missedDrops = new ArrayList<ItemStack>(5);
	protected int _failedDropTicksMax = 20;
	private int _failedDropTicks = 0;

	protected FluidTankAdv[] _tanks;

	protected ItemStack[] _inventory;

	protected boolean internalChange = false, client = false;

	protected TileEntityFactoryInventory(Machine machine) {

		super(machine);
		_inventory = new ItemStack[getSizeInventory()];
		_tanks = createTanks();
		setManageFluids(_tanks != null);
	}

	@Override
	public void cofh_validate() {

		super.cofh_validate();
		client = worldObj.isRemote;
	}

	@Override
	public String getInventoryName() {

		return _invName != null ? _invName : StatCollector.
				translateToLocal(_machine.getInternalName() + ".name");
	}

	@Override
	public boolean hasCustomInventoryName() {

		return _invName != null;
	}

	public void onDisassembled() {

		if (failedDrops != null)
			inv: while (failedDrops.size() > 0) {
				ItemStack itemstack = failedDrops.remove(0);
				if (itemstack == null || itemstack.getItem() == null) {
					continue;
				}
				float xOffset = worldObj.rand.nextFloat() * 0.8F + 0.1F;
				float yOffset = worldObj.rand.nextFloat() * 0.8F + 0.1F;
				float zOffset = worldObj.rand.nextFloat() * 0.8F + 0.1F;
				do {
					if (itemstack.stackSize <= 0) {
						continue inv;
					}
					int amountToDrop = worldObj.rand.nextInt(21) + 10;
					if (amountToDrop > itemstack.stackSize) {
						amountToDrop = itemstack.stackSize;
					}
					itemstack.stackSize -= amountToDrop;
					EntityItem entityitem = new EntityItem(worldObj,
							xCoord + xOffset, yCoord + yOffset, zCoord + zOffset,
							new ItemStack(itemstack.getItem(), amountToDrop, itemstack.getItemDamage()));
					if (itemstack.getTagCompound() != null) {
						entityitem.getEntityItem().setTagCompound(itemstack.getTagCompound());
					}
					float motionMultiplier = 0.05F;
					entityitem.motionX = (float) worldObj.rand.nextGaussian() * motionMultiplier;
					entityitem.motionY = (float) worldObj.rand.nextGaussian() * motionMultiplier + 0.2F;
					entityitem.motionZ = (float) worldObj.rand.nextGaussian() * motionMultiplier;
					worldObj.spawnEntityInWorld(entityitem);
				} while (true);
			}
	}

	public void onBlockBroken() {

		NBTTagCompound tag = new NBTTagCompound();
		writeItemNBT(tag);
		onDisassembled();
	}

	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		IFluidTank[] tanks = getTanks();
		if (tanks.length == 0)
			return emptyFluidTankInfo;
		FluidTankInfo[] r = new FluidTankInfo[tanks.length];
		for (int i = tanks.length; i-- > 0;)
			r[i] = tanks[i].getInfo();
		return r;
	}

	protected FluidTankAdv[] createTanks() {

		return null;
	}

	public FluidTankAdv[] getTanks() {

		if (_tanks != null)
			return _tanks;
		return emptyIFluidTank;
	}

	public int drain(FluidTankAdv _tank, int maxDrain, boolean doDrain) {

		if (_tank.getFluidAmount() > 0) {
			FluidStack drained = _tank.drain(maxDrain, doDrain);
			if (drained != null) {
				if (doDrain) {
					internalChange = true;
					markDirty();
					internalChange = false;
				}
				return drained.amount;
			}
		}
		return 0;
	}

	public FluidStack drain(int maxDrain, boolean doDrain) {

		for (FluidTankAdv _tank : getTanks())
			if (_tank.getFluidAmount() > 0)
				return _tank.drain(maxDrain, doDrain);
		return null;
	}

	public FluidStack drain(FluidStack resource, boolean doDrain) {

		if (resource != null)
			for (FluidTankAdv _tank : getTanks())
				if (resource.isFluidEqual(_tank.getFluid()))
					return _tank.drain(resource.amount, doDrain);
		return null;
	}

	public int fill(FluidStack resource, boolean doFill) {

		if (resource != null)
			for (FluidTankAdv _tank : getTanks())
				if (FluidHelper.isFluidEqualOrNull(_tank.getFluid(), resource))
					return _tank.fill(resource, doFill);
		return 0;
	}

	protected boolean shouldPumpLiquid() {

		return false;
	}

	protected boolean shouldPumpTank(IFluidTank tank) {

		return true;
	}

	public boolean allowBucketFill(ItemStack stack) {

		return false;
	}

	public boolean allowBucketDrain(ItemStack stack) {

		return false;
	}

	@Override
	public void updateEntity() {

		super.updateEntity();

		if (!worldObj.isRemote && shouldPumpLiquid()) {
			for (IFluidTank tank : getTanks())
				if (shouldPumpTank(tank))
					MFRLiquidMover.pumpLiquid(tank, this);
		}

		if (failedDrops != null) {
			if (_failedDropTicks < _failedDropTicksMax) {
				_failedDropTicks++;
				return;
			}
			_failedDropTicks = 0;
			if (!doDrop(failedDrops)) {
				return;
			}
			failedDrops = null;
			markDirty();
		}
	}

	public boolean doDrop(ItemStack drop) {

		drop = UtilInventory.dropStack(this, drop, this.getDropDirections(), this.getDropDirection());
		if (drop != null && drop.stackSize > 0) {
			if (failedDrops == null) {
				failedDrops = new ArrayList<ItemStack>();
			}
			failedDrops.add(drop);
			markDirty();
		}
		return true;
	}

	public boolean doDrop(List<ItemStack> drops) {

		if (drops == null || drops.size() <= 0) {
			return true;
		}
		List<ItemStack> missed = missedDrops;
		missed.clear();
		for (int i = drops.size(); i-- > 0;) {
			ItemStack dropStack = drops.get(i);
			dropStack = UtilInventory.dropStack(this, dropStack, this.getDropDirections(), this.getDropDirection());
			if (dropStack != null && dropStack.stackSize > 0) {
				missed.add(dropStack);
			}
		}

		if (missed.size() != 0) {
			if (drops != failedDrops) {
				if (failedDrops == null) {
					failedDrops = new ArrayList<ItemStack>(missed.size());
				}
				failedDrops.addAll(missed);
			}
			else {
				failedDrops.clear();
				failedDrops.addAll(missed);
			}
			markDirty();
			return false;
		}

		return true;
	}

	public boolean hasDrops() {

		return failedDrops != null;
	}

	public int getUpgradeSlot() {

		return -1;
	}

	protected boolean canUseUpgrade(ItemStack stack, IAugmentItem item) {

		return _areaManager != null && item.getAugmentLevel(stack, "radius") != 0;
	}

	public boolean isUsableAugment(ItemStack stack) {

		if (stack == null || !(stack.getItem() instanceof IAugmentItem))
			return false;
		return canUseUpgrade(stack, (IAugmentItem) stack.getItem());
	}

	public boolean acceptUpgrade(ItemStack stack) {

		int slot = getUpgradeSlot();
		if (slot < 0 | stack == null || !isUsableAugment(stack))
			return false;
		if (getStackInSlot(slot) != null)
			return false;

		setInventorySlotContents(slot, stack.splitStack(1));
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int i) {

		return _inventory[i];
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {

		if (_inventory[slot] != null) {
			if (_inventory[slot].stackSize <= size) {
				ItemStack itemstack = _inventory[slot];
				_inventory[slot] = null;
				markDirty();
				return itemstack;
			}
			ItemStack itemstack1 = _inventory[slot].splitStack(size);
			if (_inventory[slot].stackSize <= 0) {
				_inventory[slot] = null;
			}
			markDirty();
			return itemstack1;
		}
		else {
			markDirty();
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {

		if (itemstack != null) {
			if (itemstack.stackSize > getInventoryStackLimit())
				itemstack.stackSize = getInventoryStackLimit();
			else if (itemstack.stackSize < 0)
				itemstack = null;
		}
		_inventory[i] = itemstack;
		markDirty();
	}

	@Override
	public void markDirty() {

		if (!internalChange)
			onFactoryInventoryChanged();
		super.markDirty();
	}

	protected void onFactoryInventoryChanged() {

		if (_areaManager != null && getUpgradeSlot() >= 0 && !internalChange) {
			_areaManager.updateUpgradeLevel(getStackInSlot(getUpgradeSlot()));
		}
	}

	@Override
	public int getInventoryStackLimit() {

		return 127;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {

		int start = getStartInventorySide(ForgeDirection.UNKNOWN);
		if (slot < start ||
				slot > (start + getSizeInventorySide(ForgeDirection.UNKNOWN)))
			return false;
		if (itemstack == null)
			return true;
		if (itemstack.stackSize > Math.min(itemstack.getMaxStackSize(), getInventoryStackLimit()))
			return false;
		ItemStack slotContent = this.getStackInSlot(slot);
		return slotContent == null || UtilInventory.stacksEqual(itemstack, slotContent);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {

		if (isInvalid() || worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		}
		return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		_inventory = new ItemStack[getSizeInventory()];
		NBTTagList nbttaglist;
		if (tag.hasKey("Items")) {
			nbttaglist = tag.getTagList("Items", 10);
			for (int i = nbttaglist.tagCount(); i-- > 0;) {
				NBTTagCompound slot = nbttaglist.getCompoundTagAt(i);
				int j = slot.getByte("Slot") & 0xff;
				if (j < _inventory.length) {
					_inventory[j] = ItemStack.loadItemStackFromNBT(slot);
					if (_inventory[j].stackSize < 0)
						_inventory[j] = null;
				}
			}
		}
		markDirty();

		if (manageFluids() && tag.hasKey("Tanks")) {
			IFluidTank[] _tanks = getTanks();

			nbttaglist = tag.getTagList("Tanks", 10);
			for (int i = 0; i < nbttaglist.tagCount(); i++) {
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound1.getByte("Tank") & 0xff;
				if (j < _tanks.length) {
					FluidStack l = FluidStack.loadFluidStackFromNBT(nbttagcompound1);
					if (l != null) {
						((FluidTankAdv) _tanks[j]).setFluid(l);
					}
				}
			}
		}

		if (tag.hasKey("DropItems")) {
			List<ItemStack> drops = new ArrayList<ItemStack>();
			nbttaglist = tag.getTagList("DropItems", 10);
			for (int i = nbttaglist.tagCount(); i-- > 0;) {
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				ItemStack item = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				if (item != null && item.stackSize > 0) {
					drops.add(item);
				}
			}
			if (drops.size() != 0) {
				failedDrops = drops;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);
		if (_inventory.length > 0) {
			NBTTagList items = new NBTTagList();
			for (int i = 0; i < _inventory.length; i++) {
				if (_inventory[i] != null && _inventory[i].stackSize >= 0) {
					NBTTagCompound slot = new NBTTagCompound();
					slot.setByte("Slot", (byte) i);
					_inventory[i].writeToNBT(slot);
					items.appendTag(slot);
				}
			}
			if (items.tagCount() > 0)
				tag.setTag("Items", items);
		}

		if (failedDrops != null) {
			NBTTagList dropItems = new NBTTagList();
			for (ItemStack item : failedDrops) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				item.writeToNBT(nbttagcompound1);
				dropItems.appendTag(nbttagcompound1);
			}
			if (dropItems.tagCount() > 0)
				tag.setTag("DropItems", dropItems);
		}
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);

		IFluidTank[] _tanks = getTanks();
		if (_tanks.length > 0) {
			NBTTagList tanks = new NBTTagList();
			for (int i = 0, n = _tanks.length; i < n; i++) {
				FluidStack fluid = _tanks[i].getFluid();
				if (fluid != null && fluid.amount > 0) {
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setByte("Tank", (byte) i);

					FluidStack l = _tanks[i].getFluid();
					l.writeToNBT(nbttagcompound1);
					tanks.appendTag(nbttagcompound1);
				}
			}
			if (tanks.tagCount() > 0)
				tag.setTag("Tanks", tanks);
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {

		return null;
	}

	public boolean shouldDropSlotWhenBroken(int slot) {

		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		int start = getStartInventorySide(ForgeDirection.getOrientation(side));
		int size = getSizeInventorySide(ForgeDirection.getOrientation(side));

		int[] slots = new int[size];
		for (int i = 0; i < size; i++) {
			slots[i] = i + start;
		}
		return slots;
	}

	public int getStartInventorySide(ForgeDirection side) {

		return 0;
	}

	public int getSizeInventorySide(ForgeDirection side) {

		return getSizeInventory();
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {

		return itemstack == null || this.isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {

		return true;
	}

	public int getComparatorOutput(int side) {

		IFluidTank[] tanks = getTanks();
		IFluidTank tank = null;
		if (tanks.length > 0)
			tank = tanks[0];
		float tankPercent = 0, invPercent = 0;
		boolean hasTank = false, hasInventory = false;
		if (tank != null) {
			hasTank = true;
			if (tank.getFluid() != null) {
				tankPercent = ((float) tank.getFluid().amount) / tank.getCapacity();
			}
		}
		int[] accSlots = getAccessibleSlotsFromSide(side);
		if (accSlots.length > 0) {
			hasInventory = true;
			int[] slots = accSlots;
			int len = 0;
			float ret = 0;
			for (int i = slots.length; i-- > 0;) {
				if (canInsertItem(slots[i], null, side)) {
					ItemStack stack = getStackInSlot(slots[i]);
					if (stack != null) {
						float maxStack = Math.min(stack.getMaxStackSize(), getInventoryStackLimit());
						ret += Math.max(Math.min(stack.stackSize / maxStack, 1), 0);
					}
					++len;
				}
			}
			invPercent = ret / len;
		}
		float mult = hasTank & hasInventory ? (tankPercent + invPercent) / 2 : hasTank ? tankPercent : hasInventory ? invPercent : 0f;
		return (int) Math.ceil(15 * mult);
	}
}
