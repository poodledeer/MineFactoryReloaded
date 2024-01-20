package powercrystals.minefactoryreloaded.item.syringe;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ItemSyringeLiquid extends ItemSyringe implements IFluidContainerItem
{
	private boolean _prefix = false;
    @SideOnly(Side.CLIENT)
    protected IIcon fillIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		this.fillIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".fill");
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if (getFluid(stack) != null)
			return getUnlocalizedName() + (_prefix ? ".prefix" : ".suffix");
		return getUnlocalizedName();
	}

	public String getLocalizedName(String str)
	{
		String name = getUnlocalizedName() + "." + str;
		if (StatCollector.canTranslate(name))
			return StatCollector.translateToLocal(name);
		return null;
	}
	@Override
	public int getItemStackLimit(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("fluidName"))
			return 1;
		return maxStackSize;
	}

	@Override
	public String getItemStackDisplayName(ItemStack item)
	{
		String ret = getFluidName(item), t = getLocalizedName(ret);
		if (t != null && !t.isEmpty())
			return EnumChatFormatting.RESET + t + EnumChatFormatting.RESET;
		if (ret == null)
		{
			return super.getItemStackDisplayName(item);
		}
		FluidStack liquid = getFluid(item);
		if (liquid != null)
		{
			ret = liquid.getFluid().getLocalizedName(liquid);
		}
		_prefix = true;
		t = super.getItemStackDisplayName(item);
		_prefix = false;
		t = t != null ? t.trim() : "";
		ret = (t.isEmpty() ? "" : t + " ") + ret;
		t = super.getItemStackDisplayName(item);
		t = t != null ? t.trim() : "";
		ret += t.isEmpty() ? " Syringe" : " " + t;
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int pass) {
		switch (pass)
		{
		case 1:
			return this.fillIcon;
		case 0:
		default:
			return this.itemIcon;
		}
	}

	public String getFluidName(ItemStack container)
	{
		NBTTagCompound tag = container.getTagCompound();
		return tag == null || !tag.hasKey("fluidName") ? null :
							tag.getString("fluidName");
	}

	@Override
	public boolean canInject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		FluidStack fluid = getFluid(syringe);
		return fluid != null && fluid.amount >= getCapacity(syringe);
	}

	@Override
	public boolean inject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		ILiquidDrinkHandler handler = MFRRegistry.getLiquidDrinkHandlers().
				get(getFluidName(syringe));
		if (handler != null)
		{
			handler.onDrink(entity);
			drain(syringe, Integer.MAX_VALUE, true);
			return true;
		}
		return false;
	}

	@Override
	public boolean isEmpty(ItemStack syringe)
	{
		return getFluidName(syringe) == null;
	}

	@Override
	public ItemStack getEmptySyringe(ItemStack syringe)
	{
		return new ItemStack(MFRThings.syringeEmptyItem);
	}

	@Override
	public FluidStack getFluid(ItemStack container)
	{
		NBTTagCompound tag = container.getTagCompound();
		return tag == null || !tag.hasKey("fluid") ? null :
			FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid"));
	}

	@Override
	public int getCapacity(ItemStack container)
	{
		return FluidContainerRegistry.BUCKET_VOLUME;
	}

	@Override
	public int fill(ItemStack stack, FluidStack resource, boolean doFill)
	{
		if (resource == null)
			//|| resource.getFluid().getTemperature(resource) > MELTING_POINT)
			return 0;
		int fillAmount = 0, capacity = getCapacity(stack);
		NBTTagCompound tag = stack.stackTagCompound, fluidTag = null;
		FluidStack fluid = null;
		if (tag == null || !tag.hasKey("fluid") ||
				(fluidTag = tag.getCompoundTag("fluid")) == null ||
				(fluid = FluidStack.loadFluidStackFromNBT(fluidTag)) == null)
			fillAmount = Math.min(capacity, resource.amount);
		if (fluid == null)
		{
			if (doFill)
			{
				fluid = resource.copy();
				fluid.amount = 0;
			}
		}
		else if (!fluid.isFluidEqual(resource))
			return 0;
		else
			fillAmount = Math.min(capacity - fluid.amount, resource.amount);
		fillAmount = Math.max(fillAmount, 0);
		if (doFill)
		{
			if (tag == null)
				tag = stack.stackTagCompound = new NBTTagCompound();
			fluid.amount += fillAmount;
			tag.setTag("fluid", fluid.writeToNBT(fluidTag == null ? new NBTTagCompound() : fluidTag));
			tag.setString("fluidName", fluid.getFluid().getName());
		}
		return fillAmount;
	}

	@Override
	public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain)
	{
		NBTTagCompound tag = stack.stackTagCompound, fluidTag = null;
		FluidStack fluid = null;
		if (tag == null || !tag.hasKey("fluid") ||
			(fluidTag = tag.getCompoundTag("fluid")) == null ||
			(fluid = FluidStack.loadFluidStackFromNBT(fluidTag)) == null)
			return null;
		int drainAmount = Math.min(maxDrain, fluid.amount);
		if (doDrain)
		{
			tag.removeTag("fluid");
			tag.removeTag("uniqifier");
			tag.removeTag("fluidName");
			if (tag.hasNoTags())
				stack.setTagCompound(null);
			fluid.amount -= drainAmount;
			if (fluid.amount > 0)
				fill(stack, fluid, true);
		}
		fluid.amount = drainAmount;
		return fluid;
	}

}
