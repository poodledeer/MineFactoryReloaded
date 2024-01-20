package powercrystals.minefactoryreloaded.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.item.base.ItemFactoryColored;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ItemCeramicDye extends ItemFactoryColored {

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

		Block block = world.getBlock(x, y, z);
		if (!world.isRemote & block != null) {
			if (Blocks.glass.equals(block)) {
				if (world.setBlock(x, y, z, MFRThings.factoryGlassBlock, stack.getItemDamage(), 3)) {
					if (!player.capabilities.isCreativeMode)
						stack.stackSize--;
					return true;
				}
			}
			if (Blocks.glass_pane.equals(block)) {
				if (world.setBlock(x, y, z, MFRThings.factoryGlassPaneBlock, stack.getItemDamage(), 3)) {
					if (!player.capabilities.isCreativeMode)
						stack.stackSize--;
					return true;
				}
			}
			if (block.recolourBlock(world, x, y, z, ForgeDirection.getOrientation(side), stack.getItemDamage())) {
				if (!player.capabilities.isCreativeMode)
					stack.stackSize--;
				return true;
			}
		}
		return false;
	}

}
