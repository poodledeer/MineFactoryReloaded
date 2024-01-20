package powercrystals.minefactoryreloaded.farmables.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerLava implements ILiquidDrinkHandler
{
	@Override
	public void onDrink(EntityLivingBase player)
	{
		player.attackEntityFrom(new InternalLavaDamage(), 7);
		player.setFire(30);
		NBTTagCompound tag = player.getEntityData();
		tag.setLong("drankLavaTime", player.worldObj.getTotalWorldTime());
	}
	
	protected class InternalLavaDamage extends DamageSource
	{
		public InternalLavaDamage()
		{
			super(DamageSource.lava.damageType);
			this.setDamageBypassesArmor();
			this.setFireDamage();
			this.setDifficultyScaled();
		}
	}
}
