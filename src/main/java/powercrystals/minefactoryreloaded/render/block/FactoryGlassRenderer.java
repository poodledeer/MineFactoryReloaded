package powercrystals.minefactoryreloaded.render.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryGlass;

public class FactoryGlassRenderer implements ISimpleBlockRenderingHandler
{
	@Override
	public void renderInventoryBlock(Block tile, int metadata, int modelID, RenderBlocks renderer)
	{
		BlockFactoryGlass block = (BlockFactoryGlass)tile;

		Tessellator tessellator = Tessellator.instance;
		int color = block.getRenderColor(metadata);
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable)
		{
			float anaglyphRed = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
			float anaglyphGreen = (red * 30.0F + green * 70.0F) / 100.0F;
			float anaglyphBlue = (red * 30.0F + blue * 70.0F) / 100.0F;
			red = anaglyphRed;
			green = anaglyphGreen;
			blue = anaglyphBlue;
		}
		IIcon iconGlass, iconStreaks, iconOverlay;

		iconGlass = block.getIcon(0, metadata);
		iconStreaks = block.getIcon(0, 16 | metadata);
		iconOverlay = block.getBlockOverlayTexture();

		double minXGlass = iconGlass.getMinU();
		double maxXGlass = iconGlass.getMaxU();
		double minYGlass = iconGlass.getMinV();
		double maxYGlass = iconGlass.getMaxV();

		double minXStreaks = iconStreaks.getMinU();
		double maxXStreaks = iconStreaks.getMaxU();
		double minYStreaks = iconStreaks.getMinV();
		double maxYStreaks = iconStreaks.getMaxV();

		double minXOverlay = iconOverlay.getMinU();
		double maxXOverlay = iconOverlay.getMaxU();
		double minYOverlay = iconOverlay.getMinV();
		double maxYOverlay = iconOverlay.getMaxV();


		double xMin = 0, xMax = 1;
		double yMin = 0, yMax = 1;
		double zMin = 0, zMax = 1;

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		tessellator.setColorOpaque_F(red, green, blue);
		tessellator.addVertexWithUV(xMin, yMin, zMax, minXGlass, minYGlass);
		tessellator.addVertexWithUV(xMin, yMin, zMin, minXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMax, yMin, zMin, maxXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMax, yMin, zMax, maxXGlass, minYGlass);

		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.addVertexWithUV(xMin, yMin, zMax, minXStreaks, minYStreaks);
		tessellator.addVertexWithUV(xMin, yMin, zMin, minXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMax, yMin, zMin, maxXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMax, yMin, zMax, maxXStreaks, minYStreaks);

		tessellator.addVertexWithUV(xMin, yMin, zMax, minXOverlay, minYOverlay);
		tessellator.addVertexWithUV(xMin, yMin, zMin, minXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMax, yMin, zMin, maxXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMax, yMin, zMax, maxXOverlay, minYOverlay);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.setColorOpaque_F(red, green, blue);
		tessellator.addVertexWithUV(xMin, yMax, zMin, minXGlass, minYGlass);
		tessellator.addVertexWithUV(xMin, yMax, zMax, minXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMax, yMax, zMax, maxXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMax, yMax, zMin, maxXGlass, minYGlass);

		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.addVertexWithUV(xMin, yMax, zMin, minXStreaks, minYStreaks);
		tessellator.addVertexWithUV(xMin, yMax, zMax, minXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMax, yMax, zMax, maxXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMax, yMax, zMin, maxXStreaks, minYStreaks);

		tessellator.addVertexWithUV(xMin, yMax, zMin, minXOverlay, minYOverlay);
		tessellator.addVertexWithUV(xMin, yMax, zMax, minXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMax, yMax, zMax, maxXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMax, yMax, zMin, maxXOverlay, minYOverlay);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.setColorOpaque_F(red, green, blue);
		tessellator.addVertexWithUV(xMax, yMax, zMin, minXGlass, minYGlass);
		tessellator.addVertexWithUV(xMax, yMin, zMin, minXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMin, yMin, zMin, maxXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMin, yMax, zMin, maxXGlass, minYGlass);

		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.addVertexWithUV(xMax, yMax, zMin, minXStreaks, minYStreaks);
		tessellator.addVertexWithUV(xMax, yMin, zMin, minXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMin, yMin, zMin, maxXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMin, yMax, zMin, maxXStreaks, minYStreaks);

