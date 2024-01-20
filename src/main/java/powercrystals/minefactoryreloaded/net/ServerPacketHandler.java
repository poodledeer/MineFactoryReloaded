package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import powercrystals.minefactoryreloaded.net.ServerPacketHandler.MFRMessage;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoAnvil;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoDisenchanter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoEnchanter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoJukebox;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoSpawner;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityBlockSmasher;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityChronotyper;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityChunkLoader;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityDeepStorageUnit;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityEjector;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityEnchantmentRouter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityHarvester;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityItemRouter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityMobRouter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityPlanter;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

public class ServerPacketHandler implements IMessageHandler<MFRMessage, IMessage> {

	@Override
	public IMessage onMessage(MFRMessage message, MessageContext ctx) {

		if (message.packet != null)
			ctx.getServerHandler().sendPacket(message.packet);
		return null;
	}

	private static Packet readData(ByteBuf data) {

		TileEntity te;
		int x, y, z, a;
		byte amt;
		World world = DimensionManager.getWorld(data.readInt());
		EntityPlayer player;

		switch (data.readUnsignedShort()) {
		case Packets.HAMUpdate:
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = world.getTileEntity(x, y, z);
			if (te instanceof TileEntityFactory && ((TileEntityFactory) te).hasHAM()) {
				return ((TileEntityFactory) te).getHAM().getUpgradePacket();
			}
			break;
		case Packets.EnchanterButton: // client -> server: autoenchanter GUI buttons
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = world.getTileEntity(x, y, z);

			amt = data.readByte();
			if (te instanceof TileEntityAutoEnchanter) {
				((TileEntityAutoEnchanter) te).setTargetLevel(((TileEntityAutoEnchanter) te).getTargetLevel() + amt);
			} else if (te instanceof TileEntityBlockSmasher) {
				((TileEntityBlockSmasher) te).setFortune(((TileEntityBlockSmasher) te).getFortune() + amt);
			} else if (te instanceof TileEntityAutoDisenchanter) {
				((TileEntityAutoDisenchanter) te).setRepeatDisenchant(amt == 1 ? true : false);
			}
			break;
		case Packets.HarvesterButton: // client -> server: harvester setting
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = world.getTileEntity(x, y, z);

			if (te instanceof TileEntityHarvester) {
				((TileEntityHarvester) te).getSettings().put(ByteBufUtils.readUTF8String(data), data.readBoolean());
			}
			break;
		case Packets.ChronotyperButton: // client -> server: toggle chronotyper
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = world.getTileEntity(x, y, z);

			if (te instanceof TileEntityChronotyper) {
				((TileEntityChronotyper) te).setMoveOld(!((TileEntityChronotyper) te).getMoveOld());
			} else if (te instanceof TileEntityDeepStorageUnit) {
				((TileEntityDeepStorageUnit) te).setIsActive(!((TileEntityDeepStorageUnit) te).isActive());
				((TileEntityDeepStorageUnit) te).markForUpdate();
				Packets.sendToAllPlayersWatching(te);
			}
			break;
		case Packets.AutoJukeboxButton: // client -> server: copy record
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = world.getTileEntity(x, y, z);

			if (te instanceof TileEntityAutoJukebox) {
				TileEntityAutoJukebox j = ((TileEntityAutoJukebox) te);
				int button = data.readByte();
				if (button == 1)
					j.playRecord();
				else if (button == 2)
					j.stopRecord();
				else if (button == 3) j.copyRecord();
			}
			break;
		case Packets.AutoSpawnerButton: // client -> server: toggle autospawner
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = world.getTileEntity(x, y, z);

			if (te instanceof TileEntityAutoSpawner) {
				((TileEntityAutoSpawner) te).setSpawnExact(!((TileEntityAutoSpawner) te).getSpawnExact());
			}
			break;
		case Packets.CircuitDefinition: // client -> server: request circuit from server
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = world.getTileEntity(x, y, z);

			if (te instanceof TileEntityRedNetLogic) {
				((TileEntityRedNetLogic) te).sendCircuitDefinition(data.readInt());
			}
			break;
		case Packets.LogicSetCircuit: // client -> server: set circuit
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = world.getTileEntity(x, y, z);

			int circuit = data.readInt();
			if (te instanceof TileEntityRedNetLogic) {
				((TileEntityRedNetLogic) te).initCircuit(circuit, ByteBufUtils.readUTF8String(data));
				((TileEntityRedNetLogic) te).sendCircuitDefinition(circuit);
			}
			break;
		case Packets.LogicSetPin: // client -> server: set pin
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = world.getTileEntity(x, y, z);

			amt = data.readByte();
			int circuitIndex = data.readInt(),
			pinIndex = data.readInt(),
			buffer = data.readInt(),
			pin = data.readInt();
			if (te instanceof TileEntityRedNetLogic) {
				if (amt == 0) {
					((TileEntityRedNetLogic) te).setInputPinMapping(circuitIndex, pinIndex, buffer, pin);
				} else if (amt == 1) {
					((TileEntityRedNetLogic) te).setOutputPinMapping(circuitIndex, pinIndex, buffer, pin);
				}
				((TileEntityRedNetLogic) te).sendCircuitDefinition(circuitIndex);
			}
			break;
		case Packets.LogicReinitialize: // client -> server: set circuit
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = world.getTileEntity(x, y, z);
			player = (EntityPlayer) world.getEntityByID(data.readInt());

			if (te instanceof TileEntityRedNetLogic) {
				((TileEntityRedNetLogic) te).reinitialize(player);
			}
			break;
		case Packets.RouterButton: // client -> server: toggle 'levels'/'reject unmapped' mode
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = world.getTileEntity(x, y, z);

			a = data.readInt();
			if (te instanceof TileEntityEnchantmentRouter) {
				switch (a) {
				case 2:
					((TileEntityItemRouter) te).setRejectUnmapped(!((TileEntityItemRouter) te).getRejectUnmapped());
					break;
				case 1:
					((TileEntityEnchantmentRouter) te).setMatchLevels(!((TileEntityEnchantmentRouter) te).getMatchLevels());
					break;
				}
			} else if (te instanceof TileEntityItemRouter) {
				((TileEntityItemRouter) te).setRejectUnmapped(!((TileEntityItemRouter) te).getRejectUnmapped());
			} else if (te instanceof TileEntityEjector) {
				switch (a) {
				case 1:
					((TileEntityEjector) te).setIsWhitelist(!((TileEntityEjector) te).getIsWhitelist());
					break;
				case 2:
					((TileEntityEjector) te).setIsNBTMatch(!((TileEntityEjector) te).getIsNBTMatch());
					break;
				case 3:
					((TileEntityEjector) te).setIsIDMatch(!((TileEntityEjector) te).getIsIDMatch());
					break;
				}
			} else if (te instanceof TileEntityAutoAnvil) {
				((TileEntityAutoAnvil) te).setRepairOnly(!((TileEntityAutoAnvil) te).getRepairOnly());
			} else if (te instanceof TileEntityChunkLoader) {
				((TileEntityChunkLoader) te).setRadius((short) a);
			} else if (te instanceof TileEntityPlanter) {
				((TileEntityPlanter) te).setConsumeAll(!((TileEntityPlanter) te).getConsumeAll());
			} else if (te instanceof TileEntityMobRouter) {
				switch (a) {
				case 1:
					((TileEntityMobRouter) te).setWhiteList(!((TileEntityMobRouter) te).getWhiteList());
					break;
				case 2:
					((TileEntityMobRouter) te).setMatchMode(((TileEntityMobRouter) te).getMatchMode() + 1);
					break;
				case 3:
					((TileEntityMobRouter) te).setMatchMode(((TileEntityMobRouter) te).getMatchMode() - 1);
					break;
				}
			}
			break;
		case Packets.FakeSlotChange: // client -> server: client clicked on a fake slot
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = world.getTileEntity(x, y, z);
			player = (EntityPlayer) world.getEntityByID(data.readInt());

			ItemStack playerStack = player.inventory.getItemStack();
			int slotNumber = data.readInt(),
			click = data.readByte();
			if (te instanceof IInventory) {
				if (playerStack == null) {
					((IInventory) te).setInventorySlotContents(slotNumber, null);
				} else {
					playerStack = playerStack.copy();
					playerStack.stackSize = click == 1 ? -1 : 1;
					ItemStack s = ((IInventory) te).getStackInSlot(slotNumber);
					if (!UtilInventory.stacksEqual(s, playerStack))
						playerStack.stackSize = 1;
					else
						playerStack.stackSize = Math.max(playerStack.stackSize + s.stackSize, 1);
					((IInventory) te).setInventorySlotContents(slotNumber, playerStack);
				}
			}
			break;
		case Packets.RocketLaunch: // client -> server: client firing SPAMR missile
			Entity owner = world.getEntityByID(data.readInt());
			int t = data.readInt();
			Entity target = null;
			if (t != Integer.MIN_VALUE) {
				target = world.getEntityByID(t);
			}

			if (owner instanceof EntityLivingBase) {
				EntityRocket r = new EntityRocket(world, ((EntityLivingBase) owner), target);
				world.spawnEntityInWorld(r);
			}
			break;
		}
		return null;
	}

