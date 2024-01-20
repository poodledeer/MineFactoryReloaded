package powercrystals.minefactoryreloaded.core;

import static net.minecraft.util.EnumChatFormatting.*;
import static org.lwjgl.input.Keyboard.*;

import buildcraft.api.tools.IToolWrench;

import cofh.api.item.IToolHammer;
import cofh.lib.util.helpers.StringHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.input.Keyboard;

import powercrystals.minefactoryreloaded.api.IMFRHammer;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class MFRUtil {

	@SideOnly(Side.CLIENT)
	public static boolean isAltKeyDown() {

		return Keyboard.isKeyDown(KEY_LMENU) || Keyboard.isKeyDown(KEY_RMENU);
	}

	@SideOnly(Side.CLIENT)
	public static boolean isCtrlKeyDown() { // logic lifted from net.minecraft.client.gui.GuiScreen.isCtrlKeyDown()

		if (Minecraft.isRunningOnMac)
			return Keyboard.isKeyDown(KEY_LMETA) || Keyboard.isKeyDown(KEY_RMETA);
		return Keyboard.isKeyDown(KEY_LCONTROL) || Keyboard.isKeyDown(KEY_RCONTROL);
	}

	@SideOnly(Side.CLIENT)
	public static boolean isShiftKeyDown() { // logic lifted from net.minecraft.client.gui.GuiScreen.isCtrlKeyDown()

		return Keyboard.isKeyDown(KEY_LSHIFT) || Keyboard.isKeyDown(KEY_RSHIFT);
	}

	//
	public static final int[] COLORS_DYE = { 0x1e1e1e, 0xb62222, 0x47691D, 0x804020, 0x404080, 0x803880,
			0x54a69b, 0xa3a3a3, 0x505050, 0xd881a4, 0x49c14a, 0xd8b920, 0x8da7f6, 0xc730a5, 0xe36600, 0xf0f0f0 };
	public static final int[] COLORS;
	static {
		COLORS = new int[16];
		for (int i = 16, o = 0; i-- > 0; ++o)
			COLORS[o] = COLORS_DYE[i];
	}
	private static final String mojangString = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";

	public static String shiftForInfo() {

		return StringHelper.shiftForDetails();
	}

	public static String empty() {

		return ITALIC + "<" + localize("info.cofh.empty", true) + ">" + RESET;
	}

	public static String energy() {

		return localize("info.cofh.energy", true);
	}

	public static String idle() {

		return localize("info.cofh.idle", true);
	}

	public static String work() {

		return localize("info.cofh.work", true);
	}

	public static String buffer() {

		return localize("info.cofh.buffer", true);
	}

	public static String efficiency() {

		return localize("info.cofh.efficiency", true);
	}

	public static String getFluidName(FluidStack fluid) {

		return StringHelper.getFluidName(fluid);
	}

	public static boolean containsForcedUnicode(String str) {

		for (int i = 0, e = str.length(); i < e; ++i)
			if (mojangString.indexOf(str.charAt(i)) < 0)
				return true;
		return false;
	}

	public static String localize(String s) {

		return localize(s + ".name", false, s);
	}

	public static String localize(String prefix, String s) {

		return localize(prefix + s + ".name", true, s);
	}

	public static String localize(String s, boolean exists) {

		return localize(s, exists, s);
	}

	public static String localize(String s, boolean exists, String def) {

		if (!StatCollector.canTranslate(s))
			return exists ? def :
					StatCollector.canTranslate(def) ? localize(StatCollector.translateToLocal(def), true) : def;
		return StatCollector.translateToLocal(s);
	}

	public static String localize(String s, Object... data) {

		if (!StatCollector.canTranslate(s))
			return s;
		return StatCollector.translateToLocalFormatted(s, data);
	}

	private static enum Numeral {
		M(1000),
		CM(900),
		D(500),
		CD(400),
		C(100),
		XC(90),
		L(50),
		XL(40),
		X(10),
		IX(9),
		V(5),
		IV(4),
		I(1);

		public final String name = name();
		public final int value;

		private Numeral(int val) {

			value = val;
		}

		private static final Numeral[] values = values();
	}

	public static String toNumerals(short i) {

		String s = "potion.potency." + i;
		if (StatCollector.canTranslate(s))
			return StatCollector.translateToLocal(s);
		StringBuilder r = new StringBuilder();
		if (i < 0) {
			i = (short) -i;
			r.append('-');
		}
		for (Numeral k : Numeral.values) {
			for (int j = i / k.value; j-- > 0; r.append(k.name))
				;
			i %= k.value;
		}
		return r.toString();
	}

	public static final List<ForgeDirection> VALID_DIRECTIONS = Arrays.asList(ForgeDirection.VALID_DIRECTIONS);

	public static boolean isHoldingUsableTool(EntityPlayer player, int x, int y, int z) {

		if (player == null) {
			return false;
		}
		if (player.inventory.getCurrentItem() == null) {
			return false;
		}
		Item currentItem = player.inventory.getCurrentItem().getItem();
		if (currentItem instanceof IToolHammer) {
			return ((IToolHammer) currentItem).isUsable(player.inventory.getCurrentItem(), player, x, y, z);
		}
		else if (currentItem instanceof IMFRHammer) {
			return true;
		}
		else if (bcWrenchExists && canHandleBCWrench(currentItem, player, x, y, z)) {
			return true;
		}

		return false;
	}

	public static void usedWrench(EntityPlayer player, int x, int y, int z) {

		if (player == null) {
			return;
		}
		if (player.inventory.getCurrentItem() == null) {
			return;
		}
		Item currentItem = player.inventory.getCurrentItem().getItem();
		if (currentItem instanceof IToolHammer) {
			((IToolHammer) currentItem).toolUsed(player.inventory.getCurrentItem(), player, x, y, z);
		}
		else if (currentItem instanceof IMFRHammer) {
			;
		}
		else if (bcWrenchExists) {
			bcWrenchUsed(currentItem, player, x, y, z);
		}
	}

	private static boolean bcWrenchExists = false;
	static {
		try {
			Class.forName("buildcraft.api.tools.IToolWrench");
			bcWrenchExists = true;
		} catch (Throwable ignored) {
		}
	}

	private static boolean canHandleBCWrench(Item item, EntityPlayer p, int x, int y, int z) {

		return item instanceof IToolWrench && ((IToolWrench) item).canWrench(p, x, y, z);
	}

	private static void bcWrenchUsed(Item item, EntityPlayer p, int x, int y, int z) {

		if (item instanceof IToolWrench) ((IToolWrench) item).wrenchUsed(p, x, y, z);
	}

	public static boolean isHoldingHammer(EntityPlayer player) {

		if (player == null) {
			return false;
		}
		if (player.inventory.getCurrentItem() == null) {
			return false;
		}
		Item currentItem = player.inventory.getCurrentItem().getItem();
		if (currentItem instanceof IMFRHammer) {
			return true;
		}

		return false;
	}

	public static boolean isHolding(EntityPlayer player, Class<? extends Item> itemClass) {

		if (player == null) {
			return false;
		}
		if (player.inventory.getCurrentItem() == null) {
			return false;
		}
		Item currentItem = player.inventory.getCurrentItem().getItem();
		if (currentItem != null && itemClass.isAssignableFrom(currentItem.getClass())) {
			return true;
		}
		return false;
	}

	public static <V extends Entity, T extends Class<V>> V prepareMob(T entity, World world) {

		try {
			V e = entity.getConstructor(World.class).newInstance(world);
			return e;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static final TileEntity getTile(World world, int x, int y, int z) {

		return world.getChunkFromBlockCoords(x, z).getTileEntityUnsafe(x & 15, y, z & 15);
	}

	public static ForgeDirection[] directionsWithoutConveyors(World world, int x, int y, int z) {

		ArrayList<ForgeDirection> nonConveyors = new ArrayList<ForgeDirection>();
		Block id = MFRThings.conveyorBlock;

		for (int i = 0, e = ForgeDirection.VALID_DIRECTIONS.length; i < e; ++i) {
			ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[i];
			if (!world.getBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ).equals(id))
				nonConveyors.add(direction);
		}

		return nonConveyors.toArray(new ForgeDirection[nonConveyors.size()]);
	}

	public static void notifyNearbyBlocks(World world, int x, int y, int z, Block block) {

		ForgeDirection[] dirs = ForgeDirection.VALID_DIRECTIONS;
		if (world.blockExists(x, y, z)) {
			for (int j = 0; j < 6; ++j) {
				ForgeDirection d2 = dirs[j];
				int x2 = x + d2.offsetX, y2 = y + d2.offsetY, z2 = z + d2.offsetZ;
				if (world.blockExists(x2, y2, z2))
					world.notifyBlockOfNeighborChange(x2, y2, z2, block);
			}
		}
	}

	public static void notifyNearbyBlocksExcept(World world, int x, int y, int z, Block block) {

		ForgeDirection[] dirs = ForgeDirection.VALID_DIRECTIONS;
		if (world.blockExists(x, y, z) && world.getBlock(x, y, z) != block) {
			world.notifyBlockOfNeighborChange(x, y, z, block);
			for (int j = 0; j < 6; ++j) {
				ForgeDirection d2 = dirs[j];
				int x2 = x + d2.offsetX, y2 = y + d2.offsetY, z2 = z + d2.offsetZ;
				if (world.blockExists(x2, y2, z2) && world.getBlock(x2, y2, z2) != block)
					world.notifyBlockOfNeighborChange(x2, y2, z2, block);
			}
		}
	}

	public static void wideNotifyNearbyBlocksExcept(World world, int X, int Y, int Z, Block block) {

		ForgeDirection[] dirs = ForgeDirection.VALID_DIRECTIONS;
		for (int i = 0; i < 6; ++i) {
			ForgeDirection d = dirs[i];
			int x = X + d.offsetX, y = Y + d.offsetY, z = Z + d.offsetZ;
			if (world.blockExists(x, y, z) && world.getBlock(x, y, z) != block) {
				world.notifyBlockOfNeighborChange(x, y, z, block);
				for (int j = 0; j < 6; ++j) {
					if ((j ^ 1) == i)
						continue;
					ForgeDirection d2 = dirs[j];
					int x2 = x + d2.offsetX, y2 = y + d2.offsetY, z2 = z + d2.offsetZ;
					if (world.blockExists(x2, y2, z2) && world.getBlock(x2, y2, z2) != block)
						world.notifyBlockOfNeighborChange(x2, y2, z2, block);
				}
			}
		}
	}

	public static NBTTagCompound writeModifierToNBT(String name, AttributeModifier modifier) {

		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("AttributeName", name);
		tag.setString("Name", modifier.getName());
		tag.setDouble("Amount", modifier.getAmount());
		tag.setInteger("Operation", modifier.getOperation());
		tag.setLong("UUIDMost", modifier.getID().getMostSignificantBits());
		tag.setLong("UUIDLeast", modifier.getID().getLeastSignificantBits());
		return tag;
	}

}
