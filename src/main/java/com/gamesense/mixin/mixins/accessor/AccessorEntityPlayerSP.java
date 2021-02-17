package com.gamesense.mixin.mixins.accessor;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayerSP.class)
public interface AccessorEntityPlayerSP {

    @Accessor("handActive")
    void gsSetHandActive(boolean value);

}
