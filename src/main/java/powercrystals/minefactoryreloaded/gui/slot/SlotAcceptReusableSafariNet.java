package powercrystals.minefactoryreloaded.gui.slot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import powercrystals.minefactoryreloaded.item.ItemSafariNet;

public class SlotAcceptReusableSafariNet extends Slot
{
	public static IIcon background;

	public SlotAcceptReusableSafariNet(IInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return !ItemSafariNet.isEmpty(stack) && !ItemSafariNet.isSingleUse(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getBackgroundIconIndex() {
		return background;
	}
}
