package powercrystals.minefactoryreloaded.render.entity;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;

public class EntityPinkSlimeRenderer extends RenderSlime {
	private static final ResourceLocation pinkSlimeTexture = new ResourceLocation(MineFactoryReloadedCore.mobTextureFolder + "pinkslime.png");
	
	public EntityPinkSlimeRenderer(ModelBase par1ModelBase, ModelBase par2ModelBase, float par3) {
		super(par1ModelBase, par2ModelBase, par3);
	}

    @Override
	protected ResourceLocation getEntityTexture(EntitySlime par1EntitySlime)
    {
        return pinkSlimeTexture;
    }

}
