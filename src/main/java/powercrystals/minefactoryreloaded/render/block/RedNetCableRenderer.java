package powercrystals.minefactoryreloaded.render.block;

import cofh.lib.render.RenderHelper;
import cofh.repack.codechicken.lib.lighting.LightModel;
import cofh.repack.codechicken.lib.render.CCModel;
import cofh.repack.codechicken.lib.render.CCRenderState;
import cofh.repack.codechicken.lib.render.uv.IconTransformation;
import cofh.repack.codechicken.lib.vec.Rotation;
import cofh.repack.codechicken.lib.vec.Scale;
import cofh.repack.codechicken.lib.vec.Translation;
import cofh.repack.codechicken.lib.vec.Vector3;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;

public class RedNetCableRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {
	protected static CCModel base;
	protected static CCModel cage;
	protected static CCModel[] cable = new CCModel[6];
	protected static CCModel[] iface = new CCModel[6];
	protected static CCModel[] band  = new CCModel[6];
	protected static CCModel[] plate = new CCModel[6];
	protected static CCModel[] platef = new CCModel[6];
	protected static CCModel[] grip  = new CCModel[6];
	protected static CCModel[] wire  = new CCModel[6];
	protected static CCModel[] caps  = new CCModel[6];

	public static IconTransformation uvt;
	public static boolean brightBand;

	static {
		try {
			Map<String, CCModel> cableModels = CCModel.parseObjModels(MineFactoryReloadedCore.class.
					getResourceAsStream("/powercrystals/minefactoryreloaded/models/RedNetCable.obj"),
					7, new Scale(1/16f));
			Vector3 p = new Vector3(0, 0, 0);
			base = cableModels.get("base").backfacedCopy();
			compute(base);

			cage = cableModels.get("cage").backfacedCopy();
			compute(cage);

			cable[5] = cableModels.get("cable").backfacedCopy();
			calculateSidedModels(cable, p);

			iface[5] = cableModels.get("interface").backfacedCopy();
			calculateSidedModels(iface, p);

			band[5] = cableModels.get("band").backfacedCopy();
			calculateSidedModels(band, p);

			plate[5] = cableModels.get("plate").backfacedCopy();
			calculateSidedModels(plate, p);

			platef[5] = cableModels.get("plateface").backfacedCopy();
			calculateSidedModels(platef, p);

			grip[5] = cableModels.get("grip").backfacedCopy();
			calculateSidedModels(grip, p);

			wire[5] = cableModels.get("wire").backfacedCopy();
			calculateSidedModels(wire, p);

			caps[5] = cableModels.get("cap").backfacedCopy();
			calculateSidedModels(caps, p);
		} catch (Throwable throwable) { throwable.printStackTrace(); }
	}

	private static void calculateSidedModels(CCModel[] m, Vector3 p) {
		compute(m[4] = m[5].copy().apply(new Rotation(Math.PI * 1.0, 0, 1, 0)));
		compute(m[3] = m[5].copy().apply(new Rotation(Math.PI * -.5, 0, 1, 0)));
		compute(m[2] = m[5].copy().apply(new Rotation(Math.PI * 0.5, 0, 1, 0)));
		compute(m[1] = m[5].copy().apply(new Rotation(Math.PI * 0.5, 0, 0, 1).with(new Rotation(Math.PI, 0, 1, 0))));
		compute(m[0] = m[5].copy().apply(new Rotation(Math.PI * -.5, 0, 0, 1)));
		compute(m[5]);
	}

