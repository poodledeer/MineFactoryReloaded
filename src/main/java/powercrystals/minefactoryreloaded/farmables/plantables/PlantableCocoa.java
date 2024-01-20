package powercrystals.minefactoryreloaded.farmables.plantables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.ReplacementBlock;

public class PlantableCocoa extends PlantableStandard
{

	public PlantableCocoa(Item source, Block plantedBlock)
	{
		this(source, plantedBlock, WILDCARD);
	}

	public PlantableCocoa(Item source, Block plantedBlock, int validMeta)
	{
		super(source, plantedBlock, validMeta);
		_plantedBlock = new ReplacementBlock(_block) {
			@Override
			public int getMeta(World world, int x, int y, int z, ItemStack stack)
			{
				int meta = 3; // NORTH
				if (isGoodLog(world, x-1, y, z))
					meta = 1; // SOUTH
				else if (isGoodLog(world, x, y, z+1))
					meta = 0; // EAST
				else if (isGoodLog(world, x, y, z-1))
					meta = 2; // WEST

				return meta;
			}
		};
	}

	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		if (!world.isAirBlock(x, y, z))
			return false;

		return isNextToAcceptableLog(world, x, y, z);
	}

	protected boolean isNextToAcceptableLog(World world, int x, int y, int z)
	{
		return isGoodLog(world, x+1, y, z) ||
				isGoodLog(world, x-1, y, z) ||
				isGoodLog(world, x, y, z+1) ||
				isGoodLog(world, x, y, z-1);
	}

	protected boolean isGoodLog(World world, int x, int y, int z)
	{
		return world.getBlock(x, y, z).equals(Blocks.log) &&
				BlockLog.func_150165_c(world.getBlockMetadata(x, y, z)) == 3;
	}
}
