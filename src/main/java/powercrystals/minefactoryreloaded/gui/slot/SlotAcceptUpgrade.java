package powercrystals.minefactoryreloaded.gui.slot;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class SlotAcceptUpgrade extends Slot
{
	protected TileEntityFactoryInventory _inv;

	public SlotAcceptUpgrade(TileEntityFactoryInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
		_inv = inv;
	}

	public SlotAcceptUpgrade(TileEntityFactoryInventory inv, int index, int x, int y, IIcon icon)
	{
		this(inv, index, x, y);
		setBackgroundIcon(icon);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return _inv.isUsableAugment(stack);
	}
}
