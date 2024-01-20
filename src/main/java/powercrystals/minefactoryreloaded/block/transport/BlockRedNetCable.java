package powercrystals.minefactoryreloaded.block.transport;

import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.renderIdRedNet;

import cofh.api.block.IBlockInfo;
import cofh.lib.util.position.BlockPosition;
import cofh.repack.codechicken.lib.raytracer.RayTracer;
import cofh.repack.codechicken.lib.vec.Cuboid6;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetInfo;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNetworkContainer;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.tool.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.render.block.RedNetCableRenderer;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.rednet.RedstoneNetwork;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;

public class BlockRedNetCable extends BlockFactory implements IRedNetNetworkContainer, IBlockInfo, IRedNetInfo, ITileEntityProvider {

	public static final String[] _names = { null, "glass", "energy", "energyglass" };

	//@formatter:off
	private static float _wireSize   =  4.0F / 16.0F;
	private static float _cageSize   =  6.0F / 16.0F;
	private static float _gripWidth  =  8.0F / 16.0F;
	private static float _plateWidth = 14.0F / 16.0F;
	private static float _plateDepth =  2.0F / 16.0F;
	private static float _bandWidth  =  5.0F / 16.0F;
	private static float _bandOffset =  2.0F / 16.0F;
	private static float _bandDepth  =  1.0F / 16.0F;

	private static float _wireStart      = 0.5F - _wireSize   / 2.0F;
	private static float _wireEnd        = 0.5F + _wireSize   / 2.0F;
	private static float _cageStart      = 0.5F - _cageSize   / 2.0F;
	private static float _cageEnd        = 0.5F + _cageSize   / 2.0F;
	private static float _gripStart      = 0.5F - _gripWidth  / 2.0F;
	private static float _gripEnd        = 0.5F + _gripWidth  / 2.0F;
	private static float _plateStart     = 0.5F - _plateWidth / 2.0F;
	private static float _plateEnd       = 0.5F + _plateWidth / 2.0F;
	private static float _bandWidthStart = 0.5F - _bandWidth  / 2.0F;
	private static float _bandWidthEnd   = 0.5F + _bandWidth  / 2.0F;

	private static float _bandDepthStart = _bandOffset;
	private static float _bandDepthEnd   = _bandOffset + _bandDepth;
	//@formatter:on

	public static int[] _subSideMappings = new int[] { 6, 6,
			0, 1, 2, 3, 4, 5, // 6[0]
			0, 1, 2, 3, 4, 5, // 6[1]
			0, 1, 2, 3, 4, 5, // 6[2]
			0, 1, 2, 3, 4, 5, // 6[3]
			0, 1, 2, 3, 4, 5, // 6[4]
			0, 1, 2, 3, 4, 5, // 6[5]
			7, 8, 9, 10, 11, 12 };

	public static Cuboid6[] subSelection = new Cuboid6[2 + 6 * 6];

	static {
		int i = 0;
		// ** 0 **  cable hitbox
		subSelection[i++] = new Cuboid6(_wireStart, _wireStart, _wireStart, _wireEnd, _wireEnd, _wireEnd);
		// ** 1 **  wire cage hitbox
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _cageStart, _cageEnd, _cageEnd, _cageEnd);

		// ** 6[0] ** grip hitbox
		subSelection[i++] = new Cuboid6(_gripStart, 0, _gripStart, _gripEnd, _plateDepth, _gripEnd);
		subSelection[i++] = new Cuboid6(_gripStart, 1 - _plateDepth, _gripStart, _gripEnd, 1, _gripEnd);
		subSelection[i++] = new Cuboid6(_gripStart, _gripStart, 0, _gripEnd, _gripEnd, _plateDepth);
		subSelection[i++] = new Cuboid6(_gripStart, _gripStart, 1 - _plateDepth, _gripEnd, _gripEnd, 1);
		subSelection[i++] = new Cuboid6(0, _gripStart, _gripStart, _plateDepth, _gripEnd, _gripEnd);
		subSelection[i++] = new Cuboid6(1 - _plateDepth, _gripStart, _gripStart, 1, _gripEnd, _gripEnd);

