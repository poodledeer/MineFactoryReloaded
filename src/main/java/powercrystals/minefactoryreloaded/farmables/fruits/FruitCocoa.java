package powercrystals.minefactoryreloaded.farmables.fruits;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class FruitCocoa extends FactoryFruitStandard
{
	public FruitCocoa(Block block) {
		super(block);
	}

	@Override
	public boolean canBePicked(World world, int x, int y, int z)
	{
		int blockMetadata = world.getBlockMetadata(x, y, z);
		return ((blockMetadata & 12) >> 2) >= 2;
	}
}
