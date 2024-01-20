package powercrystals.minefactoryreloaded.modhelpers.thermalexpansion;

import cofh.mod.ChildMod;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.RandomMob;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.setup.MFRThings;

@ChildMod(parent = MineFactoryReloadedCore.modId, mod = @Mod(modid = "MineFactoryReloaded|CompatThermalExpansion",
		name = "MFR Compat: ThermalExpansion",
		version = MineFactoryReloadedCore.version,
		dependencies = "after:MineFactoryReloaded;after:ThermalExpansion",
		customProperties = @CustomProperty(k = "cofhversion", v = "true")))
public class ThermalExpansion implements IRandomMobProvider {

	@EventHandler
	public void init(FMLInitializationEvent e) {

		try {
			MFRRegistry.registerRandomMobProvider(this);

			// Smooth Blackstone -> Cobble
			sendPulv(new ItemStack(MFRThings.factoryDecorativeStoneBlock, 1, 0),
				new ItemStack(MFRThings.factoryDecorativeStoneBlock, 1, 2));
			// Smooth Whitestone -> Cobble
			sendPulv(new ItemStack(MFRThings.factoryDecorativeStoneBlock, 1, 1),
				new ItemStack(MFRThings.factoryDecorativeStoneBlock, 1, 3));
		} catch (Throwable $) {
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			LogManager.getLogger(This.getModId()).log(Level.ERROR, "There was a problem loading " + This.getName(), $);
		}
	}

	private static void sendPulv(ItemStack input, ItemStack output) {

		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", 3200);
		toSend.setTag("input", input.writeToNBT(new NBTTagCompound()));
		toSend.setTag("primaryOutput", output.writeToNBT(new NBTTagCompound()));
		sendComm("PulverizerRecipe", toSend);
	}

	private static void sendComm(String type, NBTTagCompound msg) {

		FMLInterModComms.sendMessage("ThermalExpansion", type, msg);
	}

	@Override
	public List<RandomMob> getRandomMobs(World world) {

		ArrayList<RandomMob> mobs = new ArrayList<RandomMob>();

		EntityCreeper creeper = MFRUtil.prepareMob(EntityCreeper.class, world);
		creeper.setCustomNameTag("Exploding Zeldo");
		creeper.setAlwaysRenderNameTag(true);
		creeper.func_110163_bv();
		ItemStack armor = new ItemStack(MFRThings.plasticBootsItem);
		armor.setStackDisplayName("Zeldo's Ruby Slippers");
		int i = EntityLiving.getArmorPosition(armor);
		creeper.setCurrentItemOrArmor(i, armor);
		creeper.setEquipmentDropChance(i, 2);
		mobs.add(new RandomMob(creeper, 20));

		return mobs;
	}

}
