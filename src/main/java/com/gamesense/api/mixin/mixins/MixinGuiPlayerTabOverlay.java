package com.gamesense.api.mixin.mixins;

import com.gamesense.api.util.players.enemy.Enemies;
import com.gamesense.api.util.players.friends.Friends;
import com.gamesense.client.module.modules.hud.ColorMain;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay{

	@Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
	public void getPlayerName(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable returnable){
			returnable.cancel();
			returnable.setReturnValue(getPlayerName(networkPlayerInfoIn));
	}

	public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn){
		String dname = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
		if (Friends.isFriend(dname)) return ColorMain.getFriendColor() + dname;
		else if (Enemies.isEnemy(dname)) return ColorMain.getEnemyColor() + dname;
		else return dname;
	}
}
