package powercrystals.minefactoryreloaded.core;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public interface IUseable
{
	public MovingObjectPosition rayTrace(World world, EntityLivingBase entity, boolean adjacent);
	public boolean addUseHandler(IUseHandler handler);
	public boolean removeUseHandler(IUseHandler handler);
}
