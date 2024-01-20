package powercrystals.minefactoryreloaded;

//this import brought to you by the department of redundancies department, the department that brought you this import
import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.*;
import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.core.world.WorldHandler;
import cofh.lib.gui.container.InventoryContainerItemWrapper;
import cofh.lib.util.RegistryUtils;
import cofh.mod.BaseMod;
import cofh.mod.updater.UpdateManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.FMLModIdMappingEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.FishingHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.Logger;

import powercrystals.minefactoryreloaded.block.BlockDetCord;
import powercrystals.minefactoryreloaded.block.BlockFactoryMachine;
import powercrystals.minefactoryreloaded.block.BlockFakeLaser;
import powercrystals.minefactoryreloaded.block.BlockFertileSoil;
import powercrystals.minefactoryreloaded.block.BlockRedNetLogic;
import powercrystals.minefactoryreloaded.block.BlockRedNetPanel;
import powercrystals.minefactoryreloaded.block.BlockRubberLeaves;
import powercrystals.minefactoryreloaded.block.BlockRubberSapling;
import powercrystals.minefactoryreloaded.block.BlockRubberWood;
import powercrystals.minefactoryreloaded.block.BlockVineScaffold;
import powercrystals.minefactoryreloaded.block.ItemBlockConveyor;
import powercrystals.minefactoryreloaded.block.ItemBlockDetCord;
import powercrystals.minefactoryreloaded.block.ItemBlockFactory;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryLeaves;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryMachine;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryRoad;
import powercrystals.minefactoryreloaded.block.ItemBlockFactoryTree;
import powercrystals.minefactoryreloaded.block.ItemBlockRedNetLogic;
import powercrystals.minefactoryreloaded.block.ItemBlockRedNetPanel;
import powercrystals.minefactoryreloaded.block.ItemBlockTank;
import powercrystals.minefactoryreloaded.block.ItemBlockVineScaffold;
import powercrystals.minefactoryreloaded.block.decor.BlockDecorativeBricks;
import powercrystals.minefactoryreloaded.block.decor.BlockDecorativeStone;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryDecoration;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryGlass;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryGlassPane;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryPlastic;
import powercrystals.minefactoryreloaded.block.decor.BlockPinkSlime;
import powercrystals.minefactoryreloaded.block.fluid.BlockExplodingFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockFactoryFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockPinkSlimeFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockTank;
import powercrystals.minefactoryreloaded.block.transport.BlockConveyor;
import powercrystals.minefactoryreloaded.block.transport.BlockFactoryRoad;
import powercrystals.minefactoryreloaded.block.transport.BlockPlasticPipe;
import powercrystals.minefactoryreloaded.block.transport.BlockRailCargoDropoff;
import powercrystals.minefactoryreloaded.block.transport.BlockRailCargoPickup;
import powercrystals.minefactoryreloaded.block.transport.BlockRailPassengerDropoff;
import powercrystals.minefactoryreloaded.block.transport.BlockRailPassengerPickup;
import powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable;
import powercrystals.minefactoryreloaded.entity.DebugTracker;
import powercrystals.minefactoryreloaded.entity.EntityFishingRod;
import powercrystals.minefactoryreloaded.entity.EntityFlyingItem;
import powercrystals.minefactoryreloaded.entity.EntityNeedle;
import powercrystals.minefactoryreloaded.entity.EntityPinkSlime;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import powercrystals.minefactoryreloaded.entity.EntitySafariNet;
import powercrystals.minefactoryreloaded.farmables.MFRFarmables;
import powercrystals.minefactoryreloaded.gui.MFRGUIHandler;
import powercrystals.minefactoryreloaded.item.ItemCeramicDye;
import powercrystals.minefactoryreloaded.item.ItemFactoryBag;
import powercrystals.minefactoryreloaded.item.ItemFactoryCup;
import powercrystals.minefactoryreloaded.item.ItemLogicUpgradeCard;
import powercrystals.minefactoryreloaded.item.ItemMilkBottle;
import powercrystals.minefactoryreloaded.item.ItemPinkSlime;
import powercrystals.minefactoryreloaded.item.ItemPlasticBoots;
import powercrystals.minefactoryreloaded.item.ItemPortaSpawner;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.item.ItemUpgrade;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryArmor;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryBucket;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryColored;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryFood;
import powercrystals.minefactoryreloaded.item.gun.ItemNeedleGun;
import powercrystals.minefactoryreloaded.item.gun.ItemPotatoCannon;
import powercrystals.minefactoryreloaded.item.gun.ItemRocketLauncher;
import powercrystals.minefactoryreloaded.item.gun.ItemSafariNetLauncher;
import powercrystals.minefactoryreloaded.item.gun.ammo.ItemNeedlegunAmmoAnvil;
import powercrystals.minefactoryreloaded.item.gun.ammo.ItemNeedlegunAmmoBlock;
import powercrystals.minefactoryreloaded.item.gun.ammo.ItemNeedlegunAmmoFire;
import powercrystals.minefactoryreloaded.item.gun.ammo.ItemNeedlegunAmmoStandard;
import powercrystals.minefactoryreloaded.item.gun.ammo.ItemRocket;
import powercrystals.minefactoryreloaded.item.syringe.ItemSyringeCure;
import powercrystals.minefactoryreloaded.item.syringe.ItemSyringeGrowth;
import powercrystals.minefactoryreloaded.item.syringe.ItemSyringeHealth;
import powercrystals.minefactoryreloaded.item.syringe.ItemSyringeLiquid;
import powercrystals.minefactoryreloaded.item.syringe.ItemSyringeSlime;
import powercrystals.minefactoryreloaded.item.syringe.ItemSyringeZombie;
import powercrystals.minefactoryreloaded.item.tool.ItemFactoryHammer;
import powercrystals.minefactoryreloaded.item.tool.ItemFishingRod;
import powercrystals.minefactoryreloaded.item.tool.ItemRedNetMemoryCard;
import powercrystals.minefactoryreloaded.item.tool.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.item.tool.ItemRuler;
import powercrystals.minefactoryreloaded.item.tool.ItemSpyglass;
import powercrystals.minefactoryreloaded.item.tool.ItemStraw;
import powercrystals.minefactoryreloaded.item.tool.ItemXpExtractor;
import powercrystals.minefactoryreloaded.net.CommonProxy;
import powercrystals.minefactoryreloaded.net.EntityHandler;
import powercrystals.minefactoryreloaded.net.ServerPacketHandler;
import powercrystals.minefactoryreloaded.net.ServerPacketHandler.MFRMessage;
import powercrystals.minefactoryreloaded.setup.BehaviorDispenseSafariNet;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.MineFactoryReloadedFuelHandler;
import powercrystals.minefactoryreloaded.setup.recipe.EnderIO;
import powercrystals.minefactoryreloaded.setup.recipe.ThermalExpansion;
import powercrystals.minefactoryreloaded.setup.recipe.Vanilla;
import powercrystals.minefactoryreloaded.setup.village.VillageCreationHandler;
import powercrystals.minefactoryreloaded.setup.village.VillageTradeHandler;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityUnifier;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetHistorian;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;
import powercrystals.minefactoryreloaded.tile.tank.TileEntityTank;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityConveyor;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe;
import powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen;

