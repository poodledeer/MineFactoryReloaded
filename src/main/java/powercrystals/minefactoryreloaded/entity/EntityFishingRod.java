package powercrystals.minefactoryreloaded.entity;

import cofh.lib.util.helpers.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.FishingHooks;

import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class EntityFishingRod extends EntityThrowable {
	public int fuse;

	public EntityFishingRod(World world) {
		super(world);
		setSize(0.10F, 0.25F);
	}

	public EntityFishingRod(World world, EntityLivingBase entity) {
		super(world, entity);
		setSize(0.10F, 0.25F);
		setAir(0);
		fuse = 40;
	}

	@Override
	protected float func_70182_d() {
		return 0.6F;
	}

	@Override
	protected float func_70183_g() {
		return 0.0F;
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY -= 0.03999999910593033D;
		moveEntity(motionX, motionY, motionZ);
		motionX *= 0.9800000190734863D;
		motionY *= 0.9800000190734863D;
		motionZ *= 0.9800000190734863D;

		if (onGround) {
			motionX *= 0.699999988079071D;
			motionZ *= 0.699999988079071D;
			motionY *= -0.5D;
		}

		if (worldObj.isRemote) {
			worldObj.spawnParticle("smoke", posX, posY + 0.25, posZ, 0, 0, 0);
		} else if (fuse-- <= 0) {
			explode();
			setDead();
		} else if (fuse == 1) {
			if (worldObj instanceof WorldServer) {
				((WorldServer)worldObj).setEntityState(this, (byte)18);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleHealthUpdate(byte state) {
		super.handleHealthUpdate(state);
		if (state == 18) {
			Block block = worldObj.getBlock((int)Math.floor(posX), (int)Math.floor(posY + 0.25), (int)Math.floor(posZ));
			if (block.isAssociatedBlock(Blocks.water) || block.isAssociatedBlock(Blocks.flowing_water)) {
				String p = "blockdust_" + Block.getIdFromBlock(Blocks.water) + "_" + (0xFFFFFF);
				double f = 0.75;
				for (int j = 60; j --> 0; ) {
					double y = MathHelper.cos(j * Math.PI / 180) * 0.70;
					double m = MathHelper.sin((j * (.35)) * Math.PI / 180);
					for (int i = 60; i --> 0; ) {
						double x = MathHelper.cos((i * 6) * Math.PI / 180) * m;
						double z = MathHelper.sin((i * 6) * Math.PI / 180) * m;
						worldObj.spawnParticle(p, posX, posY + 0.25, posZ, x * f, y * f, z * f);
					}
				}
			}
		}
	}

	private void explode() {
		float f = 2.5F;
		worldObj.createExplosion(this, this.posX, this.posY, this.posZ, f, true);
		int rate = MFRConfig.fishingDropRate.getInt();
		for (float x = (float)(posX - f); x < posX + f; ++x)
			for (float y = (float)(posY - f); y < posY + f; ++y)
				for (float z = (float)(posZ - f); z < posZ + f; ++z) {
					Block block = worldObj.getBlock((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
					if (block.isAssociatedBlock(Blocks.water) || block.isAssociatedBlock(Blocks.flowing_water))
						if (rand.nextInt(rate) == 0) {
							EntityItem e = new EntityItem(worldObj, x, y, z);
							e.motionX = rand.nextGaussian() / 2;
							e.motionZ = rand.nextGaussian() / 2;
							e.motionY = 0.4 + (rand.nextDouble() - 0.4) / 2;
							ItemStack stack = FishingHooks.getRandomFishable(rand, 1), s;
							if (rand.nextInt(30) == 0 && (s = FurnaceRecipes.smelting().getSmeltingResult(stack)) != null) {
								stack = s;
							}
							e.setEntityItemStack(stack);
							worldObj.spawnEntityInWorld(e);
						}
				}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setByte("Fuse", (byte)fuse);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		fuse = tag.getByte("Fuse");
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) { }

	// TODO: override moveEntity, handle water movement

}
