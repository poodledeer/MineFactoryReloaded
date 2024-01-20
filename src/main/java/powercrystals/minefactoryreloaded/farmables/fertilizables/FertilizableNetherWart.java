package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;

public class FertilizableNetherWart extends FertilizableBase
{
	public FertilizableNetherWart()
	{
		super(Blocks.nether_wart);
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		return true;
	}

	@Override
	protected boolean canFertilize(int metadata)
	{
		return metadata < 3;
	}
}
