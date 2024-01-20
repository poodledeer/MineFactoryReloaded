package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class HarvestableGourd extends HarvestableStandard
{
	public HarvestableGourd(Block block, powercrystals.minefactoryreloaded.api.HarvestType harvestType)
	{
		super(block, harvestType);
	}

	public HarvestableGourd(Block block)
	{
		super(block);
	}

	@Override
	public void postHarvest(net.minecraft.world.World world, int x, int y, int z)
	{
		Block ground = world.getBlock(x, y - 1, z);
		if (world.isAirBlock(x, y, z) &&
				(ground.equals(Blocks.dirt) || ground.equals(Blocks.grass)))
		{
			world.setBlock(x, y - 1, z, Blocks.farmland);
		}
		super.postHarvest(world, x, y, z);
	}
}
