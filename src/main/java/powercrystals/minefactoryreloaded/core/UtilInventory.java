package powercrystals.minefactoryreloaded.core;

import buildcraft.api.transport.IPipeTile;

import cofh.api.transport.IItemDuct;
import cofh.asm.relauncher.Strippable;
import cofh.lib.inventory.IInventoryManager;
import cofh.lib.inventory.InventoryManager;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.position.BlockPosition;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class UtilInventory
{
	/**
	 * Searches from position x, y, z, checking for TE-compatible pipes in all directions.
	 *
	 * @return Map<ForgeDirection, IItemDuct> specifying all found pipes and their directions.
	 */
	public static Map<ForgeDirection, IItemDuct> findConduits(World world, int x, int y, int z)
	{
		return findConduits(world, x, y, z, ForgeDirection.VALID_DIRECTIONS);
	}

	/**
	 * Searches from position x, y, z, checking for TE-compatible pipes in each directiontocheck.
	 *
	 * @return Map<ForgeDirection, IItemDuct> specifying all found pipes and their directions.
	 */
	public static Map<ForgeDirection, IItemDuct> findConduits(World world, int x, int y, int z,
			ForgeDirection[] directionstocheck)
	{
		Map<ForgeDirection, IItemDuct> pipes = new LinkedHashMap<ForgeDirection, IItemDuct>();
		BlockPosition bp = new BlockPosition(x, y, z);
		for (ForgeDirection direction : directionstocheck)
		{
			bp.x = x; bp.y = y; bp.z = z;
			bp.step(direction, 1);
			TileEntity te = world.getTileEntity(bp.x, bp.y, bp.z);
			if (te instanceof IItemDuct)
			{
				pipes.put(direction, (IItemDuct) te);
			}
		}
		return pipes;
	}

	/**
	 * Searches from position x, y, z, checking for BC-compatible pipes in all directions.
	 *
	 * @return Map<ForgeDirection, IPipeTile> specifying all found pipes and their directions.
	 */
	@Strippable(pipeClass)
	public static Map<ForgeDirection, IPipeTile> findPipes(World world, int x, int y, int z)
	{
		return findPipes(world, x, y, z, ForgeDirection.VALID_DIRECTIONS);
	}

	/**
	 * Searches from position x, y, z, checking for BC-compatible pipes in each directiontocheck.
	 *
	 * @return Map<ForgeDirection, IPipeTile> specifying all found pipes and their directions.
	 */
	@Strippable(pipeClass)
	public static Map<ForgeDirection, IPipeTile> findPipes(World world, int x, int y, int z,
			ForgeDirection[] directionstocheck)
	{
		Map<ForgeDirection, IPipeTile> pipes = new LinkedHashMap<ForgeDirection, IPipeTile>();
		BlockPosition bp = new BlockPosition(x, y, z);
		for (ForgeDirection direction : directionstocheck)
		{
			bp.x = x; bp.y = y; bp.z = z;
			bp.step(direction, 1);
			TileEntity te = world.getTileEntity(bp.x, bp.y, bp.z);
			if (te instanceof IPipeTile)
			{
				pipes.put(direction, (IPipeTile) te);
			}
		}
		return pipes;
	}

	/**
	 * Searches from position x, y, z, checking for inventories in all directions.
	 *
	 * @return Map<ForgeDirection, IInventory> specifying all found inventories and their directions.
	 */
	public static Map<ForgeDirection, IInventory> findChests(World world, int x, int y, int z)
	{
		return findChests(world, x, y, z, ForgeDirection.VALID_DIRECTIONS);
	}

	/**
	 * Searches from position x, y, z, checking for inventories in each directiontocheck.
	 *
	 * @return Map<ForgeDirection, IInventory> specifying all found inventories and their directions.
	 */
	public static Map<ForgeDirection, IInventory> findChests(World world, int x, int y, int z,
			ForgeDirection[] directionstocheck)
	{
		Map<ForgeDirection, IInventory> chests = new LinkedHashMap<ForgeDirection, IInventory>();
		BlockPosition bp = new BlockPosition(x, y, z);
		for (ForgeDirection direction : directionstocheck)
		{
			bp.x = x; bp.y = y; bp.z = z;
			bp.step(direction, 1);
			TileEntity te = world.getTileEntity(bp.x, bp.y, bp.z);
			if (te != null && te instanceof IInventory)
			{
				chests.put(direction, checkForDoubleChest(world, te, bp));
			}
		}
		return chests;
	}

	private static IInventory checkForDoubleChest(World world, TileEntity te, BlockPosition chestloc)
	{
		Block block = world.getBlock(chestloc.x, chestloc.y, chestloc.z);
		if (block == Blocks.chest)
		{
			for (BlockPosition bp : chestloc.getAdjacent(false))
			{
				if (world.getBlock(bp.x, bp.y, bp.z) == Blocks.chest)
				{
					return new InventoryLargeChest("", ((IInventory)te), ((IInventory)world.getTileEntity(bp.x, bp.y, bp.z)));
				}
			}
		}
		else if (block == Blocks.trapped_chest)
		{
			for (BlockPosition bp : chestloc.getAdjacent(false))
			{
				if (world.getBlock(bp.x, bp.y, bp.z) == Blocks.trapped_chest)
				{
					return new InventoryLargeChest("", ((IInventory)te), ((IInventory)world.getTileEntity(bp.x, bp.y, bp.z)));
				}
			}
		}
		return ((IInventory)te);
	}

	/**
	 * Drops an ItemStack, checking all directions for pipes > chests. DOESN'T drop items into the world.
	 * Example of this behavior: Cargo dropoff rail, item collector.
	 *
	 * @return The remainder of the ItemStack. Whatever -wasn't- successfully dropped.
	 */
	public static ItemStack dropStack(TileEntity from, ItemStack stack)
	{
		return dropStack(from.getWorldObj(), new BlockPosition(from.xCoord, from.yCoord, from.zCoord),
				stack, ForgeDirection.VALID_DIRECTIONS, ForgeDirection.UNKNOWN);
	}

	/**
	 * Drops an ItemStack, checking all directions for pipes > chests. Drops items into the world.
	 * Example of this behavior: Harvesters, sludge boilers, etc.
	 *
	 * @param airdropdirection
	 *            the direction that the stack may be dropped into air.
	 * @return The remainder of the ItemStack. Whatever -wasn't- successfully dropped.
	 */
	public static ItemStack dropStack(TileEntity from, ItemStack stack, ForgeDirection airdropdirection)
	{
		return dropStack(from.getWorldObj(), new BlockPosition(from.xCoord, from.yCoord, from.zCoord),
				stack, ForgeDirection.VALID_DIRECTIONS, airdropdirection);
	}

	/**
	 * Drops an ItemStack, into chests > pipes > the world, but only in a single direction.
	 * Example of this behavior: Item Router, Ejector
	 *
	 * @param dropdirection
	 *            a -single- direction in which to check for pipes/chests
	 * @param airdropdirection
	 *            the direction that the stack may be dropped into air.
	 * @return The remainder of the ItemStack. Whatever -wasn't- successfully dropped.
	 */
	public static ItemStack dropStack(TileEntity from, ItemStack stack, ForgeDirection dropdirection,
			ForgeDirection airdropdirection)
	{
		ForgeDirection[] dropdirections = { dropdirection };
		return dropStack(from.getWorldObj(), new BlockPosition(from.xCoord, from.yCoord, from.zCoord),
				stack, dropdirections, airdropdirection);
	}

	/**
	 * Drops an ItemStack, checks pipes > chests > world in that order.
	 *
	 * @param from
	 *            the TileEntity doing the dropping
	 * @param stack
	 *            the ItemStack being dropped
	 * @param dropdirections
	 *            directions in which stack may be dropped into chests or pipes
	 * @param airdropdirection
	 *            the direction that the stack may be dropped into air.
	 *            ForgeDirection.UNKNOWN or other invalid directions indicate that stack shouldn't be
	 *            dropped into the world.
	 * @return The remainder of the ItemStack. Whatever -wasn't- successfully dropped.
	 */
	public static ItemStack dropStack(TileEntity from, ItemStack stack, ForgeDirection[] dropdirections,
			ForgeDirection airdropdirection)
	{
		return dropStack(from.getWorldObj(), new BlockPosition(from.xCoord, from.yCoord, from.zCoord),
				stack, dropdirections, airdropdirection);
	}

	/**
	 * Drops an ItemStack, checks pipes > chests > world in that order. It generally shouldn't be necessary to call this explicitly.
	 *
	 * @param world
	 *            the worldObj
	 * @param bp
	 *            the BlockPosition to drop from
	 * @param stack
	 *            the ItemStack being dropped
	 * @param dropdirections
	 *            directions in which stack may be dropped into chests or pipes
	 * @param airdropdirection
	 *            the direction that the stack may be dropped into air.
	 *             ForgeDirection.UNKNOWN or other invalid directions indicate that stack shouldn't be
	 *            dropped into the world.
	 * @return The remainder of the ItemStack. Whatever -wasn't- successfully dropped.
	 */
	public static ItemStack dropStack(World world, BlockPosition bp, ItemStack stack,
			ForgeDirection[] dropdirections, ForgeDirection airdropdirection)
	{
		// (0) Sanity check. Don't bother dropping if there's nothing to drop, and never try to drop items on the client.
		if (world.isRemote | stack == null || stack.stackSize == 0 || stack.getItem() == null)
			return null;

		stack = stack.copy();
		// (0.5) Try to put stack in conduits that are in valid directions
		for (Entry<ForgeDirection, IItemDuct> pipe : findConduits(world, bp.x, bp.y, bp.z, dropdirections).entrySet())
		{
			ForgeDirection from = pipe.getKey().getOpposite();
			stack = pipe.getValue().insertItem(from, stack);
			if (stack == null || stack.stackSize <= 0)
			{
				return null;
			}
		}
		// (1) Try to put stack in pipes that are in valid directions
		if (handlePipeTiles) {
			stack = handleIPipeTile(world, bp, dropdirections, stack);
			if (stack == null || stack.stackSize <= 0)
			{
				return null;
			}
		}
		// (2) Try to put stack in chests that are in valid directions
		for (Entry<ForgeDirection, IInventory> chest : findChests(world, bp.x, bp.y, bp.z, dropdirections).entrySet())
		{
			IInventoryManager manager = InventoryManager.create(chest.getValue(), chest.getKey().getOpposite());
			stack = manager.addItem(stack);
			if (stack == null || stack.stackSize <= 0)
			{
				return null;
			}
		}
		// (3) Having failed to put it in a chest or a pipe, drop it in the air if airdropdirection is a valid direction.
		bp.orientation = airdropdirection;
		bp.moveForwards(1);
		if (MFRUtil.VALID_DIRECTIONS.contains(airdropdirection) && isAirDrop(world, bp.x, bp.y, bp.z))
		{
			bp.moveBackwards(1);
			dropStackInAir(world, bp, stack, airdropdirection);
			return null;
		}
		// (4) Is the stack still here? :( Better give it back.
		return stack;
	}

	public static boolean isAirDrop(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		if (block.isAir(world, x, y, z))
			return true;
		block.setBlockBoundsBasedOnState(world, x, y, z);
		return block.getCollisionBoundingBoxFromPool(world, x, y, z) == null;
	}

	@SuppressWarnings("deprecation")
	private static ItemStack handleIPipeTile(World world, BlockPosition bp, ForgeDirection[] dropdirections, ItemStack stack)
	{
		for (Entry<ForgeDirection, IPipeTile> pipe : findPipes(world, bp.x, bp.y, bp.z, dropdirections).entrySet())
		{
			ForgeDirection from = pipe.getKey().getOpposite();
			if (pipe.getValue().isPipeConnected(from))
			{
				if (pipe.getValue().injectItem(stack.copy(), false, from) > 0)
				{
					stack.stackSize -= pipe.getValue().injectItem(stack.copy(), true, from);
					if (stack.stackSize <= 0)
					{
						return null;
					}
				}
			}
		}
		return stack;
	}

	public static void dropStackInAir(World world, BlockPosition bp, ItemStack stack) {
		dropStackInAir(world, bp, stack, ForgeDirection.UNKNOWN);
	}

	public static void dropStackInAir(World world, BlockPosition bp, ItemStack stack, int delay) {
		dropStackInAir(world, bp, stack, delay, ForgeDirection.UNKNOWN);
	}

	public static void dropStackInAir(World world, BlockPosition bp, ItemStack stack, ForgeDirection towards) {
		dropStackInAir(world, bp, stack, 20, towards);
	}

	public static void dropStackInAir(World world, BlockPosition bp, ItemStack stack, int delay, ForgeDirection towards) {
		dropStackInAir(world, bp.x, bp.y, bp.z, stack, delay, towards);
	}

	public static void dropStackInAir(World world, Entity bp, ItemStack stack) {
		dropStackInAir(world, bp, stack, ForgeDirection.UNKNOWN);
	}

	public static void dropStackInAir(World world, Entity bp, ItemStack stack, int delay) {
		dropStackInAir(world, bp, stack, delay, ForgeDirection.UNKNOWN);
	}

	public static void dropStackInAir(World world, Entity bp, ItemStack stack, ForgeDirection towards) {
		dropStackInAir(world, bp, stack, 20, towards);
	}

	public static void dropStackInAir(World world, Entity bp, ItemStack stack, int delay, ForgeDirection towards) {
		dropStackInAir(world, bp.posX, bp.posY, bp.posZ, stack, delay, towards);
	}

	public static void dropStackInAir(World world, double x, double y, double z, ItemStack stack,
			int delay, ForgeDirection towards)
	{
		if (stack == null) return;

		float dropOffsetX = 0.0F;
		float dropOffsetY = 0.0F;
		float dropOffsetZ = 0.0F;

		switch (towards)
		{
		case UP:
			dropOffsetX = 0.5F;
			dropOffsetY = 1.5F;
			dropOffsetZ = 0.5F;
			break;
		case DOWN:
			dropOffsetX = 0.5F;
			dropOffsetY = -0.75F;
			dropOffsetZ = 0.5F;
			break;
		case NORTH:
			dropOffsetX = 0.5F;
			dropOffsetY = 0.5F;
			dropOffsetZ = -0.5F;
			break;
		case SOUTH:
			dropOffsetX = 0.5F;
			dropOffsetY = 0.5F;
			dropOffsetZ = 1.5F;
			break;
		case EAST:
			dropOffsetX = 1.5F;
			dropOffsetY = 0.5F;
			dropOffsetZ = 0.5F;
			break;
		case WEST:
			dropOffsetX = -0.5F;
			dropOffsetY = 0.5F;
			dropOffsetZ = 0.5F;
			break;
		case UNKNOWN:
			break;
		}

		EntityItem entityitem = new EntityItem(world, x + dropOffsetX, y + dropOffsetY, z + dropOffsetZ, stack.copy());
		entityitem.motionX = 0.0D;
		if (towards != ForgeDirection.DOWN)
			entityitem.motionY = 0.3D;
		entityitem.motionZ = 0.0D;
		entityitem.delayBeforeCanPickup = delay;
		world.spawnEntityInWorld(entityitem);
	}

	public static ItemStack consumeItem(ItemStack stack, EntityPlayer player)
	{
		return ItemHelper.consumeItem(stack, player);
	}

	public static void mergeStacks(ItemStack to, ItemStack from)
	{
		if (!stacksEqual(to, from))
			return;

		int amountToCopy = Math.min(to.getMaxStackSize() - to.stackSize, from.stackSize);
		to.stackSize += amountToCopy;
		from.stackSize -= amountToCopy;
	}

	public static boolean stacksEqual(ItemStack s1, ItemStack s2)
	{
		return stacksEqual(s1, s2, true);
	}

	public static boolean stacksEqual(ItemStack s1, ItemStack s2, boolean nbtSensitive)
	{
		if (s1 == null | s2 == null) return false;
		if (!s1.isItemEqual(s2)) return false;
		if (!nbtSensitive) return true;

		if (s1.getTagCompound() == s2.getTagCompound()) return true;
		if (s1.getTagCompound() == null || s2.getTagCompound() == null) return false;
		return s1.getTagCompound().equals(s2.getTagCompound());
	}

	private static boolean handlePipeTiles = false;
	private static final String pipeClass = "buildcraft.api.transport.IPipeTile";
	static {
		try {
			Class.forName(pipeClass);
			handlePipeTiles = true;
		} catch(Throwable ignored) {}
	}
}
