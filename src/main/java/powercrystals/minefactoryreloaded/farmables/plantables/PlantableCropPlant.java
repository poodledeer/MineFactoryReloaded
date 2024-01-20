package powercrystals.minefactoryreloaded.farmables.plantables;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PlantableCropPlant extends PlantableStandard
{
	public PlantableCropPlant(Item seed, Block plant)
	{
		super(seed, plant);
	}
	
	public PlantableCropPlant(Item seed, Block plant, int meta)
	{
		super(seed, plant, meta);
	}
	
	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		if(!world.isAirBlock(x, y, z))
			return false;
		
		Block ground = world.getBlock(x, y - 1, z);
		return ground.equals(Blocks.farmland) ||
				ground.equals(Blocks.grass) ||
				ground.equals(Blocks.dirt) ||
				super.canBePlantedHere(world, x, y, z, stack);
	}
	
	@Override
	public void prePlant(World world, int x, int y, int z, ItemStack stack)
	{
		Block ground = world.getBlock(x, y - 1, z);
		if (ground.equals(Blocks.grass) || ground.equals(Blocks.dirt))
		{
			world.setBlock(x, y - 1, z, Blocks.farmland);
		}
	}
}
