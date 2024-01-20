package powercrystals.minefactoryreloaded.block.transport;

import cofh.lib.inventory.IInventoryManager;
import cofh.lib.inventory.InventoryManager;

import java.util.Map.Entry;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.core.UtilInventory;


public class BlockRailCargoPickup extends BlockFactoryRail
{
	public BlockRailCargoPickup()
	{
		super(true, false);
		setBlockName("mfr.rail.cargo.pickup");
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart entity, int x, int y, int z)
	{
		if (world.isRemote || !(entity instanceof IInventory))
			return;

		IInventoryManager minecart = InventoryManager.create(entity, ForgeDirection.UNKNOWN);

		for (Entry<ForgeDirection, IInventory> inventory : UtilInventory.findChests(world, x, y, z).entrySet())
		{
			IInventoryManager chest = InventoryManager.create(inventory.getValue(), inventory.getKey().getOpposite()); 
			for (Entry<Integer, ItemStack> contents : chest.getContents().entrySet())
			{
				if (contents.getValue() == null || !chest.canRemoveItem(contents.getValue(), contents.getKey()))
				{
					continue;
				}
				ItemStack stackToAdd = contents.getValue().copy();

				ItemStack remaining = minecart.addItem(stackToAdd);

				if (remaining != null)
				{
					stackToAdd.stackSize -= remaining.stackSize;
					if (stackToAdd.stackSize > 0)
					{
						chest.removeItem(stackToAdd.stackSize, stackToAdd);
					}
				}
				else
				{
					chest.removeItem(stackToAdd.stackSize, stackToAdd);
					break;
				}
			}
		}
	}
}
