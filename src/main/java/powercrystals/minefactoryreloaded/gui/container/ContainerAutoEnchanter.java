package powercrystals.minefactoryreloaded.gui.container;

import cofh.lib.gui.slot.SlotAcceptInsertable;
import cofh.lib.gui.slot.SlotRemoveOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoEnchanter;

public class ContainerAutoEnchanter extends ContainerFactoryPowered {

	private TileEntityAutoEnchanter _enchanter;

	public ContainerAutoEnchanter(TileEntityAutoEnchanter enchanter, InventoryPlayer inv) {

		super(enchanter, inv);

		_enchanter = enchanter;
	}

	@Override
	protected void addSlots() {

		addSlotToContainer(new SlotAcceptInsertable(_te, 0, 8, 24));
		addSlotToContainer(new SlotRemoveOnly(_te, 1, 8, 54));
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();
		for (int i = 0; i < crafters.size(); i++) {
			((ICrafting) crafters.get(i)).sendProgressBarUpdate(this, 100, _enchanter.getTargetLevel());
		}
	}

	@Override
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);
		if (var == 100) _enchanter.setTargetLevel(value);
	}

}
