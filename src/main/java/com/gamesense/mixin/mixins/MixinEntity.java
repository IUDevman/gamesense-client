package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.EntityCollisionEvent;
import com.gamesense.client.GameSense;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
    public void velocity(Entity entityIn, CallbackInfo ci) {
        EntityCollisionEvent event = new EntityCollisionEvent();
        GameSense.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}