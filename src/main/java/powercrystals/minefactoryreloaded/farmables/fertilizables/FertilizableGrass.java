package powercrystals.minefactoryreloaded.farmables.fertilizables;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;

public class FertilizableGrass implements IFactoryFertilizable
{
	protected Block grass;
	public FertilizableGrass() { this(Blocks.grass); }
	public FertilizableGrass(Block grass)
	{
		this.grass = grass;
	}
	
	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return (fertilizerType == FertilizerType.GrowPlant ||
				fertilizerType == FertilizerType.Grass) &&
				world.getBlock(x, y + 1, z).isAir(world, x, y + 1, z);
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		((IGrowable)world.getBlock(x, y, z)).func_149853_b(world, rand, x, y, z);
		return !world.getBlock(x, y + 1, z).isAir(world, x, y + 1, z);
	}
	
	@Override
	public Block getPlant()
	{
		return grass;
	}
}
