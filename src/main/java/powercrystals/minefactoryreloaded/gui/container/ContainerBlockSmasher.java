package powercrystals.minefactoryreloaded.gui.container;

import cofh.lib.gui.slot.SlotAcceptValid;
import cofh.lib.gui.slot.SlotRemoveOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import powercrystals.minefactoryreloaded.tile.machine.TileEntityBlockSmasher;

public class ContainerBlockSmasher extends ContainerFactoryPowered {

	private TileEntityBlockSmasher _smasher;

	public ContainerBlockSmasher(TileEntityBlockSmasher te, InventoryPlayer inv) {

		super(te, inv);
		_smasher = te;
	}

	@Override
	protected void addSlots() {

		addSlotToContainer(new SlotAcceptValid(_te, 0, 8, 24));
		addSlotToContainer(new SlotRemoveOnly(_te, 1, 8, 54));
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();
		for (int i = 0; i < crafters.size(); i++) {
			((ICrafting) crafters.get(i)).sendProgressBarUpdate(this, 100, _smasher.getFortune());
		}
	}

	@Override
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);
		if (var == 100) _smasher.setFortune(value);
	}
}
