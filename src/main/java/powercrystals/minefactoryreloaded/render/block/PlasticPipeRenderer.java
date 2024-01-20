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
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe;

public class PlasticPipeRenderer implements ISimpleBlockRenderingHandler {
	protected static CCModel base;
	protected static CCModel[] cable = new CCModel[6];
	protected static CCModel[] iface = new CCModel[6];
	protected static CCModel[] gripO = new CCModel[6];
	protected static CCModel[] gripI = new CCModel[6];
	protected static CCModel[] gripP = new CCModel[6];

	public static IconTransformation uvt;

	static {
		try {
			Map<String, CCModel> cableModels = CCModel.parseObjModels(MineFactoryReloadedCore.class.
					getResourceAsStream("/powercrystals/minefactoryreloaded/models/PlasticPipe.obj"),
					7, new Scale(1/16f));
			Vector3 p = new Vector3(0, 0, 0);
			base = cableModels.get("base").backfacedCopy();
			compute(base);

			iface[5] = cableModels.get("interface").backfacedCopy();
			calculateSidedModels(iface, p);

			cable[5] = cableModels.get("cable").backfacedCopy();
			calculateSidedModels(cable, p);

			gripO[5] = cableModels.get("gripO").backfacedCopy();
			calculateSidedModels(gripO, p);

			gripI[5] = cableModels.get("gripI").backfacedCopy();
			calculateSidedModels(gripI, p);

			gripP[5] = cableModels.get("gripP").backfacedCopy();
			calculateSidedModels(gripP, p);
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
		m.computeNormals();
		m.apply(new Translation(0.5, 0.5, 0.5));
		m.computeLighting(LightModel.standardLightModel);
		m.shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	public static void updateUVT(IIcon icon) {
		uvt = new IconTransformation(icon);
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
		tess.draw();
	}

	private ForgeDirection[] dirs = ForgeDirection.VALID_DIRECTIONS;

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		CCRenderState.reset();
		CCRenderState.useNormals = true;
		CCRenderState.alphaOverride = 0xff;
		TileEntityPlasticPipe _cable = (TileEntityPlasticPipe)world.getTileEntity(x, y, z);
		int brightness = block.getMixedBrightnessForBlock(world, x, y, z);

		Tessellator tess = Tessellator.instance;
		tess.setColorOpaque_F(1,1,1);
		tess.setBrightness(brightness);

		tess.addTranslation(x, y, z);

		base.render(uvt);
		ForgeDirection[] dirs = this.dirs;

		for (int i = dirs.length; i --> 0; ) {
			ForgeDirection f = dirs[i];
			if (_cable.isInterfacing(f))
			{
				int side = f.ordinal();
				switch (_cable.interfaceMode(f)) {
				case 2: // cable
					cable[side].render(uvt);
					break;
				case 1: // IFluidHandler
					iface[side].render(uvt);
					int state = _cable.getMode(side);
					if ((state & 2) == 2)
						if (_cable.isPowered())
							gripI[side].render(uvt);
						else
							gripP[side].render(uvt);
					else
						gripO[side].render(uvt);
					break;
				default:
					break;
				}
			}
		}

		tess.addTranslation(-x, -y, -z);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return MineFactoryReloadedCore.renderIdPPipe;
	}

}
