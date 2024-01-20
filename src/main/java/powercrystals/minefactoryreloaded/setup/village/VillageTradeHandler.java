package powercrystals.minefactoryreloaded.setup.village;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class VillageTradeHandler implements IVillageTradeHandler
{
	@SuppressWarnings("unchecked")
	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random)
	{
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), new ItemStack(MFRThings.safariNetSingleItem)));
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 3), new ItemStack(MFRThings.safariNetItem)));

		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1),
				new ItemStack(MFRThings.safariNetSingleItem), getHiddenNetStack()));

		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1),
				new ItemStack(Blocks.sapling, 8, random.nextInt(6)), new ItemStack(MFRThings.rubberSaplingBlock, 8, 0)));
	}

	public static ItemStack getHiddenNetStack()
	{
		ItemStack s = new ItemStack(MFRThings.safariNetSingleItem);
		return ItemSafariNet.makeMysteryNet(s);
	}
}
