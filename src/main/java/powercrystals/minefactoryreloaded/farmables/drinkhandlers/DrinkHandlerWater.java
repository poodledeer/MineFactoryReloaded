package powercrystals.minefactoryreloaded.farmables.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerWater implements ILiquidDrinkHandler
{
	@Override
	public void onDrink(EntityLivingBase player)
	{
		player.extinguish();
		NBTTagCompound tag = player.getEntityData();
		World world = player.worldObj;
		if (tag.hasKey("drankLavaTime") && (world.getTotalWorldTime() - tag.getLong("drankLavaTime")) < 100)
		{
			//{
			EntityItem entityitem = player.entityDropItem(new ItemStack(Blocks.obsidian), player.getEyeHeight());
            float f = 0.3F;
            entityitem.motionX = -MathHelper.sin(player.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float)Math.PI) * f;
            entityitem.motionZ = MathHelper.cos(player.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float)Math.PI) * f;
            entityitem.motionY = -MathHelper.sin(player.rotationPitch / 180.0F * (float)Math.PI) * f + 0.1F;
            f = 0.02F;
            float f1 = player.getRNG().nextFloat() * (float)Math.PI * 2.0F;
            f *= player.getRNG().nextFloat();
            entityitem.motionX += Math.cos(f1) * f;
            entityitem.motionY += (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.1F;
            entityitem.motionZ += Math.sin(f1) * f;
            //}
			tag.setLong("drankLavaTime", -100);
			world.playSoundAtEntity(player, "random.burp", 1.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		}
	}
}
