package powercrystals.minefactoryreloaded.item;

import cofh.api.item.IAugmentItem;
import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Set;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.base.ItemMulti;

public class ItemUpgrade extends ItemMulti implements IAugmentItem {

	private static Set<String> types = ImmutableSet.of("radius");
	public static IIcon background;

	public ItemUpgrade() {

		setNames(new String[] { "lapis", "tin", "iron", "copper", "bronze", "silver", "gold", "quartz", "diamond", "platinum", "emerald", "cobble" });
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister ir) {

		super.registerIcons(ir);
		background = ir.registerIcon("minefactoryreloaded:gui/" + getUnlocalizedName());
	}

	@Override
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {

		super.addInfo(stack, player, infoList, advancedTooltips);
		infoList.add(String.format(MFRUtil.localize("tip.info.mfr.upgrade.radius", true), getAugmentLevel(stack, "radius")));
	}

	@Override
	public int getAugmentLevel(ItemStack stack, String type) {

		if (type.equals("radius")) {
			int dmg = stack.getItemDamage();
			switch (dmg) {
			case 11:
				return -1;
			default:
				return dmg + 1;
			}
		}
		return 0;
	}

	@Override
	public Set<String> getAugmentTypes(ItemStack stack) {

		int dmg = stack.getItemDamage();
		switch (dmg) {
		default:
			return types;
		}
	}

}
