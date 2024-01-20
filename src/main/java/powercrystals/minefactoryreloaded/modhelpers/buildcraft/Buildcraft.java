package powercrystals.minefactoryreloaded.modhelpers.buildcraft;

import buildcraft.api.fuels.BuildcraftFuelRegistry;

import cofh.asm.relauncher.Strippable;
import cofh.mod.ChildMod;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

@ChildMod(parent = MineFactoryReloadedCore.modId, mod = @Mod(modid = "MineFactoryReloaded|CompatBuildCraft",
		name = "MFR Compat: BuildCraft",
		version = MineFactoryReloadedCore.version,
		dependencies = "after:MineFactoryReloaded;after:BuildCraftAPI|fuels",
		customProperties = @CustomProperty(k = "cofhversion", v = "true")))
public class Buildcraft {

	@Mod.EventHandler
	@Strippable("api:BuildCraftAPI|fuels")
	private void postInit(FMLPostInitializationEvent evt) {

		try {
			if (BuildcraftFuelRegistry.fuel != null)
				BuildcraftFuelRegistry.fuel.addFuel(FluidRegistry.getFluid("biofuel"), 40, 15000);
		} catch (Throwable $) {
			ModContainer This = FMLCommonHandler.instance().findContainerFor(this);
			LogManager.getLogger(This.getModId()).log(Level.ERROR, "There was a problem loading " + This.getName(), $);
		}
	}

}
