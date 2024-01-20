package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;

public abstract class FertilizableBase implements IFactoryFertilizable
{
	protected final Block fertilizable;
	protected final FertilizerType validFertilizer;
	
	public FertilizableBase(Block block, FertilizerType type)
	{
		fertilizable = block;
		validFertilizer = type;
	}
	
	public FertilizableBase(Block block)
	{
		this(block, FertilizerType.GrowPlant);
	}
	
	@Override
	public Block getPlant()
	{
		return fertilizable;
	}
	
	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return fertilizerType == validFertilizer && canFertilize(world.getBlockMetadata(x, y, z));
	}
	
	protected abstract boolean canFertilize(int metadata);

	@Override
	public abstract boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType);
}
