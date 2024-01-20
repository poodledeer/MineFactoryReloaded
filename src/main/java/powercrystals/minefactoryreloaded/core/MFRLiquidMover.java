package powercrystals.minefactoryreloaded.core;

import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.position.BlockPosition;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

public abstract class MFRLiquidMover
{
	/**
	 * Attempts to fill tank with the player's current item.
	 * @param	itcb			the tank the liquid is going into
	 * @param	entityplayer	the player trying to fill the tank
	 * @return	True if liquid was transferred to the tank.
	 */
	public static boolean manuallyFillTank(ITankContainerBucketable itcb, EntityPlayer entityplayer)
	{
		ItemStack ci = entityplayer.inventory.getCurrentItem();
		FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(ci);
		if(liquid != null)
		{
			Item item = ci.getItem();
			if(itcb.fill(ForgeDirection.UNKNOWN, liquid, false) == liquid.amount)
			{
				itcb.fill(ForgeDirection.UNKNOWN, liquid, true);
				if(!entityplayer.capabilities.isCreativeMode)
				{
					if (item.hasContainerItem(ci)) {
						ItemStack drop = item.getContainerItem(ci);
						if (drop != null && drop.isItemStackDamageable() && drop.getItemDamage() > drop.getMaxDamage())
							drop = null;
						disposePlayerItem(ci, drop, entityplayer, true);
					} else
						entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, UtilInventory.consumeItem(ci, entityplayer));
					if (!entityplayer.worldObj.isRemote) {
						entityplayer.openContainer.detectAndSendChanges();
						((EntityPlayerMP)entityplayer).sendContainerAndContentsToPlayer(entityplayer.openContainer, entityplayer.openContainer.getInventory());
					}
				}
				return true;
			}
		}
		else if (ci != null && ci.getItem() instanceof IFluidContainerItem)
		{
			Item item = ci.getItem();
			IFluidContainerItem fluidContainer = (IFluidContainerItem)item;
			liquid = fluidContainer.getFluid(ci);
			if (itcb.fill(ForgeDirection.UNKNOWN, liquid, false) > 0) {
				int amount = itcb.fill(ForgeDirection.UNKNOWN, liquid, true);
				ItemStack drop = ci.splitStack(1);
				ci.stackSize++;
				fluidContainer.drain(drop, amount, true);
				if (!entityplayer.capabilities.isCreativeMode) {
					if (item.hasContainerItem(drop)) {
						drop = item.getContainerItem(drop);
						if (drop != null && drop.isItemStackDamageable() && drop.getItemDamage() > drop.getMaxDamage())
							drop = null;
					}
					disposePlayerItem(ci, drop, entityplayer, true);
					if (!entityplayer.worldObj.isRemote) {
						entityplayer.openContainer.detectAndSendChanges();
						((EntityPlayerMP)entityplayer).sendContainerAndContentsToPlayer(entityplayer.openContainer, entityplayer.openContainer.getInventory());
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Attempts to drain tank into the player's current item.
	 * @param	itcb			the tank the liquid is coming from
	 * @param	entityplayer	the player trying to take liquid from the tank
	 * @return	True if liquid was transferred from the tank.
	 */
	public static boolean manuallyDrainTank(ITankContainerBucketable itcb, EntityPlayer entityplayer)
	{
		ItemStack ci = entityplayer.inventory.getCurrentItem();
		boolean isSmartContainer = false;
		IFluidContainerItem fluidContainer;
		if(ci != null && (FluidContainerRegistry.isEmptyContainer(ci) ||
				(isSmartContainer = ci.getItem() instanceof IFluidContainerItem)))
		{
			for(FluidTankInfo tank : itcb.getTankInfo(ForgeDirection.UNKNOWN))
			{
				FluidStack tankLiquid = tank.fluid;
				if (tankLiquid == null || tankLiquid.amount == 0)
					continue;
				ItemStack filledBucket = null;
				FluidStack bucketLiquid = null;
				if (isSmartContainer)
				{
					fluidContainer = (IFluidContainerItem)ci.getItem();
					filledBucket = ci.copy();
					filledBucket.stackSize = 1;
					if (fluidContainer.fill(filledBucket, tankLiquid, false) > 0) {
						int amount = fluidContainer.fill(filledBucket, tankLiquid, true);
						bucketLiquid = new FluidStack(tankLiquid, amount);
						FluidStack l = itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, false);
						if (l == null || l.amount < amount)
							filledBucket = null;
					}
					else
						filledBucket = null;
				}
				else
				{
					filledBucket = FluidContainerRegistry.fillFluidContainer(tankLiquid, ci);
					if(FluidContainerRegistry.isFilledContainer(filledBucket))
					{
						bucketLiquid = FluidContainerRegistry.getFluidForFilledItem(filledBucket);
						FluidStack l = itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, false);
						if (l == null || l.amount < bucketLiquid.amount)
							filledBucket = null;
					}
					else
						filledBucket = null;
				}
				if (filledBucket != null)
				{
					if (disposePlayerItem(ci, filledBucket, entityplayer, MFRConfig.dropFilledContainers.getBoolean(true)))
					{
						if (!entityplayer.worldObj.isRemote) {
							entityplayer.openContainer.detectAndSendChanges();
							((EntityPlayerMP)entityplayer).sendContainerAndContentsToPlayer(entityplayer.openContainer, entityplayer.openContainer.getInventory());
						}
						itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, true);
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean disposePlayerItem(ItemStack stack, ItemStack dropStack, EntityPlayer entityplayer, boolean allowDrop)
	{
		return disposePlayerItem(stack, dropStack, entityplayer, allowDrop, true);
	}

	public static boolean disposePlayerItem(ItemStack stack, ItemStack dropStack,
			EntityPlayer entityplayer, boolean allowDrop, boolean allowReplace)
	{
		if (entityplayer == null || entityplayer.capabilities.isCreativeMode)
			return true;
		if (allowReplace && stack.stackSize <= 1)
		{
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, dropStack);
			return true;
		}
		else if (allowDrop)
		{
			stack.stackSize -= 1;
			if (dropStack != null && !entityplayer.inventory.addItemStackToInventory(dropStack))
			{
				entityplayer.func_146097_a(dropStack, false, true);
			}
			return true;
		}
		return false;
	}

	public static int fillTankWithXP(FluidTankAdv tank, EntityXPOrb orb)
	{
		int maxAmount = tank.getSpace(), maxXP = (int) (maxAmount / 66.66666667f);
		if (maxAmount <= 0)
		{
			return 0;
		}
		int found = Math.min(orb.xpValue, maxXP);
		orb.xpValue -= found;
		if (orb.xpValue <= 0)
		{
			orb.setDead();
			found = Math.max(found, 0);
		}
		if (found > 0)
		{
			found = (int)(found * 66.66666667f);
			tank.fill(FluidRegistry.getFluidStack("mobessence", found), true);
			return found;
		}
		return 0;
	}

	public static void pumpLiquid(IFluidTank iFluidTank, TileEntityFactory from)
	{
		if (iFluidTank != null && iFluidTank.getFluid() != null && iFluidTank.getFluid().amount > 0)
		{
			FluidStack l = iFluidTank.getFluid().copy();
			l.amount = Math.min(l.amount, FluidContainerRegistry.BUCKET_VOLUME);
			for (BlockPosition adj : new BlockPosition(from).getAdjacent(true))
			{
				TileEntity tile = from.getWorldObj().getTileEntity(adj.x, adj.y, adj.z);
				if (tile instanceof IFluidHandler)
				{
					if (!((IFluidHandler)tile).canFill(adj.orientation.getOpposite(), l.getFluid()))
						continue;
					int filled = ((IFluidHandler)tile).fill(adj.orientation.getOpposite(), l, true);
					iFluidTank.drain(filled, true);
					l.amount -= filled;
					if(l.amount <= 0)
					{
						break;
					}
				}
			}
		}
	}

}