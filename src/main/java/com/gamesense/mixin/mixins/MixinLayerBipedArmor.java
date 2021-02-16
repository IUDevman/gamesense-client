package com.gamesense.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.NoRender;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerBipedArmor.class)
public class MixinLayerBipedArmor {

	@Inject(method = "setModelSlotVisible", at = @At(value = "HEAD"), cancellable = true)
	protected void setModelSlotVisible(ModelBiped model, EntityEquipmentSlot slotIn, CallbackInfo callbackInfo) {
		NoRender noRender = ModuleManager.getModule(NoRender.class);

		if (noRender.isEnabled() && noRender.armor.getValue()) {
			callbackInfo.cancel();
			switch (slotIn) {
				case HEAD: {
					model.bipedHead.showModel = false;
					model.bipedHeadwear.showModel = false;
				}
				case CHEST: {
					model.bipedBody.showModel = false;
					model.bipedRightArm.showModel = false;
					model.bipedLeftArm.showModel = false;
				}
				case LEGS: {
					model.bipedBody.showModel = false;
					model.bipedRightLeg.showModel = false;
					model.bipedLeftLeg.showModel = false;
				}
				case FEET: {
					model.bipedRightLeg.showModel = false;
					model.bipedLeftLeg.showModel = false;
				}
			}
		}
	}
}