package powercrystals.minefactoryreloaded.tile.machine;

import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityBlockBreaker extends TileEntityFactoryPowered
{
	protected BlockPosition bp;
	public TileEntityBlockBreaker()
	{
		super(Machine.BlockBreaker);
		setManageSolids(true);
		setCanRotate(true);
	}

	@Override
	protected void onRotate()
	{
		bp = BlockPosition.fromRotateableTile(this).moveForwards(1);
		super.onRotate();
	}

	@Override
	public void onNeighborBlockChange()
	{
		if (bp != null && !worldObj.isAirBlock(bp.x, bp.y, bp.z))
			setIdleTicks(0);
	}

	@Override
	public boolean activateMachine()
	{
		int x = bp.x, y = bp.y, z = bp.z;
		World worldObj = this.worldObj;
		Block block = worldObj.getBlock(x, y, z);
		int blockMeta = worldObj.getBlockMetadata(x, y, z);

		if (!block.isAir(worldObj, x, y, z) &&
				!block.getMaterial().isLiquid() &&
				block.getBlockHardness(worldObj, x, y, z) >= 0)
		{
			List<ItemStack> drops = block.getDrops(worldObj, x, y, z, blockMeta, 0);
			if (worldObj.setBlockToAir(x, y, z))
			{
				doDrop(drops);
				if (MFRConfig.playSounds.getBoolean(true))
					worldObj.playAuxSFXAtEntity(null, 2001, x, y, z,
							Block.getIdFromBlock(block) + (blockMeta << 12));
			}
			return true;
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerFactoryPowered(this, inventoryPlayer);
	}

	@Override
	public int getWorkMax()
	{
		return 1;
	}

	@Override
	public int getIdleTicksMax()
	{
		return 60;
	}

	@Override
	public int getSizeInventory()
	{
		return 0;
	}
}
