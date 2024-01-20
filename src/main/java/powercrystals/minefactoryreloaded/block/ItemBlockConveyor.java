package powercrystals.minefactoryreloaded.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemBlockConveyor extends ItemBlockFactory
{
	public ItemBlockConveyor(Block p_i45328_1_, String[] names)
	{
		super(p_i45328_1_, names);
	}

	@Override
	public void getSubItems(Item itemId, List<ItemStack> subTypes)
	{
		subTypes.add(new ItemStack(itemId, 1, 16));
		for(int i = 0, e = 16; i < e; i++)
			subTypes.add(new ItemStack(itemId, 1, i));
	}
}
