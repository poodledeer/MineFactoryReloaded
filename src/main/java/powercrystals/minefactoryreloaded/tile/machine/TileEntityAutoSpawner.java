package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankAdv;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IMobSpawnHandler;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiAutoSpawner;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoSpawner;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityAutoSpawner extends TileEntityFactoryPowered implements ITankContainerBucketable {

	protected static final int _spawnRange = 4;

	protected boolean _spawnExact = false;
	protected int _spawnCost = 0;
	protected Entity _spawn = null;
	protected ItemStack _lastSpawnStack = null;

	public TileEntityAutoSpawner() {

		super(Machine.AutoSpawner);
		setManageSolids(true);
		createHAM(this, _spawnRange, 0, 2, false);
		_areaManager.setOverrideDirection(ForgeDirection.UP);
		_tanks[0].setLock(FluidRegistry.getFluid("mobessence"));
	}

	public boolean getSpawnExact() {

		return _spawnExact;
	}

	public void setSpawnExact(boolean spawnExact) {

		_spawn = null;
		_spawnExact = spawnExact;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiAutoSpawner(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerAutoSpawner getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerAutoSpawner(this, inventoryPlayer);
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	protected int getSpawnCost() {

		return _spawnExact ? MFRConfig.autospawnerCostExact.getInt() : MFRConfig.autospawnerCostStandard.getInt();
	}

	protected int getSpawnCost(Entity e, String id) {

		int r = MFRRegistry.getBaseSpawnCost(id);

		if (e instanceof EntityLiving) {
			EntityLiving el = (EntityLiving) e;
			int t = Math.abs(el.experienceValue) + 1;
			r += t + t / 3;

			ItemStack[] aitemstack = el.getLastActiveItems();
			for (int j = 0; j < aitemstack.length; ++j)
				if (aitemstack[j] != null && el.equipmentDropChances[j] <= 1.0F)
					r += 1 + 4;

			r = Math.max(r, 4);
		}

		return (int) ((Math.max(r - 1, 0) + 1) * 66.66666667f * getSpawnCost()) / 10;
	}

	@Override
	protected boolean activateMachine() {

		ItemStack item = getStackInSlot(0);
		if (item == null || !canInsertItem(0, item, 6)) {
			setWorkDone(0);
			setIdleTicks(getIdleTicksMax());
			return false;
		}
		NBTTagCompound itemTag = item.getTagCompound();

		if (_spawn == null) {
			String entityID = itemTag.getString("id");
			boolean isBlackListed = MFRRegistry.getAutoSpawnerBlacklist().contains(entityID);
			blackList: if (!isBlackListed) {
				Class<?> e = (Class<?>) EntityList.stringToClassMapping.get(entityID);
				if (e == null) {
					isBlackListed = true;
					break blackList;
				}
				for (Class<?> t : MFRRegistry.getAutoSpawnerClassBlacklist()) {
					if (t.isAssignableFrom(e)) {
						isBlackListed = true;
						break blackList;
					}
				}
			}
			if (isBlackListed) {
				setWorkDone(0);
				return false;
			}

			Entity spawnedEntity = _spawn = EntityList.createEntityByName(entityID, worldObj);

			if (!(spawnedEntity instanceof EntityLivingBase)) {
				_spawn = null;
				return false;
			}

			EntityLivingBase spawnedLiving = (EntityLivingBase) spawnedEntity;

			if (_spawnExact) {
				NBTTagCompound tag = (NBTTagCompound) itemTag.copy();
				spawnedLiving.readEntityFromNBT(tag);
				for (int i = 0; i < 5; ++i) {
					if (spawnedLiving instanceof EntityLiving)
						((EntityLiving) spawnedLiving).setEquipmentDropChance(i, Float.NEGATIVE_INFINITY);
				}
			}

			IMobSpawnHandler handler = MFRRegistry.getSpawnHandlers().get(spawnedLiving.getClass());

			if (!_spawnExact) {
				if (spawnedLiving instanceof EntityLiving)
					((EntityLiving) spawnedLiving).onSpawnWithEgg(null);
				if (handler != null)
					handler.onMobSpawn(spawnedLiving);
			} else {
				if (handler != null)
					handler.onMobExactSpawn(spawnedLiving);
			}

			_spawnCost = getSpawnCost(_spawn, entityID);
		}

		if (getWorkDone() < getWorkMax()) {
			if (drain(_tanks[0], 10, false) == 10) {
				drain(_tanks[0], 10, true);
				setWorkDone(getWorkDone() + 1);
				return true;
			} else {
				return false;
			}
		}
		else {
			Entity spawnedEntity = _spawn;
			_spawn = null;

			if (!(spawnedEntity instanceof EntityLivingBase)) {
				return false;
			}

			EntityLivingBase spawnedLiving = (EntityLivingBase) spawnedEntity;

			double x = xCoord + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * _spawnRange;
			double y = yCoord + worldObj.rand.nextInt(3) - 1;
			double z = zCoord + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * _spawnRange;

			spawnedLiving.setLocationAndAngles(x, y, z, worldObj.rand.nextFloat() * 360.0F, 0.0F);

			if (!worldObj.checkNoEntityCollision(spawnedLiving.boundingBox) ||
					!worldObj.getCollidingBoundingBoxes(spawnedLiving, spawnedLiving.boundingBox).isEmpty() ||
					(worldObj.isAnyLiquid(spawnedLiving.boundingBox) != (spawnedLiving instanceof EntityWaterMob))) {
				setIdleTicks(10);
				return false;
			}

			worldObj.spawnEntityInWorld(spawnedLiving);
			worldObj.playAuxSFX(2004, this.xCoord, this.yCoord, this.zCoord, 0);

			if (spawnedLiving instanceof EntityLiving) {
				((EntityLiving) spawnedLiving).spawnExplosionParticle();
				((EntityLiving) spawnedLiving).setCanPickUpLoot(false);
			}
			setWorkDone(0);
			return true;
		}
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		if (!internalChange && !UtilInventory.stacksEqual(_lastSpawnStack, _inventory[0])) {
			setWorkDone(0);
			setIdleTicks(getIdleTicksMax());
		}
		_lastSpawnStack = _inventory[0];
	}

	@Override
	public void setWorkDone(int w) {

		if (w == 0) {
			_spawn = null;
			_spawnCost = 0;
		}
		super.setWorkDone(w);
	}

	@SideOnly(Side.CLIENT)
	public void setWorkMax(int a) {

		_spawnCost = a;
	}

	@Override
	public int getWorkMax() {

		return _spawnCost;
	}

	@Override
	public int getIdleTicksMax() {

		return 7 * 20;
	}

	@Override
	protected FluidTankAdv[] createTanks() {

		return new FluidTankAdv[] { new FluidTankAdv(BUCKET_VOLUME * 4) };
	}

	@Override
	public boolean allowBucketFill(ItemStack stack) {

		return true;
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
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {

		return ItemSafariNet.isSafariNet(itemstack) &&
				!ItemSafariNet.isSingleUse(itemstack) &&
				!ItemSafariNet.isEmpty(itemstack);
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setBoolean("spawnExact", _spawnExact);
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		setSpawnExact(tag.getBoolean("spawnExact"));
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag) {

		super.writeItemNBT(tag);
		if (_spawnExact)
			tag.setBoolean("spawnExact", _spawnExact);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		_spawnExact = tag.getBoolean("spawnExact");
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return false;
	}
}
