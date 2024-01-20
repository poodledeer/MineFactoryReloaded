package powercrystals.minefactoryreloaded.item.gun;

import cofh.lib.util.helpers.ItemHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.entity.EntityNeedle;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryGun;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ItemNeedleGun extends ItemFactoryGun {

	@Override
	protected boolean hasGUI(ItemStack stack) {
		return true;
	}

	@Override
	protected boolean openGUI(ItemStack stack, World world, EntityPlayer player) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag("ammo");
		boolean needsAmmo = tag == null || tag.hasNoTags() || player.isSneaking();
		if (needsAmmo & !world.isRemote)
			player.openGui(MineFactoryReloadedCore.instance(), 1, world, 0, 0, 0);

		return needsAmmo;
	}

	@Override
	protected boolean fire(ItemStack stack, World world, EntityPlayer player) {
		ItemStack ammo = ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("ammo"));
		boolean reloaded = false, creative = player.capabilities.isCreativeMode;

		if (!world.isRemote) {
			float spread = 1f;
			if (MFRRegistry.getNeedleAmmoTypes().containsKey(ammo.getItem()))
				spread = MFRRegistry.getNeedleAmmoTypes().get(ammo.getItem()).getSpread(ammo);
			EntityNeedle needle = new EntityNeedle(world, player, ammo, spread);
			world.spawnEntityInWorld(needle);
			world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 2.0F);
		}

		NBTTagCompound t = new NBTTagCompound();
		if (!creative) {
			ammo.setItemDamage(ammo.getItemDamage() + 1);
		}

		if (ammo.getItemDamage() <= ammo.getMaxDamage()) {
			ammo.writeToNBT(t);
		} else {
			ItemStack[] inv = player.inventory.mainInventory;
			for (int i = 0, e = inv.length; i < e; ++i) {
				ItemStack item = inv[i];
				if (item != null && ammo.getItem().equals(item.getItem())) {
					ammo = ItemHelper.cloneStack(item, 1);
					ammo.writeToNBT(t);
					if (!creative) inv[i] = ItemHelper.consumeItem(item);
					reloaded = true;
					break;
				}
			}

			if (!(world.isRemote | creative)) {
				UtilInventory.dropStackInAir(player.worldObj, player, new ItemStack(MFRThings.needlegunAmmoEmptyItem, 1), 5);
			}
		}
		stack.setTagInfo("ammo", t);
		return reloaded;
	}

	@Override
	protected int getDelay(ItemStack stack, boolean fired) {
		return 5 + (fired ? 20 : 0);
	}

	@Override
	protected String getDelayTag(ItemStack stack) {
		return "mfr:NeedleLaunched";
	}

}
