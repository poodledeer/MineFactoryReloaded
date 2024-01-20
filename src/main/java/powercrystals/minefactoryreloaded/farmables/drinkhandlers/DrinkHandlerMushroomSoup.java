package powercrystals.minefactoryreloaded.farmables.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerMushroomSoup implements ILiquidDrinkHandler
{
	@Override
	public void onDrink(EntityLivingBase player)
	{
		player.heal(4);
		player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, 5 * 20, 1));
		player.addPotionEffect(new PotionEffect(Potion.field_76443_y.id, 15 * 20, 2));
	}
}
