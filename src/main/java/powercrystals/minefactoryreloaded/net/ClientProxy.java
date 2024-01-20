package powercrystals.minefactoryreloaded.net;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		super.init();
		MineFactoryReloadedClient.init();
	}

	@Override
	public EntityPlayer getPlayer() {

		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public void movePlayerToCoordinates(EntityLivingBase e, double x, double y, double z)
	{
		e.setPositionAndUpdate(x, y, z);
	}
}
