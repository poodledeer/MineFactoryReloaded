package powercrystals.minefactoryreloaded.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.net.Packets;

public class ItemPortaSpawner extends ItemFactory {

	private static Block _block = Blocks.mob_spawner;
	public static final String spawnerTag = "spawner";
	private static final String placeTag = "placeDelay";

	public static NBTTagCompound getSpawnerTag(ItemStack stack) {

		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {
			if (tag.hasKey(spawnerTag))
				return tag.getCompoundTag(spawnerTag);
		}
		return null;
	}

	private static String getEntityId(ItemStack stack) {

		NBTTagCompound tag = getSpawnerTag(stack);
		if (tag != null)
			return tag.getString("EntityId");
		return null;
	}

	public static boolean hasData(ItemStack stack) {

		return getEntityId(stack) != null;
	}

	private static int getDelay(ItemStack stack) {

		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {
			return tag.getInteger(placeTag);
		}
		return 0;
	}

	@Override
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {

		super.addInfo(stack, player, infoList, advancedTooltips);
		String id = getEntityId(stack);
		if (id != null)
			infoList.add(MFRUtil.localize("tile.mobSpawner") + ": " +
					MFRUtil.localize("entity.", id));
		int delay = getDelay(stack);
		if (delay > 0) {
			String s = MFRUtil.localize("tip.info.mfr.cannotplace", true, "%s");
			infoList.add(String.format(s, Math.ceil(delay / 20f)));
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {

		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey(placeTag) && tag.getInteger(placeTag) > 0) {
			tag.setInteger(placeTag, tag.getInteger(placeTag) - 1);
		}
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,
			float xOffset, float yOffset, float zOffset) {

		if (world.isRemote) {
			return true;
		}
		if (getEntityId(itemstack) == null) {
			if (world.getBlock(x, y, z).equals(_block)) {
				TileEntity te = world.getTileEntity(x, y, z);
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag(spawnerTag, new NBTTagCompound());
				te.writeToNBT(tag.getCompoundTag(spawnerTag));
				tag.setInteger(placeTag, 40 * 20);
				itemstack.setTagCompound(tag);
				world.setBlockToAir(x, y, z);
				return true;
			} else
				return false;
		} else {
			if (getDelay(itemstack) <= 0 &&
					placeBlock(itemstack, player, world, x, y, z, side, xOffset, yOffset, zOffset)) {
				return true;
			}
			return false;
		}
	}

	private boolean placeBlock(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,
			float xOffset, float yOffset, float zOffset) {

		Block block = world.getBlock(x, y, z);

		if (block == Blocks.snow_layer) {
			side = 1;
		} else if (!block.isReplaceable(world, x, y, z)) {
			switch (side) {
			case 0:
				--y;
				break;
			case 1:
				++y;
				break;
			case 2:
				--z;
				break;
			case 3:
				++z;
				break;
			case 4:
				--x;
				break;
			case 5:
				++x;
				break;
			}
		}

		if (itemstack.stackSize == 0) {
			return false;
		} else if (!player.canPlayerEdit(x, y, z, side, itemstack)) {
			return false;
		} else if (y == 255 && block.getMaterial().isSolid()) {
			return false;
		} else if (world.canPlaceEntityOnSide(_block, x, y, z, false, side, player, itemstack)) {
			int meta = block.onBlockPlaced(world, x, y, z, side, xOffset, yOffset, zOffset, 0);

			if (placeBlockAt(itemstack, player, world, x, y, z, side, xOffset, yOffset, zOffset, meta)) {
				world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, block.stepSound.func_150496_b(),
					(block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
				--itemstack.stackSize;
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {

		// TODO: record and read the block that was consumed
		if (!world.setBlock(x, y, z, _block, metadata, 3)) {
			return false;
		}

		Block block = world.getBlock(x, y, z);
		if (block.equals(_block)) {
			block.onBlockPlacedBy(world, x, y, z, player, stack);
			block.onPostBlockPlaced(world, x, y, z, metadata);
			TileEntity te = world.getTileEntity(x, y, z);
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(spawnerTag))
				tag = tag.getCompoundTag(spawnerTag);
			tag.setInteger("x", x);
			tag.setInteger("y", y);
			tag.setInteger("z", z);
			te.readFromNBT(tag);
			Packets.sendToAllPlayersWatching(world, x, y, z, te.getDescriptionPacket());
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {

		return hasData(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack par1ItemStack) {

		return hasData(par1ItemStack) ? EnumRarity.epic : EnumRarity.rare;
	}

}
