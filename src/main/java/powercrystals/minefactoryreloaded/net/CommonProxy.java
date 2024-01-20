package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.FMLCommonHandler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.server.management.PlayerManager.PlayerInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityChunkLoader;

public class CommonProxy implements LoadingCallback
{
	public static LinkedList<Ticket> ticketsInLimbo = new LinkedList<Ticket>();

	public static boolean loadTicket(Ticket ticket, boolean addToList)
	{
		int x = ticket.getModData().getInteger("X");
		int y = ticket.getModData().getInteger("Y");
		int z = ticket.getModData().getInteger("Z");

		TileEntity tile = ticket.world.getTileEntity(x, y, z);
		if (!(tile instanceof TileEntityChunkLoader))
		{
			ForgeChunkManager.releaseTicket(ticket);
			return true;
		}
		boolean r = ((TileEntityChunkLoader)tile).receiveTicket(ticket);
		if (addToList & !r)
			ticketsInLimbo.push(ticket);
		return r;
	}

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world)
	{
		for (Ticket ticket : tickets)
			loadTicket(ticket, true);
	}

	public EntityPlayer getPlayer() {

		return null;
	}

	public void init()
	{
		FMLCommonHandler.instance().bus().register(GridTickHandler.energy);
		FMLCommonHandler.instance().bus().register(GridTickHandler.redstone);
		FMLCommonHandler.instance().bus().register(GridTickHandler.fluid);
		FMLCommonHandler.instance().bus().register(new ConnectionHandler());
		ForgeChunkManager.setForcedChunkLoadingCallback(MineFactoryReloadedCore.instance(), this);
	}

	public void movePlayerToCoordinates(EntityLivingBase e, double x, double y, double z)
	{
		if (e instanceof EntityPlayerMP)
		{
			EntityPlayerMP ep = (EntityPlayerMP)e;
			ep.playerNetServerHandler.setPlayerLocation(x, y, z, ep.cameraYaw, ep.cameraPitch);
		}
		e.setPositionAndUpdate(x, y, z);
	}

	public void relightChunk(Chunk chunk)
	{
		if (chunk != null)
		{
			chunk.generateSkylightMap();
			ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
			for (int i = storage.length; i --> 0;)
				if (storage[i] != null)
				{
					//{ spigot compat: force data array to exist
					NibbleArray a = storage[i].getSkylightArray();
					a.set(0, 0, 0, 0);
					a.set(0, 0, 0, 15);
					//}
					Arrays.fill(a.data, (byte)0);
				}
			chunk.resetRelightChecks();
			chunk.isModified = true;
			World world = chunk.worldObj;
			if (world instanceof WorldServer)
			{
				PlayerManager manager = ((WorldServer)world).getPlayerManager();
				if (manager == null)
					return;
				PlayerInstance watcher = manager.
						getOrCreateChunkWatcher(chunk.xPosition, chunk.zPosition, false);
				if (watcher != null)
					watcher.sendToAllPlayersWatchingChunk(new S21PacketChunkData(chunk, false, -1));
			}
		}
	}
}
