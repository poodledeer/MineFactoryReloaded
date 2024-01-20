package powercrystals.minefactoryreloaded.tile.machine;

import cofh.api.item.IAugmentItem;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.position.Area;
import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import powercrystals.minefactoryreloaded.core.FluidFillingManager;
import powercrystals.minefactoryreloaded.core.IHarvestManager;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiUpgradeable;
import powercrystals.minefactoryreloaded.gui.container.ContainerFountain;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityFountain extends TileEntityFactoryPowered implements ITankContainerBucketable {

	private IHarvestManager _fillingManager;
	private boolean _reverse;

	public TileEntityFountain() {

		super(Machine.Fountain);
		createHAM(this, 0, 0, 0, false);
		_areaManager.setOverrideDirection(ForgeDirection.UP);
		_areaManager.setUpgradeVertical(true);
		_reverse = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiUpgradeable(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFountain getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerFountain(this, inventoryPlayer);
	}

	@Override
	protected boolean activateMachine() {

		int idleTicks = 5;
		l: {
			if (_reverse ? _tanks[0].getSpace() >= BUCKET_VOLUME :
					(_tanks[0].getFluidAmount() >= BUCKET_VOLUME &&
					_tanks[0].getFluid().getFluid().canBePlacedInWorld())) {
				int x = xCoord, y = yCoord + 1, z = zCoord;
				if (_fillingManager != null) {
					if (_fillingManager.getIsDone())
						onFactoryInventoryChanged();
					BlockPosition bp = _fillingManager.getNextBlock();
					x = bp.x;
					y = bp.y;
					z = bp.z;
					_fillingManager.moveNext();
				}
				if (!worldObj.blockExists(x, y, z)) break l;

				Block block = worldObj.getBlock(x, y, z);
				if (_reverse) {
					idleTicks = 10;
					l2: if (block != null && block.getMaterial().isLiquid())
						if (block instanceof IFluidBlock) {
							IFluidBlock fluidBlock = ((IFluidBlock) block);
							if (!fluidBlock.canDrain(worldObj, x, y, z))
								break l;
							FluidStack fluid = fluidBlock.drain(worldObj, x, y, z, false);
							int amt = _tanks[0].fill(fluid, false);
							if (amt != fluid.amount) break l2;
							_tanks[0].fill(fluidBlock.drain(worldObj, x, y, z, true), true);
							setIdleTicks(5);
							return true;
						}
						else if (block instanceof BlockLiquid) {
							if (worldObj.getBlockMetadata(x, y, z) != 0)
								break l;
							boolean drained = false;
							if (block.equals(Blocks.water) || block.equals(Blocks.flowing_water)) {
								if (_tanks[0].fill(new FluidStack(FluidRegistry.WATER, BUCKET_VOLUME), true) != 0)
									drained = true;
							} else if (block.equals(Blocks.lava) || block.equals(Blocks.flowing_lava))
								if (_tanks[0].fill(new FluidStack(FluidRegistry.LAVA, BUCKET_VOLUME), true) != 0)
									drained = true;
							if (drained) {
								worldObj.setBlockToAir(x, y, z);
								setIdleTicks(5);
								return true;
							}
						}
				}
				else if (block == null || block.isReplaceable(worldObj, x, y, z)) {
					if (block != null && block.getMaterial().isLiquid())
						if (block instanceof BlockFluidClassic) {
							if (((BlockFluidClassic) block).isSourceBlock(worldObj, x, y, z))
								break l;
						}
						else if (block instanceof BlockLiquid) {
							if (worldObj.getBlockMetadata(x, y, z) == 0)
								break l;
						}
					block = _tanks[0].getFluid().getFluid().getBlock();
					if (worldObj.setBlock(x, y, z, block)) {// TODO: when forge supports NBT fluid blocks, adapt this
						worldObj.notifyBlockOfNeighborChange(x, y, z, block);
						drain(_tanks[0], BUCKET_VOLUME, true);
						setIdleTicks(1);
						return true;
					}
				}
			}
			if (_fillingManager != null)
				_fillingManager.free();
			idleTicks = getIdleTicksMax();
		}
		setIdleTicks(idleTicks);
		return false;
	}

	@Override
	protected FluidTankAdv[] createTanks() {

		return new FluidTankAdv[] { new FluidTankAdv(BUCKET_VOLUME * 32) };
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		_reverse = false;
		if (isUsableAugment(_inventory[0])) {
			IAugmentItem upgrade = (IAugmentItem) _inventory[0].getItem();
			int r = upgrade.getAugmentLevel(_inventory[0], "radius");
			if (r > 0) {
				_areaManager.setUpgradeLevel(r);
				Area area = new Area(new BlockPosition(xCoord, yCoord + 1, zCoord), r, 0, r * 2);
				if (_fillingManager == null)
					_fillingManager = new FluidFillingManager(worldObj, area);
				else
					_fillingManager.reset(worldObj, area, null, null);
			} else if (r < 0) {
				_reverse = true;
				r = -r;
				if (r > 1) {
					_areaManager.setUpgradeLevel(r - 1);
					Area area = new Area(new BlockPosition(xCoord, yCoord + 1, zCoord), r, 0, r * 2);
					if (_fillingManager == null)
						_fillingManager = new FluidFillingManager(worldObj, area);
					else
						_fillingManager.reset(worldObj, area, null, null);
				}
			} else {
				_fillingManager = null;
				_areaManager.setUpgradeLevel(0);
			}
		} else {
			_fillingManager = null;
			_areaManager.setUpgradeLevel(0);
		}
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		return fill(resource, doFill);
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
	public boolean allowBucketDrain(ItemStack stack) {

		return true;
	}

	@Override
	public boolean allowBucketFill(ItemStack stack) {

		return true;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 20;
	}

	@Override
	public int getUpgradeSlot() {

		return 0;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal) {

		if (stack != null) {
			if (slot == 0) {
				return isUsableAugment(stack);
			}
		}
		return false;
	}
}
