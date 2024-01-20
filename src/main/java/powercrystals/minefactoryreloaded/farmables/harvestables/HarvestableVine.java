package powercrystals.minefactoryreloaded.farmables.harvestables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class HarvestableVine extends HarvestableStandard
{
	public HarvestableVine(net.minecraft.block.Block vine)
	{
		super(vine, powercrystals.minefactoryreloaded.api.HarvestType.TreeFruit);
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> settings, int x, int y, int z)
	{
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(getPlant()));
		return drops;
	}
}
