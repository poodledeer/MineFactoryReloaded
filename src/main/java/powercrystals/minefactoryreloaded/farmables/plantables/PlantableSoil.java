package powercrystals.minefactoryreloaded.farmables.plantables;

import cofh.lib.util.helpers.FluidHelper;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.ReplacementBlock;

public class PlantableSoil extends PlantableStandard
{
	public PlantableSoil(Block block)
	{
		super(Item.getItemFromBlock(block), block);
	}

	public PlantableSoil(Item block, Block plantedBlock)
	{
		super(block, plantedBlock);
	}

	public PlantableSoil(Item block, Block plantedBlock, int validMeta)
	{
		super(block, plantedBlock, validMeta);
	}

	public PlantableSoil(Block block, int plantedMeta) {
		this(Item.getItemFromBlock(block), plantedMeta, block);
	}

	public PlantableSoil(Item block, int plantedMeta, Block plantedBlock)
	{
		super(block, plantedBlock, WILDCARD, new ReplacementBlock(plantedBlock).setMeta(plantedMeta));
	}

	@Override
	public boolean canBePlanted(ItemStack stack, boolean forFermenting)
	{
		return !forFermenting && super.canBePlanted(stack, forFermenting);
	}

	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		if (!world.isAirBlock(x, y, z))
			if (FluidHelper.lookupFluidForBlock(world.getBlock(x, y, z)) == FluidHelper.WATER_FLUID) {
				if (FluidHelper.lookupFluidForBlock(world.getBlock(x, y + 1, z)) == FluidHelper.WATER_FLUID)
					return false;
				else
					return world.getBlockMetadata(x, y, z) != 0;
			} else {
				Block block = world.getBlock(x, y, z);
				return !block.getMaterial().isLiquid() && block.isReplaceable(world, x, y, z);
			}

		return true;
	}
}
