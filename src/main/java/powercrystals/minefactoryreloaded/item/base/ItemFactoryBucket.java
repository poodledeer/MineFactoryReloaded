package powercrystals.minefactoryreloaded.item.base;

import cofh.lib.render.IFluidOverlayItem;
import cofh.lib.util.RegistryUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class ItemFactoryBucket extends ItemBucket implements IFluidOverlayItem {

	private boolean _register, _needsOverlay;
	@SideOnly(Side.CLIENT)
	protected IIcon overlayIcon;

	public ItemFactoryBucket(Block liquidBlock) {

		this(liquidBlock, true);
	}

	public ItemFactoryBucket(Block liquidBlock, boolean reg) {

		super(liquidBlock);
		setCreativeTab(MFRCreativeTab.tab);
		setMaxStackSize(1);
		setContainerItem(Items.bucket);
		_register = reg;
	}

	@Override
	public Item setUnlocalizedName(String name) {

		super.setUnlocalizedName(name);
		if (_register)
			MFRRegistry.registerItem(this, getUnlocalizedName());
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister r) {

		String t = "minefactoryreloaded:" + getUnlocalizedName();
		if (iconString != null && !RegistryUtils.itemTextureExists(t))
			t = iconString;
		if (RegistryUtils.itemTextureExists(t)) {
			itemIcon = r.registerIcon(t);
			_needsOverlay = false;
		} else {
			itemIcon = r.registerIcon("minecraft:bucket_empty");
			_needsOverlay = true;
		}
		overlayIcon = r.registerIcon("minefactoryreloaded:item.mfr.bucket.fill");
	}

	@Override
	public int getRenderPasses(int metadata) {

		return _needsOverlay ? 2 : 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int meta, int pass) {

		if (pass == 1)
			return overlayIcon;
		return itemIcon;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List subTypes) {

		subTypes.add(new ItemStack(item, 1, 0));
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder(getClass().getName());
		b.append('@').append(System.identityHashCode(this)).append('{');
		b.append("l:").append(getUnlocalizedName()).append(", o:").append(_needsOverlay);
		//b.append(", b:").append(this.isFull);
		FluidStack stack = FluidContainerRegistry.getFluidForFilledItem(new ItemStack(this, 1, 0));
		Fluid fluid = stack == null ? null : stack.getFluid();
		b.append(", f:").append(fluid).append(", i:").append(fluid == null ? null : fluid.getIcon());
		b.append(", c:").append(fluid == null ? null : fluid.getClass());
		b.append('}');
		return b.toString();
	}

}
