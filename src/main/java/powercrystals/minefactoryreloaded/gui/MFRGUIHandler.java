package powercrystals.minefactoryreloaded.gui;

import cpw.mods.fml.common.network.IGuiHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.gui.client.GUIBag;
import powercrystals.minefactoryreloaded.gui.client.GuiNeedlegun;
import powercrystals.minefactoryreloaded.gui.client.GuiRedNetLogic;
import powercrystals.minefactoryreloaded.gui.container.ContainerBag;
import powercrystals.minefactoryreloaded.gui.container.ContainerNeedlegun;
import powercrystals.minefactoryreloaded.gui.container.ContainerRedNetLogic;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

public class MFRGUIHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 0)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te instanceof TileEntityFactory)
			{
				return ((TileEntityFactory)te).getContainer(player.inventory);
			}
			else if(te instanceof TileEntityRedNetLogic)
			{
				return new ContainerRedNetLogic((TileEntityRedNetLogic)te);
			}
		}
		else if(ID == 1)
		{
			if(player.getCurrentEquippedItem() != null &&
					player.getCurrentEquippedItem().getItem().equals(MFRThings.needlegunItem))
			{
				return new ContainerNeedlegun(new NeedlegunContainerWrapper(player.getCurrentEquippedItem()), player.inventory);
			}
		}
		else if(ID == 2)
		{
			if(player.getCurrentEquippedItem() != null &&
					player.getCurrentEquippedItem().getItem().equals(MFRThings.plasticBagItem))
			{
				return new ContainerBag(player.getCurrentEquippedItem(), player.inventory);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 0)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te instanceof TileEntityFactory)
			{
				return ((TileEntityFactory)te).getGui(player.inventory);
			}
			else if(te instanceof TileEntityRedNetLogic)
			{
				return new GuiRedNetLogic(new ContainerRedNetLogic((TileEntityRedNetLogic)te), (TileEntityRedNetLogic)te);
			}
		}
		else if(ID == 1)
		{
			if(player.getCurrentEquippedItem() != null &&
					player.getCurrentEquippedItem().getItem().equals(MFRThings.needlegunItem))
			{
				return new GuiNeedlegun(new ContainerNeedlegun(new NeedlegunContainerWrapper(player.getCurrentEquippedItem()), player.inventory), player.getCurrentEquippedItem());
			}
		}
		else if(ID == 2)
		{
			if(player.getCurrentEquippedItem() != null &&
					player.getCurrentEquippedItem().getItem().equals(MFRThings.plasticBagItem))
			{
				return new GUIBag(new ContainerBag(player.getCurrentEquippedItem(), player.inventory));
			}
		}
		return null;
	}
}
