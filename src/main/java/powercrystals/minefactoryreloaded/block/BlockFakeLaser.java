package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.api.IFactoryLaserSource;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.core.GrindingDamage;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrill;

public class BlockFakeLaser extends Block implements IRedNetNoConnection {

	public static Material laser = new MaterialTransparent(MapColor.airColor);
	private static GrindingDamage laserDamage = new GrindingDamage("mfr.laser");

	public BlockFakeLaser() {

		super(laser);
		setHardness(-1);
		setResistance(Float.POSITIVE_INFINITY);
		setBlockBounds(0F, 0F, 0F, 0F, 0F, 0F);
		setBlockName("mfr.laserair");
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {

		return null;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

		if (world.isRemote)
			return;

		entity.setFire(15);
		long t = entity.getEntityData().getLong("mfr:laserTime"), t2 = world.getTotalWorldTime();
		t = t2 - t;
		long d = t / 20 >= 5 ? 1 : entity.getEntityData().getLong("mfr:laserDamage") | 1;
		d &= -1L >>> 1L;
		if (t > 10 && entity.attackEntityFrom(laserDamage, d)) {
			entity.getEntityData().setLong("mfr:laserTime", t2 + (d == 1 ? 20 : 0));
			entity.getEntityData().setLong("mfr:laserDamage", d * 2);
		}
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {

		return 15;
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
	public boolean isAir(IBlockAccess world, int x, int y, int z) {

		return true;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {

		world.scheduleBlockUpdate(x, y, z, this, 1);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block id) {

		world.scheduleBlockUpdate(x, y, z, this, 1);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {

		if (world.isRemote) return;

		int meta = world.getBlockMetadata(x, y, z);
		l: if (meta != 0) {
			ForgeDirection dir = ForgeDirection.getOrientation(meta - 1);
			if (world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ).equals(this))
				if (world.getBlockMetadata(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == meta)
					return;
				else
					break l;
			TileEntity te = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if (te instanceof IFactoryLaserSource && ((IFactoryLaserSource) te).canFormBeamFrom(dir))
				return;
			world.setBlockMetadataWithNotify(x, y, z, 0, 0);
		}

		Block upperId = world.getBlock(x, y + 1, z);
		if (!upperId.equals(this) && !(world.getTileEntity(x, y + 1, z) instanceof TileEntityLaserDrill)) {
			world.setBlockToAir(x, y, z);
			return;
		}

		Block lowerId = world.getBlock(x, y - 1, z);
		if ((!lowerId.equals(this) || world.getBlockMetadata(x, y - 1, z) != 0) &&
				TileEntityLaserDrill.canReplaceBlock(lowerId, world, x, y - 1, z)) {
			world.setBlock(x, y - 1, z, this);
		}
	}

	@Override
	public boolean canRenderInPass(int pass) {

		return false;
	}

	@Override
	public int getRenderType() {

		return -1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

	}
}
