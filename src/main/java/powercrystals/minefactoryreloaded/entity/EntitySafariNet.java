package powercrystals.minefactoryreloaded.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.item.ItemSafariNet;

public class EntitySafariNet extends EntityThrowable {

	public EntitySafariNet(World world) {

		super(world);
        this.renderDistanceWeight = 10.0D;
		dataWatcher.addObjectByDataType(13, 5);
	}

	public EntitySafariNet(World world, double x, double y, double z, ItemStack netStack) {

		super(world, x, y, z);
        this.renderDistanceWeight = 10.0D;
		dataWatcher.addObject(13, netStack);
	}

	public EntitySafariNet(World world, EntityLivingBase owner, ItemStack netStack) {

		super(world, owner);
        this.renderDistanceWeight = 10.0D;
		dataWatcher.addObject(13, netStack);
	}

	public ItemStack getStoredEntity() {

		return dataWatcher.getWatchableObjectItemStack(13);
	}

	public void setStoredEntity(ItemStack s) {

		dataWatcher.updateObject(13, s);
	}

	protected boolean onHitBlock(ItemStack storedEntity, MovingObjectPosition mop) {

		if (ItemSafariNet.isEmpty(storedEntity)) {
			dropAsStack(storedEntity);
		} else {
			ItemSafariNet.releaseEntity(storedEntity, worldObj, mop.blockX, mop.blockY, mop.blockZ, mop.sideHit);
			if (ItemSafariNet.isSingleUse(storedEntity)) {
				dropAsStack(null);
			} else {
				dropAsStack(storedEntity);
			}
		}
		return true;
	}

	protected boolean onHitEntity(ItemStack storedEntity, MovingObjectPosition mop) {

		if (ItemSafariNet.isEmpty(storedEntity) && mop.entityHit instanceof EntityLivingBase) {
			ItemSafariNet.captureEntity(storedEntity, (EntityLivingBase) mop.entityHit);
			dropAsStack(storedEntity);
		} else {
			if (!ItemSafariNet.isEmpty(storedEntity)) {
				Entity releasedEntity = ItemSafariNet.releaseEntity(storedEntity, worldObj, (int) mop.entityHit.posX,
					(int) mop.entityHit.posY, (int) mop.entityHit.posZ, 1);

				if (mop.entityHit instanceof EntityLivingBase) {
					if (releasedEntity instanceof EntityLiving) {
						//Functional for skeletons.
						((EntityLiving) releasedEntity).setAttackTarget((EntityLivingBase) mop.entityHit);
					}

					if (releasedEntity instanceof EntityCreature) {
						//functional for mobs that extend EntityCreature (everything but Ghasts) and not Skeletons.
						((EntityCreature) releasedEntity).setTarget(mop.entityHit);
					}
				}

				if (ItemSafariNet.isSingleUse(storedEntity)) {
					setDead();
					return true;
				}
			}
			dropAsStack(storedEntity);
		}
		return true;
	}

	protected void impact(double x, double y, double z, int side) {

	}

	@Override
	protected void onImpact(MovingObjectPosition mop) {

		ItemStack storedEntity = dataWatcher.getWatchableObjectItemStack(13);

		boolean r = false;
		double x, y, z;
		int side;
		if (mop.typeOfHit == MovingObjectType.ENTITY) {
			r = onHitEntity(storedEntity, mop);
			x = mop.entityHit.posX;
			y = mop.entityHit.posY;
			z = mop.entityHit.posZ;
			side = -1;
		} else {
			r = onHitBlock(storedEntity, mop);
			x = mop.blockX;
			y = mop.blockY;
			z = mop.blockZ;
			side = mop.sideHit;
		}
		if (r)
			impact(x, y, z, side);
	}

	protected void dropAsStack(ItemStack stack) {

		if (!worldObj.isRemote && stack != null) {
			EntityItem ei = new EntityItem(worldObj, posX, posY, posZ, stack.copy());
			ei.delayBeforeCanPickup = 40;
			worldObj.spawnEntityInWorld(ei);
		}
		setDead();
	}

	public IIcon getIcon() {

		return dataWatcher.getWatchableObjectItemStack(13).getIconIndex();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {

		super.writeEntityToNBT(nbttagcompound);
		NBTTagCompound stackTag = new NBTTagCompound();
		dataWatcher.getWatchableObjectItemStack(13).writeToNBT(stackTag);
		nbttagcompound.setTag("safariNetStack", stackTag);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {

		super.readEntityFromNBT(nbttagcompound);
		NBTTagCompound stackTag = nbttagcompound.getCompoundTag("safariNetStack");
		setStoredEntity(ItemStack.loadItemStackFromNBT(stackTag));
	}
}
