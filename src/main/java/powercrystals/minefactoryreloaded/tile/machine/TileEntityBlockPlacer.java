package powercrystals.minefactoryreloaded.tile.machine;

import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityBlockPlacer extends TileEntityFactoryPowered {

	public TileEntityBlockPlacer() {

		super(Machine.BlockPlacer);
		setManageSolids(true);
		setCanRotate(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerFactoryPowered(this, inventoryPlayer);
	}

	@Override
	public int getSizeInventory() {

		return 9;
	}

	@Override
	protected boolean activateMachine() {

		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = _inventory[i];
			if (stack == null || !(stack.getItem() instanceof ItemBlock))
				continue;

			ItemBlock item = (ItemBlock) stack.getItem();
			Block block = item.field_150939_a;

			BlockPosition bp = BlockPosition.fromRotateableTile(this);
			bp.moveForwards(1);
			if (worldObj.isAirBlock(bp.x, bp.y, bp.z) &&
					block.canPlaceBlockOnSide(worldObj, bp.x, bp.y, bp.z, 0)) {
				int j1 = item.getMetadata(stack.getItemDamage());
				int meta = block.onBlockPlaced(worldObj, bp.x, bp.y, bp.z, 0, bp.x, bp.y, bp.z, j1);
				if (worldObj.setBlock(bp.x, bp.y, bp.z, block, meta, 3)) {
					block.onBlockPlacedBy(worldObj, bp.x, bp.y, bp.z, FakePlayerFactory.getMinecraft((WorldServer) worldObj), stack);
					block.onPostBlockPlaced(worldObj, bp.x, bp.y, bp.z, meta);
					if (MFRConfig.playSounds.getBoolean(true)) {
						worldObj.playSoundEffect(bp.x + 0.5, bp.y + 0.5, bp.z + 0.5,
							block.stepSound.func_150496_b(),
							(block.stepSound.getVolume() + 1.0F) / 2.0F,
							block.stepSound.getPitch() * 0.8F);
					}
					decrStackSize(i, 1);
					return true;
				}
			}
		}
		setIdleTicks(getIdleTicksMax());
		return false;
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
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {

		return itemstack != null && itemstack.getItem() instanceof ItemBlock;
	}

}
