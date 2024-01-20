package powercrystals.minefactoryreloaded.block.transport;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class BlockRailPassengerDropoff extends BlockFactoryRail {

	public BlockRailPassengerDropoff() {

		super(true, false);
		setBlockName("mfr.rail.passenger.dropoff");
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart minecart, int x, int y, int z) {

		if (world.isRemote)
			return;

		Class<? extends EntityLivingBase> target = isPowered(world, x, y, z) ? EntityLiving.class : EntityPlayer.class;
		if (!target.isInstance(minecart.riddenByEntity))
			return;

		Entity player = minecart.riddenByEntity;
		AxisAlignedBB dropCoords = findSpaceForPlayer(player, x, y, z, world);
		if (dropCoords == null)
			return;

		player.mountEntity(null);
		MineFactoryReloadedCore.proxy.movePlayerToCoordinates((EntityLivingBase) player,
			dropCoords.minX + (dropCoords.maxX - dropCoords.minX) / 2,
			dropCoords.minY,
			dropCoords.minZ + (dropCoords.maxZ - dropCoords.minZ) / 2);
	}

	private AxisAlignedBB findSpaceForPlayer(Entity entity, int x, int y, int z, World world) {

		final int searchX = MFRConfig.passengerRailSearchMaxHorizontal.getInt();
		final int searchY = MFRConfig.passengerRailSearchMaxVertical.getInt() * 2;

		AxisAlignedBB bb = entity.boundingBox.getOffsetBoundingBox(0, 0, 0);

		final double halfX = (bb.maxX - bb.minX) / 2;
		final double halfZ = (bb.maxZ - bb.minZ) / 2;

		bb.offset(x - bb.minX + .5 - halfX, y - bb.minY + 0.05, z - bb.minZ + .5 - halfZ);

		AxisAlignedBB home = bb.copy();

		bb.offset(0, -(searchY >> 1), 0);
		for (int offsetY = -searchY; offsetY <= searchY; offsetY++) {
			bb.offset(-searchX, 0, 0);
			for (int offsetX = -searchX; offsetX <= searchX; offsetX++) {
				bb.offset(0, 0, -searchX);
				for (int offsetZ = -searchX; offsetZ <= searchX; offsetZ++) {

					if (world.func_147461_a(bb).isEmpty()
							&& !isBadBlockToStandIn(world, bb)
							&& !world.isAnyLiquid(bb)) {
						int targetX = MathHelper.floor_double(bb.minX + halfX);
						int targetY = MathHelper.floor_double(bb.minY);
						int targetZ = MathHelper.floor_double(bb.minZ + halfZ);

						if (!isBadBlockToStandOn(world, targetX, targetY - 1, targetZ))
							return bb;
					}
					bb.offset(0, 0, 1);
				}
				bb.offset(1, 0, -searchX - 1);
			}
			bb.offset(-searchX - 1, 0.5, 0);
		}

		return null;
	}

	private boolean isBadBlockToStandOn(World world, int x, int y, int z) {

		Block block = world.getBlock(x, y, z);
		if (block.isAir(world, x, y, z)
				|| null == block.getCollisionBoundingBoxFromPool(world, x, y, z)) {
			return true;
		}
		return false;
	}

	private boolean isBadBlockToStandIn(World world, AxisAlignedBB bb) {

		int i = MathHelper.floor_double(bb.minX);
		int j = MathHelper.floor_double(bb.maxX + 1.0D);
		int k = MathHelper.floor_double(bb.minY);
		int l = MathHelper.floor_double(bb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(bb.minZ);
		int j1 = MathHelper.floor_double(bb.maxZ + 1.0D);

		if (bb.minX < 0.0D) {
			--i;
		}

		if (bb.minY < 0.0D) {
			--k;
		}

		if (bb.minZ < 0.0D) {
			--i1;
		}

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = k; l1 < l; ++l1) {
				for (int i2 = i1; i2 < j1; ++i2) {
					Block block = world.getBlock(k1, l1, i2);

					if (block == Blocks.fire ||
							block.getMaterial().isLiquid() ||
							block.isBurning(world, k1, l1, i2) ||
							BlockRailBase.func_150051_a(block)) {

						return true;
					}
				}
			}
		}

		return false;
	}

}
