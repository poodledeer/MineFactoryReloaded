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

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public class RedNetLogicRenderer implements ISimpleBlockRenderingHandler {
	protected static CCModel base;
	protected static CCModel cards;

	public static IconTransformation uvt;
	private static final double rotAmt = Math.PI * 0.5;
	private static final Vector3 axis = Rotation.axes[1];
	private static final Rotation itemRot = new Rotation(-rotAmt, axis);

	static {
		try {
			Map<String, CCModel> cableModels = CCModel.parseObjModels(MineFactoryReloadedCore.class.
					getResourceAsStream("/powercrystals/minefactoryreloaded/models/RedComp.obj"),
					7, new Scale(1/16f));
			base = cableModels.get("case").backfacedCopy();
			compute(base);

			cards = cableModels.get("cards").backfacedCopy();
			compute(cards);
		} catch (Throwable throwable) { throwable.printStackTrace(); }
	}

	private static void compute(CCModel m) {
		m.apply(new Rotation(rotAmt * 2, axis));
		m.computeNormals();
		m.shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	public static void updateUVT(IIcon icon) {
		uvt = new IconTransformation(icon);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		CCRenderState.reset();
		CCRenderState.useNormals = true;
		CCRenderState.computeLighting = false;
		Tessellator tess = Tessellator.instance;

		tess.startDrawingQuads();
		base.render(itemRot, uvt);
		cards.render(itemRot, uvt);
		tess.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		CCRenderState.reset();
		CCRenderState.useNormals = true;
		int brightness = block.getMixedBrightnessForBlock(world, x, y, z);

		int meta = world.getBlockMetadata(x, y, z);

		Tessellator tess = Tessellator.instance;
		tess.setColorOpaque_F(1,1,1);
		tess.setBrightness(brightness);

		Rotation rot = new Rotation(rotAmt * (~meta & 3), axis);
		Translation tlate = new Translation(new Vector3(x + 0.5, y + 0.5, z + 0.5));

		base.render(rot.with(tlate), LightModel.standardLightModel, uvt);
		cards.render(rot.with(tlate), LightModel.standardLightModel, uvt);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return MineFactoryReloadedCore.renderIdRedNetLogic;
	}

}
