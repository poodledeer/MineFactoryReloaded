package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankAdv;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityLiquidGenerator;

public class TileEntitySteamTurbine extends TileEntityLiquidGenerator
{
	public static final int liquidConsumedPerTick = Machine.SteamTurbine.getActivationEnergy() / 2;
	public static final int ticksBetweenConsumption = 1;

	public TileEntitySteamTurbine()
	{
		super(Machine.SteamTurbine, liquidConsumedPerTick, ticksBetweenConsumption);
		_tanks[0].setLock(FluidRegistry.getFluid("steam"));
	}
	
	@Override
	protected FluidTankAdv[] createTanks()
	{
		return new FluidTankAdv[] {new FluidTankAdv(BUCKET_VOLUME * 8)};
	}

	@Override
	protected boolean isFluidFuel(FluidStack fuel)
	{
		String name = getFluidName(fuel);
		if (name == null)
			return false;
		return name.equals("steam");
	}
}
