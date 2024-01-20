package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraftforge.fluids.FluidRegistry;

import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityLiquidFabricator;

public class TileEntityLavaFabricator extends TileEntityLiquidFabricator
{
	public TileEntityLavaFabricator()
	{
		super(FluidRegistry.getFluid("lava"), 20, Machine.LavaFabricator);
	}
}
