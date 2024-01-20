package powercrystals.minefactoryreloaded;

import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.*;
import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

import cofh.lib.render.RenderFluidOverlayItem;
import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderPlayerEvent.SetArmorModel;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.Point;

import powercrystals.minefactoryreloaded.block.fluid.BlockFactoryFluid;
import powercrystals.minefactoryreloaded.core.IHarvestAreaContainer;
import powercrystals.minefactoryreloaded.entity.DebugTracker;
import powercrystals.minefactoryreloaded.entity.EntityFishingRod;
import powercrystals.minefactoryreloaded.entity.EntityNeedle;
import powercrystals.minefactoryreloaded.entity.EntityPinkSlime;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import powercrystals.minefactoryreloaded.entity.EntitySafariNet;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoBrewer;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoDisenchanter;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoJukebox;
import powercrystals.minefactoryreloaded.gui.container.ContainerFisher;
import powercrystals.minefactoryreloaded.gui.slot.SlotAcceptLaserFocus;
import powercrystals.minefactoryreloaded.gui.slot.SlotAcceptReusableSafariNet;
import powercrystals.minefactoryreloaded.item.gun.ItemRocketLauncher;
import powercrystals.minefactoryreloaded.render.block.BlockTankRenderer;
import powercrystals.minefactoryreloaded.render.block.ConveyorRenderer;
import powercrystals.minefactoryreloaded.render.block.DetCordRenderer;
import powercrystals.minefactoryreloaded.render.block.FactoryGlassPaneRenderer;
import powercrystals.minefactoryreloaded.render.block.FactoryGlassRenderer;
import powercrystals.minefactoryreloaded.render.block.PlasticPipeRenderer;
import powercrystals.minefactoryreloaded.render.block.RedNetCableRenderer;
import powercrystals.minefactoryreloaded.render.block.RedNetLogicRenderer;
import powercrystals.minefactoryreloaded.render.block.VineScaffoldRenderer;
import powercrystals.minefactoryreloaded.render.entity.EntityDebugTrackerRenderer;
import powercrystals.minefactoryreloaded.render.entity.EntityNeedleRenderer;
import powercrystals.minefactoryreloaded.render.entity.EntityPinkSlimeRenderer;
import powercrystals.minefactoryreloaded.render.entity.EntityRocketRenderer;
import powercrystals.minefactoryreloaded.render.entity.EntitySafariNetRenderer;
import powercrystals.minefactoryreloaded.render.item.ConveyorItemRenderer;
import powercrystals.minefactoryreloaded.render.item.FactoryGlassPaneItemRenderer;
import powercrystals.minefactoryreloaded.render.item.NeedleGunItemRenderer;
import powercrystals.minefactoryreloaded.render.item.PotatoLauncherItemRenderer;
import powercrystals.minefactoryreloaded.render.item.RocketItemRenderer;
import powercrystals.minefactoryreloaded.render.item.RocketLauncherItemRenderer;
import powercrystals.minefactoryreloaded.render.tileentity.LaserDrillPrechargerRenderer;
import powercrystals.minefactoryreloaded.render.tileentity.LaserDrillRenderer;
import powercrystals.minefactoryreloaded.render.tileentity.RedNetCardItemRenderer;
import powercrystals.minefactoryreloaded.render.tileentity.RedNetHistorianRenderer;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrill;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrillPrecharger;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetHistorian;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

@SideOnly(Side.CLIENT)
public class MineFactoryReloadedClient implements IResourceManagerReloadListener {

	public static MineFactoryReloadedClient instance;

	private static final ResourceLocation targetingBlue =
			new ResourceLocation(MineFactoryReloadedCore.hudFolder + "lockon_blue.png");
	private static final ResourceLocation targetingRed =
			new ResourceLocation(MineFactoryReloadedCore.hudFolder + "lockon_red.png");
	private static final int _lockonMax = 30;
	private static final int _lockonLostMax = 60;
	private int _lockonTicks = 0;
	private int _lockonLostTicks = 0;
	private Entity _lastEntityOver = null;
	@SuppressWarnings("unused")
	private static boolean gl14 = false;

	public static HashMap<BlockPosition, Integer> prcPages = new HashMap<BlockPosition, Integer>();

	public static Set<IHarvestAreaContainer> _areaTileEntities = new LinkedHashSet<IHarvestAreaContainer>();

