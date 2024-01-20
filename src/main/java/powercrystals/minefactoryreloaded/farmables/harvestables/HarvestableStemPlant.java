package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.block.Block;

public class HarvestableStemPlant extends HarvestableGourd
{
	protected Block _fruit;

	public HarvestableStemPlant(Block block, Block fruit)
	{
		super(block, powercrystals.minefactoryreloaded.api.HarvestType.Gourd);
		_fruit = fruit;
	}

	@Override
	public boolean canBeHarvested(net.minecraft.world.World world, java.util.Map<String, Boolean> s, int x, int y, int z)
	{
		return world.getBlock(x, y, z).equals(_fruit);
	}
}
