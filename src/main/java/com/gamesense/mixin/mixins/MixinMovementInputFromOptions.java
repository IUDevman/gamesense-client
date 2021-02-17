package com.gamesense.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.PlayerTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MovementInputFromOptions.class, priority = 10000)
public abstract class MixinMovementInputFromOptions extends MovementInput {

	@Redirect(method = "updatePlayerMoveState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"))
	public boolean isKeyPressed(KeyBinding keyBinding) {
		PlayerTweaks playerTweaks = ModuleManager.getModule(PlayerTweaks.class);

		if (playerTweaks.isEnabled() && playerTweaks.guiMove.getValue()
				&& Minecraft.getMinecraft().currentScreen != null
				&& !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)
				&& Minecraft.getMinecraft().player != null) {
			return Keyboard.isKeyDown(keyBinding.getKeyCode());
		}
		return keyBinding.isKeyDown();
	}
}