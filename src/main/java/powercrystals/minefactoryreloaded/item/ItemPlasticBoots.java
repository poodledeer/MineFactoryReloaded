package powercrystals.minefactoryreloaded.item;

import static cofh.lib.util.helpers.ItemHelper.*;
import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.item.base.ItemFactoryArmor;

public class ItemPlasticBoots extends ItemFactoryArmor {

	public ItemPlasticBoots() {

		super(ItemFactoryArmor.PLASTIC_ARMOR, 3);
		setUnlocalizedName("mfr.plastic.armor.boots");
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {

		l: if (areItemsEqual(this, plasticBootsItem)) {
			if (!areItemsEqual(getItemFromStack(player.getCurrentArmor(3)), plasticHelmetItem)) {
				break l;
			}
			if (!areItemsEqual(getItemFromStack(player.getCurrentArmor(2)), plasticChestplateItem)) {
				break l;
			}
			if (!areItemsEqual(getItemFromStack(player.getCurrentArmor(1)), plasticLeggingsItem)) {
				break l;
			}
			player.removePotionEffect(Potion.poison.id);
			player.removePotionEffect(Potion.confusion.id);
		}
	}

}
