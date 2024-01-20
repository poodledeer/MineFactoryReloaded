package powercrystals.minefactoryreloaded.item.gun;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryGun;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ItemRocketLauncher extends ItemFactoryGun {

	@Override
	protected boolean hasGUI(ItemStack stack) {
		return false;
	}

	@Override
	protected boolean fire(ItemStack stack, World world, EntityPlayer player) {
		int slot = -1;
		Item rocket = MFRThings.rocketItem;
		ItemStack[] mainInventory = player.inventory.mainInventory;
		for (int j = 0, e = mainInventory.length; j < e; ++j)
			if (mainInventory[j] != null && mainInventory[j].getItem() == rocket) {
				slot = j;
				break;
			}
		if (slot > 0) {
			int damage = mainInventory[slot].getItemDamage();
			if (!player.capabilities.isCreativeMode)
				if (--mainInventory[slot].stackSize <= 0)
					mainInventory[slot] = null;

			if (world.isRemote) {
				Packets.sendToServer(Packets.RocketLaunch, player,
						damage == 0 ? MineFactoryReloadedClient.instance.getLockedEntity() : Integer.MIN_VALUE);
			} else if (!player.addedToChunk) {
				EntityRocket r = new EntityRocket(world, player, null);
				world.spawnEntityInWorld(r);
			}
			return true;
		}
		return false;
	}

	@Override
	protected int getDelay(ItemStack stack, boolean fired) {
		return fired ? 100 : 40;
	}

	@Override
	protected String getDelayTag(ItemStack stack) {
		return "mfr:SPAMRLaunched";
	}

}
