package powercrystals.minefactoryreloaded.block;

import net.minecraft.item.ItemStack;

public class ItemBlockFactoryTree extends ItemBlockFactory
{
	public ItemBlockFactoryTree(net.minecraft.block.Block id)
	{
		super(id);
		setNames(new String[] {null, "sacred", "mega", "massive"});
	}
	
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return stack.getItemDamage() == 3;
	}
}
