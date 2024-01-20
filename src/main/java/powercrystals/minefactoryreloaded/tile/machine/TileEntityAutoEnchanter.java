package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankAdv;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.core.AutoEnchantmentHelper;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiAutoEnchanter;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoEnchanter;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityAutoEnchanter extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private Random _rand;
	private int _targetLevel;


	public TileEntityAutoEnchanter()
	{
		super(Machine.AutoEnchanter);
		_rand = new Random();

		_targetLevel = 30;
		setManageSolids(true);
		_tanks[0].setLock(FluidRegistry.getFluid("mobessence"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiAutoEnchanter(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerAutoEnchanter getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerAutoEnchanter(this, inventoryPlayer);
	}

	@Override
	public int getWorkMax()
	{
		if(_inventory[0] != null && _inventory[0].getItem().equals(Items.glass_bottle))
		{
			return 250;
		}
		return (_targetLevel + (int)(Math.pow((_targetLevel) / 7.5, 4) * 10 * getEnchantmentMultiplier()));
	}

	@SuppressWarnings("unchecked")
	private double getEnchantmentMultiplier()
	{
		ItemStack s = _inventory[0];
		if(s == null)
		{
			return 1;
		}

		Map<Integer, Integer> enchantments = AutoEnchantmentHelper.getEnchantments(s);
		if(enchantments == null || enchantments.size() == 0)
		{
			return 1;
		}

		return Math.pow(enchantments.size() + 1.0, 2);
	}

	@Override
	public int getIdleTicksMax()
	{
		return 1;
	}

	public int getTargetLevel()
	{
		return _targetLevel;
	}

	public void setTargetLevel(int targetLevel)
	{
		_targetLevel = targetLevel;
		if(_targetLevel > 30) _targetLevel = 30;
		if(_targetLevel < 1) _targetLevel = 1;
		if(getWorkDone() >= getWorkMax())
		{
			activateMachine();
		}
	}

	@Override
	protected boolean activateMachine()
	{
		if (worldObj.isRemote)
		{
			return false;
		}
		ItemStack input = _inventory[0];
		ItemStack output = _inventory[1];
		if(input == null)
		{
			setWorkDone(0);
			return false;
		}
		if (input.stackSize <= 0)
		{
			setInventorySlotContents(0, null);
			setWorkDone(0);
			return false;
		}
		if (output != null)
		{
			if (output.stackSize >= output.getMaxStackSize() || output.stackSize >= getInventoryStackLimit())
			{
				setWorkDone(0);
				return false;
			}
			if (output.stackSize <= 0)
			{
				setInventorySlotContents(1, null);
				output = null;
			}
		}
		if ((input.getItem().getItemEnchantability(input) == 0 &&
				!input.getItem().equals(Items.glass_bottle)) ||
				input.getItem().equals(Items.enchanted_book))
		{
			if (output == null)
			{
				_inventory[0] = null;
				setInventorySlotContents(1, input);
			}
			else if (input.isItemEqual(output) && ItemStack.areItemStackTagsEqual(input, output))
			{
				int amountToCopy = Math.min(output.getMaxStackSize() - output.stackSize, input.stackSize);
				amountToCopy = Math.min(getInventoryStackLimit() - output.stackSize, amountToCopy);
				if (amountToCopy <= 0)
				{
					setWorkDone(0);
					return false;
				}
				output.stackSize += amountToCopy;
				input.stackSize -= amountToCopy;
				if (input.stackSize <= 0)
				{
					setInventorySlotContents(0, null);
				}
			}
			else
			{
				setWorkDone(0);
				return false;
			}
			setWorkDone(0);
			return true;
		}
		else if (getWorkDone() >= getWorkMax())
		{
			if (input.getItem().equals(Items.glass_bottle))
			{
				if (output == null)
				{
					output = new ItemStack(Items.experience_bottle, 0, 0);
				}
				if (!output.getItem().equals(Items.experience_bottle))
				{
					setWorkDone(0);
					return false;
				}
				if (--input.stackSize <= 0)
				{
					_inventory[0] = null;
				}
				output.stackSize++;
				setInventorySlotContents(1, output);
				setWorkDone(0);
			}
			else if (output == null)
			{
				output = AutoEnchantmentHelper.addRandomEnchantment(this._rand, input, _targetLevel);
				if (input.stackSize <= 0)
				{
					_inventory[0] = null;
				}
				setInventorySlotContents(1, output);
				setWorkDone(0);
			}
			else
			{
				return false;
			}
			return true;
		}
		else if (drain(_tanks[0], 4, false) == 4)
		{
			if (!incrementWorkDone()) return false;
			drain(_tanks[0], 4, true);
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public int getSizeInventory()
	{
		return 2;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return 2;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack input, int sideordinal)
	{
		if(slot == 0)
		{
			ItemStack contents = _inventory[0];
			// TODO: limit input to glass bottles and items with an enchantability > 0
			return contents == null || (contents.stackSize < getInventoryStackLimit() &&
					input.isItemEqual(contents) && ItemStack.areItemStackTagsEqual(input, contents));
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal)
	{
		if(slot == 1) return true;
		return false;
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setInteger("targetLevel", _targetLevel);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		setTargetLevel(tag.getInteger("targetLevel"));
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag)
	{
		super.writeItemNBT(tag);
		if (_targetLevel != 30)
			tag.setInteger("targetLevel", _targetLevel);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		_targetLevel = tag.getInteger("targetLevel");
	}

	@Override
	public boolean allowBucketFill(ItemStack stack)
	{
		return true;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		return drain(resource, doDrain);
	}

	@Override
	protected FluidTankAdv[] createTanks()
	{
		return new FluidTankAdv[]{new FluidTankAdv(4 * BUCKET_VOLUME)};
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return false;
	}
}
