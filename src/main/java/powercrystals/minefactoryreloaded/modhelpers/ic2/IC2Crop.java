package powercrystals.minefactoryreloaded.modhelpers.ic2;

import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.api.ReplacementBlock;
import powercrystals.minefactoryreloaded.modhelpers.EmptyReplacement;

public class IC2Crop implements IFactoryHarvestable, IFactoryFertilizable, IFactoryFruit
{
	private Block _block;

	public IC2Crop(Block block)
	{
		_block = block;
	}

	@Override
	public Block getPlant()
	{
		return _block;
	}

	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return fertilizerType != FertilizerType.Grass && canFert(world, x, y, z);
	}

	private boolean canFert(World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null || !(te instanceof ICropTile))
			return false;
		ICropTile tec = (ICropTile)te;

		return tec.getNutrientStorage() < 15;
	}

	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		ICropTile tec = (ICropTile)world.getTileEntity(x, y, z);
		tec.setNutrientStorage(100);
		tec.updateState();
		return tec.getNutrientStorage() == 100;
	}

	@Override
	public HarvestType getHarvestType()
	{
		return HarvestType.Normal;
	}

	@Override
	public boolean breakBlock()
	{
		return false;
	}

	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		return canHarvest(world, x, y, z);
	}

	@Override
	public boolean canBePicked(World world, int x, int y, int z)
	{
		return canHarvest(world, x, y, z);
	}

	private boolean canHarvest(World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te == null || !(te instanceof ICropTile))
			return false;

		ICropTile tec = (ICropTile)te;
		CropCard crop;
		try
		{
			int ID = tec.getID();
			if (ID < 0)
				return false;
			crop = Crops.instance.getCropList()[ID];
			if(!crop.canBeHarvested(tec) || crop.canGrow(tec))
			{
				return false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		List<ItemStack> drops = new ArrayList<ItemStack>();
		getDrops(drops, world, rand, x, y, z);
		return drops;
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, int x, int y, int z)
	{
		List<ItemStack> drops = new ArrayList<ItemStack>();
		getDrops(drops, world, rand, x, y, z);
		return drops;
	}

	private void getDrops(List<ItemStack> drops, World world, Random rand, int x, int y, int z)
	{
		ICropTile tec = (ICropTile)world.getTileEntity(x, y, z);
		CropCard crop;
		try
		{
			crop = Crops.instance.getCropList()[tec.getID()];

			float chance = crop.dropGainChance();
			for (int i = 0; i < tec.getGain(); i++)
			{
				chance *= 1.03F;
			}

			chance -= rand.nextFloat();
			int numDrops = 0;
			while (chance > 0.0F)
			{
				numDrops++;
				chance -= rand.nextFloat();
			}
			ItemStack[] cropDrops = new ItemStack[numDrops];
			for (int i = 0; i < numDrops; i++)
			{
				cropDrops[i] = crop.getGain(tec);
				if((cropDrops[i] != null) && (rand.nextInt(100) <= tec.getGain()))
				{
					cropDrops[i].stackSize += 1;
				}
			}

			tec.setSize(crop.getSizeAfterHarvest(tec));
			tec.updateState();

			for(ItemStack s : cropDrops)
			{
				drops.add(s);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public ReplacementBlock getReplacementBlock(World world, int x, int y, int z)
	{
		return EmptyReplacement.INSTANCE;
	}

	@Override
	public void preHarvest(World world, int x, int y, int z)
	{
	}

	@Override
	public void postHarvest(World world, int x, int y, int z)
	{
	}

	@Override
	public void prePick(World world, int x, int y, int z)
	{
	}

	@Override
	public void postPick(World world, int x, int y, int z)
	{
	}
}
