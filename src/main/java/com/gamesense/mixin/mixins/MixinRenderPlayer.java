package com.gamesense.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.TargetHUD;
import com.gamesense.client.module.modules.hud.TargetInfo;
import com.gamesense.client.module.modules.render.Nametags;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer {

    @Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
    private void renderLivingLabel(AbstractClientPlayer entity, double x, double y, double z, String name, double distanceSq, CallbackInfo callbackInfo) {
        if (ModuleManager.isModuleEnabled(Nametags.class)) {
            callbackInfo.cancel();
        }

        if (ModuleManager.isModuleEnabled(TargetHUD.class) && TargetHUD.isRenderingEntity(entity)) {
            callbackInfo.cancel();
        }

        if (ModuleManager.isModuleEnabled(TargetInfo.class) && TargetInfo.isRenderingEntity(entity)) {
            callbackInfo.cancel();
        }
    }
}