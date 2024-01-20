package powercrystals.minefactoryreloaded.block.transport;

import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.renderIdPPipe;
import static powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable._subSideMappings;

import cofh.api.block.IBlockInfo;
import cofh.lib.util.helpers.ItemHelper;
import cofh.repack.codechicken.lib.raytracer.RayTracer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.render.block.PlasticPipeRenderer;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe;

public class BlockPlasticPipe extends BlockFactory implements IBlockInfo, ITileEntityProvider {

	public BlockPlasticPipe() {

		super(0.8F);
		setBlockName("mfr.cable.plastic");
		providesPower = true;
	}

	@Override
	public boolean activated(World world, int x, int y, int z, EntityPlayer player, int side) {

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityPlasticPipe) {
			TileEntityPlasticPipe cable = (TileEntityPlasticPipe) te;

			harvesters.set(player);
			MovingObjectPosition part = collisionRayTrace(world, x, y, z, RayTracer.getStartVec(player), RayTracer.getEndVec(player));
			harvesters.set(null);
			if (part == null)
				return false;

			int subHit = part.subHit;
			if (subHit < 0) {
				MineFactoryReloadedCore.instance().getLogger().error("subHit was " + subHit, new Throwable());
				return false;
			}
			side = _subSideMappings[subHit];

			ItemStack s = player.getCurrentEquippedItem();

			l2: if (cable.onPartHit(player, side, subHit)) {
				if (MFRUtil.isHoldingUsableTool(player, x, y, z)) {
					MFRUtil.usedWrench(player, x, y, z);
				}
			}
			else if (s != null && s.isItemEqual(new ItemStack(Blocks.redstone_torch))) {
				int t = cable.getUpgrade();
				if (t != 0) {
					if (t == 1) break l2;
					if (t == 2)
						dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.redstone_block));
				}
				if (!world.isRemote) {
					if (!player.capabilities.isCreativeMode)
						player.setCurrentItemOrArmor(0, ItemHelper.consumeItem(s));
					cable.setUpgrade(1);
					onNeighborBlockChange(world, x, y, z, Blocks.air);
					player.addChatMessage(new ChatComponentTranslation(
							"chat.info.mfr.fluid.install.torch"));
				}
				return true;
			}
			else if (s != null && s.isItemEqual(new ItemStack(Blocks.redstone_block))) {
				int t = cable.getUpgrade();
				if (t != 0) {
					if (t == 2) break l2;
					if (t == 1)
						dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.redstone_torch));
				}
				if (!world.isRemote) {
					if (!player.capabilities.isCreativeMode)
						player.setCurrentItemOrArmor(0, ItemHelper.consumeItem(s));
					cable.setUpgrade(2);
					onNeighborBlockChange(world, x, y, z, Blocks.air);
					player.addChatMessage(new ChatComponentTranslation(
							"chat.info.mfr.fluid.install.block"));
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata,
			int fortune) {

		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		ItemStack machine = new ItemStack(getItemDropped(metadata, world.rand, fortune), 1,
				damageDropped(metadata));
		drops.add(machine);

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityPlasticPipe) {
			switch (((TileEntityPlasticPipe) te).getUpgrade()) {
			case 1:
				drops.add(new ItemStack(Blocks.redstone_torch));
				break;
			case 2:
				drops.add(new ItemStack(Blocks.redstone_block));
				break;
			}

		}

		return drops;
	}

	@Override
	public boolean isOpaqueCube() {

		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {

		return false;
	}

	@Override
	public int getRenderType() {

		return renderIdPPipe;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		blockIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		PlasticPipeRenderer.updateUVT(blockIcon);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {

		return new TileEntityPlasticPipe();
	}
}
