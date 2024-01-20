package powercrystals.minefactoryreloaded.block.decor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.block.BlockFactory;

public class BlockDecorativeStone extends BlockFactory {

	public static final String[] _names = new String[] { "black.smooth", "white.smooth", "black.cobble",
			"white.cobble", "black.brick.large", "white.brick.large", "black.brick.small",
			"white.brick.small", "black.gravel", "white.gravel", "black.paved", "white.paved" };
	private IIcon[] _icons = new IIcon[_names.length];

	public BlockDecorativeStone() {

		super(Material.rock);
		setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(Blocks.stone.stepSound);
		setBlockName("mfr.decorative.stone");
		providesPower = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		for (int i = 0; i < _icons.length; i++) {
			_icons[i] = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + "." + _names[i]);
		}
	}

	@Override
	public int damageDropped(int meta) {

		if (meta == 0 | meta == 1) {
			meta += 2; // smooth -> cobble
		}
		return meta;
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock) {

		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		int meta = world.getBlockMetadata(x, y, z);
		list.add(new ItemStack(getItemDropped(meta, world.rand, 0), quantityDropped(world.rand), meta)); // persist metadata

		world.setBlockToAir(x, y, z);
		if (!returnBlock)
			for (ItemStack item : list)
				dropBlockAsItem(world, x, y, z, item);
		return list;
	}

	@Override
	public IIcon getIcon(int side, int meta) {

		return _icons[Math.min(meta, _icons.length)];
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {

		world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {

		world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {

		if (!world.isRemote) {
			tryToFall(world, x, y, z);
		}
	}

	private void tryToFall(World world, int x, int y, int z) {

		int meta = world.getBlockMetadata(x, y, z);
		if (meta != 8 & meta != 9)
			return;
		if (BlockFalling.func_149831_e(world, x, y - 1, z) && y >= 0) {
			byte b0 = 32;

			if (!BlockSand.fallInstantly && world.checkChunksExist(x - b0, y - b0, z - b0, x + b0, y + b0, z + b0)) {
				if (!world.isRemote) {
					EntityFallingBlock entityfallingsand = new EntityFallingBlock(world, x + 0.5d, y + 0.5d, z + 0.5d, this, meta);
					world.spawnEntityInWorld(entityfallingsand);
				}
			} else {
				world.setBlockToAir(x, y, z);

				while (BlockFalling.func_149831_e(world, x, y - 1, z) && y > 0) {
					--y;
				}

				if (y > 0) {
					world.setBlock(x, y, z, this, meta, 3);
				}
			}
		}
	}

	@Override
	public int tickRate(World par1World) {

		return 2;
	}

	// TODO: step sounds require forge hook

}
