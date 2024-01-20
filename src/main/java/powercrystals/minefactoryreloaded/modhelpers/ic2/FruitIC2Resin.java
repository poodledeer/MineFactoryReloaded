package powercrystals.minefactoryreloaded.modhelpers.ic2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.ReplacementBlock;

public class FruitIC2Resin implements IFactoryFruit, IFactoryFertilizable
{
	private Block _rubberWood;
	private ItemStack _resin;
	private ReplacementBlock _repl;

	public FruitIC2Resin(ItemStack rubberWood, ItemStack resin)
	{
		this._rubberWood = ((ItemBlock)rubberWood.getItem()).field_150939_a;
		this._resin = resin;
		_repl = new ReplacementBlock(_rubberWood) {
			@Override
			protected int getMeta(World world, int x, int y, int z, ItemStack stack)
			{
				return world.getBlockMetadata(x, y, z) + 6;
			}
		};
	}

	@Override
	public Block getPlant()
	{
		return _rubberWood;
	}

	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		if (fertilizerType == FertilizerType.Grass)
			return false;
		Block blockID = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z) - 6;
		return blockID.equals(_rubberWood) & (meta >= 2 & (meta <= 5));
	}

	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		final int meta = world.getBlockMetadata(x, y, z);
		return world.setBlockMetadataWithNotify(x, y, z, meta - 6, 2);
	}

	@Override
	public boolean canBePicked(World world, int x, int y, int z)
	{
		Block blockID = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return blockID.equals(_rubberWood) & (meta >= 2 & (meta <= 5));
	}

	@Override
	public boolean breakBlock()
	{
		return false;
	}

	@Override
	public ReplacementBlock getReplacementBlock(World world, int x, int y, int z)
	{
		return _repl;
	}

	@Override
	public void prePick(World world, int x, int y, int z)
	{

	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, int x, int y, int z)
	{
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		ItemStack a = _resin.copy();
		a.stackSize = 1 + rand.nextInt(3);
		list.add(a);
		return list;
	}

	@Override
	public void postPick(World world, int x, int y, int z)
	{

	}

}
