package powercrystals.minefactoryreloaded.block;

import cofh.lib.util.position.IRotateableTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrill;

public class BlockFactoryMachine extends BlockFactory implements IRedNetOmniNode, ITileEntityProvider {

	private int _mfrMachineBlockIndex;

	public BlockFactoryMachine(int index) {

		super(1.5F);
		setBlockName("mfr.machine." + index);
		_mfrMachineBlockIndex = index;
		providesPower = true;
		setHarvestLevel("wrench", 0);
	}

	public int getBlockIndex() {

		return _mfrMachineBlockIndex;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		Machine.LoadTextures(_mfrMachineBlockIndex, ir);
	}

	@Override
	public IIcon getIcon(IBlockAccess iblockaccess, int x, int y, int z, int side) {

		int md = iblockaccess.getBlockMetadata(x, y, z);
		boolean isActive = false;
		TileEntity te = iblockaccess.getTileEntity(x, y, z);
		if (te instanceof TileEntityFactory) {
			side = ((TileEntityFactory) te).getRotatedSide(side);
			isActive = ((TileEntityFactory) te).isActive();
		}
		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, md).getIcon(side, isActive);
	}

	private static int[] itemRotation = { 0, 1, 3, 2, 5, 4 };

	@Override
	public IIcon getIcon(int side, int meta) {

		side = itemRotation[side];
		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, meta).getIcon(side, false);
	}

	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z) {

		if (world.getTileEntity(x, y, z) instanceof TileEntityLaserDrill) {
			return 0;
		}
		return super.getLightOpacity(world, x, y, z);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {

		TileEntity te = world.getTileEntity(x, y, z);

		if (te instanceof TileEntityFactory) {
			((TileEntityFactory) te).onNeighborTileChange(tileX, tileY, tileZ);
		}
	}

	private void dropContents(TileEntity te, ArrayList<ItemStack> list) {

		if (te instanceof IInventory) {
			World world = te.getWorldObj();
			IInventory inventory = ((IInventory) te);
			TileEntityFactoryInventory factoryInv = null;
			if (te instanceof TileEntityFactoryInventory)
				factoryInv = (TileEntityFactoryInventory) te;

			for (int i = inventory.getSizeInventory(); i-- > 0;) {
				if (factoryInv != null)
					if (!factoryInv.shouldDropSlotWhenBroken(i))
						continue;

				ItemStack itemstack = inventory.getStackInSlot(i);
				if (itemstack == null)
					continue;
				inventory.setInventorySlotContents(i, null);
				if (list != null) {
					list.add(itemstack);
				} else
					dropStack(world, te.xCoord, te.yCoord, te.zCoord, itemstack);
			}
		}
	}

	private void dropStack(World world, int x, int y, int z, ItemStack itemstack) {

		do {
			if (itemstack.stackSize <= 0)
				break;

			float xOffset = world.rand.nextFloat() * 0.8F + 0.1F;
			float yOffset = world.rand.nextFloat() * 0.8F + 0.1F;
			float zOffset = world.rand.nextFloat() * 0.8F + 0.1F;

			int amountToDrop = Math.min(world.rand.nextInt(21) + 10, itemstack.stackSize);

			EntityItem entityitem = new EntityItem(world,
					x + xOffset, y + yOffset, z + zOffset,
					itemstack.splitStack(amountToDrop));

			float motionMultiplier = 0.05F;
			entityitem.motionX = (float) world.rand.nextGaussian() * motionMultiplier;
			entityitem.motionY = (float) world.rand.nextGaussian() * motionMultiplier + 0.2F;
			entityitem.motionZ = (float) world.rand.nextGaussian() * motionMultiplier;

			world.spawnEntityInWorld(entityitem);
		} while (true);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block blockId, int meta) {

		TileEntity te = getTile(world, x, y, z);
		if (te != null) {
			dropContents(te, null); // TODO: rewrite drop logic

			if (te instanceof TileEntityFactoryInventory)
				((TileEntityFactoryInventory) te).onBlockBroken();
		}
		super.breakBlock(world, x, y, z, blockId, meta);
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock) {

		ArrayList<ItemStack> list = new ArrayList<ItemStack>(1);
		ItemStack machine = new ItemStack(getItemDropped(world.getBlockMetadata(x, y, z), world.rand, 0),
				1, damageDropped(world.getBlockMetadata(x, y, z)));
		list.add(machine);
		TileEntity te = getTile(world, x, y, z);
		if (te instanceof TileEntityBase) {
			dropContents(te, list);

			if (te instanceof TileEntityFactoryInventory)
				((TileEntityFactoryInventory) te).onDisassembled();

			NBTTagCompound tag = new NBTTagCompound();
			((TileEntityBase) te).writeItemNBT(tag);
			if (!tag.hasNoTags())
				machine.setTagCompound(tag);
		}
		world.setBlockToAir(x, y, z);
		if (!returnBlock)
			for (ItemStack stack : list)
				dropStack(world, x, y, z, stack);
		return list;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {

		return getTile(world, x, y, z) instanceof TileEntityFactory;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {

		super.onBlockPlacedBy(world, x, y, z, entity, stack);
		if (entity != null) {
			TileEntity te = getTile(world, x, y, z);
			if (te instanceof IRotateableTile)
				if (((IRotateableTile) te).canRotate())
					switch (MathHelper.floor_double((entity.rotationYaw * 4F) / 360F + 0.5D) & 3) {
					case 0:
						((IRotateableTile) te).rotateDirectlyTo(3);
						break;
					case 1:
						((IRotateableTile) te).rotateDirectlyTo(4);
						break;
					case 2:
						((IRotateableTile) te).rotateDirectlyTo(2);
						break;
					case 3:
						((IRotateableTile) te).rotateDirectlyTo(5);
						break;
					}

			if (te instanceof TileEntityFactory) {
				if (entity instanceof ICommandSender && entity.addedToChunk)
					((TileEntityFactory) te).setOwner(((ICommandSender) entity).getCommandSenderName());
				else
					((TileEntityFactory) te).setOwner(null);
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {

		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, meta).getNewTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride() {

		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {

		TileEntity te = getTile(world, x, y, z);
		if (te instanceof TileEntityFactoryInventory)
			return ((TileEntityFactoryInventory) te).getComparatorOutput(side);
		return 0;
	}

	@Override
	public boolean activated(World world, int x, int y, int z, EntityPlayer entityplayer, int side) {

		if (super.activated(world, x, y, z, entityplayer, side))
			return true;
		TileEntity te = getTile(world, x, y, z);
		if (te == null) {
			return false;
		}

		if (te instanceof TileEntityFactoryInventory) {
			if (((TileEntityFactoryInventory)te).acceptUpgrade(entityplayer.getHeldItem())) {
				if (entityplayer.capabilities.isCreativeMode) {
					++entityplayer.getHeldItem().stackSize;
				}
				if (entityplayer.getHeldItem().stackSize <= 0) {
					entityplayer.setCurrentItemOrArmor(0, null);
				}
				return true;
			}
		}

		if (te instanceof TileEntityFactory &&
				((TileEntityFactory) te).getContainer(entityplayer.inventory) != null) {
			if (!world.isRemote) {
				entityplayer.openGui(MineFactoryReloadedCore.instance(), 0, world, x, y, z);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityFactory) {
			return ((TileEntityFactory) te).getRedNetOutput(ForgeDirection.getOrientation(side));
		}
		return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {

		return isProvidingWeakPower(world, x, y, z, side);
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side) {

		return RedNetConnectionType.DecorativeSingle;
	}

	@Override
	public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side) {

		return null;
	}

	@Override
	public void onInputsChanged(World world, int x, int y, int z, ForgeDirection side, int[] inputValues) {

	}

	@Override
	public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet) {

		TileEntity te = getTile(world, x, y, z);
		if (te instanceof TileEntityFactory) {
			return ((TileEntityFactory) te).getRedNetOutput(side);
		}
		return 0;
	}

	@Override
	public void onInputChanged(World world, int x, int y, int z, ForgeDirection side, int inputValue) {

		TileEntity te = getTile(world, x, y, z);
		if (te instanceof TileEntityFactory) {
			((TileEntityFactory) te).onRedNetChanged(side, inputValue);
			onNeighborBlockChange(world, x, y, z, MFRThings.rednetCableBlock);
		}
	}
}