	public static void init() {

		instance = new MineFactoryReloadedClient();

		// IDs
		renderIdConveyor = RenderingRegistry.getNextAvailableRenderId();
		renderIdFactoryGlassPane = RenderingRegistry.getNextAvailableRenderId();
		renderIdFluidTank = RenderingRegistry.getNextAvailableRenderId();
		renderIdFluidClassic = RenderingRegistry.getNextAvailableRenderId();
		renderIdRedNetLogic = RenderingRegistry.getNextAvailableRenderId();
		renderIdVineScaffold = RenderingRegistry.getNextAvailableRenderId();
		renderIdFactoryGlass = RenderingRegistry.getNextAvailableRenderId();
		renderIdDetCord = RenderingRegistry.getNextAvailableRenderId();
		renderIdRedNet = RenderingRegistry.getNextAvailableRenderId();
		renderIdPPipe = RenderingRegistry.getNextAvailableRenderId();

		// Blocks
		RenderingRegistry.registerBlockHandler(renderIdConveyor,
			new ConveyorRenderer());
		RenderingRegistry.registerBlockHandler(renderIdFactoryGlassPane,
			new FactoryGlassPaneRenderer());
		BlockTankRenderer tankRender = new BlockTankRenderer();
		RenderingRegistry.registerBlockHandler(renderIdFluidTank, tankRender);
		/*RenderingRegistry.registerBlockHandler(renderIdFluidClassic,
				new RenderBlockFluidClassic(renderIdFluidClassic));//*/
		RenderingRegistry.registerBlockHandler(renderIdVineScaffold,
			new VineScaffoldRenderer());
		RenderingRegistry.registerBlockHandler(renderIdFactoryGlass,
			new FactoryGlassRenderer());
		RenderingRegistry.registerBlockHandler(renderIdDetCord,
			new DetCordRenderer());
		RedNetCableRenderer cableRenderer = new RedNetCableRenderer();
		RenderingRegistry.registerBlockHandler(renderIdRedNet, cableRenderer);
		RenderingRegistry.registerBlockHandler(renderIdPPipe,
			new PlasticPipeRenderer());
		RenderingRegistry.registerBlockHandler(renderIdRedNetLogic,
			new RedNetLogicRenderer());

		// TODO: convert card renderer and remove this
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRedNetLogic.class,
			new powercrystals.minefactoryreloaded.render.tileentity.RedNetLogicRenderer());

