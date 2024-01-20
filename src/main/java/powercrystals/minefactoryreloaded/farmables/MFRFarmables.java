package powercrystals.minefactoryreloaded.farmables;

import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

import cofh.core.util.oredict.OreDictionaryArbiter;

import java.util.ArrayList;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.INpc;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.INeedleAmmo;
import powercrystals.minefactoryreloaded.circuits.Fanout;
import powercrystals.minefactoryreloaded.circuits.Noop;
import powercrystals.minefactoryreloaded.circuits.Passthrough;
import powercrystals.minefactoryreloaded.circuits.PassthroughGated;
import powercrystals.minefactoryreloaded.circuits.PassthroughRoundRobin;
import powercrystals.minefactoryreloaded.circuits.analog.AdderAnalog;
import powercrystals.minefactoryreloaded.circuits.analog.DecomposeIntToDecimal;
import powercrystals.minefactoryreloaded.circuits.analog.Max2;
import powercrystals.minefactoryreloaded.circuits.analog.Max3;
import powercrystals.minefactoryreloaded.circuits.analog.Max4;
import powercrystals.minefactoryreloaded.circuits.analog.Min2;
import powercrystals.minefactoryreloaded.circuits.analog.Min3;
import powercrystals.minefactoryreloaded.circuits.analog.Min4;
import powercrystals.minefactoryreloaded.circuits.analog.Multiplier;
import powercrystals.minefactoryreloaded.circuits.analog.Negator;
import powercrystals.minefactoryreloaded.circuits.analog.RandomizerAnalog;
import powercrystals.minefactoryreloaded.circuits.analog.Scaler;
import powercrystals.minefactoryreloaded.circuits.analog.SchmittTrigger;
import powercrystals.minefactoryreloaded.circuits.analog.Subtractor;
import powercrystals.minefactoryreloaded.circuits.digital.AdderDigitalFull;
import powercrystals.minefactoryreloaded.circuits.digital.AdderDigitalHalf;
import powercrystals.minefactoryreloaded.circuits.digital.Counter;
import powercrystals.minefactoryreloaded.circuits.digital.DeMux16Analog;
import powercrystals.minefactoryreloaded.circuits.digital.DeMux4;
import powercrystals.minefactoryreloaded.circuits.digital.Inverter;
import powercrystals.minefactoryreloaded.circuits.digital.Mux4;
import powercrystals.minefactoryreloaded.circuits.digital.RandomizerDigital;
import powercrystals.minefactoryreloaded.circuits.digital.SevenSegmentEncoder;
import powercrystals.minefactoryreloaded.circuits.latch.FlipFlopJK;
import powercrystals.minefactoryreloaded.circuits.latch.FlipFlopT;
import powercrystals.minefactoryreloaded.circuits.latch.LatchDGated;
import powercrystals.minefactoryreloaded.circuits.latch.LatchSR;
import powercrystals.minefactoryreloaded.circuits.latch.LatchSRGated;
import powercrystals.minefactoryreloaded.circuits.logic.And2;
import powercrystals.minefactoryreloaded.circuits.logic.And3;
import powercrystals.minefactoryreloaded.circuits.logic.And4;
import powercrystals.minefactoryreloaded.circuits.logic.Nand2;
import powercrystals.minefactoryreloaded.circuits.logic.Nand3;
import powercrystals.minefactoryreloaded.circuits.logic.Nand4;
import powercrystals.minefactoryreloaded.circuits.logic.Nor2;
import powercrystals.minefactoryreloaded.circuits.logic.Nor3;
import powercrystals.minefactoryreloaded.circuits.logic.Nor4;
import powercrystals.minefactoryreloaded.circuits.logic.Or2;
import powercrystals.minefactoryreloaded.circuits.logic.Or3;
import powercrystals.minefactoryreloaded.circuits.logic.Or4;
import powercrystals.minefactoryreloaded.circuits.logic.Xnor2;
import powercrystals.minefactoryreloaded.circuits.logic.Xnor3;
import powercrystals.minefactoryreloaded.circuits.logic.Xnor4;
import powercrystals.minefactoryreloaded.circuits.logic.Xor2;
import powercrystals.minefactoryreloaded.circuits.logic.Xor3;
import powercrystals.minefactoryreloaded.circuits.logic.Xor4;
import powercrystals.minefactoryreloaded.circuits.logicboolean.Equal;
import powercrystals.minefactoryreloaded.circuits.logicboolean.Greater;
import powercrystals.minefactoryreloaded.circuits.logicboolean.GreaterOrEqual;
import powercrystals.minefactoryreloaded.circuits.logicboolean.Less;
import powercrystals.minefactoryreloaded.circuits.logicboolean.LessOrEqual;
import powercrystals.minefactoryreloaded.circuits.logicboolean.NotEqual;
import powercrystals.minefactoryreloaded.circuits.timing.Delay;
import powercrystals.minefactoryreloaded.circuits.timing.Multipulse;
import powercrystals.minefactoryreloaded.circuits.timing.OneShot;
import powercrystals.minefactoryreloaded.circuits.timing.PulseLengthener;
import powercrystals.minefactoryreloaded.circuits.wave.SawtoothFalling;
import powercrystals.minefactoryreloaded.circuits.wave.SawtoothRising;
import powercrystals.minefactoryreloaded.circuits.wave.Sine;
import powercrystals.minefactoryreloaded.circuits.wave.Square;
import powercrystals.minefactoryreloaded.circuits.wave.Triangle;
import powercrystals.minefactoryreloaded.entity.EntityPinkSlime;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerBiofuel;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerChocolateMilk;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerMeat;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerMilk;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerMobEssence;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerMushroomSoup;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerPinkSlime;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerSewage;
import powercrystals.minefactoryreloaded.farmables.drinkhandlers.DrinkHandlerSludge;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableIGrowable;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableStandard;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizerStandard;
import powercrystals.minefactoryreloaded.farmables.grindables.GrindableSlime;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableWood;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSapling;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSoil;
import powercrystals.minefactoryreloaded.farmables.safarinethandlers.EntityAgeableHandler;
import powercrystals.minefactoryreloaded.farmables.safarinethandlers.EntityLivingBaseHandler;
import powercrystals.minefactoryreloaded.farmables.safarinethandlers.EntityLivingHandler;
import powercrystals.minefactoryreloaded.farmables.safarinethandlers.SheepHandler;
import powercrystals.minefactoryreloaded.farmables.safarinethandlers.SlimeHandler;
import powercrystals.minefactoryreloaded.setup.MFRConfig;


