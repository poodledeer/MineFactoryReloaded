package powercrystals.minefactoryreloaded.item.tool;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryTool;

public class ItemStraw extends ItemFactoryTool {

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {

		if (!world.isRemote) {
			MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
			Map<String, ILiquidDrinkHandler> map = MFRRegistry.getLiquidDrinkHandlers();
			if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
				int x = mop.blockX, y = mop.blockY, z = mop.blockZ;
				Block block = world.getBlock(x, y, z);
				Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
				if (fluid != null && map.containsKey(fluid.getName())) {
					map.get(fluid.getName()).onDrink(player);
					world.setBlockToAir(mop.blockX, mop.blockY, mop.blockZ);
				} else if (block.hasTileEntity(world.getBlockMetadata(x, y, z))) {
					TileEntity tile = world.getTileEntity(x, y, z);
					if (tile instanceof IFluidHandler) {
						IFluidHandler handler = (IFluidHandler) tile;
						FluidTankInfo[] info = handler.getTankInfo(ForgeDirection.getOrientation(mop.sideHit));
						for (int i = info.length; i-- > 0;) {
							FluidStack fstack = info[i].fluid;
							if (fstack != null) {
								fluid = fstack.getFluid();
								if (fluid != null && map.containsKey(fluid.getName()) && fstack.amount >= 1000) {
									fstack = fstack.copy();
									fstack.amount = 1000;
									FluidStack r = handler.drain(ForgeDirection.getOrientation(mop.sideHit), fstack.copy(), false);
									if (r != null && r.amount >= 1000) {
										map.get(fluid.getName()).onDrink(player);
										handler.drain(ForgeDirection.getOrientation(mop.sideHit), fstack, true);
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {

		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {

		return EnumAction.drink;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
		Map<String, ?> map = MFRRegistry.getLiquidDrinkHandlers();
		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
			int x = mop.blockX, y = mop.blockY, z = mop.blockZ;
			Block block = world.getBlock(x, y, z);
			Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
			if (fluid != null && map.containsKey(fluid.getName())) {
				player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
			} else if (block.hasTileEntity(world.getBlockMetadata(x, y, z))) {
				TileEntity tile = world.getTileEntity(x, y, z);
				if (tile instanceof IFluidHandler) {
					IFluidHandler handler = (IFluidHandler) tile;
					FluidTankInfo[] info = handler.getTankInfo(ForgeDirection.getOrientation(mop.sideHit));
					for (int i = info.length; i-- > 0;) {
						FluidStack fstack = info[i].fluid;
						if (fstack != null) {
							fluid = fstack.getFluid();
							if (fluid != null && map.containsKey(fluid.getName()) && fstack.amount >= 1000) {
								FluidStack r = handler.drain(ForgeDirection.getOrientation(mop.sideHit), fstack, false);
								if (r != null && r.amount >= 1000) {
									player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
									break;
								}
							}
						}
					}
				}
			}
		}
		return stack;
	}

}
