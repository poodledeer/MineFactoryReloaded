package powercrystals.minefactoryreloaded.modhelpers.forestry;

import cofh.asm.relauncher.Strippable;
import cofh.asm.relauncher.Substitutable;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.ReplacementBlock;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.modhelpers.EmptyReplacement;

public class ForestryLeaf extends HarvestableTreeLeaves implements IFactoryFruit, IFactoryFertilizable
{
	private ITreeRoot root;
	private ReplacementBlock repl;
	protected Item _item;

	public ForestryLeaf(Block block)
	{
		super(block);
		root = (ITreeRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
		repl = EmptyReplacement.INSTANCE;
		_item = Item.getItemFromBlock(block);
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
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return !canBePicked(world, x, y, z);
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
		ITree tree = getTree(world, x, y, z);
		if (tree == null)
			return null;

		ArrayList<ItemStack> prod = new ArrayList<ItemStack>();

		float modifier = 1f;
		if (settings.get("silkTouch") == Boolean.TRUE)
		{
			ItemStack item = new ItemStack(_item);
			NBTTagCompound tag = new NBTTagCompound();
			tree.writeToNBT(tag);
			item.setTagCompound(tag);
			prod.add(item);
		}
		else
		{
			boolean hasMate = tree.getMate() != null;
			for (ITree s : getSaplings(tree, world, x, y, z, modifier))
				if (s != null) {
					if ((hasMate && !s.isGeneticEqual(tree)) || rand.nextInt(32) == 0)
						if (rand.nextBoolean())
							prod.add(root.getMemberStack(s, EnumGermlingType.POLLEN.ordinal()));

					prod.add(root.getMemberStack(s, EnumGermlingType.SAPLING.ordinal()));
				}

			getFruits(world, x, y, z, tree, prod);
		}

		return prod;
	}

	@Substitutable(value="mod:Forestry@[4.0,)", method="getSaplingsOld")
	private static ITree[] getSaplings(ITree tree, World world, int x, int y, int z, float modifier) {
		return tree.getSaplings(world, null, x, y, z, modifier);
	}

	@Strippable("")
	@SuppressWarnings("deprecation")
	private static ITree[] getSaplingsOld(ITree tree, World world, int x, int y, int z, float modifier) {
		return tree.getSaplings(world, null, x, y, z, modifier);
	}

	@Override // FRUIT PICKER
	public List<ItemStack> getDrops(World world, Random rand, int x, int y, int z)
	{
		ITree tree = getTree(world, x, y, z);
		if (tree == null)
			return null;

		ArrayList<ItemStack> prod = new ArrayList<ItemStack>();
		getFruits(world, x, y, z, tree, prod);
		return prod;
	}

	private ITree getTree(World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IPollinatable) {
			IIndividual t = ((IPollinatable)te).getPollen();
			if (t instanceof ITree)
				return (ITree)t;
		}
		return null;
	}

	private void getFruits(World world, int x, int y, int z, ITree tree, ArrayList<ItemStack> prod)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IFruitBearer)
		{
			IFruitBearer fruit = (IFruitBearer)te;
			if (fruit.hasFruit())
			{
				//int period = tree.getGenome().getFruitProvider().getRipeningPeriod();
				//ItemStack[] o = tree.produceStacks(world, x, y, z, (int)(fruit.getRipeness() * period + 0.1f));
				prod.addAll(fruit.pickFruit(null));
			}
		}
	}

	@Override
	public void postPick(World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IFruitBearer)
		{
			IFruitBearer fruit = (IFruitBearer)te;
			fruit.addRipeness(-fruit.getRipeness());
		}
	}
}
