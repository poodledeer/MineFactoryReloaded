package powercrystals.minefactoryreloaded.modhelpers.chococraft;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;

public class FertilizableChococraft implements IFactoryFertilizable
{
	private final Block _blockId;
	
	public FertilizableChococraft(Block blockId)
	{
		this._blockId = blockId;
	}
	
	@Override
	public Block getPlant()
	{
		return this._blockId;
	}
	
	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return fertilizerType == FertilizerType.GrowPlant && world.getBlockMetadata(x, y, z) <= 4;
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		return true;
	}
}
