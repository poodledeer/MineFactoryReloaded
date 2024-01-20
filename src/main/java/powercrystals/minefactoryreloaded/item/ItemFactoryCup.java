package powercrystals.minefactoryreloaded.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.IAdvFluidContainerItem;
import powercrystals.minefactoryreloaded.core.IUseHandler;
import powercrystals.minefactoryreloaded.core.IUseable;
import powercrystals.minefactoryreloaded.farmables.usehandlers.DefaultUseHandler;
import powercrystals.minefactoryreloaded.farmables.usehandlers.DrinkUseHandler;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;

public class ItemFactoryCup extends ItemFactory implements IAdvFluidContainerItem, IUseable {

	public final static int MELTING_POINT = 523; // melting point of Polyethylene terphthalate
	public final static IUseHandler defaultUseAction = new DefaultUseHandler();
	public final static IUseHandler drinkUseAction = new DrinkUseHandler();

	private boolean _prefix = false;
	@SideOnly(Side.CLIENT)
	protected IIcon fillIcon;
	protected List<IUseHandler> useHandlers;

	public ItemFactoryCup(int stackSize, int maxUses) {
		this.setMaxStackSize(stackSize);
		this.setMaxDamage(maxUses);
		this.setHasSubtypes(true);
		useHandlers = new LinkedList<IUseHandler>();
		useHandlers.add(defaultUseAction);
		useHandlers.add(drinkUseAction);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("fluid"))
			return 1;
		return maxStackSize;
	}

	@Override
	public boolean addUseHandler(IUseHandler handler) {
		return useHandlers.add(handler);
	}

	@Override
	public boolean removeUseHandler(IUseHandler handler) {
		return useHandlers.remove(handler);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (getFluid(stack) != null)
			return getUnlocalizedName() + (_prefix ? ".prefix" : ".suffix");
		return getUnlocalizedName();
	}

	public String getLocalizedName(String str) {
		String name = getUnlocalizedName() + "." + str;
		if (StatCollector.canTranslate(name))
			return StatCollector.translateToLocal(name);
		return null;
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		String ret = getFluidName(item), t = getLocalizedName(ret);
		if (t != null && !t.isEmpty())
			return EnumChatFormatting.RESET + t + EnumChatFormatting.RESET;
		if (ret == null) {
			return super.getItemStackDisplayName(item);
		}
		FluidStack liquid = getFluid(item);
		if (liquid != null) {
			ret = liquid.getFluid().getLocalizedName(liquid);
		}
		_prefix = true;
		t = super.getItemStackDisplayName(item);
		_prefix = false;
		t = t != null ? t.trim() : "";
		ret = (t.isEmpty() ? "" : t + " ") + ret;
		t = super.getItemStackDisplayName(item);
		t = t != null ? t.trim() : "";
		ret += t.isEmpty() ? " Cup" : " " + t;
		return ret;
	}

	public String getFluidName(ItemStack stack) {
		NBTTagCompound tag = stack.stackTagCompound;
		return tag == null || !tag.hasKey("fluid") ? null : tag.getCompoundTag("fluid").getString("FluidName");
	}

	@Override
	public FluidStack getFluid(ItemStack stack) {
		NBTTagCompound tag = stack.stackTagCompound;
		FluidStack fluid = null;
		if (tag != null && tag.hasKey("fluid")) {
			fluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid"));
			if (fluid == null)
				tag.removeTag("fluid");
		}
		return fluid;
	}

	@Override
	public int getCapacity(ItemStack container) {
		return FluidContainerRegistry.BUCKET_VOLUME;
	}

	@Override
	public int fill(ItemStack stack, FluidStack resource, boolean doFill) {
		if (resource == null || stack.stackSize != 1)
			//|| resource.getFluid().getTemperature(resource) > MELTING_POINT)
			return 0;
		int fillAmount = 0, capacity = getCapacity(stack);
		NBTTagCompound tag = stack.stackTagCompound, fluidTag = null;
		FluidStack fluid = null;
		if (tag == null || !tag.hasKey("fluid") ||
				(fluidTag = tag.getCompoundTag("fluid")) == null ||
				(fluid = FluidStack.loadFluidStackFromNBT(fluidTag)) == null)
			fillAmount = Math.min(capacity, resource.amount);
		if (fluid == null) {
			if (doFill) {
				fluid = resource.copy();
				fluid.amount = 0;
			}
		} else if (!fluid.isFluidEqual(resource))
			return 0;
		else
			fillAmount = Math.min(capacity - fluid.amount, resource.amount);
		fillAmount = Math.max(fillAmount, 0);
		if (doFill) {
			if (tag == null)
				tag = stack.stackTagCompound = new NBTTagCompound();
			fluid.amount += fillAmount;
			tag.setTag("fluid", fluid.writeToNBT(fluidTag == null ? new NBTTagCompound() : fluidTag));
		}
		return fillAmount;
	}

	@Override
	public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
		NBTTagCompound tag = stack.stackTagCompound, fluidTag = null;
		FluidStack fluid = null;
		if (tag == null || !tag.hasKey("fluid") ||
				(fluidTag = tag.getCompoundTag("fluid")) == null ||
				(fluid = FluidStack.loadFluidStackFromNBT(fluidTag)) == null)
			return null;
		int drainAmount = Math.min(maxDrain, fluid.amount);
		if (doDrain) {
			tag.removeTag("fluid");
			tag.setBoolean("drained", true);
			fluid.amount -= drainAmount;
			if (fluid.amount > 0)
				fill(stack, fluid, true);
			if (tag.hasKey("toDrain")) {
				drainAmount = tag.getInteger("toDrain");
				tag.removeTag("toDrain");
			} else
				drainAmount *= (Math.max(Math.random() - 0.75, 0) + 0.75);
		} else {
			drainAmount *= (Math.max(Math.random() - 0.75, 0) + 0.75);
			tag.setInteger("toDrain", drainAmount);
		}
		fluid.amount = drainAmount;
		return fluid;
	}

	@Override
	public Item getContainerItem() {
		return this;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		if (stack.stackSize <= 0)
			return null;
		ItemStack r = stack.copy();
		NBTTagCompound tag = r.getTagCompound();
		if (tag != null) {
			if (tag.hasKey("drained")) {
				r.stackSize = 1;
				r.attemptDamageItem(1, itemRand);
			}
			tag.removeTag("drained");
			tag.removeTag("fluid");
			tag.removeTag("toDrain");
			if (tag.hasNoTags())
				r.setTagCompound(null);
		}
		return r;
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return tag != null && (tag.hasKey("fluid") || tag.hasKey("drained"));
	}

	public boolean hasDrinkableLiquid(ItemStack stack) {
		return stack.stackSize == 1 &&
				MFRRegistry.getLiquidDrinkHandlers().containsKey(getFluidName(stack)) &&
				getFluid(stack).amount == getCapacity(stack);
	}

	@Override
	public ItemStack onEaten(ItemStack item, World world, EntityPlayer entity) {
		for (IUseHandler handler : useHandlers)
			if (handler.isUsable(item))
				return handler.onUse(item, entity);
		return item;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack item) {
		for (IUseHandler handler : useHandlers)
			if (handler.isUsable(item))
				return handler.useAction(item);
		return EnumAction.none;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack item) {
		for (IUseHandler handler : useHandlers)
			if (handler.isUsable(item))
				return handler.getMaxUseDuration(item);
		return 0;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer entity) {
		for (IUseHandler handler : useHandlers)
			if (handler.canUse(item, entity))
				return handler.onTryUse(item, world, entity);
		return item;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		this.fillIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".fill");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int pass) {
		switch (pass) {
		case 1:
			return this.fillIcon;
		case 0:
		default:
			return this.itemIcon;
		}
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
		if (armorType == 0)
			if (entity instanceof EntityPlayer &&
					((EntityPlayer)entity).getCommandSenderName().equalsIgnoreCase("Eyamaz"))
				return true;

		return false;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return MineFactoryReloadedCore.textureFolder + "armor/plastic_layer_1.png";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
		if (armorSlot == 0) {
			return null; // TODO
		}
		return null;
	}

	@Override
	public boolean canBeFilledFromWorld() {
		return true;
	}

	@Override
	public boolean canPlaceInWorld() {
		return false;
	}

	@Override
	public boolean shouldReplaceWhenFilled() {
		return true;
	}

	@Override
	public MovingObjectPosition rayTrace(World world, EntityLivingBase entity, boolean adjacent) {
		float f1 = entity.rotationPitch;
		float f2 = entity.rotationYaw;
		double y = entity.posY + entity.getEyeHeight() - entity.yOffset;
		Vec3 vec3 = Vec3.createVectorHelper(entity.posX, y, entity.posZ);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = 5.0D;
		if (entity instanceof EntityPlayerMP) {
			d3 = ((EntityPlayerMP)entity).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
		MovingObjectPosition ret = world.func_147447_a(vec3, vec31, adjacent, !adjacent, false);
		if (ret != null && adjacent) {
			ForgeDirection side = ForgeDirection.getOrientation(ret.sideHit);
			ret.blockX += side.offsetX;
			ret.blockY += side.offsetY;
			ret.blockZ += side.offsetZ;
		}
		return ret;
	}

}
