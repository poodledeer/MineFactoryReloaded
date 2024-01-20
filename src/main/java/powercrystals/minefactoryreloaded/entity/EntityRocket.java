package powercrystals.minefactoryreloaded.entity;

import powercrystals.minefactoryreloaded.setup.MFRConfig;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityRocket extends Entity
{
	private int _ticksAlive = 0;
	private String _owner;
	private Entity _target;
	private NBTTagCompound _lostTarget;
	
	public EntityRocket(World world)
	{
		super(world);
		this.renderDistanceWeight = 10.0D;
		_lostTarget = null;
	}
	
	public EntityRocket(World world, EntityLivingBase owner)
	{
		this(world);
		setSize(1.0F, 1.0F);
		setLocationAndAngles(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ, owner.rotationYaw, owner.rotationPitch);
		setPosition(posX, posY, posZ);
		recalculateVelocity();
		if (owner instanceof EntityPlayer)
			_owner = ((EntityPlayer)owner).getCommandSenderName();
	}
	
	public EntityRocket(World world, EntityLivingBase owner, Entity target)
	{
		this(world, owner);
		_target = target;
	}
	
	private void recalculateVelocity()
	{
		motionX = -MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI);
		motionZ = MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI);
		motionY = (-MathHelper.sin(rotationPitch / 180.0F * (float)Math.PI));
	}
	
	@Override
	protected void entityInit()
	{
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		_ticksAlive++;
		if(_ticksAlive > 600)
		{
			if (MFRConfig.enableSPAMRExploding.getBoolean(true))
			{
				worldObj.newExplosion(this, posX, posY, posZ, 4.0F, true, true);
			}
			setDead();
		}
		
		if(worldObj.isRemote)
		{
			for(int i = 0; i < 4; i++)
			{
				worldObj.spawnParticle("smoke", posX + motionX * i / 4.0D, posY + motionY * i / 4.0D, posZ + motionZ * i / 4.0D,
						-motionX, -motionY + 0.2D, -motionZ);
			}
		}
		
		if(!worldObj.isRemote)
		{
			Vec3 pos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
			Vec3 nextPos = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			MovingObjectPosition hit = this.worldObj.func_147447_a(pos, nextPos, false, true, false);
			pos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
			nextPos = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			
			if(hit != null)
			{
				nextPos = Vec3.createVectorHelper(hit.hitVec.xCoord, hit.hitVec.yCoord,	hit.hitVec.zCoord);
			}
			
			Entity entityHit = null;
			List<?> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, 
					this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double closestRange = 0.0D;
			double collisionRange = 0.3D;
			EntityPlayer owner = _owner == null ? null : this.worldObj.getPlayerEntityByName(_owner);
			
			for(int i = 0, end = list.size(); i < end; ++i)
			{
				Entity e = (Entity)list.get(i);
				
				if((e != owner | _ticksAlive > 5) && e.canBeCollidedWith())
				{
					AxisAlignedBB entitybb = e.boundingBox.expand(collisionRange, collisionRange, collisionRange);
					MovingObjectPosition entityHitPos = entitybb.calculateIntercept(pos, nextPos);
					
					if(entityHitPos != null)
					{
						double range = pos.distanceTo(entityHitPos.hitVec);
						
						if((range < closestRange) | closestRange == 0D)
						{
							entityHit = e;
							closestRange = range;
						}
					}
				}
			}
			
			if(entityHit != null)
			{
				hit = new MovingObjectPosition(entityHit);
			}
			
			if(hit != null && hit.entityHit != null && hit.entityHit instanceof EntityPlayer)
			{
				EntityPlayer entityplayer = (EntityPlayer)hit.entityHit;
				
				if(entityplayer.capabilities.disableDamage || (owner != null && !owner.canAttackPlayer(entityplayer)))
				{
					hit = null;
				}
			}
			
			if(hit != null && !worldObj.isRemote)
			{
				if(hit.entityHit != null)
				{
					worldObj.newExplosion(this, hit.entityHit.posX, hit.entityHit.posY,
							hit.entityHit.posZ, 4.0F, true, true);
				}
				else
				{ // spawn explosion at nextPos x/y/z?
					worldObj.newExplosion(this, hit.blockX, hit.blockY, hit.blockZ, 4.0F, true, true);
				}
				setDead();
			}
		}
		
		Vec3 targetVector = findTarget();
		if (targetVector != null)
		{
			// At this point, I suspect literally no one on this project actually understands what this does or how it works
			
			float targetYaw = clampAngle(360 - (float)(Math.atan2(targetVector.xCoord, targetVector.zCoord) * 180.0D / Math.PI), 360, false);
			float targetPitch = clampAngle(-(float)(Math.atan2(targetVector.yCoord, Math.sqrt(targetVector.xCoord * targetVector.xCoord + targetVector.zCoord * targetVector.zCoord)) * 180.0D / Math.PI), 360, false);
			
			float yawDifference = clampAngle(targetYaw - rotationYaw, 3, true);
			float pitchDifference = clampAngle(targetPitch - rotationPitch, 3, true);

			float newYaw;
			float newPitch;
			
			if(Math.max(targetYaw, rotationYaw) - Math.min(targetYaw, rotationYaw) > 180)
			{
				newYaw = rotationYaw - yawDifference;
			}
			else
			{
				newYaw = rotationYaw + yawDifference;
			}
			
			if(Math.max(targetPitch, rotationPitch) - Math.min(targetPitch, rotationPitch) > 180)
			{
				newPitch = rotationPitch - pitchDifference;
			}
			else
			{
				newPitch = rotationPitch + pitchDifference;
			}
			
			rotationYaw = clampAngle(newYaw, 360F, false);
			rotationPitch = clampAngle(newPitch, 360F, false);
			recalculateVelocity();
		}
		
		setPosition(posX + motionX, posY + motionY, posZ + motionZ);
	}
	
	private float clampAngle(float angle, float maxValue, boolean allowNegative)
	{
		if(angle >= 0F)
		{
			angle %= 360F;
		}
		else
		{ // pretty sure that negativeValue % postiveValue has the same result
			angle = -(-angle % 360);
		}
		
		if(angle < 0 & !allowNegative)
		{
			angle += 360;
		}
		
		if(Math.abs(angle) > maxValue)
		{
			angle = Math.copySign(maxValue, angle);
		}
		
		return angle;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z)
	{
		motionX = x;
		motionY = y;
		motionZ = z;
		
		if(prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
		{
			double f = MathHelper.sqrt_double(x * x + z * z);
			prevRotationYaw = rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float)(Math.atan2(y, f) * 180.0D / Math.PI);
			setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Vec3 findTarget()
	{
		findTarget: if (_lostTarget != null)
		{
            UUID uuid = new UUID(_lostTarget.getLong("UUIDMost"), _lostTarget.getLong("UUIDLeast"));
            double x = _lostTarget.getDouble("xTarget");
            double y = _lostTarget.getDouble("yTarget");
            double z = _lostTarget.getDouble("zTarget");
            List list = this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.
            		getBoundingBox(x - 5, y - 5, z - 5, x + 5, y + 5, z + 5));
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
            	Entity e = (Entity)iterator.next();

                if (e.getUniqueID().equals(uuid))
                {
                    _target = e;
                    _lostTarget = null;
                    break findTarget;
                }
            }
            return Vec3.createVectorHelper(x - posX, y - posY, z - posZ);
		}
		if (_target != null)
		{
			return Vec3.createVectorHelper(_target.posX - posX,
					_target.posY - posY + _target.getEyeHeight(), _target.posZ - posZ);
		}
		return null;
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound tag)
	{
		if (_target != null)
		{
			NBTTagCompound target = new NBTTagCompound();
			tag.setDouble("xTarget", _target.posX);
			tag.setDouble("yTarget", _target.posY);
			tag.setDouble("zTarget", _target.posZ);
			UUID uuid = _target.getUniqueID();
			tag.setLong("UUIDMost", uuid.getMostSignificantBits());
			tag.setLong("UUIDLeast", uuid.getLeastSignificantBits());
			tag.setTag("target", target);
		}
		if (_owner != null)
			tag.setString("owner", _owner);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound tag)
	{
		if(tag.hasKey("target"))
		{
			_lostTarget = tag.getCompoundTag("target");
		}
		if (tag.hasKey("owner"))
			_owner = tag.getString("owner");
	}
}
