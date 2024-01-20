package powercrystals.minefactoryreloaded.item.tool;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.item.base.ItemFactoryTool;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class ItemSpyglass extends ItemFactoryTool {

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			MovingObjectPosition mop = rayTrace();
			if (mop == null || (mop.typeOfHit == MovingObjectType.ENTITY && mop.entityHit == null)) {
				player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.spyglass.nosight"));
			} else if(mop.typeOfHit == MovingObjectType.ENTITY) {
				player.addChatMessage(new ChatComponentText("")
						.appendText(StatCollector
								.translateToLocalFormatted("chat.info.mfr.spyglass.hitentity",
										getEntityName(mop.entityHit),
										mop.entityHit.posX, mop.entityHit.posY, mop.entityHit.posZ)));
			} else {
				Block block = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
				ItemStack tempStack = null;
				if (block != null)
					tempStack = block.getPickBlock(mop, world, mop.blockX, mop.blockY, mop.blockZ, player);
				if (tempStack == null)
					tempStack = new ItemStack(block, 1, world.getBlockMetadata(mop.blockX, mop.blockY,
							mop.blockZ));
				if (tempStack.getItem() != null) {
					player.addChatMessage(new ChatComponentText("")
							.appendText(StatCollector
									.translateToLocalFormatted("chat.info.mfr.spyglass.hitblock",
											tempStack.getDisplayName(),
						Block.blockRegistry.getNameForObject(world.getBlock(mop.blockX, mop.blockY, mop.blockZ)),
											world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ),
											(float)mop.blockX, (float)mop.blockY, (float)mop.blockZ)));
				} else {
					player.addChatMessage(new ChatComponentText("")
							.appendText(StatCollector
									.translateToLocalFormatted("chat.info.mfr.spyglass.hitunknown",
										(float)mop.blockX, (float)mop.blockY, (float)mop.blockZ)));
				}
			}
		}

		return super.onItemRightClick(stack, world, player);
	}

	private String getEntityName(Entity entity) {
		String name = EntityList.getEntityString(entity);
		return name != null ? StatCollector.translateToLocal("entity." + name + ".name") : "Unknown Entity";
	}

	private MovingObjectPosition rayTrace() {
		if (Minecraft.getMinecraft().renderViewEntity == null || Minecraft.getMinecraft().theWorld == null) {
			return null;
		}

		double range = MFRConfig.spyglassRange.getInt();
		MovingObjectPosition objHit = Minecraft.getMinecraft().renderViewEntity.rayTrace(range, 1.0F);
		double blockDist = range;
		Vec3 playerPos = Minecraft.getMinecraft().renderViewEntity.getPosition(1.0F);

		if (objHit != null) {
			if (objHit.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
				objHit = null;
			} else {
				blockDist = objHit.hitVec.distanceTo(playerPos);
			}
		}

		Vec3 playerLook = Minecraft.getMinecraft().renderViewEntity.getLook(1.0F);
		Vec3 playerLookRel = playerPos.addVector(playerLook.xCoord * range, playerLook.yCoord * range, playerLook.zCoord * range);
		List<?> list = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABBExcludingEntity(
				Minecraft.getMinecraft().renderViewEntity,
				Minecraft.getMinecraft().renderViewEntity.boundingBox.addCoord(playerLook.xCoord * range, playerLook.yCoord * range, playerLook.zCoord * range).expand(1, 1, 1));

		double entityDistTotal = blockDist;
		Entity pointedEntity = null;
		for (int i = 0; i < list.size(); ++i) {
			Entity entity = (Entity)list.get(i);

			if (entity.canBeCollidedWith()) {
				double entitySize = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.boundingBox.expand(entitySize, entitySize, entitySize);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(playerPos, playerLookRel);

				if (axisalignedbb.isVecInside(playerPos)) {
					if (0.0D < entityDistTotal || entityDistTotal == 0.0D) {
						pointedEntity = entity;
						entityDistTotal = 0.0D;
					}
				} else if (movingobjectposition != null) {
					double entityDist = playerPos.distanceTo(movingobjectposition.hitVec);

					if (entityDist < entityDistTotal || entityDistTotal == 0.0D) {
						pointedEntity = entity;
						entityDistTotal = entityDist;
					}
				}
			}
		}

		if (pointedEntity != null && (entityDistTotal < blockDist || objHit == null)) {
			objHit = new MovingObjectPosition(pointedEntity);
		}
		return objHit;
	}

	@Override
	protected int getWeaponDamage(ItemStack stack) {
		return 2;
	}

}
