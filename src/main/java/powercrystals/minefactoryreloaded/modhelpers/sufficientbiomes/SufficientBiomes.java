package powercrystals.minefactoryreloaded.modhelpers.sufficientbiomes;

import cofh.mod.ChildMod;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

@ChildMod(parent = MineFactoryReloadedCore.modId, mod = @Mod(modid = "MineFactoryReloaded|CompatSufficientBiomes",
		name = "MFR Compat: Sufficient Biomes",
		version = MineFactoryReloadedCore.version,
		dependencies = "after:MineFactoryReloaded;after:EmasherWorldGen",
		customProperties = @CustomProperty(k = "cofhversion", v = "true")))
public class SufficientBiomes {

	@EventHandler
	public void load(FMLInitializationEvent ev) {

		try {
			MFRRegistry.registerRubberTreeBiome("Hollow");
			MFRRegistry.registerRubberTreeBiome("Marsh");
			MFRRegistry.registerRubberTreeBiome("Foot Hills");
			MFRRegistry.registerRubberTreeBiome("Sand Forest");
			MFRRegistry.registerRubberTreeBiome("Prairie Forest");
		} catch (Throwable $) {
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			LogManager.getLogger(This.getModId()).log(Level.ERROR, "There was a problem loading " + This.getName(), $);
		}
	}

}
