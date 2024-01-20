package powercrystals.minefactoryreloaded.modhelpers.projectred;

import cofh.mod.ChildMod;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSapling;

@ChildMod(parent = MineFactoryReloadedCore.modId, mod = @Mod(modid = "MineFactoryReloaded|CompatProjRed",
		name = "MFR Compat: ProjectRed",
		version = MineFactoryReloadedCore.version,
		dependencies = "after:MineFactoryReloaded;after:ProjRed|Core;after:ProjRed|Exploration",
		customProperties = @CustomProperty(k = "cofhversion", v = "true")))
public class ProjectRedCompat {

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {

		try {
			Block stainedLeaf = GameRegistry.findBlock("ProjRed|Exploration", "projectred.exploration.dyeleaf");
			Block stainedSapling = GameRegistry.findBlock("ProjRed|Exploration", "projectred.exploration.dyesapling");

			MFRRegistry.registerPlantable(new PlantableSapling(stainedSapling));
			MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(stainedLeaf));
			MFRRegistry.registerFertilizable(new FertilizableStandard((IGrowable) stainedSapling));
		} catch (Throwable $) {
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			LogManager.getLogger(This.getModId()).log(Level.ERROR, "There was a problem loading " + This.getName(), $);
		}
	}

}
