package powercrystals.minefactoryreloaded.block.decor;

import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.render.IconOverlay;

public class BlockFactoryGlassPane extends BlockPane implements IRedNetDecorative
{
	protected IIcon _iconSide;

	public BlockFactoryGlassPane()
	{
		this(true);
	}

	public BlockFactoryGlassPane(boolean mfr)
	{
		super("", "", Material.glass, false);
		setHardness(0.3F);
		setStepSound(soundTypeGlass);
		if (mfr)
		{
			setCreativeTab(MFRCreativeTab.tab);
			setBlockName("mfr.stainedglass.pane");
		}
		else
			setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public int damageDropped(int par1)
	{
		return par1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		// This space intentionally left blank.
	}

	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}

	@Override
	public int getRenderColor(int meta)
	{
		return MFRUtil.COLORS[Math.min(Math.max(meta, 0), 15)];
	}

	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if (meta != colour)
		{
			return world.setBlockMetadataWithNotify(x, y, z, colour, 3);
		}
		return false;
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		meta /= 16;
		switch (meta)
		{
		case 2:
			return new IconOverlay(BlockFactoryGlass._texture, 8, 8, 0, 0);
		case 1:
			return new IconOverlay(BlockFactoryGlass._texture, 8, 8, 6, 7);
		case 0:
		default:
			return new IconOverlay(BlockFactoryGlass._texture, 8, 8, 7, 7);
		}
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		BlockPosition bp = new BlockPosition(x, y, z, ForgeDirection.VALID_DIRECTIONS[side]);
		boolean[] sides = new boolean[8];
		bp.moveRight(1);
		sides[0] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveDown(1);
		sides[4] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveLeft(1);
		sides[1] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveLeft(1);
		sides[5] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveUp(1);
		sides[3] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveUp(1);
		sides[6] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveRight(1);
		sides[2] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveRight(1);
		sides[7] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		return new IconOverlay(BlockFactoryGlass._texture, 8, 8, sides);
	}

	@Override
	public IIcon func_150097_e()
	{
		return new IconOverlay(BlockFactoryGlass._texture, 8, 8, 5, 7);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		return !(canPaneConnectTo(world, x, y, z, ForgeDirection.getOrientation(side)) ||
				!super.shouldSideBeRendered(world, x, y, z, side));
	}

	@Override
	public boolean canPaneConnectTo(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
	{
		Block block = world.getBlock(x, y, z);
		return block.func_149730_j() ||
				block instanceof BlockPane ||
				block.getMaterial() == Material.glass ||
				world.isSideSolid(x, y, z, dir.getOpposite(), false);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		float xStart = 0.4375F;
		float zStart = 0.5625F;
		float xStop = 0.4375F;
		float zStop = 0.5625F;
		boolean connectedNorth = this.canPaneConnectTo(world, x, y, z - 1, ForgeDirection.NORTH);
		boolean connectedSouth = this.canPaneConnectTo(world, x, y, z + 1, ForgeDirection.SOUTH);
		boolean connectedWest = this.canPaneConnectTo(world, x - 1, y, z, ForgeDirection.WEST);
		boolean connectedEast = this.canPaneConnectTo(world, x + 1, y, z, ForgeDirection.EAST);

		if ((!connectedWest || !connectedEast) && (connectedWest || connectedEast || connectedNorth || connectedSouth))
		{
			if (connectedWest && !connectedEast)
			{
				xStart = 0.0F;
			}
			else if (!connectedWest && connectedEast)
			{
				zStart = 1.0F;
			}
		}
		else
		{
			xStart = 0.0F;
			zStart = 1.0F;
		}

		if ((!connectedNorth || !connectedSouth) && (connectedWest || connectedEast || connectedNorth || connectedSouth))
		{
			if (connectedNorth && !connectedSouth)
			{
				xStop = 0.0F;
			}
			else if (!connectedNorth && connectedSouth)
			{
				zStop = 1.0F;
			}
		}
		else
		{
			xStop = 0.0F;
			zStop = 1.0F;
		}

		this.setBlockBounds(xStart, 0.0F, xStop, zStart, 1.0F, zStop);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List blockList, Entity e)
	{
		boolean connectedNorth = this.canPaneConnectTo(world, x, y, z - 1, ForgeDirection.NORTH);
		boolean connectedSouth = this.canPaneConnectTo(world, x, y, z + 1, ForgeDirection.SOUTH);
		boolean connectedWest = this.canPaneConnectTo(world, x - 1, y, z, ForgeDirection.WEST);
		boolean connectedEast = this.canPaneConnectTo(world, x + 1, y, z, ForgeDirection.EAST);

		if ((!connectedWest || !connectedEast) && (connectedWest || connectedEast || connectedNorth || connectedSouth))
		{
			if (connectedWest && !connectedEast)
			{
				this.setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
				addCollidingBlockToList_do(world, x, y, z, aabb, blockList, e);
			}
			else if (!connectedWest && connectedEast)
			{
				this.setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
				addCollidingBlockToList_do(world, x, y, z, aabb, blockList, e);
			}
		}
		else
		{
			this.setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
			addCollidingBlockToList_do(world, x, y, z, aabb, blockList, e);
		}

		if ((!connectedNorth || !connectedSouth) && (connectedWest || connectedEast || connectedNorth || connectedSouth))
		{
			if (connectedNorth && !connectedSouth)
			{
				this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
				addCollidingBlockToList_do(world, x, y, z, aabb, blockList, e);
			}
			else if (!connectedNorth && connectedSouth)
			{
				this.setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
				addCollidingBlockToList_do(world, x, y, z, aabb, blockList, e);
			}
		}
		else
		{
			this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
			addCollidingBlockToList_do(world, x, y, z, aabb, blockList, e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addCollidingBlockToList_do(World world, int x, int y, int z, AxisAlignedBB aabb, List blockList, Entity e)
	{
		AxisAlignedBB newAABB = this.getCollisionBoundingBoxFromPool(world, x, y, z);

		if (newAABB != null && aabb.intersectsWith(newAABB))
		{
			blockList.add(newAABB);
		}
	}

	@Override
	public int getRenderType()
	{
		return MineFactoryReloadedCore.renderIdFactoryGlassPane;
	}
}
