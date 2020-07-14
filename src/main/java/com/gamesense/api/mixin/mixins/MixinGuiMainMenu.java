package com.gamesense.api.mixin.mixins;

import com.gamesense.client.GameSenseMod;
import com.gamesense.api.util.Rainbow;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {
    String s = GameSenseMod.MODNAME + " " + GameSenseMod.MODVER;

    @Inject(method = "drawScreen", at = @At("TAIL"), cancellable = true)
    public void drawText(int mouseX, int mouseY, float partialTicks, CallbackInfo ci){
        this.drawString(this.fontRenderer, s, (this.width / 2) - (this.fontRenderer.getStringWidth(s) /2), 2, Rainbow.getInt());
    }


}