		tessellator.addVertexWithUV(xMax, yMax, zMin, minXOverlay, minYOverlay);
		tessellator.addVertexWithUV(xMax, yMin, zMin, minXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMin, yMin, zMin, maxXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMin, yMax, zMin, maxXOverlay, minYOverlay);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.setColorOpaque_F(red, green, blue);
		tessellator.addVertexWithUV(xMin, yMax, zMax, minXGlass, minYGlass);
		tessellator.addVertexWithUV(xMin, yMin, zMax, minXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMax, yMin, zMax, maxXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMax, yMax, zMax, maxXGlass, minYGlass);

		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.addVertexWithUV(xMin, yMax, zMax, minXStreaks, minYStreaks);
		tessellator.addVertexWithUV(xMin, yMin, zMax, minXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMax, yMin, zMax, maxXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMax, yMax, zMax, maxXStreaks, minYStreaks);

		tessellator.addVertexWithUV(xMin, yMax, zMax, minXOverlay, minYOverlay);
		tessellator.addVertexWithUV(xMin, yMin, zMax, minXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMax, yMin, zMax, maxXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMax, yMax, zMax, maxXOverlay, minYOverlay);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		tessellator.setColorOpaque_F(red, green, blue);
		tessellator.addVertexWithUV(xMin, yMin, zMax, minXGlass, minYGlass);
		tessellator.addVertexWithUV(xMin, yMax, zMax, minXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMin, yMax, zMin, maxXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMin, yMin, zMin, maxXGlass, minYGlass);

		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.addVertexWithUV(xMin, yMin, zMax, minXStreaks, minYStreaks);
		tessellator.addVertexWithUV(xMin, yMax, zMax, minXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMin, yMax, zMin, maxXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMin, yMin, zMin, maxXStreaks, minYStreaks);

		tessellator.addVertexWithUV(xMin, yMin, zMax, minXOverlay, minYOverlay);
		tessellator.addVertexWithUV(xMin, yMax, zMax, minXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMin, yMax, zMin, maxXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMin, yMin, zMin, maxXOverlay, minYOverlay);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		tessellator.setColorOpaque_F(red, green, blue);
		tessellator.addVertexWithUV(xMax, yMin, zMin, minXGlass, minYGlass);
		tessellator.addVertexWithUV(xMax, yMax, zMin, minXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMax, yMax, zMax, maxXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMax, yMin, zMax, maxXGlass, minYGlass);

		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.addVertexWithUV(xMax, yMin, zMin, minXStreaks, minYStreaks);
		tessellator.addVertexWithUV(xMax, yMax, zMin, minXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMax, yMax, zMax, maxXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMax, yMin, zMax, maxXStreaks, minYStreaks);

