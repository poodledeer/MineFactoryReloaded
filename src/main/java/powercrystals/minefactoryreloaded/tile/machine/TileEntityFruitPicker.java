package powercrystals.minefactoryreloaded.tile.machine;

import cofh.lib.util.position.Area;
import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.ReplacementBlock;
import powercrystals.minefactoryreloaded.core.FruitHarvestManager;
import powercrystals.minefactoryreloaded.core.HarvestMode;
import powercrystals.minefactoryreloaded.core.IHarvestManager;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiUpgradeable;
import powercrystals.minefactoryreloaded.gui.container.ContainerUpgradeable;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityFruitPicker extends TileEntityFactoryPowered {

	private IHarvestManager _treeManager;

	private Random _rand;

	public TileEntityFruitPicker() {

		super(Machine.FruitPicker);
		createHAM(this, 1);
		_rand = new Random();
		setManageSolids(true);
		setCanRotate(true);
	}

	@Override
	public void validate() {

		super.validate();
		if (!worldObj.isRemote) {
			_treeManager = new FruitHarvestManager(worldObj,
					new Area(new BlockPosition(this), 0, 0, 0),
					HarvestMode.FruitTree);
		}
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public ContainerUpgradeable getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerUpgradeable(this, inventoryPlayer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiUpgradeable(getContainer(inventoryPlayer), this);
	}

	@Override
	protected boolean activateMachine() {

		BlockPosition targetCoords = getNextTree();

		if (targetCoords == null) {
			setIdleTicks(getIdleTicksMax());
			return false;
		}

		Block harvestedBlock = worldObj.getBlock(targetCoords.x, targetCoords.y, targetCoords.z);
		int harvestedBlockMetadata = worldObj.getBlockMetadata(targetCoords.x, targetCoords.y, targetCoords.z);

		IFactoryFruit harvestable = MFRRegistry.getFruits().get(harvestedBlock);

		List<ItemStack> drops = harvestable.getDrops(worldObj, _rand,
			targetCoords.x, targetCoords.y, targetCoords.z);

		ReplacementBlock replacement = harvestable.getReplacementBlock(worldObj,
			targetCoords.x, targetCoords.y, targetCoords.z);

		harvestable.prePick(worldObj, targetCoords.x, targetCoords.y, targetCoords.z);

		if (replacement == null) {
			if (!worldObj.setBlockToAir(targetCoords.x, targetCoords.y, targetCoords.z))
				return false;
			if (MFRConfig.playSounds.getBoolean(true)) {
				worldObj.playAuxSFXAtEntity(null, 2001, targetCoords.x, targetCoords.y, targetCoords.z,
					Block.getIdFromBlock(harvestedBlock) + (harvestedBlockMetadata << 12));
			}
		} else {
			if (!replacement.replaceBlock(worldObj, targetCoords.x, targetCoords.y, targetCoords.z, null))
				return false;
		}

		doDrop(drops);

		// TODO: sludge?

		harvestable.postPick(worldObj, targetCoords.x, targetCoords.y, targetCoords.z);

		return true;
	}

	private BlockPosition getNextTree() {

		BlockPosition bp = _areaManager.getNextBlock();
		if (!worldObj.blockExists(bp.x, bp.y, bp.z)) {
			return null;
		}

		Block search = worldObj.getBlock(bp.x, bp.y, bp.z);

		if (!MFRRegistry.getFruitLogBlocks().contains(search)) {
			IFactoryFruit f = MFRRegistry.getFruits().get(search);
			return f != null && f.canBePicked(worldObj, bp.x, bp.y, bp.z) ? bp : null;
		}

		BlockPosition temp = getNextTreeSegment(bp);
		if (temp != null)
			_areaManager.rewindBlock();

		return temp;
	}

	private BlockPosition getNextTreeSegment(BlockPosition pos) {

		Block block;

		if (_treeManager.getIsDone() || !_treeManager.getOrigin().equals(pos)) {
			int lowerBound = 0;
			int upperBound = MFRConfig.fruitTreeSearchMaxVertical.getInt();

			Area a = new Area(pos.copy(), MFRConfig.fruitTreeSearchMaxHorizontal.getInt(), lowerBound, upperBound);

			_treeManager.reset(worldObj, a, HarvestMode.FruitTree, null);
		}

		Map<Block, IFactoryFruit> fruits = MFRRegistry.getFruits();
		while (!_treeManager.getIsDone()) {
			BlockPosition bp = _treeManager.getNextBlock();
			block = worldObj.getBlock(bp.x, bp.y, bp.z);
			IFactoryFruit fruit = fruits.containsKey(block) ? fruits.get(block) : null;

			if (fruit != null && fruit.canBePicked(worldObj, bp.x, bp.y, bp.z))
				return bp;

			_treeManager.moveNext();
		}
		return null;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 5;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {

		return slot == 0 && isUsableAugment(itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {

		return false;
	}

	@Override
	public int getUpgradeSlot() {

		return 0;
	}

	@Override
	public ForgeDirection getDropDirection() {

		return getDirectionFacing().getOpposite();
	}
}
