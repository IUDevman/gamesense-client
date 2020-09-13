package com.gamesense.api.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.NoRender;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinPlayerOverlay {

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    protected void renderPumpkinOverlayHook(ScaledResolution scaledRes, CallbackInfo info) {
        if (ModuleManager.isModuleEnabled("NoRender") && ((NoRender) ModuleManager.getModuleByName("NoRender")).noOverlay.getValue()) {
            info.cancel();
        }
    }

    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    protected void renderPotionEffectsHook(ScaledResolution scaledRes, CallbackInfo info) {
        if (ModuleManager.isModuleEnabled("NoRender") && ((NoRender) ModuleManager.getModuleByName("NoRender")).noOverlay.getValue()) {
            info.cancel();
        }
    }
}
