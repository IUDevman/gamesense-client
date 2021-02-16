package com.gamesense.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.ChatModifier;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {

	@Shadow private int scrollPos;

	@Shadow @Final private List<ChatLine> drawnChatLines;

	@Shadow public abstract int getLineCount();

	@Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
	private void drawRectBackgroundClean(int left, int top, int right, int bottom, int color) {
		ChatModifier chatModifier = ModuleManager.getModule(ChatModifier.class);

		if (!chatModifier.isEnabled() || !chatModifier.clearBkg.getValue()) {
			Gui.drawRect(left, top, right, bottom, color);
		}
	}
}