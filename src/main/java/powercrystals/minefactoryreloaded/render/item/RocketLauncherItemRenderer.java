package powercrystals.minefactoryreloaded.render.item;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

@SideOnly(Side.CLIENT)
public class RocketLauncherItemRenderer implements IItemRenderer
{
	private static final ResourceLocation launcher =
			new ResourceLocation(MineFactoryReloadedCore.modelTextureFolder + "RocketLauncher.png");
	private IModelCustom _model;
	
	public RocketLauncherItemRenderer()
	{
		try
		{
			_model = AdvancedModelLoader.loadModel(new ResourceLocation(
					MineFactoryReloadedCore.modelFolder + "RocketLauncher.obj"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return helper != ItemRendererHelper.EQUIPPED_BLOCK;
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		TextureManager renderengine = Minecraft.getMinecraft().renderEngine;
		
		if(renderengine != null)
		{
			renderengine.bindTexture(launcher);
		}
		
		GL11.glPushMatrix();
		
		if(type == ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			GL11.glRotatef(270, 0, 1, 0);
			GL11.glRotatef(300, 1, 0, 0);
			GL11.glTranslatef(-0.3F, 0.3F, 0.5F);
			GL11.glScalef(0.03F, 0.03F, 0.03F);
		}
		else if(type == ItemRenderType.EQUIPPED)
		{
			GL11.glRotatef(270, 1, 0, 0);
			GL11.glTranslatef(0.3F, -0.4F, 1.0F);
			GL11.glScalef(0.03F, 0.03F, 0.03F);
		}
		else
		{
			GL11.glRotatef(270, 1, 0, 0);
			GL11.glScalef(0.025F, 0.025F, 0.025F);
		}
		
		_model.renderAll();
		
		GL11.glPopMatrix();
	}
}
