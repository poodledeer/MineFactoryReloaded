package powercrystals.minefactoryreloaded.farmables.harvestables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.HarvestType;

public class HarvestableShrub extends HarvestableStandard {

	public HarvestableShrub(Block block) {

		super(block, HarvestType.Normal);
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z) {

		List<ItemStack> drops = new ArrayList<ItemStack>();

		boolean doublePlant = getPlant() == Blocks.double_plant, top = false;

		int meta = world.getBlockMetadata(x, y, z);
		if (doublePlant && (meta & 8) == 8) {
			top = true;
			meta = world.getBlockMetadata(x, y - 1, z);
		}

		if (harvesterSettings.get("silkTouch") == Boolean.TRUE &&
				((getPlant() == Blocks.tallgrass && (meta == 1 || meta == 2)) ||
				(doublePlant && (meta == 2 || meta == 3)))) {
			int size = 1, oMeta = 1;
			if (doublePlant) {
				size = 2;
				if (meta == 3) {
					oMeta = 2;
				}
			} else if (meta == 2) {
				oMeta = 2;
			}
			drops.add(new ItemStack(Blocks.tallgrass , size, oMeta));
		} else {
			drops.addAll(getPlant().getDrops(world, x, y, z, meta, 0));
		}

		if (doublePlant && top) {
			world.setBlock(x, y - 1, z, Blocks.air, 0, 2);
		}

		return drops;
	}

	@Override
	public void postHarvest(World world, int x, int y, int z) {

		super.postHarvest(world, x, y, z);
		if (getPlant() == Blocks.double_plant) {
			super.postHarvest(world, x, y - 1, z);
		}
	}

}
