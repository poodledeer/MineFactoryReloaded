package powercrystals.minefactoryreloaded.item.tool;

import cofh.api.block.IBlockConfigGui;
import cofh.api.block.IBlockDebug;
import cofh.api.block.IBlockInfo;
import cofh.api.tileentity.ITileInfo;
import cofh.lib.util.helpers.ServerHelper;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.api.rednet.IRedNetInfo;
import powercrystals.minefactoryreloaded.item.base.ItemMulti;

public class ItemRedNetMeter extends ItemMulti {

	public static String[] _colorNames = new String[] { "White", "Orange", "Magenta", "LightBlue",
					"Yellow", "Lime", "Pink", "Gray", "LightGray", "Cyan", "Purple",
					"Blue", "Brown", "Green", "Red", "Black" };

	public ItemRedNetMeter() {
		setNames(null, "info", "debug");
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
		if (stack.getItemDamage() != 2)
			return false;
		player.swingItem();
		if (player.worldObj.isRemote)
			return true;
		player.addChatMessage(new ChatComponentText("ID: " + EntityList.getEntityString(entity)));
		return true;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {
		boolean r = doItemThing(stack, player, world, x, y, z, hitSide, hitX, hitY, hitZ);
		if (r) // HACK: forge is fucking stupid with this method
			ServerHelper.sendItemUsePacket(stack, player, world, x, y, z, hitSide, hitX, hitY, hitZ);
		return r;
	}

	public boolean doItemThing(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {
		switch (stack.getItemDamage()) {
		case 2:
			Block block = world.getBlock(x, y, z);
			ArrayList<IChatComponent> info = new ArrayList<IChatComponent>();
			if (player.isSneaking() && block instanceof IBlockDebug) {
				((IBlockDebug) (block)).debugBlock(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[hitSide], player);
				return true;
			} else if (block instanceof IBlockInfo) {
				if (ServerHelper.isClientWorld(world)) {
					info.add(new ChatComponentText("-Client-"));
				} else {
					info.add(new ChatComponentText("-Server-"));
				}
				((IBlockInfo) (block)).getBlockInfo(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[hitSide], player, info, true);
				for (int i = 0; i < info.size(); i++) {
					player.addChatMessage(info.get(i));
				}
				return true;
			} else {
				TileEntity theTile = world.getTileEntity(x, y, z);
				if (theTile instanceof ITileInfo) {
					if (ServerHelper.isServerWorld(world)) {
						((ITileInfo) theTile).getTileInfo(info, ForgeDirection.UNKNOWN, player, player.isSneaking());
						for (int i = 0; i < info.size(); i++) {
							player.addChatMessage(info.get(i));
						}
					}
					return true;
				}
			}
			return false;
		case 1:
			block = world.getBlock(x, y, z);
			if (ServerHelper.isClientWorld(world)) {
				if (block instanceof IBlockConfigGui || block instanceof IBlockInfo)
					return true;
				TileEntity theTile = world.getTileEntity(x, y, z);
				return theTile instanceof ITileInfo;
			}
			info = new ArrayList<IChatComponent>();
			if (player.isSneaking() && block instanceof IBlockConfigGui) {
				if (((IBlockConfigGui)block).openConfigGui(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[hitSide], player))
					return true;
			}
			if (block instanceof IBlockInfo) {
				((IBlockInfo) (block)).getBlockInfo(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[hitSide], player, info, false);
				for (int i = 0; i < info.size(); i++) {
					player.addChatMessage(info.get(i));
				}
				return true;
			} else {
				TileEntity theTile = world.getTileEntity(x, y, z);
				if (theTile instanceof ITileInfo) {
					if (ServerHelper.isServerWorld(world)) {
						((ITileInfo) theTile).getTileInfo(info, ForgeDirection.UNKNOWN, player, false);
						for (int i = 0; i < info.size(); i++) {
							player.addChatMessage(info.get(i));
						}
					}
					return true;
				}
			}
			return false;
		case 0:
			block = world.getBlock(x, y, z);
			if (ServerHelper.isClientWorld(world)) {
				return block instanceof IRedNetInfo || block.equals(Blocks.redstone_wire);
			}
			info = new ArrayList<IChatComponent>();
			if (block.equals(Blocks.redstone_wire)) {
				player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.meter.dustprefix")
						.appendText(": " + world.getBlockMetadata(x, y, z)));
			}
			else if (block instanceof IRedNetInfo) {
				((IRedNetInfo) (block)).getRedNetInfo(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[hitSide], player, info);
				for (int i = 0; i < info.size(); i++) {
					player.addChatMessage(info.get(i));
				}
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

}
