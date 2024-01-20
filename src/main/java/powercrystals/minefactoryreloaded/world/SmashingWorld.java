package powercrystals.minefactoryreloaded.world;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.core.UtilInventory;

import skyboy.core.world.WorldProxy;

// Nigel says: This is a
public class SmashingWorld extends WorldProxy
{
	protected Block block;
	protected int meta;
	protected int x = 0, y = 1, z = 0;

	public SmashingWorld(World world)
	{
		super(world);
	}

	@Override
	public boolean spawnEntityInWorld(Entity par1Entity)
	{
		return true;
	}

	@Override
	public boolean setBlock(int par1, int par2, int par3, Block par4, int par5, int par6)
	{
		return true;
	}

	@Override
	public boolean setBlockMetadataWithNotify(int par1, int par2, int par3, int par4, int par5)
	{
		return true;
	}

	@Override
	public boolean setBlockToAir(int par1, int par2, int par3)
	{
		return true;
	}

	@Override // destroyBlock
	public boolean func_147480_a(int par1, int par2, int par3, boolean par4)
	{
		return true;
	}

	@Override
	public boolean setBlock(int par1, int par2, int par3, Block par4)
	{
		return true;
	}

	@Override
	public Block getBlock(int X, int Y, int Z)
	{
		if (x == X & y == Y & z == Z)
			return block;
		return Blocks.air;
	}

	@Override
	public TileEntity getTileEntity(int X, int Y, int Z)
	{
		return null;
	}

	@Override
	public int getBlockMetadata(int X, int Y, int Z)
	{
		if (x == X & y == Y & z == Z)
			return meta;
		return 0;
	}

	public ArrayList<ItemStack> smashBlock(ItemStack input, Block block, int meta, int fortune)
	{
		ArrayList<ItemStack> drops = null;
		if (block != null)
		{
			this.meta = meta;
			this.block = block;
			drops = block.getDrops(this, x, y, z, meta, fortune);
			if (drops.size() == 1)
				if (UtilInventory.stacksEqual(drops.get(0), input, false))
					return null;
		}
		return drops;
	}
}
