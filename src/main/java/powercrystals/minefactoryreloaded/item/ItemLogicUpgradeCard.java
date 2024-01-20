package powercrystals.minefactoryreloaded.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.item.base.ItemMulti;

public class ItemLogicUpgradeCard extends ItemMulti {

	private static String[] _upgradeNames = { "100", "300", "500" };

	public ItemLogicUpgradeCard() {

		setNames(_upgradeNames);
		setHasIcons(false);
	}

	@Override
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {

		super.addInfo(stack, player, infoList, advancedTooltips);
		infoList.add("Circuits: " + getCircuitsForLevel(stack.getItemDamage() + 1));
		infoList.add("Variables: " + getVariablesForLevel(stack.getItemDamage() + 1));
	}

	public static int getCircuitsForLevel(int level) {

		return level == 0 ? 0 : 1 + 2 * (level - 1);
	}

	public static int getVariablesForLevel(int level) {

		return level == 0 ? 0 : 8 * level;
	}

}
