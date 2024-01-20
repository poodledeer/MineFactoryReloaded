package powercrystals.minefactoryreloaded.farmables.ranchables;

import cofh.lib.inventory.IInventoryManager;
import cofh.lib.inventory.InventoryManager;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.api.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.RanchedItem;

public class RanchableCow implements IFactoryRanchable {

	@Override
	public Class<? extends EntityLivingBase> getRanchableEntity() {

		return EntityCow.class;
	}

	@Override
	public List<RanchedItem> ranch(World world, EntityLivingBase entity, IInventory rancher) {

		NBTTagCompound tag = entity.getEntityData();
		if (tag.getLong("mfr:lastRanched") > world.getTotalWorldTime())
			return null;
		tag.setLong("mfr:lastRanched", world.getTotalWorldTime() + 20 * 30);

		List<RanchedItem> drops = new LinkedList<RanchedItem>();
		IInventoryManager manager = InventoryManager.create(rancher, ForgeDirection.UP);

		int bucketIndex = manager.findItem(new ItemStack(Items.bucket));
		if (bucketIndex >= 0) {
			drops.add(new RanchedItem(Items.milk_bucket));
			rancher.decrStackSize(bucketIndex, 1);
		} else {
			FluidStack milk = FluidRegistry.getFluidStack("milk", FluidContainerRegistry.BUCKET_VOLUME);
			drops.add(new RanchedItem(milk));
		}

		return drops;
	}

}
