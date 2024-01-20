package powercrystals.minefactoryreloaded.farmables.harvestables;

public class HarvestableCropPlant extends HarvestableStandard
{
	private int _targetMeta;

	public HarvestableCropPlant(net.minecraft.block.Block block, int targetMeta)
	{
		super(block, powercrystals.minefactoryreloaded.api.HarvestType.Normal);
		_targetMeta = targetMeta;
	}

	@Override
	public boolean canBeHarvested(net.minecraft.world.World world, java.util.Map<String, Boolean> settings, int x, int y, int z)
	{
		return world.getBlockMetadata(x, y, z) >= _targetMeta;
	}
}
