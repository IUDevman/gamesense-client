package com.gamesense.api.mixin.mixins;

import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.CapesModule;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {

    @Shadow @Nullable protected abstract NetworkPlayerInfo getPlayerInfo();

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir){
        UUID uuid = getPlayerInfo().getGameProfile().getId();
        CapesModule capesModule = ((CapesModule) ModuleManager.getModuleByName("Capes"));

        if(ModuleManager.isModuleEnabled("Capes") && GameSenseMod.getInstance().capeUtils.hasCape(uuid)) {
            if (capesModule.capeMode.getValue().equalsIgnoreCase("Black")) {
                cir.setReturnValue(new ResourceLocation("gamesense:textures/capeblack.png"));
            }
            else {
                cir.setReturnValue(new ResourceLocation("gamesense:textures/capewhite.png"));
            }
        }
    }
}
