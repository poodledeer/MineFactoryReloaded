package powercrystals.minefactoryreloaded.tile.machine;

import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IFactoryPlantable;
import powercrystals.minefactoryreloaded.api.ReplacementBlock;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiPlanter;
import powercrystals.minefactoryreloaded.gui.container.ContainerPlanter;
import powercrystals.minefactoryreloaded.gui.container.ContainerUpgradeable;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityPlanter extends TileEntityFactoryPowered {

	protected boolean keepLastItem = false;

	public TileEntityPlanter() {

		super(Machine.Planter);
		createHAM(this, 1);
		_areaManager.setOverrideDirection(ForgeDirection.UP);
		_areaManager.setOriginOffset(0, 1, 0);
		setManageSolids(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiPlanter(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerUpgradeable getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerPlanter(this, inventoryPlayer);
	}

	@Override
	public boolean activateMachine() {

		BlockPosition bp = _areaManager.getNextBlock();
		if (!worldObj.blockExists(bp.x, bp.y, bp.z)) {
			setIdleTicks(getIdleTicksMax());
			return false;
		}

		ItemStack match = _inventory[getPlanterSlotIdFromBp(bp)];

		for (int stackIndex = 10; stackIndex <= 25; stackIndex++) {
			ItemStack availableStack = getStackInSlot(stackIndex);

			// skip planting attempt if there's no stack in that slot,
			// or if there's a template item that's not matched
			if (availableStack == null ||
					(match != null &&
					!stacksEqual(match, availableStack)) ||
					!MFRRegistry.getPlantables().containsKey(availableStack.getItem())) {
				continue;
			}

			if (keepLastItem && availableStack.stackSize < 2) {
				continue;
			}
			IFactoryPlantable plantable = MFRRegistry.getPlantables().get(availableStack.getItem());

			if (!plantable.canBePlanted(availableStack, false) ||
					!plantable.canBePlantedHere(worldObj, bp.x, bp.y, bp.z, availableStack))
				continue;

			plantable.prePlant(worldObj, bp.x, bp.y, bp.z, availableStack);
			ReplacementBlock block = plantable.getPlantedBlock(worldObj, bp.x, bp.y, bp.z, availableStack);
			if (block == null || !block.replaceBlock(worldObj, bp.x, bp.y, bp.z, availableStack))
				continue;
			plantable.postPlant(worldObj, bp.x, bp.y, bp.z, availableStack);
			decrStackSize(stackIndex, 1);
			return true;
		}

		setIdleTicks(getIdleTicksMax());
		return false;
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setBoolean("keepLastItem", keepLastItem);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		keepLastItem = tag.getBoolean("keepLastItem");
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (keepLastItem)
			tag.setBoolean("keepLastItem", keepLastItem);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		keepLastItem = tag.getBoolean("keepLastItem");
	}

	protected boolean stacksEqual(ItemStack a, ItemStack b) {

		if (a == null | b == null ||
				(!a.getItem().equals(b.getItem())) ||
				(a.getItemDamage() != b.getItemDamage()) ||
				a.hasTagCompound() != b.hasTagCompound()) {
			return false;
		}
		if (!a.hasTagCompound()) {
			return true;
		}
		NBTTagCompound tagA = (NBTTagCompound) a.getTagCompound().copy(), tagB = (NBTTagCompound) b.getTagCompound().copy();
		tagA.removeTag("display");
		tagB.removeTag("display");
		tagA.removeTag("ench");
		tagB.removeTag("ench");
		tagA.removeTag("RepairCost");
		tagB.removeTag("RepairCost");
		return tagA.equals(tagB);
	}

	//assumes a 3x3 grid in inventory slots 0-8
	//slot 0 is northwest, slot 2 is northeast, etc
	protected int getPlanterSlotIdFromBp(BlockPosition bp) {

		int radius = _areaManager.getRadius();
		int xAdjusted = Math.round(1.49F * (bp.x - this.xCoord) / radius);
		int zAdjusted = Math.round(1.49F * (bp.z - this.zCoord) / radius);
		return 4 + xAdjusted + 3 * zAdjusted;
	}

	public boolean getConsumeAll() {

		return keepLastItem;
	}

	public void setConsumeAll(boolean b) {

		keepLastItem = b;
	}

	@Override
	public int getSizeInventory() {

		return 26;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 5;
	}

	@Override
	public int getStartInventorySide(ForgeDirection side) {

		return 9;
	}

	@Override
	public boolean shouldDropSlotWhenBroken(int slot) {

		return slot > 8;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {

		return 17;
	}

	@Override
	public int getUpgradeSlot() {

		return 9;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal) {

		if (stack != null) {
			if (slot > 9) {
				IFactoryPlantable p = MFRRegistry.getPlantables().get(stack.getItem());
				return p != null && p.canBePlanted(stack, false);
			} else if (slot == 9) {
				return isUsableAugment(stack);
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal) {

		if (slot >= 10) return true;
		return false;
	}
}
