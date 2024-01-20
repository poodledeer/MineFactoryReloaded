package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.position.Area;
import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiSewer;
import powercrystals.minefactoryreloaded.gui.container.ContainerSewer;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TileEntitySewer extends TileEntityFactoryInventory implements ITankContainerBucketable {

	private boolean _jammed;
	private int _tick;
	private long _nextSewerCheckTick;

	public TileEntitySewer() {

		super(Machine.Sewer);
		createHAM(this, 0, 1, 0, false);
		_areaManager.setOverrideDirection(ForgeDirection.UP);
		_tanks[0].setLock(FluidRegistry.getFluid("sewage"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiSewer(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerSewer getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerSewer(this, inventoryPlayer);
	}

	@Override
	protected boolean shouldPumpLiquid() {

		return true;
	}

	@Override
	public void updateEntity() {

		super.updateEntity();
		if (worldObj.isRemote) {
			return;
		}
		_tick++;

		if (_nextSewerCheckTick <= worldObj.getTotalWorldTime()) {
			Area a = new Area(BlockPosition.fromRotateableTile(this), _areaManager.getRadius(), 2, 2);
			_jammed = false;
			for (BlockPosition bp : a.getPositionsBottomFirst()) {
				if (worldObj.getBlock(bp.x, bp.y, bp.z).equals(_machine.getBlock()) &&
						worldObj.getBlockMetadata(bp.x, bp.y, bp.z) == _machine.getMeta() &&
						!(bp.x == xCoord && bp.y == yCoord && bp.z == zCoord)) {
					_jammed = true;
					break;
				}
			}

			_nextSewerCheckTick = worldObj.getTotalWorldTime() + 800 + worldObj.rand.nextInt(800);
		}

		if (_tick >= 31 && !_jammed) {
			_tick = 0;
			double massFound = 0;
			long worldTime = worldObj.getTotalWorldTime();
			AxisAlignedBB box = _areaManager.getHarvestArea().toAxisAlignedBB();
			l: {
				int maxAmount = _tanks[1].getSpace();
				if (maxAmount <= 0) {
					break l;
				}

				List<EntityXPOrb> entities = worldObj.getEntitiesWithinAABB(EntityXPOrb.class, box);
				for (EntityXPOrb orb : entities) {
					if (!orb.isDead) {
						if (MFRLiquidMover.fillTankWithXP(_tanks[1], orb) == 0)
							break;
					}
				}
			}

			List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
			for (EntityLivingBase o : entities) {
				if (o instanceof EntityAnimal || o instanceof EntityVillager || (o.isSneaking() && o instanceof EntityPlayer)) {
					if (o.getEntityData().getLong("mfr:sewerTime") > worldTime) {
						continue;
					}
					o.getEntityData().setLong("mfr:sewerTime", worldTime + 30);
					massFound += Math.pow(o.boundingBox.getAverageEdgeLength(), 2);
				}
			}

			if (massFound > 0) {
				_tanks[0].fill(FluidRegistry.getFluidStack("sewage", (int) (25 * massFound)), true);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);

		tag.setBoolean("jammed", _jammed);
		tag.setByte("tick", (byte) _tick);
		tag.setLong("next", _nextSewerCheckTick);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);

		_jammed = tag.getBoolean("jammed");
		_tick = tag.hasKey("tick") ? tag.getByte("tick") : MathHelper.RANDOM.nextInt(32);
		_nextSewerCheckTick = tag.getLong("next");
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		return 0;
	}

	@Override
	public boolean allowBucketDrain(ItemStack stack) {

		return true;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		return drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		return drain(resource, doDrain);
	}

	@Override
	protected FluidTankAdv[] createTanks() {

		return new FluidTankAdv[] { new FluidTankAdv(BUCKET_VOLUME),
				new FluidTankAdv(BUCKET_VOLUME * 4) };
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public int getUpgradeSlot() {

		return 0;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {

		return slot == 0 && isUsableAugment(itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {

		return false;
	}

}
