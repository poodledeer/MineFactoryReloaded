package powercrystals.minefactoryreloaded.world;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;

import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityGrinder;

import skyboy.core.world.WorldServerProxy;

public class GrindingWorldServer extends WorldServerProxy
{
	protected TileEntityFactoryPowered grinder;
	protected boolean allowSpawns;
	protected ArrayList<Entity> entitiesToGrind = new ArrayList<Entity>();

	public GrindingWorldServer(WorldServer world, TileEntityFactoryPowered grinder)
	{
		this(world, grinder, false);
	}

	public GrindingWorldServer(WorldServer world, TileEntityFactoryPowered grinder, boolean allowSpawns)
	{
		super(world);
		this.grinder = grinder;
		this.allowSpawns = allowSpawns;
	}

	public void setAllowSpawns(boolean allow)
	{
		this.allowSpawns = allow;
	}

	public void setMachine(TileEntityFactoryPowered machine)
	{
		this.grinder = machine;
	}

	@Override
	public boolean spawnEntityInWorld(Entity entity)
	{
		if (grinder != null)
		{
			if (entity instanceof EntityItem)
			{
				if (grinder.manageSolids())
				{
					ItemStack drop = ((EntityItem)entity).getEntityItem();
					if (drop != null) grinder.doDrop(drop);
				}
				entity.setDead();
				return true;
			}
			else if (entity instanceof EntityXPOrb)
			{
				EntityXPOrb orb = (EntityXPOrb)entity;
				if (grinder instanceof TileEntityGrinder)
				{
					((TileEntityGrinder)grinder).acceptXPOrb(orb);
				}
				entity.setDead();
				return true; // consume any orbs not made into essence
			}
		}

		if (allowSpawns)
		{
			entity.worldObj = this.proxiedWorld;
			return super.spawnEntityInWorld(entity);
		}
		entity.setDead();
		return true;
	}

	public boolean addEntityForGrinding(Entity entity)
	{
		cofh_updateProps();
		if (difficultySetting == EnumDifficulty.PEACEFUL)
			difficultySetting = EnumDifficulty.EASY;
		if(entity.worldObj == this) return true;
		if(entity.worldObj == this.proxiedWorld)
		{
			entity.worldObj = this;
			entitiesToGrind.add(entity);
			return true;
		}
		return false;
	}

	public void clearReferences()
	{
		for(Entity ent : entitiesToGrind)
		{
			if(ent.worldObj == this) ent.worldObj = this.proxiedWorld;
		}
		entitiesToGrind.clear();
	}

	public void cleanReferences()
	{
		for(int i = entitiesToGrind.size(); i --> 0;)
		{
			Entity ent = entitiesToGrind.get(i);
			if(ent.isDead) entitiesToGrind.remove(ent);
		}
	}

}
