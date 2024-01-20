package powercrystals.minefactoryreloaded.modhelpers.vanilla;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;

public class HarvestableVanillaLeaves extends HarvestableTreeLeaves {

	public HarvestableVanillaLeaves(Block block) {
		super(block);
	}
	
	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> settings, int x, int y, int z)
	{
		if (settings.get("silkTouch") == Boolean.TRUE)
			return super.getDrops(world, rand, settings, x, y, z);
		
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		int meta = world.getBlockMetadata(x, y, z);
		int d = getPlant().damageDropped(meta);
		if(rand.nextInt(d == 3 ? 40 : 20) == 0)
			drops.add(new ItemStack(getPlant().getItemDropped(meta, rand, 0), 1, d));
		
		if(getPlant() == Blocks.leaves && (world.getBlockMetadata(x, y, z) & 3) == 0)
			if(rand.nextInt(200) == 0)
				drops.add(new ItemStack(Items.apple));
		
		return drops;
	}

}
