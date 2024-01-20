package powercrystals.minefactoryreloaded.block;

import cofh.api.block.IDismantleable;
import cofh.core.render.hitbox.ICustomHitBox;
import cofh.core.render.hitbox.RenderHitbox;
import cofh.lib.util.position.IRotateableTile;
import cofh.repack.codechicken.lib.raytracer.IndexedCuboid6;
import cofh.repack.codechicken.lib.raytracer.RayTracer;
import cofh.repack.codechicken.lib.vec.BlockCoord;
import cofh.repack.codechicken.lib.vec.Vector3;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetConnection;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.IEntityCollidable;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.ITraceable;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;

public class BlockFactory extends Block implements IRedNetConnection, IDismantleable
{
	protected boolean providesPower;

	protected BlockFactory(float hardness)
	{
		super(Machine.MATERIAL);
		setHardness(hardness);
		setStepSound(soundTypeMetal);
		setCreativeTab(MFRCreativeTab.tab);
		setHarvestLevel("pickaxe", 0);
	}

	protected BlockFactory(Material material)
	{
		super(material);
		setCreativeTab(MFRCreativeTab.tab);
		setHarvestLevel("pickaxe", 0);
	}

	protected static final TileEntity getTile(World world, int x, int y, int z)
	{
		return MFRUtil.getTile(world, x, y, z);
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
	{ // HACK: called before block is destroyed by the player prior to the player getting the drops. destroy block here.
		// hack is needed because the player sets the block to air *before* getting the drops. woo good logic from mojang.
		if (!player.capabilities.isCreativeMode)
		{
			harvesters.set(player);
			dropBlockAsItem(world, x, y, z, meta, EnchantmentHelper.getFortuneModifier(player));
			harvesters.set(null);
			world.setBlock(x, y, z, Blocks.air, 0, 7);
		}
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta)
	{
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis)
	{
		if (world.isRemote)
		{
			return false;
		}
		TileEntity te = getTile(world, x, y, z);
		if (te instanceof IRotateableTile)
		{
			IRotateableTile tile = ((IRotateableTile)te);
			if (tile.canRotate(axis))
			{
				tile.rotate(axis);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		TileEntity te = getTile(world, x, y, z);

		if (te instanceof TileEntityBase && stack.getTagCompound() != null)
		{
			te.readFromNBT(stack.getTagCompound());
		}
	}

	@Override
	public final boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset)
	{
		PlayerInteractEvent e = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, x, y, z, side, world);
		if (MinecraftForge.EVENT_BUS.post(e) || e.getResult() == Result.DENY || e.useBlock == Result.DENY)
			return false;

		activationOffsets(xOffset, yOffset, zOffset);
		return activated(world, x, y, z, player, side);
	}

	protected void activationOffsets(float xOffset, float yOffset, float zOffset) {}

	protected boolean activated(World world, int x, int y, int z, EntityPlayer player, int side)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null)
		{
			return false;
		}
		ItemStack ci = player.inventory.getCurrentItem();
		if (ci != null && te instanceof ITankContainerBucketable)
		{
			boolean isFluidContainer = ci.getItem() instanceof IFluidContainerItem;
			if ((isFluidContainer || FluidContainerRegistry.isEmptyContainer(ci)) &&
					((ITankContainerBucketable)te).allowBucketDrain(ci))
			{
				if (MFRLiquidMover.manuallyDrainTank((ITankContainerBucketable)te, player))
				{
					return true;
				}
			}
			if ((isFluidContainer || FluidContainerRegistry.isFilledContainer(ci)) &&
					((ITankContainerBucketable)te).allowBucketFill(ci))
			{
				if (MFRLiquidMover.manuallyFillTank((ITankContainerBucketable)te, player))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z,
			boolean returnBlock)
	{
		ArrayList<ItemStack> list = getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);

		world.setBlockToAir(x, y, z);
		if (!returnBlock)
            for (ItemStack item : list)
                    dropBlockAsItem(world, x, y, z, item);
		return list;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		ItemStack machine = new ItemStack(getItemDropped(metadata, world.rand, fortune), 1,
				damageDropped(metadata));

		TileEntity te = getTile(world, x, y, z);
		if (te instanceof TileEntityBase)
		{
			NBTTagCompound tag = new NBTTagCompound();
			((TileEntityBase)te).writeItemNBT(tag);
			if (!tag.hasNoTags())
				machine.setTagCompound(tag);
		}

		drops.add(machine);
		return drops;
	}