		// Items
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(conveyorBlock),
			new ConveyorItemRenderer());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(factoryGlassPaneBlock),
			new FactoryGlassPaneItemRenderer());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(plasticTank), tankRender);

		MinecraftForgeClient.registerItemRenderer(logicCardItem, new RedNetCardItemRenderer());
		MinecraftForgeClient.registerItemRenderer(needlegunItem, new NeedleGunItemRenderer());
		MinecraftForgeClient.registerItemRenderer(rocketItem, new RocketItemRenderer());
		MinecraftForgeClient.registerItemRenderer(rocketLauncherItem, new RocketLauncherItemRenderer());
		MinecraftForgeClient.registerItemRenderer(potatoLauncherItem, new PotatoLauncherItemRenderer());

		RenderFluidOverlayItem fluidRender = new RenderFluidOverlayItem();
		MinecraftForgeClient.registerItemRenderer(plasticCupItem, fluidRender);
		MinecraftForgeClient.registerItemRenderer(sewageBucketItem, fluidRender);
		MinecraftForgeClient.registerItemRenderer(sludgeBucketItem, fluidRender);
		MinecraftForgeClient.registerItemRenderer(mobEssenceBucketItem, fluidRender);
		MinecraftForgeClient.registerItemRenderer(bioFuelBucketItem, fluidRender);
		MinecraftForgeClient.registerItemRenderer(meatBucketItem, fluidRender);
		MinecraftForgeClient.registerItemRenderer(pinkSlimeBucketItem, fluidRender);
		MinecraftForgeClient.registerItemRenderer(chocolateMilkBucketItem, fluidRender);
		MinecraftForgeClient.registerItemRenderer(mushroomSoupBucketItem, fluidRender);
		if (syringeEmptyItem instanceof IFluidContainerItem)
			MinecraftForgeClient.registerItemRenderer(syringeEmptyItem,
				new RenderFluidOverlayItem(false));
		//MinecraftForgeClient.registerItemRenderer(MineFactoryReloadedCore.plasticCellItem.itemID,
		//		new FactoryFluidOverlayRenderer());

		// TileEntities
		RedNetHistorianRenderer panelRenderer = new RedNetHistorianRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRedNetHistorian.class, panelRenderer);
		RenderingRegistry.registerBlockHandler(panelRenderer);

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRedNetCable.class, cableRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRedNetEnergy.class, cableRenderer);

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserDrill.class, new LaserDrillRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserDrillPrecharger.class,
			new LaserDrillPrechargerRenderer());

		// Entities
		RenderingRegistry.registerEntityRenderingHandler(DebugTracker.class, new EntityDebugTrackerRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntitySafariNet.class, new EntitySafariNetRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityPinkSlime.class,
			new EntityPinkSlimeRenderer(new ModelSlime(16), new ModelSlime(0), 0.25F));
		RenderingRegistry.registerEntityRenderingHandler(EntityNeedle.class, new EntityNeedleRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, new EntityRocketRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityFishingRod.class,
			new RenderSnowball(fishingRodItem));

		// Handlers
		VillagerRegistry.instance().registerVillagerSkin(MFRConfig.zoolologistEntityId.getInt(),
			new ResourceLocation(villagerFolder + "zoologist.png"));

		MinecraftForge.EVENT_BUS.register(instance);
		FMLCommonHandler.instance().bus().register(instance);
		gl14 = GLContext.getCapabilities().OpenGL14;

		IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
		manager.registerReloadListener(instance);
	}

	@Override
	public void onResourceManagerReload(IResourceManager p_110549_1_) {

		NeedleGunItemRenderer.updateModel();
	}

	@SubscribeEvent
	public void onPreTextureStitch(TextureStitchEvent.Pre e) {

		switch (e.map.getTextureType()) {
		case 1:
			SlotAcceptReusableSafariNet.background = e.map.registerIcon("minefactoryreloaded:gui/reusablenet");
			ContainerAutoDisenchanter.background = e.map.registerIcon("minefactoryreloaded:gui/book");
			ContainerAutoJukebox.background = e.map.registerIcon("minefactoryreloaded:gui/record");
			SlotAcceptLaserFocus.background = e.map.registerIcon("minefactoryreloaded:gui/laserfocus");
			ContainerAutoBrewer.ingredient = e.map.registerIcon("minefactoryreloaded:gui/netherwart");
			ContainerAutoBrewer.bottle = e.map.registerIcon("minefactoryreloaded:gui/bottle");
			ContainerFisher.background = e.map.registerIcon("minefactoryreloaded:gui/fishingrod");
			break;
		default:
			break;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPostTextureStitch(TextureStitchEvent.Post e) {

		switch (e.map.getTextureType()) {
		case 0:
			setIcons("milk", MFRThings.milkLiquid);
			setIcons("sludge", MFRThings.sludgeLiquid);
			setIcons("sewage", MFRThings.sewageLiquid);
			setIcons("mobessence", MFRThings.essenceLiquid);
			setIcons("biofuel", MFRThings.biofuelLiquid);
			setIcons("meat", MFRThings.meatLiquid);
			setIcons("pinkslime", MFRThings.pinkSlimeLiquid);
			setIcons("chocolatemilk", MFRThings.chocolateMilkLiquid);
			setIcons("mushroomsoup", MFRThings.mushroomSoupLiquid);
			setIcons("steam", MFRThings.steamFluid);
			break;
		default:
			break;
		}
	}

	private void setIcons(String name, BlockFactoryFluid block) {

		Fluid fluid = FluidRegistry.getFluid(name);
		if (block.equals(fluid.getBlock())) {
			fluid.setIcons(block.getIcon(1, 0), block.getIcon(2, 0));
		} else {
			block.setIcons(fluid.getStillIcon(), fluid.getFlowingIcon());
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(Unload world) {

		if (world.world.provider == null ||
				Minecraft.getMinecraft().thePlayer == null ||
				Minecraft.getMinecraft().thePlayer.worldObj == null ||
				Minecraft.getMinecraft().thePlayer.worldObj.provider == null) {
			return;
		}
		if (world.world.provider.dimensionId == Minecraft.getMinecraft().thePlayer.worldObj.provider.dimensionId) {
			_areaTileEntities.clear();
			prcPages.clear();
		}
	}

	@SubscribeEvent
	public void tickStart(PlayerTickEvent evt) {

		if (evt.side != Side.CLIENT | evt.phase != Phase.START)
			return;

		EntityPlayer player = evt.player;
		ItemStack equipped = player.inventory.getCurrentItem();
		if (equipped != null && equipped.getItem() instanceof ItemRocketLauncher) {
			Entity e = rayTrace();
			if (_lastEntityOver != null && _lastEntityOver.isDead) {
				_lastEntityOver = null;
				_lockonTicks = 0;
			} else if ((e == null || e != _lastEntityOver) && _lockonLostTicks > 0) {
				_lockonLostTicks--;
			} else if (e == null && _lockonLostTicks == 0) {
				if (_lockonTicks > 0) {
					_lockonTicks--;
				}
				_lastEntityOver = null;
			} else if (_lastEntityOver == null) {
				_lastEntityOver = e;
			} else if (_lockonTicks < _lockonMax) {
				_lockonTicks++;
				if (_lockonTicks >= _lockonMax) {
					_lockonLostTicks = _lockonLostMax;
				}
			} else if (e != null && e == _lastEntityOver) {
				_lockonLostTicks = _lockonLostMax;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void tickEnd(RenderTickEvent evt) {

		if (evt.phase != Phase.END)
			return;
		renderHUD(evt.renderTickTime);
		// this solves a bug where render pass 0 textures have alpha forced by
		// minecraft's fog on small and tiny render distances
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private void renderHUD(float partialTicks) {

		Minecraft mc = Minecraft.getMinecraft();
		if (!mc.isGamePaused() && mc.currentScreen == null && mc.thePlayer != null &&
				mc.thePlayer.inventory.getCurrentItem() != null
				&& mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemRocketLauncher) {
			ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			Point center = new Point(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2);

			if (MineFactoryReloadedClient.instance.getLockedEntity() != Integer.MIN_VALUE) {
				mc.renderEngine.bindTexture(targetingBlue);
			} else {
				mc.renderEngine.bindTexture(targetingRed);
			}

			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef(center.getX(), center.getY(), 0);
			GL11.glRotatef(((mc.theWorld.getTotalWorldTime() & 511) * 4) % 360 + partialTicks, 0, 0, 1);

			float distance = MineFactoryReloadedClient.instance.getLockTimeRemaining();

			drawLockonPart(center, distance, 0);
			drawLockonPart(center, distance, 90);
			drawLockonPart(center, distance, 180);
			drawLockonPart(center, distance, 270);

			GL11.glPopMatrix();
		}
	}

	private void drawLockonPart(Point center, float distanceFromCenter, int rotation) {

		GL11.glPushMatrix();

		GL11.glRotatef(rotation, 0, 0, 1);
		GL11.glTranslatef(-8, -13, 0);
		GL11.glTranslatef(0, -distanceFromCenter, 0);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2i(0, 0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2i(0, 16);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2i(16, 16);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2i(16, 0);
		GL11.glEnd();

		GL11.glPopMatrix();
	}

	@SubscribeEvent
	public void setArmorModel(SetArmorModel e) {

		ItemStack itemstack = e.stack;

		if (itemstack != null) {
			Item item = itemstack.getItem();
			int par2 = 3 - e.slot;
			//if (item.isValidArmor(itemstack, e.slot, e.entity))
			if (item == plasticCupItem) {
				Minecraft.getMinecraft().renderEngine.
						bindTexture(new ResourceLocation(item.getArmorTexture(itemstack, e.entity, par2, null)));
				ModelBiped modelbiped = new ModelBiped(1.0F);
				modelbiped.bipedHead.showModel = par2 == 0;
				modelbiped.bipedHeadwear.showModel = par2 == 0;
				modelbiped.bipedBody.showModel = par2 == 1 || par2 == 2;
				modelbiped.bipedRightArm.showModel = par2 == 1;
				modelbiped.bipedLeftArm.showModel = par2 == 1;
				modelbiped.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
				modelbiped.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
				e.renderer.setRenderPassModel(modelbiped);
				modelbiped.onGround = e.entityLiving.getSwingProgress(e.partialRenderTick);
				modelbiped.isRiding = e.entity.isRiding();
				modelbiped.isChild = e.entityLiving.isChild();
				float f1 = 1.0F;
				GL11.glColor3f(f1, f1, f1);

				if (itemstack.isItemEnchanted()) {
					e.result = 15;
					return;
				}

				e.result = 1;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	// first to render, so everything else is overlayed
	public void renderWorldLast(RenderWorldLastEvent e) {

		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (player.inventory.getCurrentItem() == null ||
				!player.inventory.getCurrentItem().getItem().equals(factoryHammerItem)) {
			return;
		}

		float playerOffsetX = -(float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * e.partialTicks);
		float playerOffsetY = -(float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * e.partialTicks);
		float playerOffsetZ = -(float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * e.partialTicks);

		GL11.glColorMask(true, true, true, true);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);

		for (IHarvestAreaContainer c : _areaTileEntities) {
			if (((TileEntity) c).isInvalid())
				continue;

			float r = colorFromCoord(c.getHAM().getOriginX(), 0xF8525888);
			float g = colorFromCoord(c.getHAM().getOriginY(), 0x85BDBD8C);
			float b = colorFromCoord(c.getHAM().getOriginZ(), 0x997696BF);

			GL11.glPushMatrix();
			GL11.glColor4f(r, g, b, 0.4F);
			GL11.glTranslatef(playerOffsetX, playerOffsetY, playerOffsetZ);
			renderAABB(c.getHAM().getHarvestArea().toAxisAlignedBB());
			GL11.glPopMatrix();
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private float colorFromCoord(int c, long h) {

		h = (h * c) + 0xBA;
		h ^= (h >>> 20) ^ (h >>> 12);
		h ^= (h >>> 7) ^ (h >>> 4);
		return ((h & 255) / 319f) + 0.1f; // odd values bound to 0.1 <= x < 0.9
	}

	public static void addTileToAreaList(IHarvestAreaContainer tile) {

		_areaTileEntities.add(tile);
	}

	public static void removeTileFromAreaList(IHarvestAreaContainer tile) {

		_areaTileEntities.remove(tile);
	}

	public int getLockedEntity() {

		if (_lastEntityOver != null && _lockonTicks >= _lockonMax) {
			return _lastEntityOver.getEntityId();
		}

		return Integer.MIN_VALUE;
	}

	public int getLockTimeRemaining() {

		if (_lastEntityOver != null && _lockonTicks >= _lockonMax) {
			return _lockonLostMax - _lockonLostTicks;
		} else {
			return (_lockonMax - _lockonTicks) * 2;
		}
	}

	private Entity rayTrace() {

		if (Minecraft.getMinecraft().renderViewEntity == null || Minecraft.getMinecraft().theWorld == null) {
			return null;
		}

		double range = 64;
		Vec3 playerPos = Minecraft.getMinecraft().renderViewEntity.getPosition(1.0F);

		Vec3 playerLook = Minecraft.getMinecraft().renderViewEntity.getLook(1.0F);
		Vec3 playerLookRel = playerPos.addVector(playerLook.xCoord * range, playerLook.yCoord * range, playerLook.zCoord * range);
		List<?> list = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABBExcludingEntity(
			Minecraft.getMinecraft().renderViewEntity,
			Minecraft.getMinecraft().renderViewEntity.boundingBox.addCoord(playerLook.xCoord * range, playerLook.yCoord * range,
				playerLook.zCoord * range)
					.expand(1, 1, 1));

		double entityDistTotal = range;
		Entity pointedEntity = null;
		for (int i = 0; i < list.size(); ++i) {
			Entity entity = (Entity) list.get(i);

			if (entity.canBeCollidedWith()) {
				double entitySize = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.boundingBox.expand(entitySize, entitySize, entitySize);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(playerPos, playerLookRel);

				if (axisalignedbb.isVecInside(playerPos)) {
					if (0.0D < entityDistTotal || entityDistTotal == 0.0D) {
						pointedEntity = entity;
						entityDistTotal = 0.0D;
					}
				} else if (movingobjectposition != null) {
					double entityDist = playerPos.distanceTo(movingobjectposition.hitVec);

					if (entityDist < entityDistTotal || entityDistTotal == 0.0D) {
						pointedEntity = entity;
						entityDistTotal = entityDist;
					}
				}
			}
		}

		if (pointedEntity != null) {
			return pointedEntity;
		}
		return null;
	}

	public static void renderAABB(AxisAlignedBB par0AxisAlignedBB) {

		double eps = 0.006;

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps);

		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps);

		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps);

		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps);

		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps);

		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.draw();
	}

}
