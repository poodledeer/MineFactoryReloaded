package powercrystals.minefactoryreloaded.setup;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.entity.EntitySafariNet;

public class BehaviorDispenseSafariNet extends BehaviorDefaultDispenseItem
{
	@Override
	public ItemStack dispenseStack(IBlockSource dispenser, ItemStack stack)
	{
		World world = dispenser.getWorld();
		IPosition dispenserPos = BlockDispenser.func_149939_a(dispenser);
		EnumFacing dispenserFacing = BlockDispenser.func_149937_b(dispenser.getBlockMetadata());
		EntitySafariNet proj = new EntitySafariNet(world, dispenserPos.getX(), dispenserPos.getY(), dispenserPos.getZ(), stack.splitStack(1));
		proj.setThrowableHeading(dispenserFacing.getFrontOffsetX(), dispenserFacing.getFrontOffsetY() + 0.1, dispenserFacing.getFrontOffsetZ(), 1.1F, 6.0F);
		world.spawnEntityInWorld(proj);
		return stack;
	}

	@Override
	protected void playDispenseSound(IBlockSource dispenser)
	{
		dispenser.getWorld().playAuxSFX(1002, dispenser.getXInt(), dispenser.getYInt(), dispenser.getZInt(), 0);
	}
}
