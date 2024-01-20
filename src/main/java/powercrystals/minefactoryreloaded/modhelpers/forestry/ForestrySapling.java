package powercrystals.minefactoryreloaded.modhelpers.forestry;

import forestry.api.arboriculture.ITreeRoot;
import forestry.api.genetics.AlleleManager;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.ReplacementBlock;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

public class ForestrySapling extends PlantableStandard implements IFactoryFertilizable
{
	private ITreeRoot root;

	public ForestrySapling(Item item, Block block)
	{
		super(item, block, WILDCARD, null);
		root = (ITreeRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
		_plantedBlock = new ReplacementBlock((Block)null) {
			@Override
			public boolean replaceBlock(World world, int x, int y, int z, ItemStack stack) {
				return root.plantSapling(world, root.getMember(stack), null, x, y, z);
			}
		};
	}

	@Override
	public Block getPlant()
	{
		return _block;
	}

	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		if (!world.isAirBlock(x, y, z))
			return false;

		return root.getMember(stack).canStay(world, x, y, z);
	}

	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return true;
	}

	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		Block block = world.getBlock(x, y, z);
		root.getTree(world, x, y, z).getTreeGenerator(world, x, y, z, true).generate(world, rand, x, y, z);
		return world.getBlock(x, y, z) != block;
	}
}
