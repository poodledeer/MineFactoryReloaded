package powercrystals.minefactoryreloaded.farmables.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;

import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerMobEssence implements ILiquidDrinkHandler
{
	@Override
	public void onDrink(EntityLivingBase player)
	{
		player.worldObj.spawnEntityInWorld(new EntityXPOrb(player.worldObj,
				player.posX, player.posY, player.posZ,
				player.worldObj.rand.nextInt(6) + 10));
	}
}
