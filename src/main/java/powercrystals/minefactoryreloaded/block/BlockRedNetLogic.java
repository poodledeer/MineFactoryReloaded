package powercrystals.minefactoryreloaded.block;

import cofh.lib.util.helpers.ItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetInfo;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.ItemLogicUpgradeCard;
import powercrystals.minefactoryreloaded.item.tool.ItemRedNetMemoryCard;
import powercrystals.minefactoryreloaded.item.tool.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.render.block.RedNetLogicRenderer;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

public class BlockRedNetLogic extends BlockFactory implements IRedNetOmniNode, IRedNetInfo, ITileEntityProvider {

	private int[] _sideRemap = new int[] { 3, 1, 2, 0 };

	public BlockRedNetLogic() {

		super(0.8F);
		setBlockName("mfr.rednet.logic");
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {

		if (entity != null) {
			TileEntity te = getTile(world, x, y, z);
			if (te instanceof TileEntityRedNetLogic) {
				int facing = MathHelper.floor_double((entity.rotationYaw * 4F) / 360F + 0.5D) & 3;
				world.setBlockMetadataWithNotify(x, y, z, (facing + 3) & 3, 3);
			}
		}
		super.onBlockPlacedBy(world, x, y, z, entity, stack);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {

		return new TileEntityRedNetLogic();
	}

	@Override
	public int damageDropped(int meta) {

		return 0;
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side) {

		TileEntityRedNetLogic logic = (TileEntityRedNetLogic) world.getTileEntity(x, y, z);
		if (logic != null && side.ordinal() > 1 && side.ordinal() < 6) {
			if (world.getBlockMetadata(x, y, z) == _sideRemap[side.ordinal() - 2]) {
				return RedNetConnectionType.None;
			}
		}
		return RedNetConnectionType.CableAll;
	}

	@Override
	public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet) {

		TileEntityRedNetLogic logic = (TileEntityRedNetLogic) world.getTileEntity(x, y, z);
		if (logic != null) {
			return logic.getOutputValue(side, subnet);
		} else {
			return 0;
		}
	}

	@Override
	public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side) {

		TileEntityRedNetLogic logic = (TileEntityRedNetLogic) world.getTileEntity(x, y, z);
		if (logic != null) {
			return logic.getOutputValues(side);
		} else {
			return new int[16];
		}
	}

	@Override
	public void onInputsChanged(World world, int x, int y, int z, ForgeDirection side, int[] inputValues) {

		TileEntityRedNetLogic logic = (TileEntityRedNetLogic) world.getTileEntity(x, y, z);
		if (logic != null) {
			logic.onInputsChanged(side, inputValues);
		}
	}

	@Override
	public void onInputChanged(World world, int x, int y, int z, ForgeDirection side, int inputValue) {

	}

	@Override
	public boolean activated(World world, int x, int y, int z, EntityPlayer player, int side) {

		if (MFRUtil.isHoldingUsableTool(player, x, y, z)) {
			if (rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
				MFRUtil.usedWrench(player, x, y, z);
				return true;
			}
		}

		if (MFRUtil.isHolding(player, ItemLogicUpgradeCard.class)) {
			TileEntityRedNetLogic logic = (TileEntityRedNetLogic) world.getTileEntity(x, y, z);
			if (logic != null) {
				if (logic.insertUpgrade(player.inventory.getCurrentItem().getItemDamage() + 1)) {
					if (!player.capabilities.isCreativeMode) {
						player.inventory.setInventorySlotContents(player.inventory.currentItem,
							ItemHelper.consumeItem(player.inventory.getCurrentItem(), player));
					}
					return true;
				}
			}
			return false;
		} else if (!MFRUtil.isHolding(player, ItemRedNetMeter.class) && !MFRUtil.isHolding(player, ItemRedNetMemoryCard.class)) {
			if (!world.isRemote) {
				player.openGui(MineFactoryReloadedCore.instance(), 0, world, x, y, z);
			}
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		blockIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		RedNetLogicRenderer.updateUVT(blockIcon);
	}

	@Override
	public int getRenderType() {

		return MineFactoryReloadedCore.renderIdRedNetLogic;
	}

	@Override
	public boolean isOpaqueCube() {

		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return side.ordinal() <= 1 || side.ordinal() >= 6 || world.getBlockMetadata(x, y, z) != _sideRemap[side.ordinal() - 2];
	}

	@Override
	public void getRedNetInfo(IBlockAccess world, int x, int y, int z, ForgeDirection side,
			EntityPlayer player, List<IChatComponent> info) {

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetLogic) {
			int value;
			int foundNonZero = 0;
			for (int i = 0; i < ((TileEntityRedNetLogic) te).getBufferLength(13); i++) {
				value = ((TileEntityRedNetLogic) te).getVariableValue(i);

				if (value != 0) {
					info.add(new ChatComponentTranslation("chat.info.mfr.rednet.meter.varprefix")
							.appendText(" " + i + ": " + value));
					++foundNonZero;
				}
			}

			if (foundNonZero == 0) {
				info.add(new ChatComponentTranslation("chat.info.mfr.rednet.meter.var.allzero"));
			} else if (foundNonZero < 16) {
				info.add(new ChatComponentTranslation("chat.info.mfr.rednet.meter.var.restzero"));
			}
		}
	}

}
