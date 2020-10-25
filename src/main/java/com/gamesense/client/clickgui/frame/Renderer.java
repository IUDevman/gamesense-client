package com.gamesense.client.clickgui.frame;

import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import net.minecraft.client.gui.Gui;

public class Renderer {

    /**
     * @Author Hoosiers and Lukflug
     * 09/08/20, 10/22/20
     */

    //no gradient, single color
    public static void drawRectStatic(int leftX, int leftY, int rightX, int rightY, GSColor color){
        Gui.drawRect(leftX,leftY,rightX,rightY,color.getRGB());
    }

    public static void drawCategoryBox(int posX, int posY, int finalX, int finalY, GSColor color){
        GSColor shadeColor = new GSColor(color, ClickGuiModule.opacity.getValue());
        GSColor outlineColor = new GSColor(ClickGuiModule.outlineColor.getValue(), 255);

        //box
        Gui.drawRect(posX + 1, posY + 1, finalX - 1, finalY -1, shadeColor.getRGB());
        //top outline
        Gui.drawRect(posX, posY, finalX, posY + 1, outlineColor.getRGB());
        //bottom outline
        Gui.drawRect(posX, finalY - 1, finalX, finalY, outlineColor.getRGB());
        //left outline
        Gui.drawRect(posX, posY, posX + 1, finalY, outlineColor.getRGB());
        //right outline
        Gui.drawRect(finalX -1, posY, finalX, finalY, outlineColor.getRGB());
    }

    public static void drawModuleBox(int posX, int posY, int finalX, int finalY, GSColor color){
        GSColor shadeColor = new GSColor(color, ClickGuiModule.opacity.getValue());
        GSColor outlineColor = new GSColor(ClickGuiModule.outlineColor.getValue(), 255);

        //box
        Gui.drawRect(posX + 1, posY, finalX - 1, finalY, shadeColor.getRGB());
        //left
        Gui.drawRect(posX, posY, posX + 1, finalY, outlineColor.getRGB());
        //right
        Gui.drawRect(finalX - 1, posY, finalX, finalY, outlineColor.getRGB());
    }

    public static void drawSliderBox(boolean direction, int posX, int posY, int finalX, int finalY, GSColor color){
        GSColor shadeColor = new GSColor(color, ClickGuiModule.opacity.getValue());
        GSColor outlineColor = new GSColor(ClickGuiModule.outlineColor.getValue(), 255);

        //left
        if (direction == true) {
            Gui.drawRect(posX + 1, posY, finalX, finalY, shadeColor.getRGB());
            Gui.drawRect(posX, posY, posX + 1, finalY, outlineColor.getRGB());
        }
        //right
        else {
            Gui.drawRect(posX, posY, finalX - 1, finalY, shadeColor.getRGB());
            Gui.drawRect(finalX - 1, posY, finalX, finalY, outlineColor.getRGB());
        }
    }

    public static GSColor getEnabledColor(boolean hovered) {
        GSColor enabledColor = new GSColor(ClickGuiModule.enabledColor.getValue());

        if (hovered){
            if (enabledColor.getRed() + enabledColor.getBlue() + enabledColor.getGreen() > 383) {
                enabledColor = new GSColor(ClickGuiModule.enabledColor.getValue().darker().darker());
            }
            else {
                enabledColor = new GSColor(ClickGuiModule.enabledColor.getValue().brighter().brighter());
            }
        }
        return enabledColor;
    }

    public static GSColor getBackgroundColor(boolean hovered) {
        GSColor transColor = new GSColor(ClickGuiModule.backgroundColor.getValue(), ClickGuiModule.opacity.getValue());

        if (hovered) {
            if (transColor.getRed() + transColor.getGreen() + transColor.getBlue() > 383) {
                return new GSColor(transColor.darker().darker());
            }
            else {
                return new GSColor(transColor.brighter().brighter());
            }
        }

        return transColor;
    }

    public static GSColor getSettingColor(boolean hovered) {
        GSColor settingColor = new GSColor(ClickGuiModule.settingBackgroundColor.getValue(), ClickGuiModule.opacity.getValue());

        if (hovered) {
            if (settingColor.getRed() + settingColor.getGreen() + settingColor.getBlue() > 383) {
                return new GSColor(settingColor.darker().darker());
            }
            else {
                return new GSColor(settingColor.brighter().brighter());
            }
        }

        return settingColor;
    }

    public static GSColor getFontColor() {
        return ClickGuiModule.fontColor.getValue();
    }
}