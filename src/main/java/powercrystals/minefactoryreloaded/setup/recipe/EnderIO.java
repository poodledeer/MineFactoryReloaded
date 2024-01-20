package powercrystals.minefactoryreloaded.setup.recipe;

import static cofh.lib.util.helpers.ItemHelper.*;
import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;

import java.util.Collections;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;

public class EnderIO extends Vanilla {

	private static final String EIO = "EnderIO";

	String redstone = "dustRedstone";

	/* Items */
	ItemStack capacitorBasic;
	ItemStack capacitorDouble;
	ItemStack capacitorOctadic;
	ItemStack gear;
	ItemStack chassis;
	ItemStack zombieElectrode;
	ItemStack zombieController;
	ItemStack frankNZombie;
	ItemStack enderTransmitter;
	ItemStack dsAxe;
	ItemStack dsPick;
	ItemStack dsSword;
	ItemStack probe;
	ItemStack conduitLiquid;
	ItemStack xpRod;
	ItemStack soulVial;
	ItemStack fireWaterBucket;

	/* Blocks */
	ItemStack light;
	ItemStack reservoir;
	ItemStack dimTrans;
	ItemStack vacuumChest;
	ItemStack tank, tankPressurized;
	ItemStack xpObelisk;
	ItemStack darkSteelAnvil;
	ItemStack capBank;
	ItemStack reinforcedObsidian;

	/* Machines */
	ItemStack crafter;
	ItemStack combustionGen;

	@Override
	protected void gatherItems() {

		if (!Loader.isModLoaded(EIO)) {
			MineFactoryReloadedCore.log().fatal("EnderIO is required for EnderIO recipes to be enabled.");
			throw new MissingModsException(Collections.singleton((ArtifactVersion) new DefaultArtifactVersion(EIO)));
		}

		/* Items */
		capacitorBasic = stackFor("itemBasicCapacitor");
		capacitorDouble = stackFor("itemBasicCapacitor", 1);
		capacitorOctadic = stackFor("itemBasicCapacitor", 2);
		chassis = stackFor("itemMachinePart");
		gear = stackFor("itemMachinePart", 1);
		zombieElectrode = stackFor("itemFrankenSkull");
		zombieController = stackFor("itemFrankenSkull", 1);
		frankNZombie = stackFor("itemFrankenSkull", 2);
		enderTransmitter = stackFor("itemFrankenSkull", 3);
		dsAxe = stackFor("item.darkSteel_axe");
		dsSword = stackFor("item.darkSteel_sword");
		dsPick = stackFor("item.darkSteel_pickaxe");
		probe = stackFor("itemConduitProbe");
		conduitLiquid = stackFor("itemLiquidConduit");
		xpRod = stackFor("itemXpTransfer");
		soulVial = stackFor("itemSoulVessel");
		fireWaterBucket = stackFor("bucketFire_water");

		/* Blocks */
		light = stackForBlock("blockElectricLight", 2);
		reservoir = stackForBlock("blockReservoir");
		dimTrans = stackForBlock("blockTransceiver");
		vacuumChest = stackForBlock("blockVacuumChest");
		tank = stackForBlock("blockTank");
		tankPressurized = stackForBlock("blockTank", 1);
		xpObelisk = stackForBlock("blockExperienceObelisk");
		darkSteelAnvil = stackForBlock("blockDarkSteelAnvil");
		capBank = stackForBlock("blockCapacitorBank");
		reinforcedObsidian = stackForBlock("blockReinforcedObsidian");

		/* Machines */
		crafter = stackForBlock("blockCrafter");
		combustionGen = stackForBlock("blockCombustionGenerator");
	}

	private ItemStack stackFor(String itemName) {

		return stackFor(itemName, 0);
	}

	private ItemStack stackFor(String itemName, int damage) {

		return new ItemStack(GameRegistry.findItem(EIO, itemName), 1, damage);
	}

	private ItemStack stackForBlock(String blockName) {

		return stackForBlock(blockName, 0);
	}

	private ItemStack stackForBlock(String blockName, int damage) {

		return new ItemStack(GameRegistry.findBlock(EIO, blockName), 1, damage);
	}

