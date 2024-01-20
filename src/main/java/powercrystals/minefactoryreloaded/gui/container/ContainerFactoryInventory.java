package powercrystals.minefactoryreloaded.gui.container;

import cofh.core.util.CoreUtils;
import cofh.lib.gui.container.ContainerBase;
import cofh.lib.gui.slot.SlotAcceptValid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class ContainerFactoryInventory extends ContainerBase {

	protected TileEntityFactoryInventory _te;

	private int _tankAmount;
	private int _tankIndex;
	public boolean drops, redstone;

	public ContainerFactoryInventory(TileEntityFactoryInventory tileentity, InventoryPlayer inv) {

		_te = tileentity;
		if (_te.getSizeInventory() > 0) {
			addSlots();
		}
		bindPlayerInventory(inv);
	}

	protected void addSlots() {

		addSlotToContainer(new SlotAcceptValid(_te, 0, 8, 15));
		addSlotToContainer(new SlotAcceptValid(_te, 1, 26, 15));
		addSlotToContainer(new SlotAcceptValid(_te, 2, 44, 15));
		addSlotToContainer(new SlotAcceptValid(_te, 3, 8, 33));
		addSlotToContainer(new SlotAcceptValid(_te, 4, 26, 33));
		addSlotToContainer(new SlotAcceptValid(_te, 5, 44, 33));
		addSlotToContainer(new SlotAcceptValid(_te, 6, 8, 51));
		addSlotToContainer(new SlotAcceptValid(_te, 7, 26, 51));
		addSlotToContainer(new SlotAcceptValid(_te, 8, 44, 51));
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();

		FluidTankInfo[] tank = _te.getTankInfo(ForgeDirection.UNKNOWN);
		int n = tank.length;
		for (int i = 0; i < crafters.size(); i++) {
			((ICrafting) crafters.get(i)).sendProgressBarUpdate(this, 33, (_te.hasDrops() ? 1 : 0) |
					(CoreUtils.isRedstonePowered(_te) ? 2 : 0));
			for (int j = n; j-- > 0;) {
				((ICrafting) crafters.get(i)).sendProgressBarUpdate(this, 30, j);
				if (tank[j] != null && tank[j].fluid != null) {
					((ICrafting) crafters.get(i)).sendProgressBarUpdate(this, 31, tank[j].fluid.amount);
					((ICrafting) crafters.get(i)).sendProgressBarUpdate(this, 32, tank[j].fluid.getFluid().getID());
				} else if (tank[j] != null) {
					((ICrafting) crafters.get(i)).sendProgressBarUpdate(this, 31, 0);
					((ICrafting) crafters.get(i)).sendProgressBarUpdate(this, 32, 0);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);

		if (var == 30)
			_tankIndex = value;
		else if (var == 31)
			_tankAmount = value;
		else if (var == 32) {
			Fluid fluid = FluidRegistry.getFluid(value);
			if (fluid == null) {
				_te.getTanks()[_tankIndex].setFluid(null);
			} else {
				_te.getTanks()[_tankIndex].setFluid(new FluidStack(fluid, _tankAmount));
			}
		} else if (var == 33) {
			drops = (value & 1) != 0;
			redstone = (value & 2) != 0;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return !_te.isInvalid() && _te.isUseableByPlayer(player);
	}

	@Override
	protected int getSizeInventory() {

		return _te.getSizeInventory();
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 84;
	}

}
