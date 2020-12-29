package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.RenderEntityHeadEvent;
import com.gamesense.api.event.events.RenderEntityReturnEvent;
import com.gamesense.client.GameSense;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
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
        RenderEntityHeadEvent renderEntityHeadEvent = new RenderEntityHeadEvent(entityIn, new BlockPos(x, y, z));

        GameSense.EVENT_BUS.post(renderEntityHeadEvent);

        if (renderEntityHeadEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderEntity", at = @At("RETURN"), cancellable = true)
    public void renderEntityReturn(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo callbackInfo) {
        RenderEntityReturnEvent renderEntityReturnEvent = new RenderEntityReturnEvent(entityIn, new BlockPos(x, y, z));

        GameSense.EVENT_BUS.post(renderEntityReturnEvent);

        if (renderEntityReturnEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}