	@Override
	protected void registerMachines() {

		String prefix = "ingot";

		registerMachine(Machine.Planter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.flower_pot,
				'S', Blocks.piston,
				'F', chassis,
				'O', prefix + "ElectricalSteel",
				'C', zombieController,
		});

		registerMachine(Machine.Fisher, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.fishing_rod,
				'S', Items.bucket,
				'F', chassis,
				'O', prefix + "Iron",
				'C', zombieController
		});

		registerMachine(Machine.Harvester, new Object[] {
				"PSP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'S', Items.shears,
				'T', dsAxe,
				'F', chassis,
				'O', prefix + "Gold",
				'C', zombieController
		});

		registerMachine(Machine.Rancher, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', conduitLiquid,
				'S', Items.shears,
				'F', chassis,
				'O', prefix + "ElectricalSteel",
				'C', zombieController
		});

		registerMachine(Machine.Fertilizer, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.glass_bottle,
				'S', Items.leather,
				'F', chassis,
				'O', prefix + "ElectricalSteel",
				'C', zombieController
		});

		registerMachine(Machine.Vet, new Object[] {
				"PTP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'T', syringeEmptyItem,
				'F', chassis,
				'O', prefix + "Copper",
				'C', zombieElectrode
		});

		registerMachine(Machine.ItemCollector, 8, new Object[] {
				"P P",
				" F ",
				"PCP",
				'P', "sheetPlastic",
				'F', chassis,
				'C', Blocks.chest
		});

		registerMachine(Machine.BlockBreaker, new Object[] {
				"PTP",
				"SFA",
				"OCO",
				'P', "sheetPlastic",
				'T', prefix + "ElectricalSteel",
				'S', dsPick,
				'F', chassis,
				'A', Items.iron_shovel,
				'O', prefix + "Iron",
				'C', redstone
		});

		registerMachine(Machine.WeatherCollector, new Object[] {
				"PBP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'B', Blocks.iron_bars,
				'T', Items.bucket,
				'F', chassis,
				'O', prefix + "ElectricalSteel",
				'C', redstone
		});

		registerMachine(Machine.SludgeBoiler, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.bucket,
				'S', Blocks.furnace,
				'F', chassis,
				'O', prefix + "Iron",
				'C', redstone
		});

		registerMachine(Machine.Sewer, 4, new Object[] {
				"PTP",
				"SFS",
				"SQS",
				'P', "sheetPlastic",
				'T', Items.bucket,
				'S', Items.brick,
				'F', chassis,
				'Q', reservoir,
		});

		registerMachine(Machine.Composter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.furnace,
				'S', Blocks.piston,
				'F', chassis,
				'O', Items.brick,
				'C', redstone
		});

		registerMachine(Machine.Breeder, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.golden_apple,
				'S', Items.golden_carrot,
				'F', chassis,
				'O', "dyePurple",
				'C', zombieElectrode
		});

		registerMachine(Machine.Grinder, new Object[] {
				"PTP",
				"OFO",
				"SCS",
				'P', "sheetPlastic",
				'T', dsSword,
				'O', Items.book,
				'F', chassis,
				'S', prefix + "ElectricalSteel",
				'C', conduitLiquid
		});

		registerMachine(Machine.AutoEnchanter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.obsidian,
				'S', Items.book,
				'F', chassis,
				'O', "gemDiamond",
				'C', xpRod
		});

		registerMachine(Machine.Chronotyper, new Object[] {
				"PTP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'T', "gemEmerald",
				'F', chassis,
				'O', "dyePurple",
				'C', soulVial
		});

		registerMachine(Machine.Ejector, 8, new Object[] {
				"PPP",
				" T ",
				"OFO",
				'P', "sheetPlastic",
				'T', Blocks.hopper,
				'F', chassis,
				'O', redstone
		});

		registerMachine(Machine.ItemRouter, 8, new Object[] {
				"PTP",
				"SFS",
				"PHP",
				'P', "sheetPlastic",
				'T', Blocks.chest,
				'S', probe,
				'F', chassis,
				'H', Blocks.hopper
		});

		registerMachine(Machine.LiquidRouter, 8, new Object[] {
				"PTP",
				"SFS",
				"PHP",
				'P', "sheetPlastic",
				'T', conduitLiquid,
				'S', probe,
				'F', chassis,
				'H', Blocks.hopper
		});

		int dsuCount = MFRConfig.craftSingleDSU.getBoolean(false) ? 1 : 4;
		registerMachine(Machine.DeepStorageUnit, dsuCount, new Object[] {
				"PCP",
				"CFC",
				"PCP",
				'P', "sheetPlastic",
				'C', reinforcedObsidian,
				'F', dimTrans
		});

		if (MFRConfig.enableCheapDSU.getBoolean(false)) {
			registerMachine(Machine.DeepStorageUnit, new Object[] {
					"PCP",
					"CFC",
					"PCP",
					'P', "sheetPlastic",
					'C', vacuumChest,
					'F', chassis
			});
		}

		registerMachine(Machine.LiquiCrafter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.crafting_table,
				'S', tank,
				'F', chassis,
				'O', Items.book,
				'C', crafter
		});

		registerMachine(Machine.LavaFabricator, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.obsidian,
				'S', Items.magma_cream,
				'F', chassis,
				'O', Items.blaze_rod,
				'C', redstone
		});

		registerMachine(Machine.SteamBoiler, new Object[] {
				"PPP",
				"TBT",
				"OOO",
				'P', "sheetPlastic",
				'T', tankPressurized,
				'B', Machine.SludgeBoiler.getItemStack(),
				'O', Blocks.nether_brick
		});

		registerMachine(Machine.AutoJukebox, new Object[] {
				"PJP",
				"PFP",
				'P', "sheetPlastic",
				'J', Blocks.jukebox,
				'F', chassis
		});

		registerMachine(Machine.Unifier, new Object[] {
				"PTP",
				"OFO",
				"SCS",
				'P', "sheetPlastic",
				'T', probe,
				'O', Items.comparator,
				'F', chassis,
				'S', prefix + "ElectricalSteel",
				'C', Items.book
		});

		registerMachine(Machine.AutoSpawner, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.nether_wart,
				'S', Items.magma_cream,
				'F', chassis,
				'O', "gemEmerald",
				'C', zombieController
		});

		registerMachine(Machine.BioReactor, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Items.fermented_spider_eye,
				'S', "slimeball",
				'F', chassis,
				'O', Items.brick,
				'C', Items.sugar
		});

		registerMachine(Machine.BioFuelGenerator, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.furnace,
				'S', Blocks.piston,
				'F', chassis,
				'O', Items.blaze_rod,
				'C', Blocks.piston
		});

		registerMachine(Machine.AutoDisenchanter, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', Blocks.nether_brick,
				'S', Items.book,
				'F', chassis,
				'O', "gemDiamond",
				'C', xpObelisk
		});

		registerMachine(Machine.Slaughterhouse, new Object[] {
				"GIG",
				"SFS",
				"XCX",
				'G', "sheetPlastic",
				'S', dsSword,
				'X', dsAxe,
				'I', prefix + "ElectricalSteel",
				'F', chassis,
				'C', redstone
		});

		registerMachine(Machine.MeatPacker, new Object[] {
				"GSG",
				"BFB",
				"BCB",
				'G', "sheetPlastic",
				'B', Blocks.brick_block,
				'S', fireWaterBucket,
				'F', chassis,
				'C', redstone
		});

		registerMachine(Machine.EnchantmentRouter, new Object[] {
				"PBP",
				"SFS",
				"PSP",
				'P', "sheetPlastic",
				'B', Items.book,
				'S', Items.repeater,
				'F', chassis
		});

		registerMachine(Machine.LaserDrill, new Object[] {
				"GFG",
				"CFC",
				"DHD",
				'G', "sheetPlastic",
				'D', "gemDiamond",
				'H', "blockGlassHardened",
				'F', light,
				'C', capacitorOctadic
		});

		registerMachine(Machine.LaserDrillPrecharger, new Object[] {
				"GSG",
				"HFH",
				"CDC",
				'G', "sheetPlastic",
				'D', "gemDiamond",
				'S', new ItemStack(pinkSlimeItem, 1, 1),
				'H', "blockGlassHardened",
				'F', light,
				'C', capacitorDouble
		});

		registerMachine(Machine.AutoAnvil, new Object[] {
				"GGG",
				"AFA",
				"OCO",
				'G', "sheetPlastic",
				'A', Blocks.anvil,
				'F', chassis,
				'C', darkSteelAnvil,
				'O', prefix + "Iron"
		});

		registerMachine(Machine.BlockSmasher, new Object[] {
				"GPG",
				"HFH",
				"BCB",
				'G', "sheetPlastic",
				'P', Blocks.piston,
				'H', factoryHammerItem,
				'B', Items.book,
				'F', chassis,
				'C', redstone
		});

		registerMachine(Machine.RedNote, new Object[] {
				"GNG",
				"CFC",
				'G', "sheetPlastic",
				'C', "cableRedNet",
				'N', Blocks.noteblock,
				'F', chassis
		});

		registerMachine(Machine.AutoBrewer, new Object[] {
				"GBG",
				"CFC",
				"RPR",
				'G', "sheetPlastic",
				'C', conduitLiquid,
				'B', Items.brewing_stand,
				'R', Items.repeater,
				'F', chassis,
				'P', redstone
		});

		registerMachine(Machine.FruitPicker, new Object[] {
				"GXG",
				"SFS",
				"OCO",
				'G', "sheetPlastic",
				'X', dsAxe,
				'S', Items.shears,
				'F', chassis,
				'C', zombieController,
				'O', prefix + "ElectricalSteel"
		});

		registerMachine(Machine.BlockPlacer, new Object[] {
				"GDG",
				"DMD",
				"GSG",
				'G', "sheetPlastic",
				'D', Blocks.dispenser,
				'S', redstone,
				'M', chassis,
		});

		registerMachine(Machine.MobCounter, new Object[] {
				"GGG",
				"RCR",
				"SMS",
				'G', "sheetPlastic",
				'R', Items.repeater,
				'C', Items.comparator,
				'S', probe,
				'M', chassis,
		});

		registerMachine(Machine.SteamTurbine, new Object[] {
				"PTP",
				"SFS",
				"OCO",
				'P', "sheetPlastic",
				'T', combustionGen,
				'S', Blocks.piston,
				'F', chassis,
				'O', prefix + "ElectricalSteel",
				'C', gear
		});

		registerMachine(Machine.ChunkLoader, new Object[] {
				"PEP",
				"TET",
				"OCO",
				'P', "sheetPlastic",
				'T', dimTrans,
				'E', capBank,
				'F', capacitorOctadic,
				'O', prefix + "EnergeticAlloy",
				'C', capacitorOctadic
		});
		if (MFRConfig.enableCheapCL.getBoolean(false)) {
			registerMachine(Machine.ChunkLoader, new Object[] {
					"PEP",
					"FDF",
					"OCO",
					'P', "sheetPlastic",
					'D', dimTrans,
					'E', capBank,
					'F', chassis,
					'O', prefix + "EnergeticAlloy",
					'C', capacitorOctadic
			});
		}

		registerMachine(Machine.Fountain, new Object[] {
				"PBP",
				"TFT",
				"OCO",
				'P', "sheetPlastic",
				'B', Blocks.iron_bars,
				'T', tank,
				'F', chassis,
				'O', prefix + "ElectricalSteel",
				'C', redstone
		});

		registerMachine(Machine.MobRouter, new Object[] {
				"PPP",
				"BRB",
				"OCO",
				'P', "sheetPlastic",
				'B', Blocks.iron_bars,
				'R', Machine.ItemRouter.getItemStack(),
				'O', "dyeOrange",
				'C', Machine.Chronotyper.getItemStack(),
		});

		addRecipe(ShapedRecipe(stack(plasticTank, 1), new Object[] {
				"PPP",
				"P P",
				"PMP",
				'P', "sheetPlastic",
				'M', machineBaseItem,
		}));
	}

	@Override
	protected void registerMiscItems() {

		String prefix = "ingot";

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(fertilizerItem, 16), new Object[] {
				"WBW",
				"STS",
				"WBW",
				'W', Items.wheat,
				'B', new ItemStack(Items.dye, 1, 15),
				'S', Items.string,
				'T', "stickWood",
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(spyglassItem), new Object[] {
				"GLG",
				"PLP",
				" S ",
				'G', "ingotGold",
				'L', "blockGlass",
				'P', "sheetPlastic",
				'S', "stickWood",
		}));

		if (MFRConfig.enablePortaSpawner.getBoolean(true))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(portaSpawnerItem), new Object[] {
					"GLG",
					"DND",
					"GLG",
					'G', prefix + "ElectricalSteel",
					'L', "blockGlass",
					'D', "ingotVibrantAlloy",
					'N', Items.nether_star
			}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(detCordBlock, 12), new Object[] {
				"PPP",
				"PTP",
				"PPP",
				'P', "itemRubber",
				'T', Blocks.tnt,
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(fishingRodItem, 1), new Object[] {
				"DD ",
				"DFD",
				"TDD",
				'D', "wireExplosive",
				'F', Items.fishing_rod,
				'T', Blocks.redstone_torch
		}));
	}

	@Override
	protected void registerRedNet() {

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rednetCableBlock, 8), new Object[] {
				"PPP",
				"RRR",
				"PPP",
				'R', redstone,
				'P', "sheetPlastic",
		}));

		{
			ItemStack pipe = stack(plasticPipeBlock);
			addRecipe(ShapelessRecipe(stack(rednetCableBlock, 5), pipe, pipe, pipe, pipe, pipe, redstone, redstone));
		}

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(rednetCableBlock, 1, 2), new Object[] {
				"nuggetGold",
				"nuggetGold",
				"nuggetGold",
				redstone,
				redstone,
				new ItemStack(rednetCableBlock),
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(rednetCableBlock, 6, 2), new Object[] {
				"ingotGold",
				"ingotGold",
				Blocks.redstone_block,
				new ItemStack(rednetCableBlock),
				new ItemStack(rednetCableBlock),
				new ItemStack(rednetCableBlock),
				new ItemStack(rednetCableBlock),
				new ItemStack(rednetCableBlock),
				new ItemStack(rednetCableBlock),
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(machineItem, 1, 1), new Object[] {
				"PRP",
				"RGR",
				"PIP",
				'R', redstone,
				'P', "sheetPlastic",
				'G', "blockGlass",
				'I', "ingotIron",
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rednetLogicBlock), new Object[] {
				"RDR",
				"LGL",
				"PHP",
				'H', new ItemStack(machineItem, 1, 1),
				'P', "sheetPlastic",
				'G', "ingotGold",
				'L', "gemLapis",
				'D', "gemDiamond",
				'R', redstone,
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(logicCardItem, 1, 0), new Object[] {
				"RPR",
				"PGP",
				"RPR",
				'P', "sheetPlastic",
				'G', "ingotGold",
				'R', redstone,
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(logicCardItem, 1, 1), new Object[] {
				"GPG",
				"PCP",
				"RGR",
				'C', new ItemStack(logicCardItem, 1, 0),
				'P', "sheetPlastic",
				'G', "ingotGold",
				'R', redstone,
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(logicCardItem, 1, 2), new Object[] {
				"DPD",
				"RCR",
				"GDG",
				'C', new ItemStack(logicCardItem, 1, 1),
				'P', "sheetPlastic",
				'G', "ingotGold",
				'D', "gemDiamond",
				'R', redstone,
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rednetMeterItem, 1, 0), new Object[] {
				" G",
				"PR",
				"PP",
				'P', "sheetPlastic",
				'G', "nuggetGold",
				'R', redstone,
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rednetMeterItem, 1, 1), new Object[] {
				"RGR",
				"IMI",
				"PPP",
				'P', "sheetPlastic",
				'G', capacitorBasic,
				'I', "ingotCopper",
				'R', redstone,
				'M', new ItemStack(rednetMeterItem, 1, 0)
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rednetMemoryCardItem, 1, 0), new Object[] {
				"GGG",
				"PRP",
				"PPP",
				'P', "sheetPlastic",
				'G', "nuggetGold",
				'R', redstone,
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rednetPanelBlock, 1, 0), new Object[] {
				"PCP",
				"PBP",
				"KPK",
				'P', "sheetPlastic",
				'C', rednetCableBlock,
				'B', Blocks.bookshelf,
				'K', new ItemStack(Items.dye, 1, 0)
		}));

		GameRegistry.addShapelessRecipe(new ItemStack(rednetMemoryCardItem, 1, 0), new ItemStack(rednetMemoryCardItem, 1, 0));
	}
}
