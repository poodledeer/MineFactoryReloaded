package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;

public class FertilizableCropPlant extends FertilizableStandard
{
	protected final int targetMeta;
	
	public FertilizableCropPlant(Block block, FertilizerType type, int targetMeta)
	{
		super(block, type);
		this.targetMeta = targetMeta;
	}
	
	public FertilizableCropPlant(IGrowable block, FertilizerType type, int targetMeta)
	{
		super(block, type);
		this.targetMeta = targetMeta;
	}
	
	public FertilizableCropPlant(IGrowable block, int targetMeta)
	{
		this(block, FertilizerType.GrowPlant, targetMeta);
	}

	@Override
	protected boolean canFertilize(int metadata)
	{
		return metadata < targetMeta;
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		((IGrowable)block).func_149853_b(world, rand, x, y, z);
		return world.getBlockMetadata(x, y, z) != meta;
	}
}
