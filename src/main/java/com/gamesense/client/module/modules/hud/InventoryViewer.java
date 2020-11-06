package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryViewer extends Module {
    public InventoryViewer(){
        super("InventoryViewer", Category.HUD);
    }

    Setting.Integer posX;
    Setting.Integer posY;
    Setting.ColorSetting fillColor;
    Setting.ColorSetting outlineColor;

    public void setup(){
        posX = registerInteger("X", "X", 0, 0, 1000);
        posY = registerInteger("Y", "Y", 10, 0, 1000);
        fillColor = registerColor("Fill", "Fill", new GSColor(0, 0, 0, 100));
        outlineColor = registerColor("Outline", "Outline", new GSColor(255, 0, 0, 255));
    }

    public void onRender() {
        GSColor fillWithOpacity = new GSColor(fillColor.getValue(), 100);
        GSColor outlineWithOpacity = new GSColor(outlineColor.getValue(), 255);

        //shaded box
        Gui.drawRect(posX.getValue() + 1, posY.getValue() + 1, posX.getValue() + 161, posY.getValue() + 55, fillWithOpacity.getRGB());
        //top
        Gui.drawRect(posX.getValue(), posY.getValue(), posX.getValue() + 162, posY.getValue() + 1, outlineWithOpacity.getRGB());
        //bottom
        Gui.drawRect(posX.getValue(), posY.getValue() + 55, posX.getValue() + 162, posY.getValue() + 56, outlineWithOpacity.getRGB());
        //left
        Gui.drawRect(posX.getValue(), posY.getValue(), posX.getValue() + 1, posY.getValue() + 56, outlineWithOpacity.getRGB());
        //right
        Gui.drawRect(posX.getValue() + 161, posY.getValue(), posX.getValue() + 162, posY.getValue() + 56, outlineWithOpacity.getRGB());

        //items
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        NonNullList<ItemStack> items = Minecraft.getMinecraft().player.inventory.mainInventory;
        for (int size = items.size(), item = 9; item < size; ++item) {
            final int slotX = posX.getValue() + item % 9 * 18;
            final int slotY = posY.getValue() + 2 + (item / 9 - 1) * 18;
            mc.getRenderItem().renderItemAndEffectIntoGUI(items.get(item), slotX, slotY);
            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, items.get(item), slotX, slotY);
        }
        RenderHelper.disableStandardItemLighting();
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.popMatrix();
    }
}