package powercrystals.minefactoryreloaded.block;

import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityDetCord;

public class BlockDetCord extends BlockFactory implements ITileEntityProvider {

	public BlockDetCord() {

		super(Machine.MATERIAL);
		setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(soundTypeSnow);
		setBlockName("mfr.detcord");
		setCreativeTab(MFRCreativeTab.tab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {

		return new TileEntityDetCord();
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {

		return false; // temporary
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return false;
	}

	@Override
	public boolean isOpaqueCube() {

		return false;
	}

	@Override
	public boolean isNormalCube() {

		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addCollisionBoxesToList(World w, int x, int y, int z, AxisAlignedBB t, List l, Entity e) {

	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {

		if (!canPlaceBlockAt(world, x, y, z))
			return false;
		BlockPosition bp = new BlockPosition(x, y, z, ForgeDirection.getOrientation(side)).moveBackwards(1);
		return bp.getBlock(world).isSideSolid(world, bp.x, bp.y, bp.z, bp.orientation);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {

		blockIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}

	@Override
	public int getRenderType() {

		return MineFactoryReloadedCore.renderIdDetCord;
	}
}