	public static class MFRMessage implements IMessage {

		public ByteBuf buf;
		public Packet packet;

		public MFRMessage() {

		}

		public MFRMessage(short packet, TileEntity te, Object... args) {

			ByteBuf buff = Unpooled.buffer();
			buff.writeInt(te.getWorldObj().provider.dimensionId);
			buff.writeShort(packet);
			buff.writeInt(te.xCoord);
			buff.writeInt(te.yCoord);
			buff.writeInt(te.zCoord);
			handleObjects(buff, args);
			buf = buff;
		}

		public MFRMessage(short packet, Entity e, Object... args) {

			ByteBuf buff = Unpooled.buffer();
			buff.writeInt(e.worldObj.provider.dimensionId);
			buff.writeShort(packet);
			buff.writeInt(e.getEntityId());
			handleObjects(buff, args);
			buf = buff;
		}

		@Override
		public void fromBytes(ByteBuf buf) {

			packet = readData(buf);
		}

		@Override
		public void toBytes(ByteBuf buf) {

			buf.writeBytes(this.buf);
		}

		private static void handleObjects(ByteBuf data, Object[] objects) {

			for (Object obj : objects) {
				Class<?> objClass = obj.getClass();
				if (objClass.equals(Integer.class)) {
					data.writeInt((Integer) obj);
				} else if (objClass.equals(Boolean.class)) {
					data.writeBoolean((Boolean) obj);
				} else if (objClass.equals(Byte.class)) {
					data.writeByte((Byte) obj);
				} else if (objClass.equals(Short.class)) {
					data.writeShort((Short) obj);
				} else if (objClass.equals(String.class)) {
					ByteBufUtils.writeUTF8String(data, (String) obj);
				} else if (Entity.class.isAssignableFrom(objClass)) {
					data.writeInt(((Entity) obj).getEntityId());
				} else if (objClass.equals(Double.class)) {
					data.writeDouble((Double) obj);
				} else if (objClass.equals(Float.class)) {
					data.writeFloat((Float) obj);
				} else if (objClass.equals(Long.class)) {
					data.writeLong((Long) obj);
				}
			}
		}
	}
}
