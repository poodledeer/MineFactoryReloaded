package powercrystals.minefactoryreloaded.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.entity.DebugTracker;

public class EntityDebugTrackerRenderer extends Render {

	@Override
	public void doRender(Entity p_76986_1_, double x, double y, double z, float yaw, float partialTicks) {

		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		float playerOffsetX = -(float)(player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks);
		float playerOffsetY = -(float)(player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks);
		float playerOffsetZ = -(float)(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks);

		DebugTracker ent = (DebugTracker) p_76986_1_;
		GL11.glColorMask(true, true, true, true);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);

		GL11.glPushMatrix();
		x = ent.posX;
		y = ent.posY;
		z = ent.posZ;

		{
			float r = colorFromCoord(Float.floatToIntBits((float) z), 0xF8525888);
			float g = colorFromCoord(Float.floatToIntBits((float) x), 0x85BDBD8C);
			float b = colorFromCoord(Float.floatToIntBits((float) y), 0x997696BF);

			GL11.glPushMatrix();
			GL11.glColor4f(r, g, b, 0.4F);
			GL11.glTranslatef(playerOffsetX, playerOffsetY, playerOffsetZ);
			AxisAlignedBB bb = ent.getPrjBB();
			float q = ent.getDataWatcher().getWatchableObjectFloat(14);
			float w = ent.getDataWatcher().getWatchableObjectFloat(15);
			float e = ent.getDataWatcher().getWatchableObjectFloat(16);
			for (int i = 3; i --> 0; ) {
				renderAABB(bb);
				bb.offset(q, w, e);
			}
			GL11.glPopMatrix();
		}

		{
			float r = colorFromCoord(Float.floatToIntBits((float) x), 0xF8525888);
			float g = colorFromCoord(Float.floatToIntBits((float) y), 0x85BDBD8C);
			float b = colorFromCoord(Float.floatToIntBits((float) z), 0x997696BF);

			GL11.glPushMatrix();
			GL11.glColor4f(r, g, b, 0.4F);
			GL11.glTranslatef(playerOffsetX, playerOffsetY, playerOffsetZ);
			AxisAlignedBB bb = ent.getSrcBB();
			renderAABB(bb);
			float e = ent.getDataWatcher().getWatchableObjectFloat(17);
			bb.minY += e;
			bb.maxY = bb.minY + (1/8f);
			renderAABB(bb.expand(0.006, 0, 0.006));
			GL11.glPopMatrix();
		}

		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_TEXTURE_2D);

	}

	private float colorFromCoord(int c, long h)
	{

		h = (h * c) + 0xBA;
		h ^= (h >>> 20) ^ (h >>> 12);
		h ^= (h >>> 7) ^ (h >>> 4);
		return ((h & 255) / 319f) + 0.1f; // odd values bound to 0.1 <= x < 0.9
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {

		return null;
	}

}
