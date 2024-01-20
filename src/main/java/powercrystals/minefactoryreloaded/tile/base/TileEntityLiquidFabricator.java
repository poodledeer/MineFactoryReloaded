package powercrystals.minefactoryreloaded.tile.base;

import cofh.core.util.fluid.FluidTankAdv;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class TileEntityLiquidFabricator extends TileEntityFactoryPowered implements ITankContainerBucketable {

	private FluidStack fluid;
	private int _liquidFabPerTick;

	protected TileEntityLiquidFabricator(Fluid liquid, int liquidFabPerTick, Machine machine) {
		super(machine, machine.getActivationEnergy() * liquidFabPerTick);
		_liquidFabPerTick = liquidFabPerTick;
		if (liquid != null)
			fluid = new FluidStack(liquid, liquidFabPerTick);
		else
			fluid = null;
	}

	@Override
	protected boolean activateMachine() {
		if (fluid != null && _tanks[0].getSpace() >= _liquidFabPerTick)
			if (_tanks[0].fill(fluid, true) > 0)
				return true;

		setIdleTicks(getIdleTicksMax());
		return false;
	}

	@Override
	public int getWorkMax() {
		return 0;
	}

	@Override
	public int getIdleTicksMax() {
		return 200;
	}

	@Override
	protected boolean shouldPumpLiquid() {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	protected FluidTankAdv[] createTanks() {
		return new FluidTankAdv[] {new FluidTankAdv(BUCKET_VOLUME)};
	}

	@Override
	public boolean allowBucketDrain(ItemStack stack) {
		return true;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return drain(resource, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {
		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer) {
		return new ContainerFactoryPowered(this, inventoryPlayer);
	}
}
