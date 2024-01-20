package powercrystals.minefactoryreloaded.modhelpers.forestry;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.api.ReplacementBlock;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSoil;

public class ForestryBogEarth extends PlantableSoil implements IFactoryFertilizable, IFactoryHarvestable, IFactoryFruit
{
	private ReplacementBlock repl;
	private Item dirt;

	public ForestryBogEarth(Block block)
	{
		super(block);
		_plantedBlock.setMeta(true);
		repl = new ReplacementBlock(Blocks.dirt);
		dirt = Item.getItemFromBlock(Blocks.dirt);
	}

	@Override
	public boolean breakBlock()
	{
		return true;
	}

	@Override
	public Block getPlant()
	{
		return _block;
	}

	@Override
	public HarvestType getHarvestType()
	{
		return HarvestType.Normal;
	}

	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return fertilizerType == FertilizerType.GrowPlant && (world.getBlockMetadata(x, y, z) & 3) == 1;
	}

	@Override
	public boolean canBePicked(World world, int x, int y, int z)
	{
		return world.getBlockMetadata(x, y, z) == 13;
	}

	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> settings, int x, int y, int z)
	{
		return world.getBlockMetadata(x, y, z) == 13;
	}

	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		return world.setBlockMetadataWithNotify(x, y, z, 13, 3);
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> settings, int x, int y, int z)
	{
		return world.getBlock(x, y, z).getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
	}

	@Override
	public ReplacementBlock getReplacementBlock(World world, int x, int y, int z)
	{
		return repl;
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, int x, int y, int z)
	{
		List<ItemStack> list = world.getBlock(x, y, z).getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		for (ItemStack a : list)
			if (a.getItem() == dirt) {
				list.remove(a);
				break;
			}
		return list;
	}

	@Override
	public void preHarvest(World world, int x, int y, int z)
	{
	}

	@Override
	public void postHarvest(World world, int x, int y, int z)
	{
	}

	@Override
	public void prePick(World world, int x, int y, int z)
	{
	}

	@Override
	public void postPick(World world, int x, int y, int z)
	{
	}
}
