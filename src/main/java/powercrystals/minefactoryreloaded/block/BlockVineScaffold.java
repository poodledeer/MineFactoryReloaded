package powercrystals.minefactoryreloaded.block;

import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class BlockVineScaffold extends Block implements IRedNetDecorative {

	private IIcon _sideIcon;
	private IIcon _topIcon;

	private static final ForgeDirection[] _attachDirections = new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH,
			ForgeDirection.EAST, ForgeDirection.WEST };
	private static final int _attachDistance = 11;

	public BlockVineScaffold() {

		super(Material.leaves);
		setBlockName("mfr.vinescaffold");
		setStepSound(soundTypeGrass);
		setHardness(0.1F);
		setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
		setTickRandomly(true);
		setCreativeTab(MFRCreativeTab.tab);
	}

	@Override
	public int getRenderBlockPass() {

		return 1;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

		float shrinkAmount = 1f / 45f;
		if (entity.boundingBox.minY >= y + (1f - shrinkAmount) ||
				entity.boundingBox.maxY <= y + shrinkAmount)
			return;
		entity.fallDistance = 0;
		if (entity.isCollidedHorizontally) {
			entity.motionY = 0.2D;
		} else if (entity.isSneaking()) {
			double diff = entity.prevPosY - entity.posY;
			entity.boundingBox.minY += diff;
			entity.boundingBox.maxY += diff;
			entity.posY = entity.prevPosY;
		} else {
			entity.motionY = -0.12D;
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {

		float shrinkAmount = 0.125F;
		return AxisAlignedBB.getBoundingBox(x + this.minX + shrinkAmount, y + this.minY,
			z + this.minZ + shrinkAmount, x + this.maxX - shrinkAmount,
			y + this.maxY, z + this.maxZ - shrinkAmount);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		_sideIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".side");
		_topIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {

		return side < 2 ? _topIcon : _sideIcon;
	}

	@Override
	public boolean isOpaqueCube() {

		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {

		return false;
	}

	@Override
	public int getRenderType() {

		return MineFactoryReloadedCore.renderIdVineScaffold;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {

		return !world.getBlock(x, y, z).isOpaqueCube();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta) {

		return ColorizerFoliage.getFoliageColorBasic();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {

		int r = 0;
		int g = 0;
		int b = 0;

		for (int zOffset = -1; zOffset <= 1; ++zOffset) {
			for (int xOffset = -1; xOffset <= 1; ++xOffset) {
				int biomeColor = world.getBiomeGenForCoords(x + xOffset, z + zOffset).getBiomeFoliageColor(x, y, z);
				r += (biomeColor & 16711680) >> 16;
				g += (biomeColor & 65280) >> 8;
				b += biomeColor & 255;
			}
		}

		return (r / 9 & 255) << 16 | (g / 9 & 255) << 8 | b / 9 & 255;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset,
			float yOffset, float zOffset) {

		ItemStack ci = player.inventory.mainInventory[player.inventory.currentItem];
		if (ci != null && Block.getBlockFromItem(ci.getItem()).equals(this)) {
			for (int i = y + 1, e = world.getActualHeight(); i < e; ++i) {
				Block block = world.getBlock(x, i, z);
				if (block.isAir(world, x, i, z) || block.isReplaceable(world, x, i, z)) {
					if (!world.isRemote && world.setBlock(x, i, z, this, 0, 3)) {
						world.playAuxSFXAtEntity(null, 2001, x, i, z, Block.getIdFromBlock(this));
						if (!player.capabilities.isCreativeMode) {
							ci.stackSize--;
							if (ci.stackSize == 0) {
								player.inventory.mainInventory[player.inventory.currentItem] = null;
							}
						}
					}
					return true;
				} else if (!block.equals(this)) {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {

		return canBlockStay(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {

		if (world.isSideSolid(x, y - 1, z, ForgeDirection.UP)) {
			return true;
		}
		for (ForgeDirection d : _attachDirections) {
			BlockPosition bp = new BlockPosition(x, y, z, d);
			for (int i = 0; i < _attachDistance; i++) {
				bp.moveForwards(1);
				if (world.getBlock(bp.x, bp.y, bp.z).equals(this)) {
					if (world.isSideSolid(bp.x, bp.y - 1, bp.z, ForgeDirection.UP)) {
						return true;
					}
				} else
					break;
			}
		}
		return false;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {

		onNeighborBlockChange(world, x, y, z, null);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {

		if (!canBlockStay(world, x, y, z)) {
			for (int e = world.getActualHeight(); y < e; ++y) {
				block = world.getBlock(x, y, z);
				if (!block.equals(this))
					break;
				dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				world.setBlock(x, y, z, Blocks.air, 0, 2);
				for (ForgeDirection d : _attachDirections) {
					BlockPosition bp = new BlockPosition(x, y, z, d);
					for (int i = 0; i < _attachDistance; i++) {
						bp.moveForwards(1);
						block = world.getBlock(bp.x, bp.y, bp.z);
						if (block.equals(this)) {
							world.func_147446_b(bp.x, bp.y, bp.z, block, 0, 0);
						} else
							break;
					}
				}
			}
		}
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return side == ForgeDirection.UP || side == ForgeDirection.DOWN;
	}
}