		// ** 6[1] ** plate hitbox
		subSelection[i++] = new Cuboid6(_plateStart, 0, _plateStart, _plateEnd, _plateDepth, _plateEnd);
		subSelection[i++] = new Cuboid6(_plateStart, 1 - _plateDepth, _plateStart, _plateEnd, 1, _plateEnd);
		subSelection[i++] = new Cuboid6(_plateStart, _plateStart, 0, _plateEnd, _plateEnd, _plateDepth);
		subSelection[i++] = new Cuboid6(_plateStart, _plateStart, 1 - _plateDepth, _plateEnd, _plateEnd, 1);
		subSelection[i++] = new Cuboid6(0, _plateStart, _plateStart, _plateDepth, _plateEnd, _plateEnd);
		subSelection[i++] = new Cuboid6(1 - _plateDepth, _plateStart, _plateStart, 1, _plateEnd, _plateEnd);

		// ** 6[2] ** color band hitbox
		subSelection[i++] = new Cuboid6(_bandWidthStart, _bandDepthStart, _bandWidthStart, _bandWidthEnd, _bandDepthEnd, _bandWidthEnd);
		subSelection[i++] = new Cuboid6(_bandWidthStart, 1 - _bandDepthEnd, _bandWidthStart, _bandWidthEnd, 1 - _bandDepthStart, _bandWidthEnd);
		subSelection[i++] = new Cuboid6(_bandWidthStart, _bandWidthStart, _bandDepthStart, _bandWidthEnd, _bandWidthEnd, _bandDepthEnd);
		subSelection[i++] = new Cuboid6(_bandWidthStart, _bandWidthStart, 1 - _bandDepthEnd, _bandWidthEnd, _bandWidthEnd, 1 - _bandDepthStart);
		subSelection[i++] = new Cuboid6(_bandDepthStart, _bandWidthStart, _bandWidthStart, _bandDepthEnd, _bandWidthEnd, _bandWidthEnd);
		subSelection[i++] = new Cuboid6(1 - _bandDepthEnd, _bandWidthStart, _bandWidthStart, 1 - _bandDepthStart, _bandWidthEnd, _bandWidthEnd);

		// ** 6[3] ** cable connection hitbox
		subSelection[i++] = new Cuboid6(_wireStart, 0, _wireStart, _wireEnd, _wireStart, _wireEnd);
		subSelection[i++] = new Cuboid6(_wireStart, _wireEnd, _wireStart, _wireEnd, 1 - 0, _wireEnd);
		subSelection[i++] = new Cuboid6(_wireStart, _wireStart, 0, _wireEnd, _wireEnd, _wireStart);
		subSelection[i++] = new Cuboid6(_wireStart, _wireStart, _wireEnd, _wireEnd, _wireEnd, 1 - 0);
		subSelection[i++] = new Cuboid6(0, _wireStart, _wireStart, _wireStart, _wireEnd, _wireEnd);
		subSelection[i++] = new Cuboid6(_wireEnd, _wireStart, _wireStart, 1 - 0, _wireEnd, _wireEnd);

