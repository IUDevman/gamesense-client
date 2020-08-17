package com.gamesense.api.mixin.mixins;

import com.gamesense.api.event.events.TransformSideFirstPersonEvent;
import com.gamesense.client.GameSenseMod;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

    @Inject(method = "transformSideFirstPerson", at = @At("HEAD"))
    public void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_, CallbackInfo ci) {
        TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
        GameSenseMod.EVENT_BUS.post(event);
    }
}
