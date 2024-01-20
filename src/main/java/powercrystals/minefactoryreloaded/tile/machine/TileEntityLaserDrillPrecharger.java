package powercrystals.minefactoryreloaded.tile.machine;

import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.api.IFactoryLaserSource;
import powercrystals.minefactoryreloaded.api.IFactoryLaserTarget;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityLaserDrillPrecharger extends TileEntityFactoryPowered implements IFactoryLaserSource
{
	public TileEntityLaserDrillPrecharger()
	{
		super(Machine.LaserDrillPrecharger);
		setCanRotate(true);
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
	public int getSizeInventory()
	{
		return 0;
	}

	@Override
	protected boolean activateMachine()
	{
		IFactoryLaserTarget drill = getDrill();
		if (drill == null)
		{
			setIdleTicks(getIdleTicksMax());
		}
		else
		{
			int energy = getActivationEnergy();
			ForgeDirection facing = getDirectionFacing().getOpposite();
			if (drill.canFormBeamWith(facing))
			{
				stripBlock(false);
				int excess = drill.addEnergy(facing, energy, true);
				if (excess == 0)
				{
					drill.addEnergy(facing, energy, false);
					return true;
				}
				else
				{
					excess = drill.addEnergy(facing, energy, false);
					drainEnergy(energy - excess);
					return false; // energy is manually drained because it's less than activation energy
				}
			}
			else
				stripBlock(true);
		}
		return false;
	}

	@Override
	public int getWorkMax()
	{
		return 1;
	}

	@Override
	public int getIdleTicksMax()
	{
		return 200;
	}

	public boolean shouldDrawBeam()
	{
		IFactoryLaserTarget drill = getDrill();
		return drill != null && drill.canFormBeamWith(getDirectionFacing().getOpposite());
	}

	protected IFactoryLaserTarget getDrill()
	{
		BlockPosition bp = new BlockPosition(this);
		bp.orientation = getDirectionFacing();
		bp.moveForwards(1);

		if (!worldObj.blockExists(bp.x, bp.y, bp.z))
			return null;

		if (!TileEntityLaserDrill.canReplaceBlock(worldObj.getBlock(bp.x, bp.y, bp.z),
				worldObj, bp.x, bp.y, bp.z))
			return null;

		bp.moveForwards(1);

		TileEntity te = worldObj.getTileEntity(bp.x, bp.y, bp.z);
		if (te instanceof IFactoryLaserTarget)
			return ((IFactoryLaserTarget)te);

		return null;
	}

	private int stripTick = 0;
	protected void stripBlock(boolean set)
	{
		if (stripTick > 0)
		{
			--stripTick;
			return;
		}
		stripTick = 20;
		BlockPosition bp = new BlockPosition(this);
		bp.orientation = getDirectionFacing();
		bp.moveForwards(1);
		if (set == worldObj.getBlock(bp.x, bp.y, bp.z).equals(MFRThings.fakeLaserBlock))
			worldObj.setBlock(bp.x, bp.y, bp.z, MFRThings.fakeLaserBlock, bp.orientation.getOpposite().ordinal() + 1, 3);
		else if (set)
			worldObj.scheduleBlockUpdate(bp.x, bp.y, bp.z, MFRThings.fakeLaserBlock, 1);
	}

	@Override
	public boolean canFormBeamFrom(ForgeDirection from)
	{
		if (from != getDirectionFacing().getOpposite())
			return false;
		IFactoryLaserTarget drill = getDrill();
		return drill != null && drill.canFormBeamWith(getDirectionFacing().getOpposite());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536;
	}
}