		tessellator.addVertexWithUV(xMax, yMin, zMin, minXOverlay, minYOverlay);
		tessellator.addVertexWithUV(xMax, yMax, zMin, minXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMax, yMax, zMax, maxXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMax, yMin, zMax, maxXOverlay, minYOverlay);
		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z,
			Block tile, int modelId, RenderBlocks renderer)
	{
		if (renderer.hasOverrideBlockTexture())
		{ // usually: block is being broken
			renderer.renderFaceYNeg(tile, x, y, z, null);
			renderer.renderFaceYPos(tile, x, y, z, null);
			renderer.renderFaceZNeg(tile, x, y, z, null);
			renderer.renderFaceZPos(tile, x, y, z, null);
			renderer.renderFaceXNeg(tile, x, y, z, null);
			renderer.renderFaceXPos(tile, x, y, z, null);
			return true;
		}
		BlockFactoryGlass block = (BlockFactoryGlass)tile;

		int worldHeight = blockAccess.getHeight();
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z));
		int color = block.getRenderColor(metadata);
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable)
		{
			float anaglyphRed = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
			float anaglyphGreen = (red * 30.0F + green * 70.0F) / 100.0F;
			float anaglyphBlue = (red * 30.0F + blue * 70.0F) / 100.0F;
			red = anaglyphRed;
			green = anaglyphGreen;
			blue = anaglyphBlue;
		}

		IIcon iconGlass, iconStreaks, iconOverlayTop, iconOverlaySouth, iconOverlayWest;

		iconGlass = block.getIcon(0, metadata);
		iconStreaks = block.getIcon(0, 16 | metadata);
		iconOverlayTop = block.getBlockOverlayTexture(blockAccess, x, y, z, 1);
		iconOverlaySouth = block.getBlockOverlayTexture(blockAccess, x, y, z, 2);
		iconOverlayWest = block.getBlockOverlayTexture(blockAccess, x, y, z, 5);

		double minXGlass = iconGlass.getMinU();
		double maxXGlass = iconGlass.getMaxU();
		double minYGlass = iconGlass.getMinV();
		double maxYGlass = iconGlass.getMaxV();

		double minXStreaks = iconStreaks.getMinU();
		double maxXStreaks = iconStreaks.getMaxU();
		double minYStreaks = iconStreaks.getMinV();
		double maxYStreaks = iconStreaks.getMaxV();

		double minXSouth = iconOverlaySouth.getMinU();
		double maxXSouth = iconOverlaySouth.getMaxU();
		double minYSouth = iconOverlaySouth.getMinV();
		double maxYSouth = iconOverlaySouth.getMaxV();

		double minXWest = iconOverlayWest.getMinU();
		double maxXWest = iconOverlayWest.getMaxU();
		double minYWest = iconOverlayWest.getMinV();
		double maxYWest = iconOverlayWest.getMaxV();

		double minXTop = iconOverlayTop.getMinU();
		double maxXTop = iconOverlayTop.getMaxU();
		double minYTop = iconOverlayTop.getMinV();
		double maxYTop = iconOverlayTop.getMaxV();

		final double xMin = x, xMax = x + 1;
		final double yMin = y, yMax = y + 1;
		final double zMin = z, zMax = z + 1;
		final double offset = 0.003;

		boolean renderAll = renderer.renderAllFaces;

		boolean[] renderSide = {
				renderAll || y <= 0 || block.shouldSideBeRendered(blockAccess, x, y - 1, z, 0),
						renderAll || y >= worldHeight || block.shouldSideBeRendered(blockAccess, x, y + 1, z, 1),
						renderAll || block.shouldSideBeRendered(blockAccess, x, y, z - 1, 2),
						renderAll || block.shouldSideBeRendered(blockAccess, x, y, z + 1, 3),
						renderAll || block.shouldSideBeRendered(blockAccess, x - 1, y, z, 4),
						renderAll || block.shouldSideBeRendered(blockAccess, x + 1, y, z, 5),
		};

		if (renderSide[1])
		{ // UP
			tessellator.setColorOpaque_F(red, green, blue);
			tessellator.addVertexWithUV(xMin, yMax - offset, zMin, minXGlass, minYGlass);
			tessellator.addVertexWithUV(xMin, yMax - offset, zMax, minXGlass, maxYGlass);
			tessellator.addVertexWithUV(xMax, yMax - offset, zMax, maxXGlass, maxYGlass);
			tessellator.addVertexWithUV(xMax, yMax - offset, zMin, maxXGlass, minYGlass);

			tessellator.setColorOpaque_F(1, 1, 1);
			tessellator.addVertexWithUV(xMin, yMax, zMin, minXStreaks, minYStreaks);
			tessellator.addVertexWithUV(xMin, yMax, zMax, minXStreaks, maxYStreaks);
			tessellator.addVertexWithUV(xMax, yMax, zMax, maxXStreaks, maxYStreaks);
			tessellator.addVertexWithUV(xMax, yMax, zMin, maxXStreaks, minYStreaks);

			tessellator.addVertexWithUV(xMin - offset, yMax, zMin - offset, minXTop, minYTop);
			tessellator.addVertexWithUV(xMin - offset, yMax, zMax + offset, minXTop, maxYTop);
			tessellator.addVertexWithUV(xMax + offset, yMax, zMax + offset, maxXTop, maxYTop);
			tessellator.addVertexWithUV(xMax + offset, yMax, zMin - offset, maxXTop, minYTop);
		}

		if (renderSide[0])
		{ // DOWN
			tessellator.setColorOpaque_F(red, green, blue);
			tessellator.addVertexWithUV(xMin, yMin + offset, zMax, minXGlass, maxYGlass);
			tessellator.addVertexWithUV(xMin, yMin + offset, zMin, minXGlass, minYGlass);
			tessellator.addVertexWithUV(xMax, yMin + offset, zMin, maxXGlass, minYGlass);
			tessellator.addVertexWithUV(xMax, yMin + offset, zMax, maxXGlass, maxYGlass);

			tessellator.setColorOpaque_F(1, 1, 1);
			tessellator.addVertexWithUV(xMin, yMin, zMax, minXStreaks, maxYStreaks);
			tessellator.addVertexWithUV(xMin, yMin, zMin, minXStreaks, minYStreaks);
			tessellator.addVertexWithUV(xMax, yMin, zMin, maxXStreaks, minYStreaks);
			tessellator.addVertexWithUV(xMax, yMin, zMax, maxXStreaks, maxYStreaks);

			tessellator.addVertexWithUV(xMin - offset, yMin, zMax + offset, minXTop, maxYTop);
			tessellator.addVertexWithUV(xMin - offset, yMin, zMin - offset, minXTop, minYTop);
			tessellator.addVertexWithUV(xMax + offset, yMin, zMin - offset, maxXTop, minYTop);
			tessellator.addVertexWithUV(xMax + offset, yMin, zMax + offset, maxXTop, maxYTop);
		}

		if (renderSide[2])
		{
			tessellator.setColorOpaque_F(red, green, blue);
			tessellator.addVertexWithUV(xMax, yMax, zMin + offset, maxXGlass, minYGlass);
			tessellator.addVertexWithUV(xMax, yMin, zMin + offset, maxXGlass, maxYGlass);
			tessellator.addVertexWithUV(xMin, yMin, zMin + offset, minXGlass, maxYGlass);
			tessellator.addVertexWithUV(xMin, yMax, zMin + offset, minXGlass, minYGlass);

			tessellator.setColorOpaque_F(1, 1, 1);
			tessellator.addVertexWithUV(xMax, yMax, zMin, maxXStreaks, minYStreaks);
			tessellator.addVertexWithUV(xMax, yMin, zMin, maxXStreaks, maxYStreaks);
			tessellator.addVertexWithUV(xMin, yMin, zMin, minXStreaks, maxYStreaks);
			tessellator.addVertexWithUV(xMin, yMax, zMin, minXStreaks, minYStreaks);

			tessellator.addVertexWithUV(xMax + offset, yMax + offset, zMin, maxXSouth, minYSouth);
			tessellator.addVertexWithUV(xMax + offset, yMin - offset, zMin, maxXSouth, maxYSouth);
			tessellator.addVertexWithUV(xMin - offset, yMin - offset, zMin, minXSouth, maxYSouth);
			tessellator.addVertexWithUV(xMin - offset, yMax + offset, zMin, minXSouth, minYSouth);
		}

		if (renderSide[3])
		{
			tessellator.setColorOpaque_F(red, green, blue);
			tessellator.addVertexWithUV(xMin, yMax, zMax - offset, minXGlass, minYGlass);
			tessellator.addVertexWithUV(xMin, yMin, zMax - offset, minXGlass, maxYGlass);
			tessellator.addVertexWithUV(xMax, yMin, zMax - offset, maxXGlass, maxYGlass);
			tessellator.addVertexWithUV(xMax, yMax, zMax - offset, maxXGlass, minYGlass);

			tessellator.setColorOpaque_F(1, 1, 1);
			tessellator.addVertexWithUV(xMin, yMax, zMax, minXStreaks, minYStreaks);
			tessellator.addVertexWithUV(xMin, yMin, zMax, minXStreaks, maxYStreaks);
			tessellator.addVertexWithUV(xMax, yMin, zMax, maxXStreaks, maxYStreaks);
			tessellator.addVertexWithUV(xMax, yMax, zMax, maxXStreaks, minYStreaks);

			tessellator.addVertexWithUV(xMin - offset, yMax + offset, zMax, minXSouth, minYSouth);
			tessellator.addVertexWithUV(xMin - offset, yMin - offset, zMax, minXSouth, maxYSouth);
			tessellator.addVertexWithUV(xMax + offset, yMin - offset, zMax, maxXSouth, maxYSouth);
			tessellator.addVertexWithUV(xMax + offset, yMax + offset, zMax, maxXSouth, minYSouth);
		}

		if (renderSide[4])
		{
			tessellator.setColorOpaque_F(red, green, blue);
			tessellator.addVertexWithUV(xMin + offset, yMin, zMax, maxXGlass, maxYGlass);
			tessellator.addVertexWithUV(xMin + offset, yMax, zMax, maxXGlass, minYGlass);
			tessellator.addVertexWithUV(xMin + offset, yMax, zMin, minXGlass, minYGlass);
			tessellator.addVertexWithUV(xMin + offset, yMin, zMin, minXGlass, maxYGlass);

			tessellator.setColorOpaque_F(1, 1, 1);
			tessellator.addVertexWithUV(xMin, yMin, zMax, maxXStreaks, maxYStreaks);
			tessellator.addVertexWithUV(xMin, yMax, zMax, maxXStreaks, minYStreaks);
			tessellator.addVertexWithUV(xMin, yMax, zMin, minXStreaks, minYStreaks);
			tessellator.addVertexWithUV(xMin, yMin, zMin, minXStreaks, maxYStreaks);

			tessellator.addVertexWithUV(xMin, yMin - offset, zMax + offset, maxXWest, maxYWest);
			tessellator.addVertexWithUV(xMin, yMax + offset, zMax + offset, maxXWest, minYWest);
			tessellator.addVertexWithUV(xMin, yMax + offset, zMin - offset, minXWest, minYWest);
			tessellator.addVertexWithUV(xMin, yMin - offset, zMin - offset, minXWest, maxYWest);
		}

		if (renderSide[5])
		{
			tessellator.setColorOpaque_F(red, green, blue);
			tessellator.addVertexWithUV(xMax - offset, yMin, zMin, minXGlass, maxYGlass);
			tessellator.addVertexWithUV(xMax - offset, yMax, zMin, minXGlass, minYGlass);
			tessellator.addVertexWithUV(xMax - offset, yMax, zMax, maxXGlass, minYGlass);
			tessellator.addVertexWithUV(xMax - offset, yMin, zMax, maxXGlass, maxYGlass);

			tessellator.setColorOpaque_F(1, 1, 1);
			tessellator.addVertexWithUV(xMax, yMin, zMin, minXStreaks, maxYStreaks);
			tessellator.addVertexWithUV(xMax, yMax, zMin, minXStreaks, minYStreaks);
			tessellator.addVertexWithUV(xMax, yMax, zMax, maxXStreaks, minYStreaks);
			tessellator.addVertexWithUV(xMax, yMin, zMax, maxXStreaks, maxYStreaks);

			tessellator.addVertexWithUV(xMax, yMin - offset, zMin - offset, minXWest, maxYWest);
			tessellator.addVertexWithUV(xMax, yMax + offset, zMin - offset, minXWest, minYWest);
			tessellator.addVertexWithUV(xMax, yMax + offset, zMax + offset, maxXWest, minYWest);
			tessellator.addVertexWithUV(xMax, yMin - offset, zMax + offset, maxXWest, maxYWest);
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return MineFactoryReloadedCore.renderIdFactoryGlass;
	}
}
