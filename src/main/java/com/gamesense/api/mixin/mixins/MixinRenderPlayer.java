package com.gamesense.api.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.TargetHUD;
import com.gamesense.client.module.modules.hud.TargetInfo;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (RenderPlayer.class)
public abstract class MixinRenderPlayer {

	@Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
	private void renderLivingLabel(AbstractClientPlayer entity, double x, double y, double z, String name, double distanceSq, CallbackInfo callback){
		if (ModuleManager.isModuleEnabled("Nametags")){
			callback.cancel();
		}
		if (ModuleManager.isModuleEnabled("TargetHUD") && TargetHUD.isRenderingEntity(entity)){
			callback.cancel();
		}

		if (ModuleManager.isModuleEnabled("TargetInfo") && TargetInfo.isRenderingEntity(entity)){
			callback.cancel();
		}
	}
}
