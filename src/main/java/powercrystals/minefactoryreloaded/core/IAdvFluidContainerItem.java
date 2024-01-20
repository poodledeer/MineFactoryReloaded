package powercrystals.minefactoryreloaded.core;

import net.minecraftforge.fluids.IFluidContainerItem;

public interface IAdvFluidContainerItem extends IFluidContainerItem
{
	public boolean canBeFilledFromWorld();
	public boolean canPlaceInWorld();
	public boolean shouldReplaceWhenFilled();
}