public class MFRFarmables {

	public static void load() {

		if (MFRConfig.conveyorNeverCapturesPlayers.getBoolean(false)) {
			MFRRegistry.registerConveyerBlacklist(EntityPlayer.class);
		}

		if (!MFRConfig.conveyorCaptureNonItems.getBoolean(true)) {
			MFRRegistry.registerConveyerBlacklist(Entity.class);
		}

		MFRRegistry.registerSafariNetHandler(new EntityLivingBaseHandler());
		MFRRegistry.registerSafariNetHandler(new EntityLivingHandler());
		MFRRegistry.registerSafariNetHandler(new EntityAgeableHandler());
		MFRRegistry.registerSafariNetHandler(new SheepHandler());
		MFRRegistry.registerSafariNetHandler(new SlimeHandler());

		MFRRegistry.registerSafariNetBlacklist(EntityPlayer.class);
		MFRRegistry.registerSafariNetBlacklist(IBossDisplayData.class);

		MFRRegistry.registerGrinderBlacklist(EntityPlayer.class);
		MFRRegistry.registerGrinderBlacklist(INpc.class);
		MFRRegistry.registerGrinderBlacklist(IBossDisplayData.class);

		MFRRegistry.registerPlantable(new PlantableSapling(rubberSaplingBlock));
		MFRRegistry.registerPlantable(new PlantableSoil(fertileSoil, 3));

		MFRRegistry.registerHarvestable(new HarvestableWood(rubberWoodBlock));
		MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(rubberLeavesBlock));

		MFRRegistry.registerFertilizable(new FertilizableStandard(rubberSaplingBlock));
		MFRRegistry.registerFertilizable(new FertilizableIGrowable(fertileSoil));

		MFRRegistry.registerFertilizer(new FertilizerStandard(fertilizerItem, 0));

