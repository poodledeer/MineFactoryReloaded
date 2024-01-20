package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;

public class FertilizableStandard extends FertilizableBase
{
	public FertilizableStandard(Block block, FertilizerType type)
	{
		super(block, type);
	}

	public FertilizableStandard(IGrowable block, FertilizerType type)
	{
		super((Block)block, type);
	}
	
	public FertilizableStandard(IGrowable block)
	{
		this(block, FertilizerType.GrowPlant);
	}
	
	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return fertilizerType == validFertilizer;
	}
	
	@Override
	protected boolean canFertilize(int metadata)
	{
		return true;
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		Block block = world.getBlock(x, y, z);
		((IGrowable)block).func_149853_b(world, rand, x, y, z);
		return world.getBlock(x, y, z) != block;
	}
}
