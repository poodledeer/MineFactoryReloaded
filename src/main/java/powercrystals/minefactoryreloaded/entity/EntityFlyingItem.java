package powercrystals.minefactoryreloaded.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityFlyingItem extends EntitySafariNet {

	public int canBePickedUp = 0, pickupChance = 7;
	protected float damage = 2.0f;
	protected int knockbackStrength;

	public EntityFlyingItem(World world) {

		super(world);
	}

	public EntityFlyingItem(World world, double x, double y, double z, ItemStack stack) {

		super(world, x, y, z, stack);
	}

	public EntityFlyingItem(World world, EntityLivingBase owner, ItemStack stack) {

		super(world, owner, stack);
	}

	public void setDamage(float damage) {

		this.damage = damage;
	}

	public float getDamage() {

		return this.damage;
	}

	public void setKnockbackStrength(int p_70240_1_) {

		this.knockbackStrength = p_70240_1_;
	}

	@Override
	public boolean canAttackWithItem() {

		return false;
	}

	@Override
	protected boolean onHitBlock(ItemStack storedEntity, MovingObjectPosition mop) {

		if (canBePickedUp == 0 && (pickupChance > 0 && (pickupChance == 1 || worldObj.rand.nextInt(pickupChance) == 0))) {
			dropAsStack(this.getStoredEntity());
			return false;
		}
		return true;
	}

	@Override
	protected boolean onHitEntity(ItemStack storedEntity, MovingObjectPosition mop) {

		DamageSource d = DamageSource.causeThrownDamage(this, getThrower() == null ? this : getThrower());
		if (mop.entityHit.attackEntityFrom(d, damage)) {
			if (this.knockbackStrength > 0) {
				float f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

				if (f4 > 0.0F) {
					mop.entityHit.addVelocity(this.motionX * this.knockbackStrength * 0.6D /
							f4, 0.1D, this.motionZ * this.knockbackStrength * 0.6D / f4);
				}
			}
		}
		return true;
	}

	@Override
	protected void impact(double x, double y, double z, int side) {
		ItemStack stack = dataWatcher.getWatchableObjectItemStack(13);
		String impact;
		if (stack == null)
			impact = "snowballpoof";
		else
			impact = "iconcrack_" + Item.getIdFromItem(stack.getItem()) + "_" + stack.getItemDamage();

		float X = 0, Y = 0.14f, Z = 0;
		switch (side) {
		case 1:
			y += 1;
		case 0:
			x += 0.5;
			z += 0.5;
			if (side == 0)
				Y = 0;
			break;
		case 3:
			z += 1;
		case 2:
			x += 0.5;
			y += 0.5;
			Z = side == 3 ? 1 : -1;
			break;
		case 5:
			x += 1;
		case 4:
			y += 0.5;
			z += 0.5;
			X = side == 5 ? 1 : -1;
			break;
		}
		for (int j = 0; j < 8; ++j) {
			float f = (worldObj.rand.nextFloat() - 0.5f) * 0.37f;
			if (X != 0) f = Math.copySign(f, X);
			float f2 = (worldObj.rand.nextFloat() - 0.5f) * 0.37f;
			if (Z != 0) f2 = Math.copySign(f2, Z);
			worldObj.spawnParticle(impact, x, y, z, f, Y, f2);
		}
		if (!worldObj.isRemote)
			setDead();
	}

}
