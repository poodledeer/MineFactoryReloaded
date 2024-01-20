package powercrystals.minefactoryreloaded.core;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.IFluidHandler;
/**
 * @author Emy
 *
 * Extends the IFluidHandler interface to allow manual draining/filling via buckets.
 * 
 * what am I even doing here
 */
public interface ITankContainerBucketable extends IFluidHandler
{
	/**
	 * Called to determine if the IFluidHandler should be filled by buckets.
	 * @return True if the IFluidHandler is allowed to be filled manually (with buckets)
	 */
	public boolean allowBucketFill(ItemStack stack);
	
	/**
	 * Called to determine if the IFluidHandler should be drained by buckets.
	 * @return True if the IFluidHandler is allowed to be drained manually (with buckets)
	 */
	public boolean allowBucketDrain(ItemStack stack);
}