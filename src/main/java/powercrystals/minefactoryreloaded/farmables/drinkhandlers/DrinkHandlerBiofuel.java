package powercrystals.minefactoryreloaded.farmables.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerBiofuel implements ILiquidDrinkHandler
{
	@Override
	public void onDrink(EntityLivingBase player)
	{
		player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 40 * 20, 0));
		player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 40 * 20, 0));
	}
}
