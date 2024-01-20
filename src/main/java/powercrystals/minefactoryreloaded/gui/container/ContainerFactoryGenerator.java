package powercrystals.minefactoryreloaded.gui.container;

import cofh.lib.gui.slot.SlotEnergy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryGenerator;

public class ContainerFactoryGenerator extends ContainerFactoryInventory
{
	public ContainerFactoryGenerator(TileEntityFactoryGenerator tileentity, InventoryPlayer inv)
	{
		super(tileentity, inv);
	}

	@Override
	protected void addSlots()
	{
		addSlotToContainer(new SlotEnergy(_te, 0, 8, 15));
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for(int i = 0; i < crafters.size(); i++)
		{
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 100, ((TileEntityFactoryGenerator)_te).getBuffer());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);
		if(var == 100) ((TileEntityFactoryGenerator)_te).setBuffer(value);
	}
}