		// ** 6[4] ** wire cage minus band connection hitbox
		subSelection[i++] = new Cuboid6(_cageStart, _bandDepthEnd, _cageStart, _cageEnd, _cageStart, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageStart, _cageEnd, _cageStart, _cageEnd, 1 - _bandDepthEnd, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _bandDepthEnd, _cageEnd, _cageEnd, _cageStart);
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _cageEnd, _cageEnd, _cageEnd, 1 - _bandDepthEnd);
		subSelection[i++] = new Cuboid6(_bandDepthEnd, _cageStart, _cageStart, _cageStart, _cageEnd, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageEnd, _cageStart, _cageStart, 1 - _bandDepthEnd, _cageEnd, _cageEnd);

		// ** 6[5] ** wire cage connection hitbox
		subSelection[i++] = new Cuboid6(_cageStart, 0, _cageStart, _cageEnd, _cageStart, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageStart, _cageEnd, _cageStart, _cageEnd, 1 - 0, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, 0, _cageEnd, _cageEnd, _cageStart);
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _cageEnd, _cageEnd, _cageEnd, 1 - 0);
		subSelection[i++] = new Cuboid6(0, _cageStart, _cageStart, _cageStart, _cageEnd, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageEnd, _cageStart, _cageStart, 1 - 0, _cageEnd, _cageEnd);
	}

	public BlockRedNetCable() {

		super(0.8F);
		setBlockName("mfr.cable.redstone");
		providesPower = true;
	}

	@Override
	public boolean activated(World world, int x, int y, int z, EntityPlayer player, int side) {

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable) {
			TileEntityRedNetCable cable = (TileEntityRedNetCable) te;

			harvesters.set(player);
			MovingObjectPosition part = collisionRayTrace(world, x, y, z,
				RayTracer.getStartVec(player), RayTracer.getEndVec(player));
			harvesters.set(null);
			if (part == null)
				return false;

			int subHit = part.subHit;
			if (subHit < 0) {
				MineFactoryReloadedCore.instance().getLogger().error("subHit was " + subHit, new Throwable());
				return false;
			}
			side = _subSideMappings[subHit];

			ItemStack s = player.inventory.getCurrentItem();

			if (cable.onPartHit(player, side, subHit)) {
				;
			} else if (subHit >= (2 + 6 * 2) && subHit < (2 + 6 * 3)) {
				if (MFRUtil.isHoldingUsableTool(player, x, y, z)) {
					if (!world.isRemote) {
						int nextColor;
						if (!player.isSneaking()) {
							nextColor = cable.getSideColor(ForgeDirection.getOrientation(side)) + 1;
							if (nextColor > 15) nextColor = 0;
						} else {
							nextColor = cable.getSideColor(ForgeDirection.getOrientation(side)) - 1;
							if (nextColor < 0) nextColor = 15;
						}
						cable.setSideColor(ForgeDirection.getOrientation(side), nextColor);
						return true;
					}
				} else if (s != null && s.getItem().equals(Items.dye)) {
					if (!world.isRemote) {
						cable.setSideColor(ForgeDirection.getOrientation(side), 15 - s.getItemDamage());
						return true;
					}
				}
			} else if (subHit >= 0 && subHit < (2 + 6 * 2) || subHit >= (2 + 6 * 5)) {
				l: if (MFRUtil.isHoldingUsableTool(player, x, y, z)) {
					if (!world.isRemote) {
						if (side > 6) {
							ForgeDirection dir = ForgeDirection.getOrientation(side - 7);
							TileEntityRedNetCable cable2 = new BlockPosition(x, y, z).step(dir).getTileEntity(world, TileEntityRedNetCable.class);
							cable.toggleSide(side - 7);
							if (cable2 != null) {
								cable2.toggleSide(1 ^ side - 7);
								if (cable.canInterface(cable2, dir)) {
									cable.getNetwork().addConduit(cable2);
								}
							}
							break l;
						}

						byte mode = cable.getMode(side);
						mode++;
						if (side == 6) {
							if (mode > 1)
								mode = 0;
							cable.setMode(side, mode);
							switch (mode) {
							case 0:
								player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.tile.standard"));
								break;
							case 1:
								player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.tile.cableonly"));
								break;
							default:
							}
							break l;
						}
						if (mode > 3) {
							mode = 0;
						}
						cable.setMode(side, mode);
						switch (mode) {
						case 0:
							player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.connection.standard"));
							break;
						case 1:
							player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.connection.forced"));
							break;
						case 2:
							player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.connection.forcedstrong"));
							break;
						case 3:
							player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.connection.cableonly"));
							break;
						default:
						}
					}
					MFRUtil.usedWrench(player, x, y, z);
				} else if (s != null && s.getItem().equals(Items.dye)) {
					if (!world.isRemote) {
						cable.setSideColor(ForgeDirection.getOrientation(side), 15 - s.getItemDamage());
						world.markBlockForUpdate(x, y, z);
						return true;
					}
				}
			}
		}
		return false;
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
	public void onPostBlockPlaced(World world, int x, int y, int z, int metadata) {

		if (MFRConfig.defaultRedNetCableOnly.getBoolean(false)) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileEntityRedNetCable) {
				TileEntityRedNetCable cable = (TileEntityRedNetCable) te;
				cable.setMode(6, (byte) 1);
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block id, int meta) {

		super.breakBlock(world, x, y, z, id, meta);
		MFRUtil.wideNotifyNearbyBlocksExcept(world, x, y, z, id);
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {

		MovingObjectPosition part = collisionRayTrace(world, x, y, z,
			RayTracer.getStartVec(player), RayTracer.getEndVec(player));
		if (part == null)
			return false;
		int subHit = part.subHit;
		return subHit < (2 + 6 * 2) | (subHit > (2 + 6 * 3));
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {

		int power = 0;
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable) {
			power = ((TileEntityRedNetCable) te).getWeakPower(ForgeDirection.getOrientation(side).getOpposite());
		}
		return power;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {

		int power = 0;
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable) {
			power = ((TileEntityRedNetCable) te).getStrongPower(ForgeDirection.getOrientation(side).getOpposite());
		}
		return power;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable)
			return ((TileEntityRedNetCable) te).isSolidOnSide(side.ordinal());

		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {

		switch (meta) {
		default:
		case 0:
			return new TileEntityRedNetCable();
		case 2:
			return new TileEntityRedNetEnergy();
		}
	}

	@Override
	public int getRenderType() {

		return renderIdRedNet;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		blockIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		RedNetCableRenderer.updateUVT(blockIcon);
	}

	@Override
	public void updateNetwork(World world, int x, int y, int z, ForgeDirection from) {

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable) {
			((TileEntityRedNetCable) te).updateNearbyNode(from);
		}
	}

	@Override
	public void updateNetwork(World world, int x, int y, int z, int subnet, ForgeDirection from) {

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable) {
			((TileEntityRedNetCable) te).updateNearbyNode(subnet, from);
		}
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side) {

		return RedNetConnectionType.CableAll;
	}

	@Override
	public void getBlockInfo(IBlockAccess world, int x, int y, int z, ForgeDirection side, EntityPlayer player, List<IChatComponent> info, boolean debug) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityRedNetCable) {
			MovingObjectPosition part = collisionRayTrace(world, x, y, z, RayTracer.getStartVec(player), RayTracer.getEndVec(player));
			if (part == null)
				return;

			int subHit = part.subHit;
			side = ForgeDirection.getOrientation(_subSideMappings[subHit]);
			((TileEntityRedNetCable) tile).getTileInfo(info, side, player, debug);
		}
	}

	@Override
	public void getRedNetInfo(IBlockAccess world, int x, int y, int z, ForgeDirection side, EntityPlayer player, List<IChatComponent> info) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityRedNetCable) {
			MovingObjectPosition part = collisionRayTrace(world, x, y, z, RayTracer.getStartVec(player), RayTracer.getEndVec(player));
			if (part == null)
				return;

			int subHit = part.subHit;
			side = ForgeDirection.getOrientation(_subSideMappings[subHit]);
			info.add(new ChatComponentText(((TileEntityRedNetCable) tile).getRedNetInfo(side, player)));

			int value;
			int foundNonZero = 0;
			RedstoneNetwork _network = ((TileEntityRedNetCable) tile).getNetwork();
			for (int i = 0; i < 16; i++) {
				value = _network.getPowerLevelOutput(i);

				if (value != 0) {
					// TODO: localize color names v
					info.add(new ChatComponentText(ItemRedNetMeter._colorNames[i]).appendText(": " + value));
					++foundNonZero;
				}
			}

			if (foundNonZero == 0) {
				info.add(new ChatComponentTranslation("chat.info.mfr.rednet.meter.cable.allzero"));
			} else if (foundNonZero < 16) {
				info.add(new ChatComponentTranslation("chat.info.mfr.rednet.meter.cable.restzero"));
			}
		}
	}
}
