package powercrystals.minefactoryreloaded.farmables.harvestables;

public class HarvestableMushroom extends HarvestableStandard
{
	public HarvestableMushroom(net.minecraft.block.Block block)
	{
		super(block, powercrystals.minefactoryreloaded.api.HarvestType.Normal);
	}

	@Override
	public boolean canBeHarvested(net.minecraft.world.World world, java.util.Map<String, Boolean> settings, int x, int y, int z)
	{
		return settings.get("harvestSmallMushrooms") == Boolean.TRUE;
	}
}
