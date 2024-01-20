package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.ItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiAutoBrewer;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoBrewer;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityAutoBrewer extends TileEntityFactoryPowered implements ITankContainerBucketable {

	protected boolean _inventoryDirty;

	public TileEntityAutoBrewer() {

		super(Machine.AutoBrewer);
		setManageSolids(true);
		_tanks[0].setLock(FluidRegistry.getFluid("water"));
	}

	private int getProcessSlot(int row) {

		return row * 5;
	}

	private int getTemplateSlot(int row) {

		return row * 5 + 1;
	}

	private int getResourceSlot(int row, int slot) {

		return row * 5 + slot + 2;
	}

	@Override
	protected FluidTankAdv[] createTanks() {

		return new FluidTankAdv[] { new FluidTankAdv(BUCKET_VOLUME * 4) };
	}

	@Override
	public int getSizeInventory() {

		// 6 sets of: process, template, res, res, res
		// 30 is output, 31 is empty bottle input
		return 32;
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerAutoBrewer(this, inventoryPlayer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiAutoBrewer(getContainer(inventoryPlayer), this);
	}

	@Override
	protected boolean activateMachine() {

		boolean hasWorkToDo = false, didWork = false;
		boolean doingWork = getWorkDone() > 0;
		if (doingWork & !_inventoryDirty)
			hasWorkToDo = true;
		else {
			final int waterCost = MFRConfig.autobrewerFluidCost.getInt();
			for (int row = 0; row < 6; row++) {
				int processSlot = getProcessSlot(row), templateSlot = getTemplateSlot(row);
				if (_inventory[31] != null && _inventory[processSlot] == null && _inventory[templateSlot] != null) {
					if (row == 0 || _inventory[getTemplateSlot(row - 1)] == null)
						if (getPotionResult(0, _inventory[templateSlot]) != 0)
							if (drain(_tanks[0], waterCost, false) == waterCost) {
								drain(_tanks[0], waterCost, true);
								_inventory[31] = ItemHelper.consumeItem(_inventory[31]);
								_inventory[processSlot] = new ItemStack(Items.potionitem, 1, 0);
								didWork = true;
							}
				}
				if (_inventory[processSlot] != null) {
					if (_inventory[getProcessSlot(row + 1)] == null && canBrew(row))
						hasWorkToDo = true;
				}
			}
			_inventoryDirty = false;
		}

		if (!hasWorkToDo) {
			setWorkDone(0);
			setIdleTicks(getIdleTicksMax());
			return didWork;
		}

		if (getWorkDone() < getWorkMax()) {
			return incrementWorkDone();
		}

		setWorkDone(0);

		for (int row = 6; row-- > 0;) {
			ItemStack current = _inventory[getProcessSlot(row)];
			ItemStack next = _inventory[getProcessSlot(row + 1)];
			if (next != null && current != null) {
				continue;
				// no exiting early, we know there's a potion that can be moved/brewed
			}

			ItemStack ingredient = _inventory[getTemplateSlot(row)];

			if (current != null && current.getItem() instanceof ItemPotion) {
				if (ingredient == null) {
					_inventory[getProcessSlot(row + 1)] = current;
					_inventory[getProcessSlot(row)] = null;
					continue;
				}
				for (int i = 0; i < 3; i++) {
					int slot = getResourceSlot(row, i);
					if (ingredient.stackSize <= 0 && !UtilInventory.stacksEqual(_inventory[slot], ingredient)) {
						continue;
					}

					int existingPotion = current.getItemDamage();
					int newPotion = this.getPotionResult(existingPotion, ingredient);
					@SuppressWarnings("unchecked")
                    List<PotionEffect> existingEffects = Items.potionitem.getEffects(existingPotion);
					@SuppressWarnings("unchecked")
                    List<PotionEffect> newEffects = Items.potionitem.getEffects(newPotion);

					if ((existingPotion <= 0 || existingEffects != newEffects) &&
							(existingEffects == null || !existingEffects.equals(newEffects) && newEffects != null)) {
						if (existingPotion != newPotion) {
							current.setItemDamage(newPotion);
						}
					} else if (!ItemPotion.isSplash(existingPotion) && ItemPotion.isSplash(newPotion)) {
						current.setItemDamage(newPotion);
					}

					_inventory[getProcessSlot(row + 1)] = current;
					_inventory[getProcessSlot(row)] = null;

					if (existingPotion == newPotion)
						break;

					if (ingredient.stackSize > 0) {
						--ingredient.stackSize;
						break;
					}
					--_inventory[slot].stackSize;
					++ingredient.stackSize;
					if (ingredient.getItem().hasContainerItem(_inventory[slot])) {
						ItemStack r = ingredient.getItem().getContainerItem(_inventory[slot]);
						if (r != null && r.isItemStackDamageable() && r.getItemDamage() > r.getMaxDamage())
							r = null;
						_inventory[slot] = r;
					}
					if (_inventory[slot] != null && _inventory[slot].stackSize <= 0)
						_inventory[slot] = null;
					break;
				}
			}
		}
		return true;
	}

	private boolean canBrew(int row) {

		if (_inventory[getTemplateSlot(row)] == null) {
			return false;
		}

		boolean hasIngredients = _inventory[getTemplateSlot(row)].stackSize > 0;
		if (!hasIngredients) for (int i = 0; i < 3; i++) {
			if (UtilInventory.stacksEqual(_inventory[getTemplateSlot(row)], _inventory[getResourceSlot(row, i)])) {
				hasIngredients = true;
				break;
			}
		}
		if (!hasIngredients) {
			return false;
		}

		ItemStack ingredient = _inventory[getTemplateSlot(row)];

		if (!ingredient.getItem().isPotionIngredient(ingredient)) {
			return false;
		}

		if (_inventory[getProcessSlot(row)] != null &&
				_inventory[getProcessSlot(row)].getItem() instanceof ItemPotion) {
			int existingPotion = _inventory[getProcessSlot(row)].getItemDamage();
			int newPotion = this.getPotionResult(existingPotion, ingredient);

			if (!ItemPotion.isSplash(existingPotion) && ItemPotion.isSplash(newPotion)) {
				return true;
			}

			@SuppressWarnings("unchecked")
			List<PotionEffect> existingEffects = Items.potionitem.getEffects(existingPotion);
			@SuppressWarnings("unchecked")
			List<PotionEffect> newEffects = Items.potionitem.getEffects(newPotion);

			if ((existingPotion <= 0 || existingEffects != newEffects) &&
					(existingEffects == null || !existingEffects.equals(newEffects) && newEffects != null)) {
				return true; //existingPotion != newPotion;
				// push potions without effects that have been previously brewed on through
			}
		}

		return false;
	}

	private int getPotionResult(int existingPotion, ItemStack ingredient) {

		if (ingredient == null || !ingredient.getItem().isPotionIngredient(ingredient)) {
			return existingPotion;
		}
		return PotionHelper.applyIngredient(existingPotion, ingredient.getItem().getPotionEffect(ingredient));
	}

	@Override
	public int getWorkMax() {

		return 160;
	}

	@Override
	public int getIdleTicksMax() {

		return 10;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {

		int row = slot / 5;
		int column = slot % 5;

		if (itemstack == null) return false;
		if (slot == 31) return itemstack.getItem().equals(Items.glass_bottle);
		if (row == 6) return false;
		if (column == 1) return false;
		if (column == 0) return _inventory[getTemplateSlot(row)] != null &&
				itemstack.getItem() instanceof ItemPotion &&
				(row == 0 || _inventory[getTemplateSlot(row - 1)] == null);
		return ingredientsEqual(_inventory[getTemplateSlot(row)], itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {

		int row = slot / 5;
		int column = slot % 5;

		if (row == 6) return slot != 31;
		if (column == 1) return false;
		if (column == 0) return _inventory[getTemplateSlot(row)] == null;
		return !ingredientsEqual(_inventory[getTemplateSlot(row)], itemstack);
	}

	private boolean ingredientsEqual(ItemStack template, ItemStack ingredient) {

		if (template == null | ingredient == null || !template.getItem().isPotionIngredient(template)) {
			return false;
		}
		return template.getItem().getPotionEffect(template).equals(ingredient.getItem().getPotionEffect(ingredient));
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {

		if (itemstack != null && !shouldDropSlotWhenBroken(slot))
			itemstack.stackSize = 0;
		super.setInventorySlotContents(slot, itemstack);
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		_inventoryDirty = true;
	}

	@Override
	public boolean shouldDropSlotWhenBroken(int slot) {

		return slot % 5 != 1 || slot == 31;
	}

	@Override
	public boolean allowBucketFill(ItemStack stack) {

		return !stack.getItem().equals(Items.potionitem);
	}

	@Override
	public boolean allowBucketDrain(ItemStack stack) {

		return !stack.getItem().equals(Items.glass_bottle);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		return fill(resource, doFill);
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
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return false;
	}

}
