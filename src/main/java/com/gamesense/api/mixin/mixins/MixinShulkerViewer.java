package com.gamesense.api.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin (GuiScreen.class)
public class MixinShulkerViewer {

    RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
    ResourceLocation resource;
    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    public void renderToolTip(ItemStack stack, int x, int y, CallbackInfo info) {
        resource = new ResourceLocation("textures/gui/container/shulker_box.png");
        if (ModuleManager.isModuleEnabled("ShulkerViewer") && stack.getItem() instanceof ItemShulkerBox) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10)) {
                NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
                if (blockEntityTag.hasKey("Items", 9)) {
                    info.cancel();

                    NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
                    ItemStackHelper.loadAllItems(blockEntityTag, nonnulllist);

                    GlStateManager.enableBlend();
                    GlStateManager.disableRescaleNormal();
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();

                    int x1 = x + 4;
                    int y1 = y - 30;
                    this.itemRender.zLevel = 300.0F;

                    //renders shulker gui
                    Minecraft.getMinecraft().renderEngine.bindTexture(resource);
                    GlStateManager.color(1, 1, 1, 1);
                    Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x1, y1, 7, 5, 162, 66);

                    //renders name
                    fontRenderer.drawString(stack.getDisplayName(), x +6 , y - 28, Color.DARK_GRAY.getRGB());

                    GlStateManager.enableBlend();
                    GlStateManager.enableAlpha();
                    GlStateManager.enableTexture2D();
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                    RenderHelper.enableGUIStandardItemLighting();
                    //renders items
                    for (int i = 0; i < nonnulllist.size(); i++) {
                        int iX = x + 5 + i % 9 * 18;
                        int iY = y + 1 + (i / 9 - 1) * 18;
                        ItemStack itemStack = nonnulllist.get(i);
                        itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
                        itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemStack, iX, iY, null);
                    }
                    RenderHelper.disableStandardItemLighting();
                    this.itemRender.zLevel = 0.0F;
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.enableRescaleNormal();
                }
            }
        }
    }
}