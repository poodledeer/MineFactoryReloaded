package powercrystals.minefactoryreloaded.item.gun;

import cofh.lib.util.helpers.ItemHelper;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.entity.EntitySafariNet;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryGun;

public class ItemSafariNetLauncher extends ItemFactoryGun {

	public ItemSafariNetLauncher() {
		setHasIcons(true);
	}

	@Override
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {
		super.addInfo(stack, player, infoList, advancedTooltips);
		infoList.add(StatCollector.translateToLocal("tip.info.mfr.safarinet.mode"));
	}

	@Override
	protected boolean hasGUI(ItemStack stack) {
		return false;
	}

	@Override
	protected boolean fire(ItemStack stack, World world, EntityPlayer player) {
		if (player.isSneaking()) {
			stack.setItemDamage(stack.getItemDamage() == 0 ? 1 : 0);
			if (world.isRemote) {
				if (isCaptureMode(stack)) {
					player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.safarinet.capture"));
				} else {
					player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.safarinet.release"));
				}
			}
			return false;
		}

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack ammo = player.inventory.getStackInSlot(i);
			if (ItemSafariNet.isSafariNet(ammo)) {
				if (ItemSafariNet.isEmpty(ammo) == isCaptureMode(stack)) {
					player.inventory.setInventorySlotContents(i, ItemHelper.consumeItem(ammo));
					if (ammo.stackSize > 0) {
						ammo = ammo.copy();
					}
					ammo.stackSize = 1;
					if (!world.isRemote) {
						EntitySafariNet esn = new EntitySafariNet(world, player, ammo);
						world.spawnEntityInWorld(esn);

						world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
					}
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isCaptureMode(ItemStack stack) {
		return stack != null && stack.getItemDamage() == 1;
	}

	@Override
	protected int getDelay(ItemStack stack, boolean fired) {
		return fired ? 10 : 3;
	}

	@Override
	protected String getDelayTag(ItemStack stack) {
		return "mfr:SafariLaunch";
	}
}
