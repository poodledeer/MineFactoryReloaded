package powercrystals.minefactoryreloaded.item.tool;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.item.base.ItemFactoryTool;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ItemXpExtractor extends ItemFactoryTool {

	public static DamageSource damage = new DamageSource("mfr.xpsuck").setDamageBypassesArmor().setDamageIsAbsolute();
	private IIcon _icon1;
	private IIcon _icon2;
	private IIcon _icon3;

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.bow;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (player.experienceLevel > 0 && player.inventory.hasItem(Items.bucket)) {
			player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		}

		return stack;
	}

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote)
			return stack;
		suckExperience(player, player);
		return stack;
	}

	private void suckExperience(EntityPlayer target, EntityPlayer source) {
		if (target.capabilities.isCreativeMode && !source.capabilities.isCreativeMode)
			return;

		if (target.experienceLevel > 0 && source.inventory.hasItem(Items.bucket)) {
			if (source.inventory.consumeInventoryItem(Items.bucket)) {
				if (!target.capabilities.isCreativeMode) {
					target.addExperienceLevel(-1);
					target.attackEntityFrom(damage, 0.25f);
					target.worldObj.playSoundAtEntity(target, "random.levelup", 0.15f, 0.25f);
				}
				if (!source.inventory.addItemStackToInventory(new ItemStack(MFRThings.mobEssenceBucketItem))) {
					source.dropItem(MFRThings.mobEssenceBucketItem, 1);
				}
			}
		}
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if (usingItem != null && usingItem.getItem().equals(this)) {
			if (useRemaining > 24) return _icon1;
			if (useRemaining > 12) return _icon2;
			return _icon3;
		}
		return _icon1;
	}

	@Override
	public void registerIcons(IIconRegister ir) {
		_icon1 = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".1");
		_icon2 = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".2");
		_icon3 = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".3");

		itemIcon = _icon1;
	}

}
