package com.gamesense.api.mixin.mixins;

import net.minecraft.block.Block;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class MixinBlock {

    @Shadow
    public abstract String getLocalizedName();

    @Inject(method = "getRenderLayer", at = @At("HEAD"))
    public void preGetRenderLayer(CallbackInfoReturnable<BlockRenderLayer> cir) {
        if (this.getLocalizedName().equalsIgnoreCase("hmmm")) {
        }
    }
}
