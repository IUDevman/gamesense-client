package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.RenderEntityEvent;
import com.gamesense.client.GameSense;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityXPOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Hoosiers
 * @since 12/29/2020
 */

@Mixin(RenderManager.class)
public class MixinRenderManager {

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    public void renderEntityHead(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo callbackInfo) {
        RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head(entityIn, RenderEntityEvent.Type.TEXTURE);

        GameSense.EVENT_BUS.post(renderEntityHeadEvent);

        if (entityIn instanceof EntityEnderPearl || entityIn instanceof EntityXPOrb || entityIn instanceof EntityExpBottle || entityIn instanceof EntityEnderCrystal) {
            RenderEntityEvent.Head renderEntityEvent = new RenderEntityEvent.Head(entityIn, RenderEntityEvent.Type.COLOR);

            GameSense.EVENT_BUS.post(renderEntityEvent);

            if (renderEntityEvent.isCancelled()) {
                callbackInfo.cancel();
            }
        }

        if (renderEntityHeadEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderEntity", at = @At("RETURN"), cancellable = true)
    public void renderEntityReturn(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo callbackInfo) {
        RenderEntityEvent.Return renderEntityReturnEvent = new RenderEntityEvent.Return(entityIn, RenderEntityEvent.Type.TEXTURE);

        GameSense.EVENT_BUS.post(renderEntityReturnEvent);

        if (entityIn instanceof EntityEnderPearl || entityIn instanceof EntityXPOrb || entityIn instanceof EntityExpBottle || entityIn instanceof EntityEnderCrystal) {
            RenderEntityEvent.Return renderEntityEvent = new RenderEntityEvent.Return(entityIn, RenderEntityEvent.Type.COLOR);

            GameSense.EVENT_BUS.post(renderEntityEvent);

            if (renderEntityEvent.isCancelled()) {
                callbackInfo.cancel();
            }
        }

        if (renderEntityReturnEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}