	private static void compute(CCModel m) {
		m.apply(new Translation(0.5, 0.5, 0.5));
		m.computeNormals();
		m.computeLighting(LightModel.standardLightModel);
		m.shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	public static void updateUVT(IIcon icon) {
		uvt = new IconTransformation(icon);
		brightBand = MFRConfig.brightRednetBand.getBoolean(true);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		CCRenderState.reset();
		CCRenderState.useNormals = true;
		Tessellator tess = Tessellator.instance;

		GL11.glTranslatef(-.5f, -.5f, -.5f);
		tess.startDrawingQuads();
		base.render(uvt);
		cable[2].render(uvt);
		cable[3].render(uvt);
		if (metadata == 3 | metadata == 2)
		{
			cage.render(uvt);
			wire[2].render(uvt);
			wire[3].render(uvt);
			caps[2].render(uvt);
			caps[3].render(uvt);
		}
		tess.draw();
	}

	private ForgeDirection[] dirs = ForgeDirection.VALID_DIRECTIONS;

	@Override
	public void renderTileEntityAt(TileEntity tile, double rx, double ry, double rz, float partialTick)
	{
		TextureManager renderengine = Minecraft.getMinecraft().renderEngine;

		if (renderengine != null)
		{
			renderengine.bindTexture(RenderHelper.MC_BLOCK_SHEET);
		}

		GL11.glPushMatrix();
		GL11.glTranslated(rx, ry, rz);
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);

		if (Minecraft.isAmbientOcclusionEnabled())
		{
			GL11.glShadeModel(GL11.GL_SMOOTH);
		}
		else
		{
			GL11.glShadeModel(GL11.GL_FLAT);
		}

		World world = tile.getWorldObj();
		int x = tile.xCoord, y = tile.yCoord, z = tile.zCoord;
		int brightness = world.getBlock(x, y, z).getMixedBrightnessForBlock(world, x, y, z);

		Tessellator.instance.startDrawingQuads();
		renderCable(world, 0, 0, 0, brightness, (TileEntityRedNetCable)tile);
		Tessellator.instance.draw();

		GL11.glPopMatrix();
		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		TileEntityRedNetCable _cable = (TileEntityRedNetCable)world.getTileEntity(x, y, z);

		if (MFRConfig.TESRCables && _cable.onRender())
			return false;

		int brightness = block.getMixedBrightnessForBlock(world, x, y, z);

		return renderCable(world, x, y, z, brightness, _cable);
	}

	private boolean renderCable(IBlockAccess world, int x, int y, int z, int brightness,
			TileEntityRedNetCable _cable)
	{
		CCRenderState.reset();
		CCRenderState.useNormals = false;
		TileEntityRedNetEnergy _cond = null;
		int bandBrightness = brightBand ? 0xd00070 : brightness;

		Tessellator tess = Tessellator.instance;
		tess.setColorOpaque_F(1,1,1);
		tess.setBrightness(brightness);

		if ((x | y | z) != 0)
			tess.addTranslation(x, y, z);

		base.render(uvt);
		if (_cable instanceof TileEntityRedNetEnergy) {
			cage.render(uvt);
			_cond = (TileEntityRedNetEnergy)_cable;
		}

		ForgeDirection[] dirs = this.dirs;

		for (int i = dirs.length; i --> 0; ) {
			ForgeDirection f = dirs[i];
			int side = f.ordinal();
			RedNetConnectionType state = _cable.getCachedConnectionState(f);
			switch (state.flags & 31) {
			case 11: // isCable, isSingleSubnet
			tess.setBrightness(bandBrightness);
			band[side].setColour(_cable.getSideColorValue(f));
			band[side].render(uvt);
			tess.setColorOpaque_F(1,1,1);
			tess.setBrightness(brightness);
			case 19: // isCable, isAllSubnets
				if (state.isSingleSubnet) {
					iface[side].render(uvt);
					grip[side].render(uvt);
				} else
					cable[side].render(uvt);
				break;
			case 13: // isPlate, isSingleSubnet
				tess.setBrightness(bandBrightness);
				band[side].setColour(_cable.getSideColorValue(f));
				band[side].render(uvt);
				platef[side].setColour(_cable.getSideColorValue(f));
				platef[side].render(uvt);
				tess.setColorOpaque_F(1,1,1);
				tess.setBrightness(brightness);
			case 21: // isPlate, isAllSubnets
				iface[side].render(uvt);
				plate[side].render(uvt);
				if (state.isAllSubnets) {
					platef[side].setColour(-1);
					platef[side].render(uvt);
				}
			default:
				break;
			}
			if (_cond != null && _cond.isInterfacing(f)) {
				wire[side].render(uvt);
				if (_cond.interfaceMode(f) != 4)
					caps[side].render(uvt);
			}
		}

		if ((x | y | z) != 0)
			tess.addTranslation(-x, -y, -z);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return MineFactoryReloadedCore.renderIdRedNet;
	}

}
