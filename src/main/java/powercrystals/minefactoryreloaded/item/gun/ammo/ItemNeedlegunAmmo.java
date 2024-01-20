package powercrystals.minefactoryreloaded.item.gun.ammo;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.api.INeedleAmmo;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;

public abstract class ItemNeedlegunAmmo extends ItemFactory implements INeedleAmmo {

	public ItemNeedlegunAmmo() {
		setHasSubtypes(false);
	}

	@Override
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {
		super.addInfo(stack, player, infoList, advancedTooltips);
		infoList.add(String.format(MFRUtil.localize("tip.info.mfr.needlegun.ammo", true),
				(stack.getMaxDamage() - stack.getItemDamage() + 1)));
	}

}
