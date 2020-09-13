package com.gamesense.api.mixin.mixins;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {

    @Shadow
    private final Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();

    @Inject(
            method = "Lnet/minecraft/client/network/NetHandlerPlayClient;handlePlayerListItem(Lnet/minecraft/network/play/server/SPacketPlayerListItem;)V",
            at = @At("HEAD")
    )
    public void preHandlePlayerListItem(SPacketPlayerListItem listItem, CallbackInfo callbackInfo) {
        try {
            if (listItem.getEntries().size() <= 1) {
                if (listItem.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
                    listItem.getEntries().forEach(data -> {
                        if (!data.getProfile().getId().equals(Minecraft.getMinecraft().player.getGameProfile().getId()) && data.getProfile().getName() != null) {
                        }
                    });
                } else if (listItem.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                    listItem.getEntries().forEach(data2 -> {
                        if (data2.getProfile().getId() != null && !data2.getProfile().getId().equals(Minecraft.getMinecraft().player.getGameProfile().getId())) {
                        }
                    });
                }
            }
        } catch (Exception e) {
        }
    }
}
