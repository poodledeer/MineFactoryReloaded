package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

public class TileEntityMobCounter extends TileEntityFactory
{
	private int _lastMobCount;

	public TileEntityMobCounter()
	{
		super(Machine.MobCounter);
		createEntityHAM(this);
		setCanRotate(true);
	}

	@Override
	public boolean canUpdate()
	{
		return true;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (worldObj == null)
		{
			return;
		}

		int mobCount = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, _areaManager.getHarvestArea().toAxisAlignedBB()).size();
		if (mobCount != _lastMobCount)
		{
			_lastMobCount = mobCount;
			if (!worldObj.isRemote)
			{
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, MFRThings.machineBlocks.get(Machine.MobCounter.getBlockIndex()));
			}
		}
	}

	@Override
	public int getRedNetOutput(ForgeDirection side)
	{
		return _lastMobCount;
	}
}