	public void getBlockInfo(IBlockAccess world, int x, int y, int z, ForgeDirection side,
			EntityPlayer player, List<IChatComponent> info, boolean debug)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityBase)
			((TileEntityBase)tile).getTileInfo(info, side, player, debug);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		onNeighborBlockChange(world, x, y, z, this);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block blockId)
	{
		super.onNeighborBlockChange(world, x, y, z, blockId);
		if (world.isRemote)
		{
			return;
		}

		TileEntity te = getTile(world, x, y, z);
		if (te instanceof TileEntityBase)
		{
			if (blockId != this)
				((TileEntityBase)te).onNeighborBlockChange();
			else
				((TileEntityBase)te).onMatchedNeighborBlockChange();
		}
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
    {
		TileEntity te = world instanceof World ? getTile((World)world, x, y, z) : world.getTileEntity(x, y, z);

		if (te instanceof TileEntityBase)
		{
			((TileEntityBase)te).onNeighborTileChange(tileX, tileY, tileZ);
		}
    }

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		TileEntity te = getTile(world, x, y, z);
		if (te instanceof IEntityCollidable)
		{
			float shrinkAmount = 0.125F;
			return AxisAlignedBB.getBoundingBox(x + shrinkAmount, y + shrinkAmount, z + shrinkAmount,
					x + 1 - shrinkAmount, y + 1 - shrinkAmount, z + 1 - shrinkAmount);
		}
		else
		{
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (world.isRemote)
			return;

		TileEntity te = getTile(world, x, y, z);
		if (te instanceof IEntityCollidable)
			((IEntityCollidable)te).onEntityCollided(entity);

		super.onEntityCollidedWithBlock(world, x, y, z, entity);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB collisionTest, List collisionBoxList,
			Entity entity)
	{
		TileEntity te = getTile(world, x, y, z);
		if (te instanceof ITraceable)
		{
			List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
			((ITraceable)te).addTraceableCuboids(cuboids, false, false);
			for (IndexedCuboid6 c : cuboids)
			{
				AxisAlignedBB aabb = c.toAABB();
				if (collisionTest.intersectsWith(aabb))
					collisionBoxList.add(aabb);
			}
		}
		else
		{
			super.addCollisionBoxesToList(world, x, y, z, collisionTest, collisionBoxList, entity);
		}
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end)
	{
		if (world.isRemote) {
			harvesters.set(MineFactoryReloadedCore.proxy.getPlayer());
		}
		MovingObjectPosition r = collisionRayTrace((IBlockAccess)world, x, y, z, start, end);
		if (world.isRemote) {
			harvesters.set(null);
		}
		return r;
	}

	public MovingObjectPosition collisionRayTrace(IBlockAccess world, int x, int y, int z, Vec3 start, Vec3 end)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof ITraceable)
		{
			List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
			((ITraceable)te).addTraceableCuboids(cuboids, true, MFRUtil.isHoldingUsableTool(harvesters.get(), x, y, z));
			return RayTracer.instance().rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids,
				new BlockCoord(x, y, z), this);
		}
		else if (world instanceof World)
		{
			return super.collisionRayTrace((World)world, x, y, z, start, end);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onBlockHighlight(DrawBlockHighlightEvent event) {
		EntityPlayer player = event.player;
		World world = player.worldObj;
		MovingObjectPosition omop = event.target;
		harvesters.set(player);
		MovingObjectPosition mop = omop;//RayTracer.reTrace(world, player);
		harvesters.set(null);
		if (mop == null)
			return;
		if (mop.typeOfHit != MovingObjectType.BLOCK || omop.typeOfHit != MovingObjectType.BLOCK)
			return;
		int x = mop.blockX, y = mop.blockY, z = mop.blockZ;
		TileEntity te = getTile(world, x, y, z);
		if (te instanceof ITraceable) {
			int subHit = mop.subHit;
			if (te instanceof ICustomHitBox)
			{
				ICustomHitBox tile = ((ICustomHitBox)te);
				if (tile.shouldRenderCustomHitBox(subHit, player))
				{
					event.setCanceled(true);
					RenderHitbox.drawSelectionBox(player, mop, event.partialTicks, tile.getCustomHitBox(subHit, player));
					return;
				}
			}
			event.context.drawSelectionBox(player, mop, 0, event.partialTicks);
			event.setCanceled(true);
		}
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
	{
		return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
	{
		return isProvidingWeakPower(world, x, y, z, side);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}

	@Override
	public boolean isNormalCube()
	{
		return !providesPower;
	}

	@Override
	public boolean canProvidePower()
	{
		return providesPower;
	}

	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		blockIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side)
	{
		if (providesPower)
			return RedNetConnectionType.DecorativeSingle;
		else
			return RedNetConnectionType.ForcedDecorativeSingle;
	}
}
