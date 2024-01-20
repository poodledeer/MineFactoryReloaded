package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;

public class FertilizableIGrowable implements IFactoryFertilizable
{
	protected final Block block;
	protected final IGrowable fertilizable;
	protected final FertilizerType validFertilizer;
	
	public FertilizableIGrowable(Block block, FertilizerType type)
	{
		this.block = block;
		fertilizable = (IGrowable)block;
		validFertilizer = type;
	}
	
	public FertilizableIGrowable(Block block)
	{
		this(block, FertilizerType.GrowPlant);
	}
	
	@Override
	public Block getPlant()
	{
		return block;
	}
	
	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return fertilizerType == validFertilizer && fertilizable.func_149851_a(world, x, y, z, world.isRemote);
	}

	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		fertilizable.func_149853_b(world, rand, x, y, z);
		return block != world.getBlock(x, y, z) || meta != world.getBlockMetadata(x, y, z);
	}
}
