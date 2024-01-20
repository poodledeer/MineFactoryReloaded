package powercrystals.minefactoryreloaded.block.transport;

import cofh.core.util.CoreUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class BlockFactoryRoad extends Block {

	private IIcon _iconRoad;
	private IIcon _iconRoadOff;
	private IIcon _iconRoadOn;

	public BlockFactoryRoad() {

		super(Material.rock);
		setHardness(2.0F);
		setBlockName("mfr.road");
		setResistance(25.0F);
		setStepSound(Blocks.stone.stepSound);
		setCreativeTab(MFRCreativeTab.tab);
	}

    @Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {

        final float f = 1 / 128f;
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1 - f, z + 1);
    }

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {

		if (!e.canTriggerWalking())
			return;
		if (e.getEntityData().getInteger("mfr:r") == e.ticksExisted)
			return;
		e.getEntityData().setInteger("mfr:r", e.ticksExisted);

		final double boost = .99 * slipperiness;
		final double minSpeed = 1e-9;

		if (Math.abs(e.motionX) > minSpeed || Math.abs(e.motionZ) > minSpeed) {
			e.motionX += e.motionX * boost;
			e.motionZ += e.motionZ * boost;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {

		_iconRoad = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		_iconRoadOff = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".light.off");
		_iconRoadOn = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".light.on");
	}

	@Override
	public IIcon getIcon(int side, int meta) {

		switch (meta) {
		case 1:
		case 3:
			return _iconRoadOff;
		case 2:
		case 4:
			return _iconRoadOn;
		default:
			return _iconRoad;
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {

		if (!world.isRemote) {
			int meta = world.getBlockMetadata(x, y, z);
			boolean isPowered = CoreUtils.isRedstonePowered(world, x, y, z);
			int newMeta = -1;

			if (meta == 1 && isPowered) {
				newMeta = 2;
			}
			else if (meta == 2 && !isPowered) {
				newMeta = 1;
			}
			else if (meta == 3 && !isPowered) {
				newMeta = 4;
			}
			else if (meta == 4 && isPowered) {
				newMeta = 3;
			}

			if (newMeta >= 0) {
				world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
			}
		}
	}

	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {

		return false;
	}

	@Override
	public int damageDropped(int meta) {

		switch (meta) {
		case 1:
		case 2:
			return 1;
		case 3:
		case 4:
			return 4;
		default:
			return 0;
		}
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {

		int meta = world.getBlockMetadata(x, y, z);
		return meta == 2 | meta == 4 ? 15 : 0;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {

		onNeighborBlockChange(world, x, y, z, this);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {

		if (entity instanceof EntityDragon) {
			return false;
		}

		return true;
	}
}