		MFRRegistry.registerGrindable(new GrindableSlime(EntityPinkSlime.class, new ItemStack(pinkSlimeItem), 1));

		MFRRegistry.registerLiquidDrinkHandler("milk", new DrinkHandlerMilk());
		MFRRegistry.registerLiquidDrinkHandler("biofuel", new DrinkHandlerBiofuel());
		MFRRegistry.registerLiquidDrinkHandler("sewage", new DrinkHandlerSewage());
		MFRRegistry.registerLiquidDrinkHandler("sludge", new DrinkHandlerSludge());
		MFRRegistry.registerLiquidDrinkHandler("mobessence", new DrinkHandlerMobEssence());
		MFRRegistry.registerLiquidDrinkHandler("meat", new DrinkHandlerMeat());
		MFRRegistry.registerLiquidDrinkHandler("pinkslime", new DrinkHandlerPinkSlime());
		MFRRegistry.registerLiquidDrinkHandler("chocolatemilk", new DrinkHandlerChocolateMilk());
		MFRRegistry.registerLiquidDrinkHandler("mushroomsoup", new DrinkHandlerMushroomSoup());

		MFRRegistry.registerNeedleAmmoType(needlegunAmmoStandardItem, (INeedleAmmo)needlegunAmmoStandardItem);
		MFRRegistry.registerNeedleAmmoType(needlegunAmmoPierceItem, (INeedleAmmo)needlegunAmmoPierceItem);
		MFRRegistry.registerNeedleAmmoType(needlegunAmmoLavaItem, (INeedleAmmo)needlegunAmmoLavaItem);
		MFRRegistry.registerNeedleAmmoType(needlegunAmmoSludgeItem, (INeedleAmmo)needlegunAmmoSludgeItem);
		MFRRegistry.registerNeedleAmmoType(needlegunAmmoSewageItem, (INeedleAmmo)needlegunAmmoSewageItem);
		MFRRegistry.registerNeedleAmmoType(needlegunAmmoFireItem, (INeedleAmmo)needlegunAmmoFireItem);
		MFRRegistry.registerNeedleAmmoType(needlegunAmmoAnvilItem, (INeedleAmmo)needlegunAmmoAnvilItem);

