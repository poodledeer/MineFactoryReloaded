package powercrystals.minefactoryreloaded.modhelpers;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.ReplacementBlock;

public class EmptyReplacement extends ReplacementBlock
{
	public static final EmptyReplacement INSTANCE = new EmptyReplacement();

	public EmptyReplacement()
	{
		super((Block)null);
	}

	@Override
	public boolean replaceBlock(World world, int x, int y, int z, ItemStack stack) {
		return true;
	}
}
