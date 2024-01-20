package powercrystals.minefactoryreloaded.block.transport;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class BlockRailPassengerPickup extends BlockFactoryRail {

	public BlockRailPassengerPickup() {

		super(true, false);
		setBlockName("mfr.rail.passenger.pickup");
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart minecart, int x, int y, int z) {

		if (world.isRemote | minecart.riddenByEntity != null || !minecart.canBeRidden())
			return;

		int searchX = MFRConfig.passengerRailSearchMaxHorizontal.getInt();
		int searchY = MFRConfig.passengerRailSearchMaxVertical.getInt();
		AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(x - searchX, y - searchY, z - searchX,
			x + searchX + 1, y + searchY + 1, z + searchX + 1);

		Class<? extends EntityLivingBase> target = isPowered(world, x, y, z) ? EntityLiving.class : EntityPlayer.class;
		List<? extends EntityLivingBase> entities = world.getEntitiesWithinAABB(target, bb);

		for (EntityLivingBase o : entities)
			if (!o.isDead & o.ridingEntity == null && o.getHealth() > 0) {
				o.mountEntity(minecart);
				return;
			}
	}

}
