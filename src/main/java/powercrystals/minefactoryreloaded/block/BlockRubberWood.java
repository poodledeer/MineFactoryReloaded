package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class BlockRubberWood extends BlockLog implements IRedNetDecorative
{
	private IIcon _iconLogTop;
	private IIcon _iconLogSide;

	public BlockRubberWood()
	{
		setBlockName("mfr.rubberwood.log");
		setCreativeTab(MFRCreativeTab.tab);
		setHarvestLevel("axe", 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		_iconLogSide = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".side");
		_iconLogTop = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected IIcon getSideIcon(int par1)
	{
		return _iconLogSide;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected IIcon getTopIcon(int par1)
	{
		return _iconLogTop;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		drops.add(new ItemStack(this, 1, 0));
		if((metadata & 3) == 1)
			drops.add(new ItemStack(MFRThings.rawRubberItem,
					fortune <= 0 ? 1 : 1 + world.rand.nextInt(fortune)));

		return drops;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face)
	{
		return super.getFireSpreadSpeed(world, x, y, z, face) * ((world.getBlockMetadata(x, y, z) & 3) + 1);
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
	{
		return super.getFlammability(world, x, y, z, face) * ((world.getBlockMetadata(x, y, z) & 3) + 1);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item blockId, CreativeTabs tab, List subBlocks)
	{
		subBlocks.add(new ItemStack(blockId, 1, 0));
		subBlocks.add(new ItemStack(blockId, 1, 1));
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}
}
