package powercrystals.minefactoryreloaded.net;

import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import powercrystals.minefactoryreloaded.item.ItemPortaSpawner;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class EntityHandler {

	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent evt) {

		if (evt.world.isRemote || !(evt.entity instanceof EntitySkeleton))
			return;
	}

	@SubscribeEvent
	public void onMinecartInteract(MinecartInteractEvent e) {

		if (e.player.worldObj.isRemote)
			return;
		if (!MFRConfig.enableSpawnerCarts.getBoolean(true))
			return;
		if (e.minecart != null && !e.minecart.isDead) {
			ItemStack item = e.player.getCurrentEquippedItem();
			if (item != null && item.getItem().equals(portaSpawnerItem) &
					e.minecart.ridingEntity == null &
					e.minecart.riddenByEntity == null) {
				if (e.minecart.getMinecartType() == 0) {
					if (ItemPortaSpawner.hasData(item)) {
						e.setCanceled(true);
						NBTTagCompound tag = ItemPortaSpawner.getSpawnerTag(item);
						e.player.destroyCurrentEquippedItem();
						e.minecart.writeToNBT(tag);
						e.minecart.setDead();
						EntityMinecartMobSpawner ent = new EntityMinecartMobSpawner(e.minecart.worldObj);
						ent.readFromNBT(tag);
						ent.worldObj.spawnEntityInWorld(ent);
						ent.worldObj.playAuxSFXAtEntity(null, 2004, // particles
							(int) ent.posX, (int) ent.posY, (int) ent.posZ, 0);
					}
				}
				else if (e.minecart.getMinecartType() == 4) {
					// maybe
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemExpire(ItemExpireEvent e) {

		ItemStack stack = e.entityItem.getEntityItem();
		if (stack.getItem().equals(rubberLeavesItem) && stack.getItemDamage() == 0) {
			e.setCanceled(true);
			e.extraLife = 0;
			e.entityItem.age = 0;
			e.entityItem.setEntityItemStack(new ItemStack(stack.getItem(), stack.stackSize, 1));
		}
	}

}
