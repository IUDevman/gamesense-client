package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.RenderEntityEvent;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author linustouchtips
 * @since 12/14/2020
 * @author Hoosiers
 * @since 12/31/2020
 */

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T> {

    protected MixinRenderLivingBase() {
        super(null);
    }

    protected final Minecraft mc = Minecraft.getMinecraft();

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    protected void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo callbackInfo) {
        if (!this.bindEntityTexture(entitylivingbaseIn)) {
            return;
        }

        if (NoRender.noCluster.getValue() && ModuleManager.getModuleByName("NoRender").isEnabled() && mc.player.getDistance(entitylivingbaseIn) < 1 && entitylivingbaseIn != mc.player) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        }

        RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head(entitylivingbaseIn, RenderEntityEvent.Type.COLOR);

        GameSense.EVENT_BUS.post(renderEntityHeadEvent);

        if (renderEntityHeadEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderModel", at = @At("RETURN"), cancellable = true)
    protected void renderModelReturn(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo callbackInfo) {
        RenderEntityEvent.Return renderEntityReturnEvent = new RenderEntityEvent.Return(entitylivingbaseIn, RenderEntityEvent.Type.COLOR);

        GameSense.EVENT_BUS.post(renderEntityReturnEvent);

        if (renderEntityReturnEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}