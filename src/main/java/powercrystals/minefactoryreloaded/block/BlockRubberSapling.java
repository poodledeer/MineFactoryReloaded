package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.terraingen.TerrainGen;

import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen;
import powercrystals.minefactoryreloaded.world.WorldGenMassiveTree;
import powercrystals.minefactoryreloaded.world.WorldGenRubberTree;

public class BlockRubberSapling extends BlockSapling implements IRedNetNoConnection {

	private static WorldGenRubberTree treeGen = new WorldGenRubberTree(true);

	public BlockRubberSapling() {

		setHardness(0.0F);
		setStepSound(soundTypeGrass);
		setBlockName("mfr.rubberwood.sapling");
		setCreativeTab(MFRCreativeTab.tab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {

		blockIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}

	@Override
	public IIcon getIcon(int side, int metadata) {

		return blockIcon;
	}

	@Override
	public void func_149878_d(World world, int x, int y, int z, Random rand) {

		if (world.isRemote || !TerrainGen.saplingGrowTree(world, rand, x, y, z))
			return;

		int meta = damageDropped(world.getBlockMetadata(x, y, z));
		world.setBlockToAir(x, y, z);

		switch (meta) {
		case 1:
			if (MineFactoryReloadedWorldGen.generateSacredSpringRubberTree(world, rand, x, y, z))
				return;
			break;
		case 2:
			if (MineFactoryReloadedWorldGen.generateMegaRubberTree(world, rand, x, y, z, true))
				return;
			break;
		case 3:
			if (new WorldGenMassiveTree().setSloped(true).generate(world, rand, x, y, z))
				return;
			break;
		default:
		case 0:
			BiomeGenBase b = world.getBiomeGenForCoords(x, z);
			if (b != null && b.biomeName.toLowerCase(Locale.US).contains("mega"))
				if (rand.nextInt(50) == 0)
					if (MineFactoryReloadedWorldGen.generateMegaRubberTree(world, rand, x, y, z, true))
						return;
			if (treeGen.growTree(world, rand, x, y, z))
				return;
			break;
		}
		world.setBlock(x, y, z, this, meta, 4);
	}

	@Override
	public int damageDropped(int par1) {

		return par1 & 7;
	}

}
