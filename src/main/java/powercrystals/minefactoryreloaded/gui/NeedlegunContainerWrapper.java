package powercrystals.minefactoryreloaded.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NeedlegunContainerWrapper implements IInventory
{
	private ItemStack _stack;

	public NeedlegunContainerWrapper(ItemStack stack)
	{
		_stack = stack;
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
	}

	public ItemStack getStack()
	{
		return _stack;
	}

	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		if(_stack.getTagCompound().getCompoundTag("ammo") == null || _stack.getTagCompound().getCompoundTag("ammo").hasNoTags())
		{
			return null;
		}
		else
		{
			return ItemStack.loadItemStackFromNBT(_stack.getTagCompound().getCompoundTag("ammo"));
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if(_stack.getTagCompound().getCompoundTag("ammo") == null || _stack.getTagCompound().getCompoundTag("ammo").hasNoTags())
		{
			return null;
		}
		ItemStack s = ItemStack.loadItemStackFromNBT(_stack.getTagCompound().getCompoundTag("ammo"));
		ItemStack r = s.splitStack(j);
		if(s.stackSize <= 0)
		{
			_stack.getTagCompound().setTag("ammo", new NBTTagCompound());
		}
		else
		{
			NBTTagCompound t = new NBTTagCompound();
			s.writeToNBT(t);
			_stack.getTagCompound().setTag("ammo", t);
		}
		return r;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		if(itemstack == null)
		{
			_stack.getTagCompound().setTag("ammo", new NBTTagCompound());
		}
		else
		{
			NBTTagCompound t = new NBTTagCompound();
			itemstack.writeToNBT(t);
			_stack.getTagCompound().setTag("ammo", t);
		}
	}

	@Override
	public String getInventoryName()
	{
		return "Needlegun";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public void markDirty()
	{
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		return true;
	}

	@Override
	public void openInventory()
	{
	}

	@Override
	public void closeInventory()
	{
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return true;
	}
}
