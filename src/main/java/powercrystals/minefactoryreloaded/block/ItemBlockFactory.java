package powercrystals.minefactoryreloaded.block;

import static powercrystals.minefactoryreloaded.item.base.ItemMulti.getName;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import powercrystals.minefactoryreloaded.core.MFRUtil;

public class ItemBlockFactory extends ItemBlock
{
	protected String[] _names = {null};
	protected int metaOffset = 0;

	public ItemBlockFactory(Block p_i45328_1_)
	{
		super(p_i45328_1_);
		setMaxDamage(0);
	}

	public ItemBlockFactory(Block p_i45328_1_, String[] names)
	{
		this(p_i45328_1_);
		setNames(names);
	}

	public ItemBlockFactory(Block p_i45328_1_, Integer metaOffset, String[] names)
	{
		this(p_i45328_1_, names);
		this.metaOffset = metaOffset.intValue();
	}

	public ItemBlockFactory(Block p_i45328_1_, Integer metaOffset)
	{
		this(p_i45328_1_);
		this.metaOffset = metaOffset.intValue();
	}

	protected void setNames(String[] names)
	{
		_names = names;
		setHasSubtypes(true);
	}

	protected String name(ItemStack stack)
	{
		return _names[Math.min(stack.getItemDamage(), _names.length - 1)];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		return field_150939_a.getIcon(2, damage);
	}

	@Override
	public int getMetadata(int meta)
	{
		return (metaOffset + meta) & 15;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return getName(getUnlocalizedName(), name(stack));
	}

	@SuppressWarnings("rawtypes")
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv)
	{
		String str = getName("tip.info" + getUnlocalizedName().substring(4), name(stack));
		str = MFRUtil.localize(str, true, null);
		if (str != null)
			info.add(str);
	}

	public void getSubItems(Item itemId, List<ItemStack> subTypes)
	{
		for(int i = 0; i < _names.length; i++)
		{
			subTypes.add(new ItemStack(itemId, 1, i));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item itemId, CreativeTabs creativeTab, List subTypes)
	{
		getSubItems(itemId, subTypes);
	}
}
