package powercrystals.minefactoryreloaded.modhelpers.forestry;

import forestry.api.genetics.IFruitBearer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.ReplacementBlock;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStandard;
import powercrystals.minefactoryreloaded.modhelpers.EmptyReplacement;

public class ForestryPod extends HarvestableStandard implements IFactoryFruit, IFactoryFertilizable
{
	private Item grafter;
	private ReplacementBlock repl;

	public ForestryPod(Block block, Item tool)
	{
		super(block, HarvestType.TreeFruit);
		repl = EmptyReplacement.INSTANCE;
		grafter = tool;
	}

	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> settings, int x, int y, int z)
	{
		if (settings.get("isHarvestingTree") == Boolean.TRUE)
			return true;

		return canBePicked(world, x, y, z);
	}

	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		if (fertilizerType != FertilizerType.GrowPlant)
			return false;

		return !canBePicked(world, x, y, z);
	}

	@Override
	public boolean canBePicked(World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IFruitBearer)
		{
			IFruitBearer fruit = (IFruitBearer)te;
			return fruit.getRipeness() >= 0.99f;
		}
		return false;
	}

	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IFruitBearer)
		{
			IFruitBearer fruit = (IFruitBearer)te;
			fruit.addRipeness(1f);
			return true;
		}
		return false;
	}

	@Override
	public ReplacementBlock getReplacementBlock(World world, int x, int y, int z)
	{
		return repl;
	}

	@Override
	public void prePick(World world, int x, int y, int z)
	{
	}

	@Override // HARVESTER
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> settings, int x, int y, int z)
	{
		return getDrops(world, rand, x, y, z);
	}

	@Override // FRUIT PICKER
	public List<ItemStack> getDrops(World world, Random rand, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IFruitBearer)
		{
			List<ItemStack> prod = new ArrayList<ItemStack>();
			prod.addAll(((IFruitBearer)te).pickFruit(new ItemStack(grafter)));
			return prod;
		}
		return null;
	}

	@Override
	public void postPick(World world, int x, int y, int z)
	{
	}
}
