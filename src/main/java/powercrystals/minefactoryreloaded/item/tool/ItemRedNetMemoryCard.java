package powercrystals.minefactoryreloaded.item.tool;

import cofh.api.tileentity.IPortableData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

public class ItemRedNetMemoryCard extends ItemFactory {

	@Override
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {

		super.addInfo(stack, player, infoList, advancedTooltips);
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {
			l: {
				if (tag.hasKey("Type")) {
					String type = tag.getString("Type");
					String entry = MFRUtil.localize("tip.info.mfr.memorycard.programmedFor", new Object[] {
						MFRUtil.localize(type)
					});
					infoList.addAll(Arrays.asList(entry.split("\n", -1)));

					if (!type.equals("tile.mfr.rednet.logic.name"))
						break l;
				}
				if (tag.hasKey("circuits", 9)) {
					int c = stack.getTagCompound().getTagList("circuits", 10).tagCount();
					infoList.add(MFRUtil.localize("tip.info.mfr.memorycard.programmed", c));
				}
			}
			infoList.add(MFRUtil.localize("tip.info.mfr.memorycard.wipe", true));
		}
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float xOffset, float yOffset, float zOffset) {

		if (world.isRemote) {
			return true;
		}

		TileEntity te = world.getTileEntity(x, y, z);
		NBTTagCompound tag = stack.getTagCompound();
		boolean read = tag == null || !tag.hasKey("Type"), special = false;
		if (tag == null)
			stack.setTagCompound(tag = new NBTTagCompound());
		else if (read && tag.hasKey("circuits", 9)) {
			special = true;
			read = false;
		}

		l: if (!special && te instanceof IPortableData) {
			if (read) {
				NBTTagCompound tag2 = (NBTTagCompound) tag.copy();
				((IPortableData) te).writePortableData(player, tag2);
				if (!tag2.equals(tag)) {
					tag = tag2;
					tag.setString("Type", ((IPortableData) te).getDataType());
				}
			} else {
				if (tag.getString("Type").equals(((IPortableData) te).getDataType())) {
					((IPortableData) te).readPortableData(player, tag);
				} else
					break l;
			}
			stack.setTagCompound(tag);

			return true;
		}
		else if (te instanceof TileEntityRedNetLogic) {
			if (special)
				((IPortableData) te).readPortableData(player, tag);

			return true;
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {

		NBTTagCompound tag = stack.getTagCompound();
		return tag != null && (tag.hasKey("Type") || tag.hasKey("circuits", 9));
	}

}
