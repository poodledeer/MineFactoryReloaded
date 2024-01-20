package powercrystals.minefactoryreloaded.modhelpers.ic2;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableWood;

public class HarvestableIC2RubberWood extends HarvestableWood
{
	private Item _resin;
	
	public HarvestableIC2RubberWood(Block sourceId, Item resin)
	{
		super(sourceId);
		_resin = resin;
	}
	
	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		List<ItemStack> drops = super.getDrops(world, rand, harvesterSettings, x, y, z);
		int md = world.getBlockMetadata(x, y, z);
		if(md >= 2 && md <= 5)
		{
			drops.add(new ItemStack(_resin, 1, 0));
		}
		
		return drops;
	}
}