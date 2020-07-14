package com.gamesense.api.mixin.mixins;

import com.gamesense.client.GameSenseMod;
import com.gamesense.api.event.events.PlayerJumpEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {
    @Shadow public abstract String getName();

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void onJump(CallbackInfo ci){
        if(Minecraft.getMinecraft().player.getName() == this.getName()){
            GameSenseMod.EVENT_BUS.post(new PlayerJumpEvent());
        }
    }
}
