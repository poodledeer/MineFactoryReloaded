package powercrystals.minefactoryreloaded.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;


public class DebugTracker extends Entity {

	public DebugTracker(World world) {

		super(world);
		ignoreFrustumCheck = true;
	}

	public DebugTracker(World world, Entity src, Entity proj) {

		this(world);

		setPosition(src.posX, src.posY, src.posZ);

		AxisAlignedBB bb = src.boundingBox;
		dataWatcher.updateObject(2, (float)bb.minX);
		dataWatcher.updateObject(3, (float)bb.minY);
		dataWatcher.updateObject(4, (float)bb.minZ);
		dataWatcher.updateObject(5, (float)bb.maxX);
		dataWatcher.updateObject(6, (float)bb.maxY);
		dataWatcher.updateObject(7, (float)bb.maxZ);

		bb = proj.boundingBox;
		dataWatcher.updateObject(8, (float)bb.minX);
		dataWatcher.updateObject(9, (float)bb.minY);
		dataWatcher.updateObject(10, (float)bb.minZ);
		dataWatcher.updateObject(11, (float)bb.maxX);
		dataWatcher.updateObject(12, (float)bb.maxY);
		dataWatcher.updateObject(13, (float)bb.maxZ);

		dataWatcher.updateObject(14, (float)proj.motionX);
		dataWatcher.updateObject(15, (float)proj.motionY);
		dataWatcher.updateObject(16, (float)proj.motionZ);

		dataWatcher.updateObject(17, src.getEyeHeight());
	}
	@Override
	protected void entityInit() {

		for (int i = 2; i <= 18; ++i) {
			dataWatcher.addObject(i, 0.0F);
		}
	}

	public AxisAlignedBB getSrcBB() {

		return AxisAlignedBB.getBoundingBox(
			dataWatcher.getWatchableObjectFloat(2),
			dataWatcher.getWatchableObjectFloat(3),
			dataWatcher.getWatchableObjectFloat(4),
			dataWatcher.getWatchableObjectFloat(5),
			dataWatcher.getWatchableObjectFloat(6),
			dataWatcher.getWatchableObjectFloat(7)
			);
	}

	public AxisAlignedBB getPrjBB() {

		return AxisAlignedBB.getBoundingBox(
			dataWatcher.getWatchableObjectFloat(8),
			dataWatcher.getWatchableObjectFloat(9),
			dataWatcher.getWatchableObjectFloat(10),
			dataWatcher.getWatchableObjectFloat(11),
			dataWatcher.getWatchableObjectFloat(12),
			dataWatcher.getWatchableObjectFloat(13)
			);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.ticksExisted > 600)
			setDead();
	}


	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {

		setDead();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {

	}

}
