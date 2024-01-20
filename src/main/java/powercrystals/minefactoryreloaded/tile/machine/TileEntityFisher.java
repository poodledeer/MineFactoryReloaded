package powercrystals.minefactoryreloaded.tile.machine;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.position.Area;
import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.FishingHooks;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFisher;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityFisher extends TileEntityFactoryPowered {

	public static final int workBase = 1800;

	protected boolean _isJammed = true, _needItem = false;
	protected byte _speed, _luck;
	protected int _workNeeded = workBase, boost;
	protected float _next = Float.NaN;
	protected Random _rand = null;

	public TileEntityFisher() {

		super(Machine.Fisher);
		createHAM(this, 1);
		setManageSolids(true);
	}

	@Override
	public void cofh_validate() {

		super.cofh_validate();
		if (_rand == null) {
			_rand = new Random(worldObj.getSeed() ^ worldObj.rand.nextLong());
			_next = _rand.nextFloat();
		}
		validateLocation();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFisher getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerFisher(this, inventoryPlayer);
	}

	@Override
	public ForgeDirection getDirectionFacing() {

		return ForgeDirection.DOWN;
	}

	@Override
	public boolean activateMachine() {

		if (worldObj.getTotalWorldTime() % 137 == 0) {
			validateLocation();
		}
		if (_isJammed | _needItem)
			return false;

		if (!incrementWorkDone()) return false;

		if (getWorkDone() > getWorkMax()) {
			setInventorySlotContents(0, ItemHelper.damageItem(_inventory[0], 1, _rand));
			ItemStack stack = FishingHooks.getRandomFishable(_rand, _next, _luck, _speed);
			if (_inventory[0] == null && isItemValidForSlot(0, stack)) {
				setInventorySlotContents(0, stack);
			} else {
				doDrop(stack);
			}
			_next = _rand.nextFloat();
			setWorkDone(0);
		}
		return true;
	}

	protected void validateLocation() {

		Area fishingHole = _areaManager.getHarvestArea();
		int extraBlocks = 0;
		for (BlockPosition bp : fishingHole.getPositionsBottomFirst()) {
			if (!isValidBlock(bp.x, bp.y, bp.z)) {
				_isJammed = true;
				setIdleTicks(getIdleTicksMax());
				return;
			} else if (isValidBlock(bp.x, bp.y - 1, bp.z)) {
				++extraBlocks;
			}
			if ((bp.x != xCoord) | bp.z != zCoord) {
				if (isValidBlock(bp.x - (xCoord - bp.x), bp.y, bp.z - (zCoord - bp.z))) {
					++extraBlocks;
				} else if (isFisher(bp.x - (xCoord - bp.x), bp.y, bp.z - (zCoord - bp.z)))
					extraBlocks -= 18;
				if (isFisher(bp.x - (xCoord - bp.x), bp.y, bp.z))
					extraBlocks -= 18;
				if (isFisher(bp.x, bp.y, bp.z - (zCoord - bp.z)))
					extraBlocks -= 18;
			} else if (isValidBlock(bp.x, bp.y - 2, bp.z)) {
				++extraBlocks;
			}
		}
		_workNeeded = workBase - extraBlocks * 50;
		_isJammed = false;
	}

	protected boolean isFisher(int x, int y, int z) {

		if (y == yCoord - 1 && !(x == xCoord && z == zCoord)) {
			if (worldObj.getBlockMetadata(x, yCoord, z) == _machine.getMeta() &&
					worldObj.getBlock(x, yCoord, z) == _machine.getBlock())
				return true;
		}
		return false;
	}

	protected boolean isValidBlock(int x, int y, int z) {

		if (!worldObj.blockExists(x, y, z) || isFisher(x, y, z))
			return false;
		int meta = worldObj.getBlockMetadata(x, y, z);
		if (meta != 0) return false;
		Block block = worldObj.getBlock(x, y, z);
		return block.isAssociatedBlock(Blocks.water) || block.isAssociatedBlock(Blocks.flowing_water);
	}

	@Override
	public ForgeDirection getDropDirection() {

		return ForgeDirection.UP;
	}

	@Override
	public int getWorkMax() {

		return _workNeeded - boost;
	}

	@SideOnly(Side.CLIENT)
	public void setWorkMax(int work) {

		_workNeeded = work;
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		boost = 0;
		_needItem = false;
		if (!client && _inventory[0] != null) {
			_luck = (byte) EnchantmentHelper.getEnchantmentLevel(Enchantment.field_151370_z.effectId, _inventory[0]);
			_speed = (byte) EnchantmentHelper.getEnchantmentLevel(Enchantment.field_151369_A.effectId, _inventory[0]);
			boost = 75 * _speed + 75;
		} else {
			_needItem = MFRConfig.fisherNeedsRod.getBoolean(false);
			if (_needItem) {
				setIdleTicks(getIdleTicksMax());
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		if (tag.hasKey("workNeeded"))
			_workNeeded = tag.getInteger("workNeeded");
		if (tag.hasKey("jam"))
			_isJammed = tag.getBoolean("jam");
		if (tag.hasKey("seed"))
			_rand = new Random(tag.getLong("seed"));
		if (tag.hasKey("next"))
			_next = tag.getFloat("next");
	}

	private static Field _Random_seed = null;
	static {
		try {
			Field f = Random.class.getDeclaredField("seed");
			f.setAccessible(true);
			_Random_seed = f;
		} catch (Throwable ignored) {
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);
		tag.setInteger("workNeeded", _workNeeded);
		tag.setBoolean("jam", _isJammed);
		tag.setFloat("next", _next);
		if (_Random_seed != null)
			try {
				tag.setLong("seed", ((AtomicLong) _Random_seed.get(_rand)).get());
			} catch (Throwable e) {
			}
	}

	@Override
	public int getIdleTicksMax() {

		return 200;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return stack == null || (stack.isItemStackDamageable() && stack.getItem().isItemTool(stack) && Enchantment.field_151369_A.canApply(stack));
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

}
