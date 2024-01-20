package powercrystals.minefactoryreloaded.block.decor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.block.BlockFactory;

public class BlockDecorativeBricks extends BlockFactory
{
	public static final String[] _names = new String [] { "ice", "glowstone", "lapis", "obsidian", "pavedstone", "snow",
		"ice_large", "glowstone_large", "lapis_large", "obsidian_large", "pavedstone_large", "snow_large",
		"meat.raw", "meat.cooked", "brick_large", "sugar_charcoal" };
	private IIcon[] _icons = new IIcon[_names.length];

	public BlockDecorativeBricks()
	{
		super(Material.rock);
		setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(Blocks.stone.stepSound);
		setBlockName("mfr.decorative.brick");
		providesPower = false;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return meta == 1 | meta == 7 ? 15 : 0;
	}

	@Override
	public float getExplosionResistance(Entity e, World world, int x, int y, int z, double eX, double eY, double eZ)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return meta == 3 | meta == 9 ? Blocks.obsidian.getExplosionResistance(e) : getExplosionResistance(e);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister ir)
	{
		for(int i = 0; i < _icons.length; i++)
		{
			_icons[i] = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + "." + _names[i]);
		}
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		return _icons[Math.min(meta, _icons.length - 1)];
	}
}
