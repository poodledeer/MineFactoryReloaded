package powercrystals.minefactoryreloaded.setup;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import cpw.mods.fml.common.Loader;

import java.io.File;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class MFRConfig {

	// client config
	public static Property spyglassRange;
	public static Property brightRednetBand;
	public static boolean TESRCables;

	// common config
	public static Property dropFilledContainers;
	public static Property autoRegisterHarvestables;

	public static Property zoolologistEntityId;

	public static Property enableBonemealFertilizing;
	public static Property conveyorCaptureNonItems;
	public static Property conveyorNeverCapturesPlayers;
	public static Property conveyorNeverCapturesTCGolems;
	public static Property playSounds;
	public static Property defaultRedNetCableOnly;
	public static Property fisherNeedsRod;

	public static Property treeSearchMaxVertical;
	public static Property treeSearchMaxHorizontal;
	public static Property verticalHarvestSearchMaxVertical;
	public static Property fruitTreeSearchMaxVertical;
	public static Property fruitTreeSearchMaxHorizontal;
	public static Property breederShutdownThreshold;
	public static Property autospawnerCostStandard;
	public static Property autospawnerCostExact;
	public static Property laserdrillCost;
	public static Property disenchanterEssence;
	public static Property steamBoilerExplodes;
	public static Property autobrewerFluidCost;

	public static Property largeSlimesDrop;
	public static Property meatSaturation;
	public static Property fishingDropRate;
	public static Property armorStacks;

	public static Property vanillaOverrideMilkBucket;

	public static Property enableCheapDSU;
	public static Property craftSingleDSU;
	public static Property enableMossyCobbleRecipe;
	public static Property enableSmoothSlabRecipe;
	public static Property enablePortaSpawner;
	public static Property enableSyringes;
	public static Property enableLiquidSyringe;
	public static Property enableGuns;
	public static Property enableNetLauncher;
	public static Property enableSPAMRExploding;
	public static Property enableFuelExploding;
	public static Property enableSpawnerCarts;
	public static Property enableExpensiveSafariNet;
	public static Property enableFancySafariNet;
	public static Property enableExpensiveUpgrades;

	public static Property enableChunkLimitBypassing;
	public static Property enableChunkLoaderRequiresOwner;
	public static Property enableCheapCL;
	public static Property enableConfigurableCLEnergy;

	public static Property redNetDebug;
	public static Property redNetConnectionBlacklist;

	public static Property worldGenDimensionBlacklist;

	public static Property rubberTreeWorldGen;
	public static Property rubberTreeBiomeWhitelist;
	public static Property rubberTreeBiomeBlacklist;
	public static Property enableMassiveTree;

	public static Property mfrLakeWorldGen;
	public static Property mfrLakeSewageRarity;
	public static Property mfrLakeSewageBiomeList;
	public static Property mfrLakeSewageBiomeListToggle;
	public static Property mfrLakeSludgeRarity;
	public static Property mfrLakeSludgeBiomeList;
	public static Property mfrLakeSludgeBiomeListToggle;

	public static Property mfrLakeSewageRetrogen;
	public static Property mfrLakeSludgeRetrogen;
	public static Property rubberTreeRetrogen;

	public static Property unifierBlacklist;
	public static Property spawnerBlacklist;
	public static Property safarinetBlacklist;

	public static ConfigCategory spawnerCustomization;
	public static Property harvesterSkip;

	public static Property passengerRailSearchMaxHorizontal;
	public static Property passengerRailSearchMaxVertical;

	// recipes config
	public static Property vanillaRecipes;
	public static Property thermalExpansionRecipes;
    public static Property enderioRecipes;

	public static String CATEGORY_ITEM = "item";

	public static void loadClientConfig(File configFile) {

		Configuration c = new Configuration(configFile, true);

		spyglassRange = c.get(CATEGORY_GENERAL, "SpyglassRange", 200);
		spyglassRange.comment = "The maximum number of blocks the spyglass and ruler can look to find something. This calculation is performed only on the client side.";
		brightRednetBand = c.get(CATEGORY_GENERAL, "BrightRedNetColors", false);
		brightRednetBand.comment = "If true, RedNet color bands will always be bright.";
		TESRCables = !c.get(CATEGORY_GENERAL, "DisableRedNetFramerateStabilization", false,
				"Set to true to disable RedNet cables switching to TESRs when they detect " +
				"that they are updating too rapidly.").getBoolean(false);

		c.save();
	}

	//private static Configuration config;

	public static void loadCommonConfig(File configFile) {

		Configuration c = new Configuration(configFile, true);
		c.load();
		//config = c;

		{
			// Alternate recipe sets TODO: auto-register for config handling
			boolean te = Loader.isModLoaded("ThermalExpansion");
			boolean eio = Loader.isModLoaded("EnderIO");
			vanillaRecipes = c.get("RecipeSets", "Vanilla", !eio && !te).setRequiresMcRestart(true);
			vanillaRecipes.comment = "If true, MFR will register its standard (vanilla-item-only) recipes.";
			thermalExpansionRecipes = c.get("RecipeSets", "ThermalExpansion", te).setRequiresMcRestart(true);
			thermalExpansionRecipes.comment = "If true, MFR will register its Thermal Expansion-based recipes.";
			enderioRecipes = c.get("RecipeSets", "EnderIO", !te && eio).setRequiresMcRestart(true);
			enderioRecipes.comment = "If true, MFR will register its EnderIO-based recipes.";
		}

		String category = "Entity", subCategory = "";
		zoolologistEntityId = c.get(category, "ID.Zoologist", 330).setRequiresMcRestart(true);
		enableSpawnerCarts = c.get(category, "EnableSpawnerCarts", true);
		enableSpawnerCarts.comment = "If true, using a portaspawner on an empty minecart will make it into a spawner cart";

		playSounds = c.get(CATEGORY_GENERAL, "PlaySounds", true);
		playSounds.comment = "Set to false to disable various sounds and particle effects, such as when a block is harvested.";

		safarinetBlacklist = c.get(CATEGORY_GENERAL, "SafariNetBlacklist", new String[0]);
		safarinetBlacklist.comment = "A list of entity IDs (e.g.: CaveSpider or VillagerGolem or Forestry.butterflyGE) to blacklist from being captured by the SafariNet. The Debugger item will display an entity's ID when used.";

		//{ Searching
		treeSearchMaxHorizontal = c.get(CATEGORY_GENERAL + ".SearchDistance", "Tree.MaxHorizontal", 512);
		treeSearchMaxHorizontal.comment = "When searching for parts of a tree, how far out to the sides (radius) to search";
		treeSearchMaxVertical = c.get(CATEGORY_GENERAL + ".SearchDistance", "Tree.MaxVertical", 256);
		treeSearchMaxVertical.comment = "When searching for parts of a tree, how far up to search";
		verticalHarvestSearchMaxVertical = c.get(CATEGORY_GENERAL + ".SearchDistance", "StackingBlock.MaxVertical", 5);
		verticalHarvestSearchMaxVertical.comment = "How far upward to search for members of \"stacking\" blocks, like cactus and sugarcane";
		passengerRailSearchMaxVertical = c.get(CATEGORY_GENERAL + ".SearchDistance", "PassengerRail.MaxVertical", 2);
		passengerRailSearchMaxVertical.comment = "When searching for players or dropoff locations, how far up to search";
		passengerRailSearchMaxHorizontal = c.get(CATEGORY_GENERAL + ".SearchDistance", "PassengerRail.MaxHorizontal", 3);
		passengerRailSearchMaxHorizontal.comment = "When searching for players or dropoff locations, how far out to the sides (radius) to search";
		fruitTreeSearchMaxHorizontal = c.get(CATEGORY_GENERAL + ".SearchDistance", "FruitTree.MaxHoriztonal", 5);
		fruitTreeSearchMaxHorizontal.comment = "When searching for parts of a fruit tree, how far out to the sides (radius) to search";
		fruitTreeSearchMaxVertical = c.get(CATEGORY_GENERAL + ".SearchDistance", "FruitTree.MaxVertical", 20);
		fruitTreeSearchMaxVertical.comment = "When searching for parts of a fruit tree, how far up to search";
		//}

		//{ RedNet
		category = CATEGORY_GENERAL + ".RedNet";
		redNetDebug = c.get(category, "Debug", false);
		redNetDebug.comment = "If true, RedNet cables will dump a massive amount of data to the log file. You should probably only use this if PC tells you to.";
		redNetConnectionBlacklist = c.get(category, "ConnectionBlackList", new String[] {}).setRequiresMcRestart(true);
		redNetConnectionBlacklist.comment = "A list of block IDs to prevent RedNet cables from connecting to. (e.g., minecraft:torch)";
		defaultRedNetCableOnly = c.get(category, "CableOnly", false);
		defaultRedNetCableOnly.comment = "If true, placed rednet cable will default to cable-only connections.";
		//}

		//{ Worldgen
		category = CATEGORY_GENERAL + ".WorldGen";
		worldGenDimensionBlacklist = c.get(category, "Dimension.Blacklist", new int[] {}).setRequiresMcRestart(true);
		worldGenDimensionBlacklist.comment = "A list of dimension IDs to disable MFR worldgen in.";

		subCategory = category + ".RetroGen";
		c.getCategory(subCategory).setComment("Enable or disable specific retrogen items.\nOnly has an effect if retroactive geneneration is enabled in CoFHCore.");
		mfrLakeSewageRetrogen = c.get(subCategory, "SewageLakes", false).setRequiresMcRestart(true);
		mfrLakeSludgeRetrogen = c.get(subCategory, "SludgeLakes", false).setRequiresMcRestart(true);
		rubberTreeRetrogen = c.get(subCategory, "RubberTrees", true).setRequiresMcRestart(true);

		subCategory = category + ".RubberTrees";
		rubberTreeWorldGen = c.get(subCategory, "Enable", true).setRequiresMcRestart(true);
		rubberTreeWorldGen.comment = "Whether or not to generate MFR rubber trees during map generation";
		rubberTreeBiomeWhitelist = c.get(subCategory, "Biome.Whitelist", new String[] {}).setRequiresMcRestart(true);
		rubberTreeBiomeWhitelist.comment = "A list of biomes to allow rubber trees to spawn in. Does nothing if rubber tree worldgen is disabled.";
		rubberTreeBiomeBlacklist = c.get(subCategory, "Biome.Blacklist", new String[] {}).setRequiresMcRestart(true);
		rubberTreeBiomeBlacklist.comment = "A list of biomes to disallow rubber trees to spawn in. Overrides any other biomes added.";
		enableMassiveTree = c.get(subCategory, "SacredRubberSapling", true).setRequiresMcRestart(true);
		enableMassiveTree.comment = "If true, enable adding Enchanted Sacred Rubber Saplings to stronghold library loot.";

		subCategory = category + ".Lakes";
		mfrLakeWorldGen = c.get(subCategory, "Enable", true).setRequiresMcRestart(true);
		mfrLakeWorldGen.comment = "Whether or not to generate MFR lakes during map generation. By default, MFR will not attempt lake worldgen in dimensions where the player cannot respawn.";

		mfrLakeSludgeRarity = c.get(subCategory + ".Sludge", "Rarity", 32).setRequiresMcRestart(true);
		mfrLakeSludgeRarity.comment = "Higher numbers make sludge lakes rarer. A value of one will be approximately one per chunk. 0 will disable.";
		mfrLakeSludgeBiomeList = c.get(subCategory + ".Sludge", "BiomeList", new String[] {}).setRequiresMcRestart(true);
		mfrLakeSludgeBiomeList.comment = "A list of biomes to allow/disallow Sludge lakes to spawn in. Does nothing if lake worldgen is disabled.";
		mfrLakeSludgeBiomeListToggle = c.get(subCategory + ".Sludge", "BiomeList.Mode", false).setRequiresMcRestart(true);
		mfrLakeSludgeBiomeListToggle.comment = "If false, the biome list is a blacklist. If true, the biome list is a whitelist.";

		mfrLakeSewageRarity = c.get(subCategory + ".Sewage", "Rarity", 32).setRequiresMcRestart(true);
		mfrLakeSewageRarity.comment = "Higher numbers make Sewage lakes rarer. A value of one will be approximately one per chunk. 0 will disable.";
		mfrLakeSewageBiomeList = c.get(subCategory + ".Sewage", "BiomeList", new String[] {}).setRequiresMcRestart(true);
		mfrLakeSewageBiomeList.comment = "A list of biomes to allow/disallow Sewage lakes to spawn in. Does nothing if lake worldgen is disabled.";
		mfrLakeSewageBiomeListToggle = c.get(subCategory + ".Sewage", "BiomeList.Mode", false).setRequiresMcRestart(true);
		mfrLakeSewageBiomeListToggle.comment = "If false, the biome list is a blacklist. If true, the biome list is a whitelist.";
		//}

		//{ Item/block behavior overriding
		category = CATEGORY_ITEM + ".VanillaOverride";
		vanillaOverrideMilkBucket = c.get(category, "MilkBucket", true).setRequiresMcRestart(true);
		vanillaOverrideMilkBucket.comment = "If true, replaces the vanilla milk bucket so milk can be placed in the world.";
		//}

		//{ misc. item changes
		meatSaturation = c.get(CATEGORY_ITEM, "Meat.IncreasedSaturation", false).setRequiresMcRestart(true);
		meatSaturation.comment = "If true, meat will be worth steak saturation instead of cookie saturation.";
		fishingDropRate = c.get(CATEGORY_ITEM, "FishDropRate", 5);
		fishingDropRate.comment = "The rate at which fish are dropped from the fishing rod. The drop rate is 1 / this number. Must be greater than 0.";

		enableSPAMRExploding = c.get(CATEGORY_ITEM, "SPAMR.Exploding", true);
		enableSPAMRExploding.comment = "If true, SPAMRs will explode when they run out of fuel.";
		enableFuelExploding = c.get(CATEGORY_ITEM, "Biofuel.Exploding", true);
		enableFuelExploding.comment = "If true, biofuel will explode when in the nether.";

		enableLiquidSyringe = c.get(CATEGORY_ITEM, "LiquidSyringes", true).setRequiresMcRestart(true);
		enableLiquidSyringe.comment = "If true, Empty Syringes will be able to contain liquids and inject players.";

		largeSlimesDrop = c.get(CATEGORY_ITEM, "LargeSlimeDrop", false);
		largeSlimesDrop.comment = "If true, only pink slimes larger than tiny will drop pink slimeballs. Provided for those who want a more work-intensive laser drill. (slimes can only be made larger through the slime embiggening syringe)";

		armorStacks = c.get(CATEGORY_ITEM, "ArmorStacks", false);
		armorStacks.comment = "If true, Plastic Armor will stack to 4";
		//}

		//{ Alterations to recipes
		category = CATEGORY_ITEM + ".Recipe";
		enableCheapDSU = c.get(category, "CheaperDSU",  false).setRequiresMcRestart(true);
		enableCheapDSU.comment = "If true, the DSU can be built out of chests instead of ender pearls. Does nothing if the recipe is disabled.";
		craftSingleDSU = c.get(category, "SingleDSU",  true).setRequiresMcRestart(true);
		craftSingleDSU.comment = "DSU recipes will always craft one DSU. Does nothing for recipes that already only craft one DSU (cheap mode, etc).";
		enableMossyCobbleRecipe = c.get(category, "MossyCobble",  true).setRequiresMcRestart(true);
		enableMossyCobbleRecipe.comment = "If true, mossy cobble can be crafted.";
		enablePortaSpawner = c.get(category, "PortaSpawner", true).setRequiresMcRestart(true);
		enablePortaSpawner.comment = "If true, the PortaSpawner will be craftable.";
		enableSyringes = c.get(category, "Syringes", true).setRequiresMcRestart(true);
		enableSyringes.comment = "If true, the Syringes will be craftable.";
		enableGuns = c.get(category, "Guns", true).setRequiresMcRestart(true);
		enableGuns.comment = "If true, the Guns will be craftable.";
		enableNetLauncher = c.get(category, "NetLauncher", true).setRequiresMcRestart(true);
		enableNetLauncher.comment = "If true, the safarinet launcher will be craftable.";
		enableSmoothSlabRecipe = c.get(category, "SmoothSlab", true).setRequiresMcRestart(true);
		enableSmoothSlabRecipe.comment = "If true, smooth double stone slabs can be craftable.";
		enableCheapCL = c.get(category, "CheaperChunkLoader", false).setRequiresMcRestart(true);
		enableCheapCL.comment = "If true, the ChunkLoader can be built out of cheaper materials. Does nothing if the recipe is disabled.";
		enableExpensiveSafariNet = c.get(category, "ExpensiveSafariNet", false).setRequiresMcRestart(true);
		enableExpensiveSafariNet.comment = "If true, the reusable safarinet will require a portaspawner to craft. The portaspawner must be enabled for the safarinet to be craftable.";
		enableFancySafariNet = c.get(category, "GoldenJailerSafariNet", true).setRequiresMcRestart(true);
		enableFancySafariNet.comment = "If true, the golden jailer safarinet will be craftable. It causes released mobs to always render their nametag, like a player would.";
		enableExpensiveUpgrades = c.get(category, "ExpensiveRangeUpgrades", false).setRequiresMcRestart(true);
		enableExpensiveUpgrades.comment = "If true, upgrades will require the previous level upgrade and a diamond. NOTE: this option requires all upgrades have recipes";
		//}

		//{ Additional machine configs
		{
			final String machine = "Machine.";
			category = machine + "Conveyor";
			conveyorCaptureNonItems = c.get(category, "CaptureNonItems", true).setRequiresMcRestart(true);
			conveyorCaptureNonItems.comment = "If false, conveyors will not grab non-item entities. Breaks conveyor mob grinders but makes them safe for golems, etc.";
			conveyorNeverCapturesPlayers = c.get(category, "NeverCapturePlayers", false).setRequiresMcRestart(true);
			conveyorNeverCapturesPlayers.comment = "If true, conveyors will NEVER capture players regardless of other settings.";
			conveyorNeverCapturesTCGolems = c.get(category, "NeverCaptureTCGolems", false).setRequiresMcRestart(true);
			conveyorNeverCapturesTCGolems.comment = "If true, conveyors will NEVER capture ThaumCraft golems regardless of other settings.";

			category = machine + Machine.ChunkLoader.getName();
			enableChunkLimitBypassing = c.get(category, "IgnoreChunkLimit", false);
			enableChunkLimitBypassing.comment = "If true, the Chunk Loader will ignore forgeChunkLoading.cfg.";
			enableChunkLoaderRequiresOwner = c.get(category, "RequiresOwnerOnline", false);
			enableChunkLoaderRequiresOwner.comment = "If true, the Chunk Loader will require that the player who placed it be online to function";
			enableConfigurableCLEnergy = c.get(category, "EnableConfigurableActivationEnergy", false).setRequiresMcRestart(true);
			enableConfigurableCLEnergy.comment = "If true, the Chunk Loader will use the activation energy config in this section. WARNING: this makes it much more expensive at lower values. (non-configurable is exponential)";

			category = machine + Machine.AutoSpawner.getName();
			spawnerBlacklist = c.get(category, "Blacklist", new String[] {"VillagerGolem"}).setRequiresMcRestart(true);
			spawnerBlacklist.comment = "A list of entity IDs (e.g.: CaveSpider or VillagerGolem or Forestry.butterflyGE) to blacklist from the AutoSpawner. The Debugger item will display an entity's ID when used.";
			category += ".Cost";
			autospawnerCostExact = c.get(category, "Exact", 5).setRequiresMcRestart(true);
			autospawnerCostExact.comment = "The multiplier for work required to generate a mob in exact mode.";
			autospawnerCostStandard = c.get(category, "Standard", 1).setRequiresMcRestart(true);
			autospawnerCostStandard.comment = "The multiplier for work required to generate a mob in standard (non-exact) mode.";
			spawnerCustomization = c.getCategory(category + ".Custom").setRequiresMcRestart(true);
			spawnerCustomization.setComment("Custom base XP costs for entities. format: I:<entityid> = #. e.g.:\n" + "I:VillagerGolem = 25\nI:Slime = 50");

			harvesterSkip = c.get(machine + Machine.Harvester.getName(), "SkipWork", false).setRequiresMcRestart(true);
			harvesterSkip.comment = "If true, the harvester will skip scanning some bocks when filled with sludge";

			laserdrillCost = c.get(machine + Machine.LaserDrill.getName(), "Work", 300).setRequiresMcRestart(true);
			laserdrillCost.comment = "The work required by the drill to generate a single ore.";

			unifierBlacklist = c.get(machine + Machine.Unifier.getName(), "Blacklist", new String[] {"dyeBlue","dyeWhite","dyeBrown","dyeBlack","listAllwater","listAllmilk"}).setRequiresMcRestart(true);
			unifierBlacklist.comment = "A list of ore dictionary entrys to disable unifying for. By default, MFR will not attempt to unify anything with more than one oredict name.";

			breederShutdownThreshold = c.get(machine + Machine.Breeder.getName(), "ShutdownThreshold", 50).setRequiresMcRestart(true);
			breederShutdownThreshold.comment = "If the number of entities in the breeder's target area exceeds this value, the breeder will cease operating. This is provided to control server lag.";

			enableBonemealFertilizing = c.get(machine + Machine.Fertilizer.getName(), "EnableBonemeal", false).setRequiresMcRestart(true);
			enableBonemealFertilizing.comment = "If true, the fertilizer will use bonemeal as well as MFR fertilizer. Provided for those who want a less work-intensive farm.";

			disenchanterEssence = c.get(machine + Machine.AutoDisenchanter.getName(), "EnableEssence", false).setRequiresMcRestart(true);
			disenchanterEssence.comment = "If true, the disenchanter will use essence to disenchant items. Provided for those who want a more work-intensive enchanting system.";

			steamBoilerExplodes = c.get(machine + Machine.SteamBoiler.getName(), "Explodes", false);
			steamBoilerExplodes.comment = "If true, the steam boiler will explode if it's hot and dry when you try to pump water into it.";

			fisherNeedsRod = c.get(machine + Machine.Fisher.getName(), "RequiresFishingRod", false);
			fisherNeedsRod.comment = "If true, the fisher will require a fishing rod to function.";

			autobrewerFluidCost = c.get(machine + Machine.AutoBrewer.getName(), "WaterPerBottle", 250).setMinValue(1).setMaxValue(4000);
			autobrewerFluidCost.comment = "The amount of water used by the Auto-Brewer to fill a bottle with water";
		}
		//}

		for (Machine machine : Machine.values()) {
			machine.load(c);
		}

		autoRegisterHarvestables = c.get(CATEGORY_GENERAL, "Harvestables.Automatic", false);
		autoRegisterHarvestables.comment = "If true, MFR will attempt to automatically detect harvestable blocks and register them.";

		// TODO: make this config per-player
		dropFilledContainers = c.get(CATEGORY_GENERAL, "Tanks.FillWithoutEmptySlots", true);
		dropFilledContainers.comment = "If true, when you have no empty slots in your inventory, you will continue filling buckets from tanks and drop them on the ground.";

		c.save();
	}

}