@Mod(modid = modId, name = modName, version = version, dependencies = dependencies,
		customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class MineFactoryReloadedCore extends BaseMod {

	//static{DepLoader.load();}
	public static final String modId = "MineFactoryReloaded";
	public static final String modName = "MineFactory Reloaded";
	public static final String version = "1.7.10R2.8.2B1";
	public static final String dependencies = CoFHCore.version_group;
	public static final String modNetworkChannel = "MFReloaded";

	@SidedProxy(clientSide = "powercrystals.minefactoryreloaded.net.ClientProxy",
			serverSide = "powercrystals.minefactoryreloaded.net.ServerProxy")
	public static CommonProxy proxy;

	public static SimpleNetworkWrapper networkWrapper = null;

	public static Object balance = "balance";

	public static final String prefix = "minefactoryreloaded:";
	public static final String textureFolder = prefix + "textures/";
	public static final String guiFolder = textureFolder + "gui/";
	public static final String hudFolder = textureFolder + "hud/";
	public static final String villagerFolder = textureFolder + "villager/";
	public static final String tileEntityFolder = textureFolder + "tileentity/";
	public static final String mobTextureFolder = textureFolder + "mob/";
	public static final String modelTextureFolder = textureFolder + "itemmodels/";
	public static final String armorTextureFolder = textureFolder + "armor/";
	public static final String modelFolder = prefix + "models/";

	public static int renderIdConveyor = 1000;
	public static int renderIdFactoryGlassPane = 1001;
	public static int renderIdFluidTank = 1002;
	public static int renderIdFluidClassic = 1003;
	public static int renderIdRedNetLogic = 1004;
	public static int renderIdVineScaffold = 1005;
	public static int renderIdRedNetPanel = 1006;
	public static int renderIdFactoryGlass = 1007;
	public static int renderIdDetCord = 1008;
	public static int renderIdRedNet = 1009;
	public static int renderIdPPipe = 1009;

	public static final String CHEST_GEN = "mfr:villageZoolologist";

	private static MineFactoryReloadedCore instance;
	private LinkedList<Vanilla> recipeSets = new LinkedList<Vanilla>();

	public static MineFactoryReloadedCore instance() {

		return instance;
	}

	public static Logger log() {

		return instance.getLogger();
	}

	public static void registerFluids() {

		registerFluid("milk", 1050, EnumRarity.common);
		registerFluid("sludge", 1700, EnumRarity.common);
		registerFluid("sewage", 1200, EnumRarity.common);
		registerFluid("mobessence", 400, 9, 310, EnumRarity.epic);
		registerFluid("biofuel", 800, EnumRarity.uncommon);
		registerFluid("meat", 2000, EnumRarity.common);
		registerFluid("pinkslime", 3000, EnumRarity.rare);
		registerFluid("chocolatemilk", 1100, EnumRarity.common);
		registerFluid("mushroomsoup", 1500, EnumRarity.common);
		registerFluid("steam", -100, 0, 673, EnumRarity.common);
	}

	public static Fluid registerFluid(String name, int density, EnumRarity rarity) {

		return registerFluid(name, density, -1, -1, rarity);
	}

	public static Fluid registerFluid(String name, int density, int lightValue, int temp, EnumRarity rarity) {

		name = name.toLowerCase(Locale.ENGLISH);
		Fluid fluid = new Fluid(name);
		if (!FluidRegistry.registerFluid(fluid))
			fluid = FluidRegistry.getFluid(name);
		if (density != 0) {
			fluid.setDensity(density);
			fluid.setViscosity(Math.abs(density)); // works for my purposes
		}
		if (lightValue >= 0)
			fluid.setLuminosity(lightValue);
		if (temp >= 0)
			fluid.setTemperature(temp);
		fluid.setUnlocalizedName("mfr." + name + ".still.name");
		fluid.setRarity(rarity);
		return fluid;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) throws IOException {

		instance = this;
		setConfigFolderBase(evt.getModConfigurationDirectory());

		machineBlocks.put(0, new BlockFactoryMachine(0));
		machineBlocks.put(1, new BlockFactoryMachine(1));
		machineBlocks.put(2, new BlockFactoryMachine(2));

		MFRConfig.loadClientConfig(getClientConfig());
		MFRConfig.loadCommonConfig(getCommonConfig());

		loadLang();

		networkWrapper = new SimpleNetworkWrapper(modNetworkChannel);
		networkWrapper.registerMessage(ServerPacketHandler.class, MFRMessage.class, 0, Side.SERVER);

		float meatNuggetSaturation = MFRConfig.meatSaturation.getBoolean(false) ? 0.1F : 0.2F;
		float meatIngotSaturation = MFRConfig.meatSaturation.getBoolean(false) ? 0.2F : 0.8F;

		conveyorBlock = new BlockConveyor();
		factoryGlassBlock = new BlockFactoryGlass();
		factoryGlassPaneBlock = new BlockFactoryGlassPane();
		factoryRoadBlock = new BlockFactoryRoad();
		factoryPlasticBlock = new BlockFactoryPlastic();
		factoryDecorativeBrickBlock = new BlockDecorativeBricks();
		factoryDecorativeStoneBlock = new BlockDecorativeStone();
		pinkSlimeBlock = new BlockPinkSlime();
		rubberWoodBlock = new BlockRubberWood();
		rubberLeavesBlock = new BlockRubberLeaves();
		rubberSaplingBlock = new BlockRubberSapling();
		railDropoffCargoBlock = new BlockRailCargoDropoff();
		railPickupCargoBlock = new BlockRailCargoPickup();
		railDropoffPassengerBlock = new BlockRailPassengerDropoff();
		railPickupPassengerBlock = new BlockRailPassengerPickup();
		rednetCableBlock = new BlockRedNetCable();
		rednetLogicBlock = new BlockRedNetLogic();
		rednetPanelBlock = new BlockRedNetPanel();
		fakeLaserBlock = new BlockFakeLaser();
		vineScaffoldBlock = new BlockVineScaffold();
		detCordBlock = new BlockDetCord();
		plasticPipeBlock = new BlockPlasticPipe();

		fertileSoil = new BlockFertileSoil();

		machineItem = new BlockFactoryDecoration();

		plasticTank = new BlockTank();

		registerFluids();

		milkLiquid = new BlockFactoryFluid("milk");
		sludgeLiquid = new BlockFactoryFluid("sludge");
		sewageLiquid = new BlockFactoryFluid("sewage");
		essenceLiquid = new BlockFactoryFluid("mobessence");
		biofuelLiquid = new BlockExplodingFluid("biofuel");
		meatLiquid = new BlockFactoryFluid("meat");
		pinkSlimeLiquid = new BlockPinkSlimeFluid("pinkslime");
		chocolateMilkLiquid = new BlockFactoryFluid("chocolatemilk");
		mushroomSoupLiquid = new BlockFactoryFluid("mushroomsoup");
		steamFluid = new BlockFactoryFluid("steam", BlockFactoryFluid.material);

		factoryHammerItem = (new ItemFactoryHammer()).setUnlocalizedName("mfr.hammer").setMaxStackSize(1);
		plasticHelmetItem = new ItemFactoryArmor(ItemFactoryArmor.PLASTIC_ARMOR, 0);
		plasticChestplateItem = new ItemFactoryArmor(ItemFactoryArmor.PLASTIC_ARMOR, 1);
		plasticLeggingsItem = new ItemFactoryArmor(ItemFactoryArmor.PLASTIC_ARMOR, 2);
		plasticBootsItem = new ItemPlasticBoots();
		plasticGlasses = new ItemFactoryArmor(ItemFactoryArmor.GLASS_ARMOR, 0);

		rawRubberItem = (new ItemFactory()).setUnlocalizedName("mfr.rubber.raw");
		rubberBarItem = (new ItemFactory()).setUnlocalizedName("mfr.rubber.bar");

		rawPlasticItem = (new ItemFactory()).setUnlocalizedName("mfr.plastic.raw");
		plasticSheetItem = (new ItemFactory()).setUnlocalizedName("mfr.plastic.sheet").setMaxStackSize(96);
		{
			int i = MFRConfig.armorStacks.getBoolean(false) ? 4 : 1;
			plasticHelmetItem.setRepairIngot("itemPlastic").setUnlocalizedName("mfr.plastic.armor.helm").setMaxStackSize(i);
			plasticChestplateItem.setRepairIngot("itemPlastic").setUnlocalizedName("mfr.plastic.armor.chest").setMaxStackSize(i);
			plasticLeggingsItem.setRepairIngot("itemPlastic").setUnlocalizedName("mfr.plastic.armor.legs").setMaxStackSize(i);
			plasticBootsItem.setRepairIngot("itemPlastic").setMaxStackSize(i);
			plasticGlasses.setRepairIngot("itemPlastic").setUnlocalizedName("mfr.glass.armor.helm");
		}

		upgradeItem = (new ItemUpgrade()).setUnlocalizedName("mfr.upgrade.radius").setMaxStackSize(64);

		rednetMeterItem = (new ItemRedNetMeter()).setUnlocalizedName("mfr.rednet.meter").setMaxStackSize(1);
		rednetMemoryCardItem = (new ItemRedNetMemoryCard()).setUnlocalizedName("mfr.rednet.memorycard").setMaxStackSize(1);
		logicCardItem = (new ItemLogicUpgradeCard()).setUnlocalizedName("mfr.upgrade.logic").setMaxStackSize(1);

		meatIngotRawItem = (new ItemFactoryFood(4, meatIngotSaturation)).setUnlocalizedName("mfr.meat.ingot.raw");
		meatIngotCookedItem = (new ItemFactoryFood(10, meatIngotSaturation)).setUnlocalizedName("mfr.meat.ingot.cooked");
		meatNuggetRawItem = (new ItemFactoryFood(1, meatNuggetSaturation)).setUnlocalizedName("mfr.meat.nugget.raw");
		meatNuggetCookedItem = (new ItemFactoryFood(4, meatNuggetSaturation)).setUnlocalizedName("mfr.meat.nugget.cooked");
		pinkSlimeItem = (new ItemPinkSlime()).setUnlocalizedName("mfr.pinkslime");

		if (MFRConfig.enableLiquidSyringe.getBoolean(true))
			syringeEmptyItem = (new ItemSyringeLiquid()).setUnlocalizedName("mfr.syringe.empty");
		else
			syringeEmptyItem = (new ItemFactory()).setUnlocalizedName("mfr.syringe.empty");
		syringeHealthItem = (new ItemSyringeHealth()).setUnlocalizedName("mfr.syringe.health").setContainerItem(syringeEmptyItem);
		syringeGrowthItem = (new ItemSyringeGrowth()).setUnlocalizedName("mfr.syringe.growth").setContainerItem(syringeEmptyItem);
		syringeZombieItem = (new ItemSyringeZombie()).setUnlocalizedName("mfr.syringe.zombie").setContainerItem(syringeEmptyItem);
		syringeSlimeItem = (new ItemSyringeSlime()).setUnlocalizedName("mfr.syringe.slime").setContainerItem(syringeEmptyItem);
		syringeCureItem = (new ItemSyringeCure()).setUnlocalizedName("mfr.syringe.cure").setContainerItem(syringeEmptyItem);

		safariNetLauncherItem = (new ItemSafariNetLauncher()).setUnlocalizedName("mfr.safarinet.launcher").setMaxStackSize(1);
		safariNetItem = (new ItemSafariNet(0, true)).setUnlocalizedName("mfr.safarinet.reusable");
		safariNetSingleItem = (new ItemSafariNet(0)).setUnlocalizedName("mfr.safarinet.singleuse");
		safariNetJailerItem = (new ItemSafariNet(1)).setUnlocalizedName("mfr.safarinet.jailer");
		safariNetFancyJailerItem = (new ItemSafariNet(3)).setUnlocalizedName("mfr.safarinet.jailer.fancy");

		portaSpawnerItem = (new ItemPortaSpawner()).setUnlocalizedName("mfr.portaspawner").setMaxStackSize(1);

		xpExtractorItem = (new ItemXpExtractor()).setUnlocalizedName("mfr.xpextractor").setMaxStackSize(1);
		strawItem = (new ItemStraw()).setUnlocalizedName("mfr.straw").setMaxStackSize(1);
		milkBottleItem = (new ItemMilkBottle()).setUnlocalizedName("mfr.milkbottle").setMaxStackSize(16);
		plasticCupItem = (ItemFactoryCup) new ItemFactoryCup(24, 16).setUnlocalizedName("mfr.plastic.cup");
		/*
		CarbonContainer.cell = new CarbonContainer(MFRConfig.plasticCellItemId.getInt(), 64, "mfr.bucket.plasticcell", false);
		CarbonContainer.cell.setFilledItem(CarbonContainer.cell).setEmptyItem(CarbonContainer.cell);

		MinecraftForge.EVENT_BUS.register(new LiquidRegistry(_configFolder,
				Loader.instance().activeModContainer()));//*/
		//plasticCellItem = CarbonContainer.cell;
		plasticBagItem = (new ItemFactoryBag()).setUnlocalizedName("mfr.plastic.bag").setMaxStackSize(24);

		sugarCharcoalItem = (new ItemFactory()).setUnlocalizedName("mfr.sugarcharcoal");
		fertilizerItem = (new ItemFactory()).setUnlocalizedName("mfr.fertilizer");

		ceramicDyeItem = (new ItemCeramicDye()).setUnlocalizedName("mfr.ceramicdye");
		laserFocusItem = (new ItemFactoryColored()).setUnlocalizedName("mfr.laserfocus").setMaxStackSize(1);

		blankRecordItem = (new ItemFactory()).setUnlocalizedName("mfr.record.blank").setMaxStackSize(1);
		spyglassItem = (new ItemSpyglass()).setUnlocalizedName("mfr.spyglass").setMaxStackSize(1);
		rulerItem = (new ItemRuler()).setUnlocalizedName("mfr.ruler").setMaxStackSize(1);
		fishingRodItem = (new ItemFishingRod());

		potatoLauncherItem = new ItemPotatoCannon().setUnlocalizedName("mfr.potatolauncher").setMaxStackSize(1);

		needlegunItem = (new ItemNeedleGun()).setUnlocalizedName("mfr.needlegun").setMaxStackSize(1);
		needlegunAmmoEmptyItem = (new ItemFactory()).setUnlocalizedName("mfr.needlegun.ammo.empty");
		needlegunAmmoStandardItem = (new ItemNeedlegunAmmoStandard()).setUnlocalizedName("mfr.needlegun.ammo.standard");
		needlegunAmmoPierceItem = (new ItemNeedlegunAmmoStandard(16, 2f, 8)).setUnlocalizedName("mfr.needlegun.ammo.pierce");
		needlegunAmmoLavaItem = (new ItemNeedlegunAmmoBlock(Blocks.flowing_lava, 3))
				.setUnlocalizedName("mfr.needlegun.ammo.lava");
		needlegunAmmoSludgeItem = (new ItemNeedlegunAmmoBlock(sludgeLiquid, 6)).setUnlocalizedName("mfr.needlegun.ammo.sludge");
		needlegunAmmoSewageItem = (new ItemNeedlegunAmmoBlock(sewageLiquid, 6)).setUnlocalizedName("mfr.needlegun.ammo.sewage");
		needlegunAmmoFireItem = (new ItemNeedlegunAmmoFire()).setUnlocalizedName("mfr.needlegun.ammo.fire");
		needlegunAmmoAnvilItem = (new ItemNeedlegunAmmoAnvil()).setUnlocalizedName("mfr.needlegun.ammo.anvil");

		rocketLauncherItem = (new ItemRocketLauncher()).setUnlocalizedName("mfr.rocketlauncher").setMaxStackSize(1);
		rocketItem = (new ItemRocket()).setUnlocalizedName("mfr.rocket").setMaxStackSize(16);

		registerBlock(conveyorBlock, ItemBlockConveyor.class, BlockConveyor._names);
		registerBlock(machineItem, ItemBlockFactory.class, BlockFactoryDecoration._names);
		machineBaseItem = Item.getItemFromBlock(machineItem);

		for (int i = 0, e = machineBlocks.size(); i < e; ++i) {
			registerBlock(machineBlocks.get(i), ItemBlockFactoryMachine.class);
		}

		registerBlock(plasticTank, ItemBlockTank.class);
		plasticTankItem = Item.getItemFromBlock(plasticTank);
		registerBlock(plasticPipeBlock, ItemBlockFactory.class);

		registerBlock(rednetCableBlock, ItemBlockFactory.class, BlockRedNetCable._names);
		registerBlock(rednetLogicBlock, ItemBlockRedNetLogic.class);
		registerBlock(rednetPanelBlock, ItemBlockRedNetPanel.class);

		registerBlock(railPickupCargoBlock, ItemBlock.class);
		registerBlock(railDropoffCargoBlock, ItemBlock.class);
		registerBlock(railPickupPassengerBlock, ItemBlock.class);
		registerBlock(railDropoffPassengerBlock, ItemBlock.class);

		registerBlock(rubberSaplingBlock, ItemBlockFactoryTree.class);
		registerBlock(rubberWoodBlock, ItemBlock.class);
		registerBlock(rubberLeavesBlock, ItemBlockFactoryLeaves.class);
		rubberSaplingItem = Item.getItemFromBlock(rubberSaplingBlock);
		rubberWoodItem = Item.getItemFromBlock(rubberWoodBlock);
		rubberLeavesItem = Item.getItemFromBlock(rubberLeavesBlock);

		registerBlock(factoryGlassBlock, ItemBlockFactory.class, BlockFactoryGlass._names);
		registerBlock(factoryGlassPaneBlock, ItemBlockFactory.class, BlockFactoryGlass._names);
		registerBlock(factoryRoadBlock, ItemBlockFactoryRoad.class);
		registerBlock(factoryPlasticBlock, ItemBlockFactory.class, BlockFactoryPlastic._names);
		registerBlock(factoryDecorativeBrickBlock, ItemBlockFactory.class, BlockDecorativeBricks._names);
		factoryDecorativeBrickItem = Item.getItemFromBlock(factoryDecorativeBrickBlock);
		registerBlock(factoryDecorativeStoneBlock, ItemBlockFactory.class, BlockDecorativeStone._names);
		registerBlock(pinkSlimeBlock, ItemBlockFactory.class);
		pinkSlimeBlockItem = Item.getItemFromBlock(pinkSlimeBlock);

		registerBlock(vineScaffoldBlock, ItemBlockVineScaffold.class);
		registerBlock(fertileSoil, ItemBlockFactory.class, 3);

		registerBlock(detCordBlock, ItemBlockDetCord.class);

		registerBlock(fakeLaserBlock, null);

		sewageBucketItem = (new ItemFactoryBucket(sewageLiquid)).setUnlocalizedName("mfr.bucket.sewage");
		sludgeBucketItem = (new ItemFactoryBucket(sludgeLiquid)).setUnlocalizedName("mfr.bucket.sludge");
		mobEssenceBucketItem = (new ItemFactoryBucket(essenceLiquid)).setUnlocalizedName("mfr.bucket.essence");
		bioFuelBucketItem = (new ItemFactoryBucket(biofuelLiquid)).setUnlocalizedName("mfr.bucket.biofuel");
		meatBucketItem = (new ItemFactoryBucket(meatLiquid)).setUnlocalizedName("mfr.bucket.meat");
		pinkSlimeBucketItem = (new ItemFactoryBucket(pinkSlimeLiquid)).setUnlocalizedName("mfr.bucket.pinkslime");
		chocolateMilkBucketItem = (new ItemFactoryBucket(chocolateMilkLiquid)).setUnlocalizedName("mfr.bucket.chocolatemilk");
		mushroomSoupBucketItem = (new ItemFactoryBucket(mushroomSoupLiquid)).setUnlocalizedName("mfr.bucket.mushroomsoup");

		registerBlock(milkLiquid, ItemBlock.class);
		registerBlock(sludgeLiquid, ItemBlock.class);
		registerBlock(sewageLiquid, ItemBlock.class);
		registerBlock(essenceLiquid, ItemBlock.class);
		registerBlock(biofuelLiquid, ItemBlock.class);
		registerBlock(meatLiquid, ItemBlock.class);
		registerBlock(pinkSlimeLiquid, ItemBlock.class);
		registerBlock(chocolateMilkLiquid, ItemBlock.class);
		registerBlock(mushroomSoupLiquid, ItemBlock.class);
		registerBlock(steamFluid, ItemBlock.class);

		Blocks.fire.setFireInfo(rubberWoodBlock, 50, 15);
		Blocks.fire.setFireInfo(rubberLeavesBlock, 80, 25);
		Blocks.fire.setFireInfo(rubberSaplingBlock, 30, 20);
		Blocks.fire.setFireInfo(detCordBlock, 100, 20);
		Blocks.fire.setFireInfo(biofuelLiquid, 300, 30);

		if (MFRConfig.vanillaOverrideMilkBucket.getBoolean(true)) {
			final Item milkBucket = Items.milk_bucket;
			Items.milk_bucket = new ItemFactoryBucket(milkLiquid, false) {

				@Override
				public int hashCode() {

					return milkBucket.hashCode();
				}

				@Override
				public boolean equals(Object obj) {

					return obj == milkBucket || obj == this;
				}
			}.setUnlocalizedName("mfr.bucket.milk").setTextureName("minecraft:bucket_milk").
					setCreativeTab(CreativeTabs.tabMisc);
			RegistryUtils.overwriteEntry(Item.itemRegistry, "minecraft:milk_bucket", Items.milk_bucket);
		}

		if (MFRConfig.vanillaRecipes.getBoolean(true))
			recipeSets.add(new Vanilla());

		if (MFRConfig.thermalExpansionRecipes.getBoolean(false))
			recipeSets.add(new ThermalExpansion());

		if (MFRConfig.enderioRecipes.getBoolean(false))
			recipeSets.add(new EnderIO());

		GameRegistry.registerTileEntity(TileEntityConveyor.class, "factoryConveyor");
		GameRegistry.registerTileEntity(TileEntityRedNetCable.class, "factoryRedstoneCable");
		GameRegistry.registerTileEntity(TileEntityRedNetLogic.class, "factoryRednetLogic");
		GameRegistry.registerTileEntity(TileEntityRedNetHistorian.class, "factoryRednetHistorian");
		GameRegistry.registerTileEntity(TileEntityRedNetEnergy.class, "factoryRedstoneCableEnergy");
		GameRegistry.registerTileEntity(TileEntityPlasticPipe.class, "factoryPlasticPipe");
		GameRegistry.registerTileEntity(TileEntityTank.class, "factoryTank");

		EntityRegistry.registerModEntity(EntitySafariNet.class, "SafariNet", 0, instance, 160, 5, true);
		EntityRegistry.registerModEntity(EntityPinkSlime.class, "mfrEntityPinkSlime", 1, instance, 160, 5, true);
		EntityRegistry.registerModEntity(EntityNeedle.class, "Needle", 2, instance, 160, 3, true);
		EntityRegistry.registerModEntity(EntityRocket.class, "Rocket", 3, instance, 160, 1, true);
		EntityRegistry.registerModEntity(EntityFishingRod.class, "FishingRod", 4, instance, 80, 3, true);
		EntityRegistry.registerModEntity(EntityFlyingItem.class, "Item", 5, instance, 160, 7, true);
		EntityRegistry.registerModEntity(DebugTracker.class, "DebugTracker", 99, instance, 250, 10, true);

		Vanilla.registerOredict();

		for (Vanilla e : recipeSets)
			e.registerOredictEntries();

		Items.wooden_door.setMaxStackSize(8);
		Items.iron_door.setMaxStackSize(8);

		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("milk",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(milkBottleItem), new ItemStack(Items.glass_bottle)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("sludge",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(sludgeBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("sewage",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(sewageBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("mobessence",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(mobEssenceBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("biofuel",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(bioFuelBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("meat",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(meatBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("pinkslime",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(pinkSlimeBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("chocolatemilk",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(chocolateMilkBucketItem), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("mushroomsoup",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(mushroomSoupBucketItem), new ItemStack(Items.bucket)));

		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("milk",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Items.milk_bucket), new ItemStack(Items.bucket)));
		FluidContainerRegistry.registerFluidContainer(new FluidContainerData(FluidRegistry.getFluidStack("mushroomsoup",
			FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(Items.mushroom_stew), new ItemStack(Items.bowl)));

		GameRegistry.registerFuelHandler(new MineFactoryReloadedFuelHandler());
	}

	private void registerBlock(Block block, Class<? extends ItemBlock> item, String[] args) {

		MFRRegistry.registerBlock(block, item, new Object[] { args });
	}

	private void registerBlock(Block block, Class<? extends ItemBlock> item, Object... args) {

		MFRRegistry.registerBlock(block, item, args);
	}

	@EventHandler
	public void missingMappings(FMLMissingMappingsEvent e) {

		List<MissingMapping> list = e.get();
		if (list.size() > 0) for (MissingMapping mapping : list) {
			String name = mapping.name;
			if (name.indexOf(':') >= 0)
				name = name.substring(name.indexOf(':') + 1);
			l: switch (mapping.type) {
			case BLOCK:
				Block block = MFRRegistry.remapBlock(name);
				if (block != null)
					mapping.remap(block);
				else if ("tile.null".equals(name))
					mapping.remap(fakeLaserBlock);
				else
					mapping.warn();
				break l;
			case ITEM:
				Item item = MFRRegistry.remapItem(name);
				if (item != null)
					mapping.remap(item);
				else
					mapping.warn();
				break l;
			default:
			}
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {

		MinecraftForge.EVENT_BUS.register(rednetCableBlock);
		MinecraftForge.EVENT_BUS.register(new EntityHandler());

		proxy.init();
		MFRFarmables.load();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MFRGUIHandler());

		addDispenserBehavior();
		addChestGenItems();

		VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler());
		VillagerRegistry.instance().registerVillagerId(MFRConfig.zoolologistEntityId.getInt());
		VillagerRegistry.instance().registerVillageTradeHandler(MFRConfig.zoolologistEntityId.getInt(),
			new VillageTradeHandler());

		WorldHandler.instance.registerFeature(new MineFactoryReloadedWorldGen());

		UpdateManager.registerUpdater(new UpdateManager(this, null, CoFHProps.DOWNLOAD_URL));
	}

	private void addDispenserBehavior() {

		IBehaviorDispenseItem behavior = new BehaviorDispenseSafariNet();
		BlockDispenser.dispenseBehaviorRegistry.putObject(safariNetItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(safariNetSingleItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(safariNetJailerItem, behavior);

		behavior = (IBehaviorDispenseItem) BlockDispenser.dispenseBehaviorRegistry.getObject(Items.water_bucket);
		BlockDispenser.dispenseBehaviorRegistry.putObject(sewageBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(sludgeBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(mobEssenceBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(bioFuelBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(meatBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(pinkSlimeBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(chocolateMilkBucketItem, behavior);
		BlockDispenser.dispenseBehaviorRegistry.putObject(mushroomSoupBucketItem, behavior);
	}

	private void addChestGenItems() {

		//{ Vanilla chests
		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
			new WeightedRandomChestContent(new ItemStack(safariNetSingleItem), 1, 1, 25));
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(
			new WeightedRandomChestContent(new ItemStack(safariNetSingleItem), 1, 1, 25));
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(
			new WeightedRandomChestContent(new ItemStack(safariNetJailerItem), 1, 1, 15));
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(
			new WeightedRandomChestContent(new ItemStack(safariNetSingleItem), 1, 1, 25));
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_DISPENSER).addItem(
			new WeightedRandomChestContent(VillageTradeHandler.getHiddenNetStack(), 1, 1, 25));
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(
			new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock, 1, 2), 1, 4, 8));
		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
			new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock, 1, 2), 1, 4, 8));
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(
			new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock, 1, 1), 1, 2, 1));
		if (MFRConfig.enableMassiveTree.getBoolean(true)) {
			ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(
				new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock, 1, 3), 1, 1, 1));
		}
		//}

		final WeightedRandomChestContent saplings = new WeightedRandomChestContent(new ItemStack(Blocks.sapling, 2), 1, 16, 7) {

			@Override
			public ItemStack[] generateChestContent(Random random, IInventory newInventory) {

				ItemStack item = theItemId.copy();
				item.setItemDamage(random.nextInt(6));
				return ChestGenHooks.generateStacks(random, item, theMinimumChanceToGenerateItem, theMaximumChanceToGenerateItem);
			}
		};

		//{ Fishing
		FishingHooks.addJunk(new WeightedRandomFishable(new ItemStack(rubberSaplingBlock, 1, 0), 5));
		FishingHooks.addJunk(new WeightedRandomFishable(new ItemStack(plasticSheetItem, 1, 0), 10));
		FishingHooks.addTreasure(new WeightedRandomFishable(VillageTradeHandler.getHiddenNetStack(), 1));
		FishingHooks.addTreasure(new WeightedRandomFishable(new ItemStack(plasticBagItem, 1, 0), 1) {

			WeightedRandomChestContent[] loot = {
					new WeightedRandomChestContent(new ItemStack(safariNetSingleItem, 1), 1, 1, 35),
					new WeightedRandomChestContent(new ItemStack(Blocks.sand, 4), 1, 16, 20),
					new WeightedRandomChestContent(new ItemStack(plasticSheetItem, 16), 1, 16, 16),
					new WeightedRandomChestContent(new ItemStack(plasticSheetItem, 23), 1, 23, 16),
					new WeightedRandomChestContent(new ItemStack(plasticSheetItem, 6), 1, 6, 16),
					new WeightedRandomChestContent(new ItemStack(Items.paper), 1, 16, 14),
					new WeightedRandomChestContent(new ItemStack(spyglassItem, 1), 1, 1, 7),
					saplings,
					new WeightedRandomChestContent(new ItemStack(strawItem), 1, 1, 5),
					new WeightedRandomChestContent(new ItemStack(Items.reeds, 3), 1, 3, 2),
					new WeightedRandomChestContent(new ItemStack(Items.pumpkin_seeds, 1), 1, 1, 2),
					new WeightedRandomChestContent(new ItemStack(Items.melon_seeds, 1), 1, 1, 2),
					new WeightedRandomChestContent(new ItemStack(Items.dye, 1, 4), 1, 1, 2),
					new WeightedRandomChestContent(new ItemStack(Items.netherbrick, 1), 1, 1, 1),
			};

			@Override
			public ItemStack func_150708_a(Random r) {

				ItemStack a = field_150711_b.copy();
				a.setTagInfo("loot", new NBTTagByte((byte) 1));
				InventoryContainerItemWrapper w = new InventoryContainerItemWrapper(a);
				WeightedRandomChestContent.generateChestContents(r, loot, w, 1);
				return w.getContainerStack();
			}
		});
		//}

		//{ DimensionalDoors chestgen compat
		// reference weights[iron: 160; coal: 120; gold: 80; golden apple: 10]
		ChestGenHooks.getInfo("dimensionalDungeonChest").addItem(
			new WeightedRandomChestContent(new ItemStack(safariNetJailerItem), 1, 1, 15));
		ChestGenHooks.getInfo("dimensionalDungeonChest").addItem(
			new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock), 1, 8, 70));
		ChestGenHooks.getInfo("dimensionalDungeonChest").addItem(
			new WeightedRandomChestContent(new ItemStack(pinkSlimeItem), 1, 1, 1));
		// tempting as a sacred sapling is, chests are too common with too few possible items
		// maybe as a custom dungeon for integration
		///}

		//{ Villager house loot chest
		ChestGenHooks.getInfo(CHEST_GEN).setMax(2 * 9);
		ChestGenHooks.getInfo(CHEST_GEN).setMin(5);
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(safariNetSingleItem), 1, 1, 35));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(safariNetJailerItem), 1, 1, 20));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(safariNetItem), 1, 1, 5));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(
			new WeightedRandomChestContent(ItemSafariNet.makeMysteryNet(new ItemStack(safariNetSingleItem)), 1, 1, 17));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(
			new WeightedRandomChestContent(ItemSafariNet.makeMysteryNet(new ItemStack(safariNetJailerItem)), 1, 1, 10));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(
			new WeightedRandomChestContent(ItemSafariNet.makeMysteryNet(new ItemStack(safariNetItem)), 1, 1, 2));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.lead), 1, 17, 10));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.name_tag), 1, 14, 10));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(safariNetLauncherItem), 1, 1, 8));

		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.paper), 1, 16, 14));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.coal, 1, 1), 1, 16, 14));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(blankRecordItem), 1, 1, 14));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.book), 1, 5, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(spyglassItem), 1, 1, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(rulerItem), 1, 1, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(mobEssenceBucketItem), 1, 1, 6));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(syringeEmptyItem), 1, 4, 6));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(syringeHealthItem), 1, 1, 6));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(syringeGrowthItem), 1, 2, 6));

		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(factoryHammerItem), 1, 1, 25));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(plasticBootsItem), 1, 1, 25));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(rubberSaplingBlock), 1, 8, 20));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(vineScaffoldBlock), 1, 32, 20));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(plasticSheetItem), 1, 64, 16));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(plasticBagItem), 1, 24, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.reeds), 1, 16, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.pumpkin_seeds), 1, 16, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(Items.snowball), 1, 16, 7));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(saplings);
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(strawItem), 1, 1, 5));
		ChestGenHooks.getInfo(CHEST_GEN).addItem(new WeightedRandomChestContent(new ItemStack(portaSpawnerItem), 1, 1, 1));
		//}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {

		TileEntityUnifier.updateUnifierLiquids();

		String[] list = MFRConfig.rubberTreeBiomeWhitelist.getStringList();
		for (String biome : list) {
			MFRRegistry.registerRubberTreeBiome(biome);
		}

		list = MFRConfig.unifierBlacklist.getStringList();
		for (String entry : list) {
			MFRRegistry.registerUnifierBlacklist(entry);
		}

		list = MFRConfig.spawnerBlacklist.getStringList();
		for (String entry : list) {
			MFRRegistry.registerAutoSpawnerBlacklist(entry);
		}

		for (Vanilla e : recipeSets)
			e.registerRecipes();

		MFRFarmables.post();
	}

	@EventHandler
	public void handleIMC(IMCEvent e) {

		IMCHandler.processIMC(e.getMessages());
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent evt) {

		IMCHandler.processIMC(FMLInterModComms.fetchRuntimeMessages(this));

		// catch biomes whitelisted via IMC that are in the config blacklist
		String[] list = MFRConfig.rubberTreeBiomeBlacklist.getStringList();
		for (String biome : list) {
			MFRRegistry.getRubberTreeBiomes().remove(biome);
		}
		for (Property prop : MFRConfig.spawnerCustomization.values()) {
			MFRRegistry.setBaseSpawnCost(prop.getName(), prop.getInt(0));
		}
		list = MFRConfig.safarinetBlacklist.getStringList();
		for (String s : list) {
			Class<?> cl = (Class<?>) EntityList.stringToClassMapping.get(s);
			if (cl != null)
				MFRRegistry.registerSafariNetBlacklist(cl);
		}

		powercrystals.minefactoryreloaded.core.OreDictionaryArbiter.initialize();
		_log.info("Load Complete.");
	}

	@EventHandler
	public void remap(FMLModIdMappingEvent evt) {

		powercrystals.minefactoryreloaded.core.OreDictionaryArbiter.bake();
	}

	@Override
	public String getModId() {

		return modId;
	}

	@Override
	public String getModName() {

		return modName;
	}

	@Override
	public String getModVersion() {

		return version;
	}
}
