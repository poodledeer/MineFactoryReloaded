package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import powercrystals.minefactoryreloaded.core.IDelayedValidate;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ConnectionHandler
{
	public static HashMap<String, Boolean> onlinePlayerMap = new HashMap<String, Boolean>();

	private static LinkedHashSet<IDelayedValidate> nodes = new LinkedHashSet<IDelayedValidate>();
	private static LinkedHashSet<IDelayedValidate> nodesToAdd = new LinkedHashSet<IDelayedValidate>();

	public static void update(IDelayedValidate node)
	{
		nodesToAdd.add(node);
	}

	@SubscribeEvent
	public void tick(ServerTickEvent evt)
	{
		// TODO: this needs split up into groups per-world when worlds are threaded
		l: if (evt.phase != Phase.START)
		{
			if (nodesToAdd.isEmpty())
				break l;
			synchronized(nodesToAdd)
			{
				nodes.addAll(nodesToAdd);
				nodesToAdd.clear();
			}
			for(IDelayedValidate n : nodes)
				if (!n.isNotValid())
					n.firstTick();
			nodes.clear();
		}
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent player)
	{
		String name = player.player.getCommandSenderName();
		onlinePlayerMap.put(name, Boolean.TRUE);
		Iterator<Ticket> i = CommonProxy.ticketsInLimbo.iterator();
		while (i.hasNext())
		{
			Ticket ticket = i.next();
			if (ticket.getPlayerName().equals(name) && CommonProxy.loadTicket(ticket, false))
				i.remove();
		}
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent player)
	{
		onlinePlayerMap.remove(player.player.getCommandSenderName());
	}

	@SubscribeEvent
	public void onItemPickUp(ItemPickupEvent evt) {
		if (evt.pickedUp.getEntityItem().getItem() == MFRThings.rubberWoodItem) {
			// TODO: give player a book
			evt.player.triggerAchievement(AchievementList.mineWood);
		}
	}
}
