package powercrystals.minefactoryreloaded.block.fluid;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class BlockExplodingFluid extends BlockFactoryFluid
{
	public BlockExplodingFluid(String liquidName)
	{
		super(liquidName);
	}
	
	protected void explode(World world, int x, int y, int z, Random rand, boolean noReplace)
	{
		if (noReplace || world.setBlockToAir(x, y, z))
		{
			if (isSourceBlock(world, x, y, z) && MFRConfig.enableFuelExploding.getBoolean(true))
				world.createExplosion(null, x, y, z, 8, true);
			fizz(world, x, y, z, rand);
			return;
		}
	}
	
	@Override
	protected void checkCanStay(World world, int x, int y, int z, Random rand)
	{
		if (world.provider.isHellWorld)
		{
			explode(world, x, y, z, rand, false);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block This, int meta)
	{
		if (world.getBlock(x, y, z).equals(Blocks.fire))
		{
			explode(world, x, y, z, world.rand, true);
		}
	}
}
