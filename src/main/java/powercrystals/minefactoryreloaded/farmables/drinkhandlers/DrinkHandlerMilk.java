package powercrystals.minefactoryreloaded.farmables.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerMilk implements ILiquidDrinkHandler
{
	@Override
	public void onDrink(EntityLivingBase player)
	{
		player.curePotionEffects(new ItemStack(Items.milk_bucket));
	}
}
