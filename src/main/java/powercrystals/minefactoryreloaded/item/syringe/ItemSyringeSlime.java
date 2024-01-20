package powercrystals.minefactoryreloaded.item.syringe;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSyringeSlime extends ItemSyringe
{
	@Override
	public boolean canInject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		return entity instanceof EntitySlime && ((EntitySlime)entity).getSlimeSize() < 8;
	}
	
	@Override
	public boolean inject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		EntitySlime slime = (EntitySlime)entity;
		slime.setSlimeSize(slime.getSlimeSize() << 1);
		return true;
	}
	
}
