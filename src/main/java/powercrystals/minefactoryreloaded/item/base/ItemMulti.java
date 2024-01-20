package powercrystals.minefactoryreloaded.item.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Arrays;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemMulti extends ItemFactory {

	protected String[] _names;
	private IIcon[] _icons;

	public ItemMulti() {

		setHasSubtypes(true);
		setMaxDamage(0);
	}

	protected void setNames(String... names) {

		_names = names;
		_icons = new IIcon[_names.length];
		setMetaMax(_names.length - 1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage) {

		if (!_hasIcons)
			return null;
		return _icons[Math.min(damage, _metaMax)];
	}

	@Override
	public int getMetadata(int meta) {

		return meta;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return getName(getUnlocalizedName(), _names[Math.min(stack.getItemDamage(), _metaMax)]);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister ir) {

		if (!_hasIcons)
			return;
		String str = "minefactoryreloaded:" + getUnlocalizedName();
		for (int i = 0; i < _icons.length; i++) {
			_icons[i] = ir.registerIcon(getName(str, _names[i]));
		}
	}

	public static String getName(String name, String postfix) {

		return name + (postfix != null ? "." + postfix : "");
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder(getClass().getName());
		b.append('@').append(System.identityHashCode(this)).append('{');
		b.append("l:").append(getUnlocalizedName()).append(", n:");
		b.append(Arrays.toString(_names)).append('}');
		return b.toString();
	}

}
