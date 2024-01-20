package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiDeepStorageUnit;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerDeepStorageUnit;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TileEntityDeepStorageUnit extends TileEntityFactoryInventory implements IDeepStorageUnit {

	private boolean _ignoreChanges = true;
	private boolean _passingItem = false;

	private int _storedQuantity;
	private ItemStack _storedItem = null;

	public TileEntityDeepStorageUnit() {

		super(Machine.DeepStorageUnit);

		setManageSolids(true);
	}

	@Override
	public void cofh_validate() {

        //MineFactoryReloadedCore.log().info("cofh validate started");
		//super.cofh_validate();
		_ignoreChanges = false;
		onFactoryInventoryChanged();

        //MineFactoryReloadedCore.log().info("cofh validate ended?");
	}
    public void markDirty(){
        _ignoreChanges = false;
        onFactoryInventoryChanged();
    }
	@Override
	public void invalidate() {

		super.invalidate();
		_ignoreChanges = true;
	}

	@Override
	public boolean shouldDropSlotWhenBroken(int slot) {

		return slot < 2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiDeepStorageUnit(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerDeepStorageUnit getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerDeepStorageUnit(this, inventoryPlayer);
	}

	public int getQuantity() {

		return _storedQuantity;
	}

	public int getQuantityAdjusted() {
        //MineFactoryReloadedCore.log().info("" + getSizeInventory());

		int quantity = _storedQuantity;

		for (int i = 2; i < getSizeInventory(); i++) {
			if (_inventory[i] != null && UtilInventory.stacksEqual(_storedItem, _inventory[i])) {
				quantity += _inventory[i].stackSize;
                //MineFactoryReloadedCore.log().info("" + _inventory[i].stackSize);
			}
		}
        //MineFactoryReloadedCore.log().info("completed getquantityadjusted");

		return quantity;
	}

	public void setQuantity(int quantity) {

		_storedQuantity = quantity;
	}

	public void clearSlots() {
        //MineFactoryReloadedCore.log().debug("clearslots");

		for (int i = 0; i < getSizeInventory(); i++) {
			_inventory[i] = null;
		}
        //MineFactoryReloadedCore.log().debug("completed clearslots");

	}

	@Override
	public ForgeDirection getDropDirection() {

		return ForgeDirection.UP;
	}

	@Override
	public void updateEntity() {

		super.updateEntity();

		if (worldObj.isRemote)
			return;
	}

	@Override
	public void setIsActive(boolean isActive) {

		super.setIsActive(isActive);
		onFactoryInventoryChanged();
	}

	@Override
	protected void onFactoryInventoryChanged() {

        //MineFactoryReloadedCore.log().info("started onFactoryInventoryChanged main");
		super.onFactoryInventoryChanged();
//        MineFactoryReloadedCore.log().info(" isActive " + isActive() + " _inventory[2] " + _inventory[2] + " storedItem " + _storedItem + " storedQuantity " + _storedQuantity);

 //       MineFactoryReloadedCore.log().info("_ignoreChanges " + _ignoreChanges );
        if (_ignoreChanges | worldObj == null || worldObj.isRemote){
       //     MineFactoryReloadedCore.log().info("case 1");
			return;
        }
        if (!isActive() && (_inventory[2] == null) & _storedItem != null & _storedQuantity == 0) {
        //    MineFactoryReloadedCore.log().info("case 2");
            _storedItem = null;
		}
		checkInput(0);
		checkInput(1);

		if ((_inventory[2] == null) & _storedItem != null & _storedQuantity > 0) {
        //    MineFactoryReloadedCore.log().info("case 3");

			_inventory[2] = _storedItem.copy();
			_inventory[2].stackSize = Math.min(_storedQuantity,
				Math.min(_storedItem.getMaxStackSize(), getInventoryStackLimit()));
			_storedQuantity -= _inventory[2].stackSize;
		} else if (_inventory[2] != null & _storedQuantity > 0 &&
				_inventory[2].stackSize < _inventory[2].getMaxStackSize() &&
				UtilInventory.stacksEqual(_storedItem, _inventory[2])) {
        //    MineFactoryReloadedCore.log().info("case 4");
			int amount = Math.min(_inventory[2].getMaxStackSize() - _inventory[2].stackSize, _storedQuantity);
			_inventory[2].stackSize += amount;
			_storedQuantity -= amount;
		}

       // MineFactoryReloadedCore.log().info("ended onFactoryInventoryChanged main");
	}

	private void checkInput(int slot) {
        //MineFactoryReloadedCore.log().info("started checkInput");
		l: if (_inventory[slot] != null) {
        //    MineFactoryReloadedCore.log().info("slot not empty");
			if (_storedItem == null) {
				_storedItem = _inventory[slot].copy();
				_storedItem.stackSize = 1;
				_storedQuantity = _inventory[slot].stackSize;
				_inventory[slot] = null;
			} else if (UtilInventory.stacksEqual(_inventory[slot], _storedItem)) {
				if ((getMaxStoredCount() - _storedItem.getMaxStackSize()) - _inventory[slot].stackSize < _storedQuantity) {
					int amt = (getMaxStoredCount() - _storedItem.getMaxStackSize()) - _storedQuantity;
					_inventory[slot].stackSize -= amt;
					_storedQuantity += amt;
				} else {
					_storedQuantity += _inventory[slot].stackSize;
					_inventory[slot] = null;
				}
			}
			// boot improperly typed items from the input slots
			else {
				_passingItem = true;
				_inventory[slot] = UtilInventory.dropStack(this, _inventory[slot], this.getDropDirection());
				_passingItem = false;
				break l;
			}
			// internal inventory is full
			if (_inventory[slot] != null) {
				_passingItem = true;
				_inventory[slot] = UtilInventory.dropStack(this, _inventory[slot], this.getDropDirection());
				_passingItem = false;
			}
		}
	}

	@Override
	public int getSizeInventory() {

		return 3;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return player.getDistanceSq(xCoord, yCoord, zCoord) <= 64D;
	}

	@Override
	public int getStartInventorySide(ForgeDirection side) {

		return 0;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {

		return getSizeInventory();
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {

		if (itemstack != null) {
			if (itemstack.stackSize < 0)
				itemstack = null;
		}
		_inventory[i] = itemstack;
		markDirty();
	}

	/*
	 * Should only allow matching items to be inserted in the "in" slots. Nothing goes in the "out" slot.
	 */
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal) {

		if (_passingItem)
			return false;
		if (slot >= 2) return false;
		ItemStack stored = _storedItem;
		if (stored == null) stored = _inventory[2];
		return stored == null || (UtilInventory.stacksEqual(stored, stack) && (getMaxStoredCount() - stored.getMaxStackSize()) > _storedQuantity);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {

		return canInsertItem(slot, itemstack, -1);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal) {

		return true;
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		int storedAdd = 0;
		ItemStack o = _inventory[2];
		if (o != null) {
			storedAdd = o.stackSize;
			_inventory[2] = null;
		}
		super.writeItemNBT(tag);
		_inventory[2] = o;

		if (_storedItem != null) {
			tag.setTag("storedStack", _storedItem.writeToNBT(new NBTTagCompound()));
			tag.setInteger("storedQuantity", _storedQuantity + storedAdd);
			tag.setBoolean("locked", isActive());
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		ItemStack o = _inventory[2];
		_inventory[2] = null;
		super.writeToNBT(tag);
		_inventory[2] = o;
		writeItemNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		_ignoreChanges = true;
		super.readFromNBT(tag);

		_storedQuantity = tag.getInteger("storedQuantity");
		_storedItem = null;

		if (tag.hasKey("storedStack")) {
			_storedItem = ItemStack.loadItemStackFromNBT((NBTTagCompound) tag.getTag("storedStack"));
			if (_storedItem != null) {
				_storedItem.stackSize = 1;
				setIsActive(tag.getBoolean("locked"));
			}
		}

		if (_storedItem == null) {
			_storedQuantity = 0;
		}
		_ignoreChanges = false;
	}

	public ItemStack getStoredItemRaw() {

		if (_storedItem != null) {
			return _storedItem.copy();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public void setStoredItemRaw(ItemStack type) {

		if (worldObj.isRemote) {
			_storedItem = type;
		}
	}

	@Override
	public ItemStack getStoredItemType() {

		int quantity = getQuantityAdjusted();
		if ((isActive() || quantity != 0) & _storedItem != null) {
			ItemStack stack = _storedItem.copy();
			stack.stackSize = quantity;
			return stack;
		}
		return null;
	}

	@Override
	public void setStoredItemCount(int amount) {

		for (int i = 0; i < getSizeInventory(); i++) {
			if (UtilInventory.stacksEqual(_inventory[i], _storedItem)) {
				if (amount == 0) {
					_inventory[i] = null;
				} else if (amount >= _inventory[i].stackSize) {
					amount -= _inventory[i].stackSize;
				} else if (amount < _inventory[i].stackSize) {
					_inventory[i].stackSize = amount;
					amount = 0;
				}
			}
		}
		_storedQuantity = amount;
		markDirty();
	}

	@Override
	public void setStoredItemType(ItemStack type, int amount) {

		if (_storedItem != null && isActive() && !UtilInventory.stacksEqual(type, _storedItem))
			return;
		clearSlots();
		_storedQuantity = amount;
		_storedItem = null;
		if (type == null)
			return;
		_storedItem = type.copy();
		_storedItem.stackSize = 1;
		markDirty();
	}

	@Override
	public int getMaxStoredCount() {

		return Integer.MAX_VALUE;
	}
}
