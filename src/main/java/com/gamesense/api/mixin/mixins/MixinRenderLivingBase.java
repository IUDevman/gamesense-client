package com.gamesense.api.mixin.mixins;

import com.gamesense.api.util.misc.Wrapper;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.NoRender;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author linustouchtips
 * @since 12/14/2020
 */

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T> {
    protected MixinRenderLivingBase() {
        super(null);
    }

    @Overwrite
    protected void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        if (!this.bindEntityTexture(entitylivingbaseIn))
            return;

        if (NoRender.noCluster.getValue() && ModuleManager.getModuleByName("NoRender").isEnabled() && Wrapper.getPlayer().getDistance(entitylivingbaseIn) < 1)
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
    }
}
