package powercrystals.minefactoryreloaded.tile.machine;

import cofh.lib.util.WeightedRandomItemStack;
import cofh.lib.util.helpers.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IFactoryLaserTarget;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiLaserDrill;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerLaserDrill;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TileEntityLaserDrill extends TileEntityFactoryInventory implements IFactoryLaserTarget {

	private static final int _energyPerWork = Machine.LaserDrillPrecharger.getActivationEnergy() * 4;
	private static final int _energyStoredMax = 1000000;

	private int color = 0xFFFFFF;

	private int _energyStored;

	private int _workStoredMax = MFRConfig.laserdrillCost.getInt();
	private float _workStored;

	private int _bedrockLevel;

	private Random _rand;

	public static boolean canReplaceBlock(Block block, World world, int x, int y, int z) {

		return block == null || block.getBlockHardness(world, x, y, z) == 0 || block.isAir(world, x, y, z);
	}

	public TileEntityLaserDrill() {

		super(Machine.LaserDrill);
		_rand = new Random();
		setManageSolids(true);
	}

	@Override
	public ContainerFactoryInventory getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerLaserDrill(this, inventoryPlayer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiLaserDrill(getContainer(inventoryPlayer), this);
	}

	@Override
	public boolean canFormBeamWith(ForgeDirection from) {

		return from.ordinal() > 1 && from.ordinal() < 6;
	}

	@Override
	public int addEnergy(ForgeDirection from, int energy, boolean simulate) {

		if (!canFormBeamWith(from))
			return energy;
		int energyToAdd = Math.min(energy, _energyStoredMax - _energyStored);
		if (!simulate)
			_energyStored += energyToAdd;
		return energy - energyToAdd;
	}

	@Override
	public void updateEntity() {

		if (isInvalid() || worldObj.isRemote) {
			return;
		}

		super.updateEntity();

		if (hasDrops())
			return;

		if (shouldCheckDrill()) {
			updateDrill();
		}

		Block lowerId = worldObj.getBlock(xCoord, yCoord - 1, zCoord);

		if (_bedrockLevel < 0) {
			if (lowerId.equals(MFRThings.fakeLaserBlock)) {
				worldObj.setBlockToAir(xCoord, yCoord - 1, zCoord);
			}
			return;
		}

		if (!lowerId.equals(MFRThings.fakeLaserBlock) &&
				canReplaceBlock(lowerId, worldObj, xCoord, yCoord - 1, zCoord)) {
			worldObj.setBlock(xCoord, yCoord - 1, zCoord, MFRThings.fakeLaserBlock);
		}

		int energyToDraw = Math.min(_energyPerWork, _energyStored);
		float energyPerWorkHere = _energyPerWork * (1.2f - 0.4f * Math.min(yCoord - _bedrockLevel, 128f) / 128f);

		float workDone = energyToDraw / energyPerWorkHere;
		_workStored += workDone;
		_energyStored -= workDone * energyPerWorkHere;

		while (_workStored >= _workStoredMax) {
			_workStored -= _workStoredMax;
			doDrop(getRandomDrop());
		}
	}

	public int getWorkDone() {

		return (int) _workStored;
	}

	public void setWorkDone(int work) {

		_workStored = work;
	}

	public int getWorkMax() {

		return _workStoredMax;
	}

	public int getEnergyStored() {

		return _energyStored;
	}

	public void setEnergyStored(int energy) {

		_energyStored = energy;
	}

	public int getEnergyMax() {

		return _energyStoredMax;
	}

	private boolean shouldCheckDrill() {

		return worldObj.getTotalWorldTime() % 32 == 0;
	}

	private void updateDrill() {

		int y = Integer.MAX_VALUE;
		for (y = yCoord; y-- > 0;) {
			Block block = worldObj.getBlock(xCoord, y, zCoord);
			if (!block.equals(MFRThings.fakeLaserBlock)) {
				if (!block.isAir(worldObj, xCoord, y, zCoord) &&
						canReplaceBlock(block, worldObj, xCoord, y, zCoord))
					if (worldObj.func_147480_a(xCoord, y, zCoord, true))
						continue;

				if (block.isAssociatedBlock(Blocks.bedrock)) {
					_bedrockLevel = y;
					return;
				} else if (!worldObj.isAirBlock(xCoord, y, zCoord)) {
					_bedrockLevel = -1;
					return;
				}

			}
		}

		_bedrockLevel = 0;
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();

		int r = 0, g = 0, b = 0, d = 0;
		for (ItemStack s : _inventory) {
			++d;
			if (s == null || !s.getItem().equals(MFRThings.laserFocusItem)) {
				r += 255;
				g += 255;
				b += 255;
				continue;
			}
			int c = MFRUtil.COLORS[s.getItemDamage()];
			r += (c >> 16) & 255;
			g += (c >> 8) & 255;
			b += (c >> 0) & 255;
		}
		if (d == 0) {
			return;
		}
		r /= d;
		g /= d;
		b /= d;
		color = (r << 16) | (g << 8) | b;
		if (worldObj != null) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	public int getColor() {

		return color;
	}

	@Override
	protected void writePacketData(NBTTagCompound tag) {

		super.writePacketData(tag);

		tag.setInteger("color", color);
	}

	@Override
	protected void readPacketData(NBTTagCompound tag) {

		super.readPacketData(tag);

		color = tag.getInteger("color");
	}

	private ItemStack getRandomDrop() {

		List<WeightedRandomItemStack> drops = new LinkedList<WeightedRandomItemStack>();
		int boost = WeightedRandom.getTotalWeight(MFRRegistry.getLaserOres()) / 30;

		for (WeightedRandom.Item i : MFRRegistry.getLaserOres()) {
			WeightedRandomItemStack oldStack = (WeightedRandomItemStack) i;
			WeightedRandomItemStack newStack = new WeightedRandomItemStack(oldStack.getStack(), oldStack.itemWeight);
			drops.add(newStack);
			for (ItemStack s : _inventory) {
				if (s == null || !s.getItem().equals(MFRThings.laserFocusItem) ||
						MFRRegistry.getLaserPreferredOres(s.getItemDamage()) == null) {
					continue;
				}

				List<ItemStack> preferredOres = MFRRegistry.getLaserPreferredOres(s.getItemDamage());
				int realBoost = boost / Math.max(1, preferredOres.size() / 2) + 1;

				for (ItemStack preferredOre : preferredOres) {
					if (UtilInventory.stacksEqual(newStack.getStack(), preferredOre)) {
						newStack.itemWeight += realBoost;
					}
				}
			}
		}

		return ((WeightedRandomItemStack) WeightedRandom.getRandomItem(_rand, drops)).getStack();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {

		return INFINITE_EXTENT_AABB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {

		return 65536;
	}

	public boolean shouldDrawBeam() {

		if (shouldCheckDrill()) {
			updateDrill();
		}
		return _bedrockLevel >= 0;
	}

	public int getBeamHeight() {

		return yCoord - _bedrockLevel;
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);

		if (_energyStored > 0)
			tag.setInteger("energyStored", _energyStored);
		if (!MathHelper.between(-1e-5, _workStored, 1e-5))
			tag.setFloat("workDone", _workStored);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);

		_energyStored = Math.min(tag.getInteger("energyStored"), _energyStoredMax);
		_workStored = Math.min(tag.getFloat("workDone"), getWorkMax());
	}

	@Override
	public int getSizeInventory() {

		return 6;
	}

	@Override
	public int getInventoryStackLimit() {

		return 1;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {

		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {

		return false;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {

		return entityplayer.getDistanceSq(xCoord, yCoord, zCoord) <= 64;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {

		return false;
	}
}
