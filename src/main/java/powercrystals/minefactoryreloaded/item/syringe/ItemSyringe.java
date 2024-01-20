package powercrystals.minefactoryreloaded.item.syringe;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.api.ISyringe;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public abstract class ItemSyringe extends ItemFactory implements ISyringe
{
	public ItemSyringe()
	{
		setMaxStackSize(1);
	}

	@Override
	public boolean isFull3D()
	{
		return true;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack s, EntityPlayer player, EntityLivingBase e)
	{
		if (!e.worldObj.isRemote && canInject(e.worldObj, e, s))
		{
			if (inject(e.worldObj, e, s))
			{
				s.func_150996_a(MFRThings.syringeEmptyItem);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isEmpty(ItemStack syringe)
	{
		return false;
	}

	@Override
	public ItemStack getEmptySyringe(ItemStack syringe)
	{
		return new ItemStack(MFRThings.syringeEmptyItem);
	}
}
