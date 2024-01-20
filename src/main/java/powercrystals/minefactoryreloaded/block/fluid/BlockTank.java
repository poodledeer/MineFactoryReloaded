package powercrystals.minefactoryreloaded.block.fluid;

import cofh.api.block.IBlockInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.render.IconOverlay;
import powercrystals.minefactoryreloaded.tile.tank.TileEntityTank;

public class BlockTank extends BlockFactory implements IBlockInfo, ITileEntityProvider {

	protected IIcon[] icons = new IIcon[3];

	public BlockTank() {

		super(0.5f);
		setBlockName("mfr.tank");
		setLightOpacity(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		icons[0] = ir.registerIcon("minefactoryreloaded:machines/tile.mfr.tank.bottom");
		icons[1] = ir.registerIcon("minefactoryreloaded:machines/tile.mfr.tank.top");
		icons[2] = ir.registerIcon("minefactoryreloaded:machines/tile.mfr.tank.side");
	}

	@Override
	public int getRenderType() {

		return MineFactoryReloadedCore.renderIdFluidTank;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {

		Block block = world.getBlock(x, y, z);
		if (side > 1 && block.equals(this)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isOpaqueCube() {

		return false;
	}

	@Override
	public int getRenderBlockPass() {

		return 1;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank) tile;
			FluidStack fluid = tank.getFluid();
			if (fluid != null)
				return fluid.getFluid().getLuminosity(fluid);
		}
		return 0;
	}

	@Override
	public IIcon getIcon(int side, int meta) {

		if (side == 0)
			return icons[0];
		if ((side % 6) == 1) {
			return new IconOverlay(icons[1], 2, 2, meta);
		}
		return new IconOverlay(icons[2], 3, 3, meta);
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {

		int meta = 0;
		if (side == 6 || side == 7) {
			meta = 1;
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileEntityTank) {
				TileEntityTank tank = (TileEntityTank) tile;
				FluidStack fluid = tank.getFluid();
				if (fluid != null)
					return fluid.getFluid().getIcon(fluid);
			}
		}
		if (side > 1 && side < 6) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileEntityTank) {
				TileEntityTank tank = (TileEntityTank) tile;
				int side2 = (((side / 2 - 1) ^ 1) + 1) * 2 + ((side & 1) ^ (side / 2 - 1));
				boolean a = tank.isInterfacing(ForgeDirection.getOrientation(side2));
				boolean b = tank.isInterfacing(ForgeDirection.getOrientation(side2 ^ 1));
				if (a) {
					if (b)
						meta = 2;
					else
						meta = 1;
				} else if (b)
					meta = 3;
				if (meta != 0)
					meta += 2;
			}
		}
		return getIcon(side, meta);
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank) tile;
			FluidStack fluid = tank.getFluid();
			if (fluid != null)
				return fluid.getFluid().getColor(fluid);
		}
		return 0xFFFFFF;
	}

	@Override
	protected boolean activated(World world, int x, int y, int z, EntityPlayer player, int side) {

		super.activated(world, x, y, z, player, side);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {

		return new TileEntityTank();
	}

	@Override
	public void getBlockInfo(IBlockAccess world, int x, int y, int z, ForgeDirection side, EntityPlayer player, List<IChatComponent> info, boolean debug) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityTank) {
			((TileEntityTank) tile).getTileInfo(info, side, player, debug);
		}
	}

}
