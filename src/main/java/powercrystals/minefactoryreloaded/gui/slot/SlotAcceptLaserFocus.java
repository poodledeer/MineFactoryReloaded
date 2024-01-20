package powercrystals.minefactoryreloaded.gui.slot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import powercrystals.minefactoryreloaded.setup.MFRThings;

public class SlotAcceptLaserFocus extends Slot {

	public static IIcon background;

	public SlotAcceptLaserFocus(IInventory inv, int index, int x, int y) {

		super(inv, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return stack != null && stack.getItem().equals(MFRThings.laserFocusItem);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getBackgroundIconIndex() {

		return background;
	}

}
