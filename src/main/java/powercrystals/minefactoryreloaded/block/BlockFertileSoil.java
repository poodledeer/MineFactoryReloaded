package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class BlockFertileSoil extends Block implements IGrowable
{
	public BlockFertileSoil()
	{
		super(Material.ground);
		setHardness(0.6F);
		setLightOpacity(255);
		setTickRandomly(false);
		setHarvestLevel("hoe", 0);
		setHarvestLevel("shovel", 0);
		setBlockName("mfr.farmland");
		setStepSound(soundTypeGravel);
		setBlockBounds(0f,0f,0f, 1f,15f/16f,1f);
		setCreativeTab(MFRCreativeTab.tab);
		useNeighborBrightness = true; // THIS IS SET IN THE DUMBEST DAMN WAY ON FARMLAND
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable)
	{
		if (direction != ForgeDirection.UP)
			return false;
		EnumPlantType plantType = plantable.getPlantType(world, x, y + 1, z);

		switch (plantType)
		{
		case Cave:
		case Crop:
		case Beach:
		case Desert:
		case Nether:
		case Plains:
			return true;
		default:
			return false;
		}
	}

	@Override
	public void setBlockBoundsForItemRender()
	{
		setBlockBounds(0f,0f,0f, 1f,15f/16f,1f);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		if (world.getBlock(x, y + 1, z).getMaterial().isSolid())
		{
			setBlockBounds(0f,0f,0f, 1f,1f,1f);
		}
		else
		{
			setBlockBounds(0f,0f,0f, 1f,15f/16f,1f);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return AxisAlignedBB.getBoundingBox(x + 0, y + 0, z + 0, x + 1, y + 1, z + 1);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta,
			float hitX, float hitY, float hitZ)
	{
		if (!func_149851_a(world, x, y, z, world.isRemote))
			return false;

		ItemStack stack = player.getCurrentEquippedItem();
		if (stack != null)
		{
			if (MFRRegistry.getFertilizers().get(stack.getItem()) != null)
			{
				if (func_149852_a(world, world.rand, x, y, z))
				{
					func_149853_b(world, world.rand, x, y, z);
					world.playAuxSFXAtEntity(null, 2005, x, y, z, 0);
				}
				if (!player.capabilities.isCreativeMode)
					player.setCurrentItemOrArmor(0, UtilInventory.consumeItem(stack, player));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isFertile(World world, int x, int y, int z)
	{
		return world.getBlockMetadata(x, y, z) > 0;
	}

	protected void alterMetadata(World world, int x, int y, int z, int d)
	{
		world.setBlockMetadataWithNotify(x, y, z, Math.min(15, Math.max(0, world.getBlockMetadata(x, y, z) + d)), 6);
	}

	@Override
	public void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
	{
		if (!isFertile(world, x, y, z))
			world.setBlock(x, y, z, Blocks.sand);
		else
			alterMetadata(world, x, y, z, -1);
	}

	@SuppressWarnings("unused")
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		super.onNeighborBlockChange(world, x, y, z, block);
		if (true) return; // TODO: if neighborChange becomes sided, remove
		Block above = world.getBlock(x, y + 1, z);
		if (block != above)
			return;
		Material material = above.getMaterial();
		boolean flag = material == Material.gourd || isFertile(world, x, y, z);

		if (!flag && material.isSolid())
		{
			world.setBlock(x, y, z, Blocks.dirt);
		}
		else
		{
			alterMetadata(world, x, y, z, -1);
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) { ; }

	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity ent, float distance)
	{
		if (distance > 10)
		{
			if (!world.isRemote && world.rand.nextFloat() * 20 < distance)
			{
				if (!(ent instanceof EntityPlayer) && !world.getGameRules().getGameRuleBooleanValue("mobGriefing"))
				{
					return;
				}

				world.setBlock(x, y, z, Blocks.farmland);
				if (world.rand.nextFloat() >= 0.25)
					dropBlockAsItem(world, x, y, z, new ItemStack(MFRThings.fertilizerItem));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		blockIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		return side == 1 ? blockIcon : Blocks.dirt.getIcon(side, 2);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return side != ForgeDirection.UP;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		ret.add(new ItemStack(Blocks.dirt));
		if (world.rand.nextFloat() >= 0.5)
			ret.add(new ItemStack(MFRThings.fertilizerItem));

		return ret;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean worthlessBoolean)
	{ // canFertilize
		return world.getBlockMetadata(x, y, z) < 15;
	}

	@Override
	public boolean func_149852_a(World world, Random rand, int x, int y, int z)
	{ // shouldFertilize
		return world.getBlockMetadata(x, y, z) < 15 && rand.nextInt(3) == 0;
	}

	@Override
	public void func_149853_b(World world, Random rand, int x, int y, int z)
	{ // fertilize
		alterMetadata(world, x, y, z, 1 + (int)(rand.nextFloat() * 1.5f));
	}
}
