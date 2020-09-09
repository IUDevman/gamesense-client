package com.gamesense.api.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (RenderPlayer.class)
public abstract class MixinNametags{

	@Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
	private void renderLivingLabel(AbstractClientPlayer entity, double x, double y, double z, String name, double distanceSq, CallbackInfo callback){
		if (ModuleManager.isModuleEnabled("Nametags")){
			callback.cancel();
		}
	}
}
