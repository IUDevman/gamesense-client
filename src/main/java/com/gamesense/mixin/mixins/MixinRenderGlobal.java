package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.DrawBlockDamageEvent;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.BlockHighlight;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Hoosiers
 * @since 12/14/2020
 */

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

    @Inject(method = "drawSelectionBox", at = @At("HEAD"), cancellable = true)
    public void drawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks, CallbackInfo callbackInfo) {
        if (ModuleManager.isModuleEnabled(BlockHighlight.class)) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "drawBlockDamageTexture", at = @At("HEAD"), cancellable = true)
    public void drawBlockDamageTexture(Tessellator tessellatorIn, BufferBuilder bufferBuilderIn, Entity entityIn, float partialTicks, CallbackInfo callbackInfo) {
        DrawBlockDamageEvent drawBlockDamageEvent = new DrawBlockDamageEvent();

        GameSense.EVENT_BUS.post(drawBlockDamageEvent);

        if (drawBlockDamageEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}