		MFRRegistry.registerRedNetLogicCircuit(new AdderAnalog());
		MFRRegistry.registerRedNetLogicCircuit(new AdderDigitalFull());
		MFRRegistry.registerRedNetLogicCircuit(new AdderDigitalHalf());
		MFRRegistry.registerRedNetLogicCircuit(new And2());
		MFRRegistry.registerRedNetLogicCircuit(new And3());
		MFRRegistry.registerRedNetLogicCircuit(new And4());
		MFRRegistry.registerRedNetLogicCircuit(new Counter());
		MFRRegistry.registerRedNetLogicCircuit(new DecomposeIntToDecimal());
		MFRRegistry.registerRedNetLogicCircuit(new Delay());
		MFRRegistry.registerRedNetLogicCircuit(new DeMux16Analog());
		MFRRegistry.registerRedNetLogicCircuit(new DeMux4());
		MFRRegistry.registerRedNetLogicCircuit(new Equal());
		MFRRegistry.registerRedNetLogicCircuit(new Fanout());
		MFRRegistry.registerRedNetLogicCircuit(new FlipFlopJK());
		MFRRegistry.registerRedNetLogicCircuit(new FlipFlopT());
		MFRRegistry.registerRedNetLogicCircuit(new Greater());
		MFRRegistry.registerRedNetLogicCircuit(new GreaterOrEqual());
		MFRRegistry.registerRedNetLogicCircuit(new Inverter());
		MFRRegistry.registerRedNetLogicCircuit(new LatchDGated());
		MFRRegistry.registerRedNetLogicCircuit(new LatchSR());
		MFRRegistry.registerRedNetLogicCircuit(new LatchSRGated());
		MFRRegistry.registerRedNetLogicCircuit(new Less());
		MFRRegistry.registerRedNetLogicCircuit(new LessOrEqual());
		MFRRegistry.registerRedNetLogicCircuit(new Max2());
		MFRRegistry.registerRedNetLogicCircuit(new Max3());
		MFRRegistry.registerRedNetLogicCircuit(new Max4());
		MFRRegistry.registerRedNetLogicCircuit(new Min2());
		MFRRegistry.registerRedNetLogicCircuit(new Min3());
		MFRRegistry.registerRedNetLogicCircuit(new Min4());
		MFRRegistry.registerRedNetLogicCircuit(new Multiplier());
		MFRRegistry.registerRedNetLogicCircuit(new Multipulse());
		MFRRegistry.registerRedNetLogicCircuit(new Mux4());
		MFRRegistry.registerRedNetLogicCircuit(new Nand2());
		MFRRegistry.registerRedNetLogicCircuit(new Nand3());
		MFRRegistry.registerRedNetLogicCircuit(new Nand4());
		MFRRegistry.registerRedNetLogicCircuit(new Negator());
		MFRRegistry.registerRedNetLogicCircuit(new Noop());
		MFRRegistry.registerRedNetLogicCircuit(new Nor2());
		MFRRegistry.registerRedNetLogicCircuit(new Nor3());
		MFRRegistry.registerRedNetLogicCircuit(new Nor4());
		MFRRegistry.registerRedNetLogicCircuit(new NotEqual());
		MFRRegistry.registerRedNetLogicCircuit(new OneShot());
		MFRRegistry.registerRedNetLogicCircuit(new Or2());
		MFRRegistry.registerRedNetLogicCircuit(new Or3());
		MFRRegistry.registerRedNetLogicCircuit(new Or4());
		MFRRegistry.registerRedNetLogicCircuit(new Passthrough());
		MFRRegistry.registerRedNetLogicCircuit(new PassthroughGated());
		MFRRegistry.registerRedNetLogicCircuit(new PassthroughRoundRobin());
		MFRRegistry.registerRedNetLogicCircuit(new PulseLengthener());
		MFRRegistry.registerRedNetLogicCircuit(new RandomizerAnalog());
		MFRRegistry.registerRedNetLogicCircuit(new RandomizerDigital());
		MFRRegistry.registerRedNetLogicCircuit(new SevenSegmentEncoder());
		MFRRegistry.registerRedNetLogicCircuit(new SawtoothFalling());
		MFRRegistry.registerRedNetLogicCircuit(new SawtoothRising());
		MFRRegistry.registerRedNetLogicCircuit(new Scaler());
		MFRRegistry.registerRedNetLogicCircuit(new SchmittTrigger());
		MFRRegistry.registerRedNetLogicCircuit(new Sine());
		MFRRegistry.registerRedNetLogicCircuit(new Square());
		MFRRegistry.registerRedNetLogicCircuit(new Subtractor());
		MFRRegistry.registerRedNetLogicCircuit(new Triangle());
		MFRRegistry.registerRedNetLogicCircuit(new Xnor2());
		MFRRegistry.registerRedNetLogicCircuit(new Xnor3());
		MFRRegistry.registerRedNetLogicCircuit(new Xnor4());
		MFRRegistry.registerRedNetLogicCircuit(new Xor2());
		MFRRegistry.registerRedNetLogicCircuit(new Xor3());
		MFRRegistry.registerRedNetLogicCircuit(new Xor4());
	}

	public static void post() {

		if (MFRConfig.autoRegisterHarvestables.getBoolean(false)) {
			ArrayList<ItemStack> list = OreDictionaryArbiter.getOres("logWood");
			for (ItemStack stack : list) {
				if (stack == null || stack.getItem() == null)
					continue;
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block != Blocks.air && !MFRRegistry.getHarvestables().containsKey(block))
					MFRRegistry.registerHarvestable(new HarvestableWood(block));
			}

			list = OreDictionaryArbiter.getOres("treeLeaves");
			for (ItemStack stack : list) {
				if (stack == null || stack.getItem() == null)
					continue;
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block != Blocks.air && !MFRRegistry.getHarvestables().containsKey(block))
					MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(block));
			}

			for (String key : (Set<String>) Block.blockRegistry.getKeys()) {
				Block block = Block.getBlockFromName(key);
				if (block instanceof IGrowable && !MFRRegistry.getFertilizables().containsKey(block)) {
					MFRRegistry.registerFertilizable(new FertilizableIGrowable(block));
				}
			}
		}
	}

}
