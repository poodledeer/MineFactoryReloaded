package powercrystals.minefactoryreloaded.tile.machine;

import static net.minecraftforge.fluids.FluidRegistry.WATER;

import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityWeather extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	protected int _canSeeSky = 0;
	protected boolean _canWeather = false, _willSnow = false, _openSky = false;
	protected BiomeGenBase _biome = null;

	public TileEntityWeather()
	{
		super(Machine.WeatherCollector);
		setManageSolids(true);
		_tanks[0].setLock(WATER);
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
	public int getWorkMax()
	{
		return 50;
	}

	@Override
	public int getIdleTicksMax()
	{
		return 600;
	}

	@Override
	public boolean activateMachine()
	{
		if (worldObj.getWorldInfo().isRaining())
		{
			l: {
				BiomeGenBase bgb = worldObj.getBiomeGenForCoords(xCoord, zCoord);

				if (_canWeather && _biome == bgb) break l;
				_biome = bgb;
				if (!bgb.canSpawnLightningBolt() && !bgb.getEnableSnow())
				{
					_canWeather = false;
					setIdleTicks(getIdleTicksMax());
					return false;
				}
				_canWeather = true;
				_willSnow = bgb.getFloatTemperature(xCoord, yCoord, zCoord) < 0.15F;
			}
			if (!canSeeSky())
			{
				setIdleTicks(getIdleTicksMax());
				return false;
			}
			if (!incrementWorkDone()) return false;
			if (getWorkDone() >= getWorkMax())
			{
				if (!_willSnow)
				{
					if (_tanks[0].getSpace() >= BUCKET_VOLUME &&
							_tanks[0].fill(FluidHelper.WATER, true) > 0)
					{
						setWorkDone(0);
						setIdleTicks(3);
						return true;
					}
					else
					{
						setWorkDone(getWorkMax());
						setIdleTicks(10);
						return false;
					}
				}
				else
				{
					doDrop(new ItemStack(Items.snowball, 3));
					setWorkDone(0);
					setIdleTicks(1);
				}
			}
			return true;
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}

	@Override
	public ForgeDirection getDropDirection()
	{
		return ForgeDirection.DOWN;
	}

	private boolean canSeeSky()
	{
		if (--_canSeeSky > 0) return _openSky;
		_canSeeSky = 70;
		int h = BlockHelper.getHighestY(worldObj, xCoord, zCoord);
		_openSky = true;
		for (int y = yCoord + 1; y < h; y++)
		{
			Block block = worldObj.getBlock(xCoord, y, zCoord);
			if (block.getCollisionBoundingBoxFromPool(worldObj, xCoord, y, zCoord) == null)
				continue;
			_openSky = false;
			break;
		}
		return _openSky;
	}

	@Override
	public int getSizeInventory()
	{
		return 0;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return 0;
	}

	@Override
	public boolean shouldPumpLiquid()
	{
		return true;
	}

	@Override
	public boolean allowBucketDrain(ItemStack stack)
	{
		return true;
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
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return true;
	}
}
