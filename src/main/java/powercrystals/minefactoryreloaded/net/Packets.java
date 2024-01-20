package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.server.management.PlayerManager.PlayerInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.net.ServerPacketHandler.MFRMessage;

public final class Packets
{
	public static void sendToServer(IMessage message)
	{
		MineFactoryReloadedCore.networkWrapper.sendToServer(message);
	}
	public static void sendToAllPlayersWatching(World world, int x, int y, int z, Packet packet)
	{
		if (packet == null)
			return;
		if (world instanceof WorldServer)
		{
			PlayerManager manager = ((WorldServer)world).getPlayerManager();
			if (manager == null)
				return;
			PlayerInstance watcher = manager.getOrCreateChunkWatcher(x >> 4, x >> 4, false);
			if (watcher != null)
				watcher.sendToAllPlayersWatchingChunk(packet);
		}
	}
	public static void sendToAllPlayersInRange(World world, int x, int y, int z, int range, Packet packet)
	{
		if (packet == null)
			return;
		if (world instanceof WorldServer)
		{
			PlayerManager manager = ((WorldServer)world).getPlayerManager();
			if (manager == null)
				return;
			int xS = (x - range) >> 4, zS = (z - range) >> 4;
			int xE = (x + range) >> 4, zE = (z + range) >> 4;
			for (; xS < xE; ++xS) for (; zS < zE; ++zS)
			{
				PlayerInstance watcher = manager.getOrCreateChunkWatcher(xS, zS, false);
				if (watcher != null)
				{
					@SuppressWarnings("unchecked")
					List<EntityPlayerMP> players = watcher.playersWatchingChunk;
					for (int i = players.size(); i --> 0; )
					{
						EntityPlayerMP player = players.get(i);
						if (Math.abs(x - player.posX) < range)
							if (Math.abs(y - player.posY) < range)
								if (Math.abs(z - player.posZ) < range)
									player.playerNetServerHandler.sendPacket(packet);
					}
				}
			}
		}
	}

	public static final short EnchanterButton	= 0;
	public static final short HarvesterButton	= 1;
	public static final short ChronotyperButton	= 2;
	public static final short HAMUpdate			= 3;
	public static final short AutoJukeboxButton	= 4;
	public static final short AutoSpawnerButton	= 5;
	public static final short CircuitDefinition	= 6;
	public static final short LogicSetCircuit	= 7;
	public static final short LogicSetPin		= 8;
	public static final short LogicReinitialize	= 9;
	public static final short RouterButton		= 10;
	public static final short RocketLaunch		= 11;
	public static final short FakeSlotChange		= 20; // TODO: remove in favor of CoFH fake slots

	public static void sendToServer(short packet, TileEntity te, Object... args)
	{
		sendToServer(new MFRMessage(packet, te, args));
	}
	public static void sendToServer(short packet, Entity te, Object... args)
	{
		sendToServer(new MFRMessage(packet, te, args));
	}
	public static void sendToAllPlayersWatching(TileEntity te, Packet packet)
	{
		sendToAllPlayersWatching(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, packet);
	}
	public static void sendToAllPlayersWatching(TileEntity te)
	{
		sendToAllPlayersWatching(te, te.getDescriptionPacket());
	}
}
