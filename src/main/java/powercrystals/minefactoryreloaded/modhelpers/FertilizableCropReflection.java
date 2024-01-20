package powercrystals.minefactoryreloaded.modhelpers;

import java.lang.reflect.Method;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;

public class FertilizableCropReflection implements IFactoryFertilizable
{
	private Method _fertilize;
	private Block _block;
	protected int _targetMeta;
	
	public FertilizableCropReflection(Block block, Method fertilize, int targetMeta)
	{
		_block = block;
		_fertilize = fertilize;
		_targetMeta = targetMeta;
	}
	
	@Override
	public Block getPlant()
	{
		return _block;
	}
	
	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return world.getBlockMetadata(x, y, z) < _targetMeta && fertilizerType == FertilizerType.GrowPlant;
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		try
		{
			_fertilize.invoke(_block, world, x, y, z);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return world.getBlockMetadata(x, y, z) >= _targetMeta;
	}
}
