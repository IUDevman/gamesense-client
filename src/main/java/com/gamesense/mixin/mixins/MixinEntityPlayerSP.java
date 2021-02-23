package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.Sprint;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    public MixinEntityPlayerSP(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(AbstractClientPlayer player, MoverType type, double x, double y, double z) {
        PlayerMoveEvent moveEvent = new PlayerMoveEvent(type, x, y, z);
        GameSense.EVENT_BUS.post(moveEvent);
        super.move(type, moveEvent.x, moveEvent.y, moveEvent.z);
    }

    @ModifyArg(method = "setSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;setSprinting(Z)V"), index = 0)
    public boolean modifySprinting(boolean sprinting) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        Sprint sprint = ModuleManager.getModule(Sprint.class);

        if (player != null && sprint.isEnabled() && sprint.shouldSprint(player)) {
            return true;
        } else {
            return sprinting;
        }
    }
}