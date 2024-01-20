package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;

public class FertilizableCocoa extends FertilizableCropPlant
{
	public FertilizableCocoa(Block block)
	{
		super(block, FertilizerType.GrowPlant, 8);
	}
	
	public FertilizableCocoa(Block block, FertilizerType type)
	{
		super(block, type, 8);
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		world.setBlockMetadataWithNotify(x, y, z, 8 | BlockDirectional.getDirection(world.getBlockMetadata(x, y, z)), 2);
		return true;
	}
}
