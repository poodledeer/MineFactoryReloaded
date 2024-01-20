package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.WeightedRandomItemStack;
import cofh.lib.util.position.Area;
import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntitySludgeBoiler extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private Random _rand;
	private int _tick;
	private Area _area;

	public TileEntitySludgeBoiler()
	{
		super(Machine.SludgeBoiler);
		setManageSolids(true);
		_activeSyncTimeout = 5;
		_rand = new Random();
		_tanks[0].setLock(FluidRegistry.getFluid("sludge"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerFactoryPowered(this, inventoryPlayer);
	}

	@Override
	public void validate()
	{
		super.validate();
		_area = new Area(new BlockPosition(this), 3, 3, 3);
	}

	@Override
	public int getWorkMax()
	{
		return 100;
	}

	@Override
	public int getIdleTicksMax()
	{
		return 1;
	}

	@Override
	protected boolean activateMachine()
	{
		if (drain(_tanks[0], 10, false) == 10)
		{
			if (!incrementWorkDone()) return false;
			drain(_tanks[0], 10, true);
			_tick++;

			if (getWorkDone() >= getWorkMax())
			{
				ItemStack s = ((WeightedRandomItemStack)WeightedRandom.getRandomItem(_rand, MFRRegistry.getSludgeDrops())).getStack();

				doDrop(s);

				setWorkDone(0);
			}

			if (_tick >= 23)
			{
				List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, _area.toAxisAlignedBB());
				for (EntityLivingBase ent : entities)
				{
					ent.addPotionEffect(new PotionEffect(Potion.hunger.id, 20 * 20, 0));
					ent.addPotionEffect(new PotionEffect(Potion.poison.id, 6 * 20, 0));
				}
				_tick = 0;
			}
			return true;
		}
		return false;
	}

	@Override
	protected boolean updateIsActive(boolean failedDrops)
	{
		return super.updateIsActive(failedDrops) && drain(_tanks[0], 10, false) == 10;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void machineDisplayTick()
	{
		int s = Minecraft.getMinecraft().gameSettings.particleSetting;
		if (s < 2 && isActive())
		{
			int color = MFRThings.sludgeLiquid.color;
			for (int a = 8 >> s, i = 4 >> s;
					i --> 0; )
				worldObj.spawnParticle(_rand.nextInt(a) == 0 ? "mobSpell" : "mobSpellAmbient",
						_area.xMin + _rand.nextFloat() * (_area.xMax - _area.xMin),
						_area.yMin + _rand.nextFloat() * (_area.yMax - _area.yMin),
						_area.zMin + _rand.nextFloat() * (_area.zMax - _area.zMin),
						((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f);
		}
	}

	@Override
	public ForgeDirection getDropDirection()
	{
		return ForgeDirection.DOWN;
	}

	@Override
	public boolean allowBucketFill(ItemStack stack)
	{
		return true;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		return drain(resource, doDrain);
	}

	@Override
	protected FluidTankAdv[] createTanks()
	{
		return new FluidTankAdv[]{new FluidTankAdv(4 * BUCKET_VOLUME)};
	}

	@Override
	public int getSizeInventory()
	{
		return 0;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return false;
	}
}
