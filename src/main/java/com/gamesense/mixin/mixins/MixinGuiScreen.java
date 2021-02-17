package com.gamesense.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.ShulkerViewer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (GuiScreen.class)
public class MixinGuiScreen {

	@Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
	public void renderToolTip(ItemStack stack, int x, int y, CallbackInfo callbackInfo) {
		if (ModuleManager.isModuleEnabled(ShulkerViewer.class) && stack.getItem() instanceof ItemShulkerBox) {
			if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("BlockEntityTag", 10)) {
				if (stack.getTagCompound().getCompoundTag("BlockEntityTag").hasKey("Items", 9)) {
					callbackInfo.cancel();
					ShulkerViewer.renderShulkerPreview(stack, x + 6, y - 33, 162, 66);
				}
			}
		}
	}
}