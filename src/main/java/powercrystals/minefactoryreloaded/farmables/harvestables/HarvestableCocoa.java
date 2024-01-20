package powercrystals.minefactoryreloaded.farmables.harvestables;

public class HarvestableCocoa extends HarvestableStandard
{
	public HarvestableCocoa(net.minecraft.block.Block blockId)
	{
		super(blockId, powercrystals.minefactoryreloaded.api.HarvestType.TreeFruit);
	}

	@Override
	public boolean canBeHarvested(net.minecraft.world.World world, java.util.Map<String, Boolean> settings, int x, int y, int z)
	{
		if (settings.get("isHarvestingTree") == Boolean.TRUE)
			return true;
		int blockMetadata = world.getBlockMetadata(x, y, z);
		return ((blockMetadata & 12) >> 2) >= 2;
	}
}
