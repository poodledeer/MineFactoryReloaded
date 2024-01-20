package powercrystals.minefactoryreloaded.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandom;

public class AutoEnchantmentHelper extends EnchantmentHelper
{
	public static ItemStack addRandomEnchantment(Random rand, ItemStack stack, int level)
	{
		return addRandomEnchantment(rand, stack, level, false);
	}

	public static ItemStack addRandomEnchantment(Random rand, ItemStack stack, int level, boolean invalid)
	{
		if (stack == null)
		{
			return null;
		}
		ItemStack output = stack.splitStack(1);

		List<EnchantmentData> enchantments = buildEnchantmentList(rand, output, level, !invalid);
		if(enchantments == null)
		{
			return output;
		}

		Map<Integer, Integer> existingEnchants = getEnchantments(output);

		boolean isBook = output.getItem().equals(Items.book);

		if (isBook)
		{
			output.func_150996_a(Items.enchanted_book);
		}

		Collections.shuffle(enchantments);

		outerlist:	for(EnchantmentData newEnchant : enchantments)
		{
			if(isBook)
			{
				Items.enchanted_book.addEnchantment(output, newEnchant);
				return output;
			}
			else
			{
				for(Entry<Integer, Integer> oldEnchant : existingEnchants.entrySet())
				{
					if(oldEnchant.getKey() == newEnchant.enchantmentobj.effectId)
					{
						if(oldEnchant.getValue() <= newEnchant.enchantmentLevel)
						{
							updateEnchantment(output, oldEnchant.getKey(), (short)newEnchant.enchantmentLevel);
						}
						continue outerlist;
					}
				}
				output.addEnchantment(newEnchant.enchantmentobj, newEnchant.enchantmentLevel);
			}
		}

		return output;
	}

	private static void updateEnchantment(ItemStack stack, int enchantId, short newLevel)
	{
		NBTTagList tagList = stack.getTagCompound().getTagList("ench", 10);
		for(int i = 0, e = tagList.tagCount(); i < e; ++i)
		{
			NBTTagCompound entry = tagList.getCompoundTagAt(i);
			if (entry.getShort("id") == enchantId)
			{
				entry.setShort("lvl", newLevel);
			}
		}
		stack.getTagCompound().setTag("ench", tagList);
	}

	public static List<EnchantmentData> buildEnchantmentList(Random rand, ItemStack stack,
			int level, boolean blockInvalid)
	{
		int itemEnchantability = stack.getItem().getItemEnchantability(stack);

		if(itemEnchantability <= 0)
		{
			return null;
		}
		else
		{
			itemEnchantability /= 2;
			itemEnchantability = 1 + rand.nextInt((itemEnchantability >> 1) + 1) +
					rand.nextInt((itemEnchantability >> 1) + 1);
			int var5 = itemEnchantability + level;
			float var6 = (rand.nextFloat() + rand.nextFloat() - 1.0F) * 0.15F;
			int targetEnchantability = (int)(var5 * (1.0F + var6) + 0.5F);

			if(targetEnchantability < 1)
			{
				targetEnchantability = 1;
			}

			List<EnchantmentData> enchantmentList = null;
			Map<Integer, EnchantmentData> enchantmentMap = mapEnchantmentData((blockInvalid ?
					targetEnchantability : Math.min(40, targetEnchantability)), stack);

			if(enchantmentMap != null && !enchantmentMap.isEmpty())
			{
				Collection<EnchantmentData> map = enchantmentMap.values();
				EnchantmentData enchData = (EnchantmentData)WeightedRandom.getRandomItem(rand, map);

				if(enchData != null)
				{
					enchantmentList = new ArrayList<EnchantmentData>();
					enchantmentList.add(enchData);
					Set<Integer> mapSet = enchantmentMap.keySet();

					for(int i = targetEnchantability; rand.nextInt(50) <= i; i >>= 1)
					{
						if (blockInvalid) for (Iterator<Integer> iter = mapSet.iterator(); iter.hasNext();)
						{
							Enchantment ench = Enchantment.enchantmentsList[iter.next()];
							for(EnchantmentData newEnchantment : enchantmentList)
								if (!newEnchantment.enchantmentobj.canApplyTogether(ench))
								{
									iter.remove();
									break;
								} // TODO: this inner loop probably isn't needed
						}

						if(!enchantmentMap.isEmpty())
						{
							EnchantmentData randomEnchant = (EnchantmentData)WeightedRandom.
									getRandomItem(rand, map);
							enchantmentList.add(randomEnchant);
						}
						else
							break;
					}
				}
			}

			return enchantmentList;
		}
	}

	public static Map<Integer, EnchantmentData> mapEnchantmentData(int targetEnchantability, ItemStack stack)
	{
		HashMap<Integer, EnchantmentData> enchantmentMap = null;
		boolean isBook = stack.getItem().equals(Items.book);

		for(int var7 = 0; var7 < Enchantment.enchantmentsList.length; ++var7)
		{
			Enchantment enchantment = Enchantment.enchantmentsList[var7];

			if(enchantment != null && (isBook || enchantment.canApplyAtEnchantingTable(stack)))
			{
				for(int enchLevel = enchantment.getMinLevel(); enchLevel <= enchantment.getMaxLevel(); ++enchLevel)
				{
					if(targetEnchantability >= enchantment.getMinEnchantability(enchLevel) &&
							targetEnchantability <= enchantment.getMaxEnchantability(enchLevel))
					{
						if(enchantmentMap == null)
						{
							enchantmentMap = new HashMap<Integer, EnchantmentData>();
						}

						enchantmentMap.put(Integer.valueOf(enchantment.effectId),
								new EnchantmentData(enchantment, enchLevel));
					}
				}
			}
		}

		return enchantmentMap;
	}
}
