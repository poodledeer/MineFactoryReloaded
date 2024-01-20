package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.item.base.ItemFactory;

public class ItemMilkBottle extends ItemFactory {

	public ItemMilkBottle() {

		setContainerItem(Items.glass_bottle);
	}

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {

		if (!world.isRemote) {
			player.curePotionEffects(new ItemStack(Items.milk_bucket));
		}

		if (!player.capabilities.isCreativeMode) {
			stack.stackSize--;

			if (stack.stackSize <= 0) {
				return new ItemStack(Items.glass_bottle);
			} else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle))) {
				player.func_146097_a(new ItemStack(Items.glass_bottle), false, true);
			}
		}

		if (stack.stackSize <= 0) {
			stack.stackSize = 0;
		}

		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {

		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {

		return EnumAction.drink;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return stack;
	}

}
