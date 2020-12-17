package com.gamesense.api.mixin.mixins;

import com.gamesense.api.event.events.TransformSideFirstPersonEvent;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.NoRender;
import com.gamesense.client.module.modules.render.ViewModel;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Check ViewModel.class for further credits
 */

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

	@Inject(method = "transformSideFirstPerson", at = @At("HEAD"))
	public void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_, CallbackInfo callbackInfo) {
		TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
		GameSenseMod.EVENT_BUS.post(event);
	}

	@Inject(method = "transformEatFirstPerson", at = @At("HEAD"), cancellable = true)
	public void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack, CallbackInfo callbackInfo) {
		TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
		GameSenseMod.EVENT_BUS.post(event);
		if (ModuleManager.isModuleEnabled("ViewModel") && ((ViewModel)ModuleManager.getModuleByName("ViewModel")).cancelEating.getValue()) {
			callbackInfo.cancel();
		}
	}

	@Inject(method = "transformFirstPerson", at = @At("HEAD"))
	public void transformFirstPerson(EnumHandSide hand, float p_187453_2_, CallbackInfo callbackInfo) {
		TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
		GameSenseMod.EVENT_BUS.post(event);
	}

	@Inject(method = "renderOverlays", at = @At("HEAD"), cancellable = true)
	public void renderOverlays(float partialTicks, CallbackInfo callbackInfo) {
		if (ModuleManager.isModuleEnabled("NoRender") && ((NoRender)ModuleManager.getModuleByName("NoRender")).noOverlay.getValue()) {
			callbackInfo.cancel();
		}
	}
}