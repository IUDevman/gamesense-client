package com.gamesense.mixin.mixins;

import com.gamesense.api.util.player.enemy.Enemies;
import com.gamesense.api.util.player.friend.Friends;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    public void getPlayerNameHead(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(getPlayerNameGS(networkPlayerInfoIn));
    }

    private String getPlayerNameGS(NetworkPlayerInfo networkPlayerInfoIn) {
        String displayName = networkPlayerInfoIn.getDisplayName() != null ?
                networkPlayerInfoIn.getDisplayName().getFormattedText() :
                ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());

        if (Friends.isFriend(displayName)) {
            return ColorMain.getFriendColor() + displayName;
        } else if (Enemies.isEnemy(displayName)) {
            return ColorMain.getEnemyColor() + displayName;
        } else {
            return displayName;
        }
    }
}