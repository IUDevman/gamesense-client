package com.gamesense.client.devgui;

import com.gamesense.api.util.FontUtils;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.devgui.elements.DevButton;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.DevGuiModule;
import com.gamesense.client.module.modules.hud.HUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;

public class DevFrame{

    public ArrayList<DevComponent> devcomponents;
    public Module.Category category;
    private final int width;
    private final int barHeight;
    private int height;
    public int x;
    public int y;
    public int dragX;
    public int dragY;
    private boolean isDragging;
    public boolean open;
    boolean font;

    DevGuiModule mod = ((DevGuiModule) ModuleManager.getModuleByName("DevGuiModule"));

    public DevFrame(final Module.Category catg){
        this.devcomponents = new ArrayList<DevComponent>();
        this.category = catg;
        this.open = true;
        this.isDragging = false;
        this.x = 5;
        this.y = 5;
        this.dragX = 0;
        this.width = 100;
        this.barHeight = 16;
        int tY = this.barHeight;

        for (final Module mod : ModuleManager.getModulesInCategory(catg)){
            final DevButton devmodButton = new DevButton(mod, this, tY);
            this.devcomponents.add(devmodButton);
            tY += 16;
        }
        this.refresh();
    }

    public ArrayList<DevComponent> getComponents() {
        return this.devcomponents;
    }

    public int getWidth() {
        return this.width;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public void setX(final int newX) {
        this.x = newX;
    }

    public void setY(final int newY) {
        this.y = newY;
    }

    public void renderDevFrame(final FontRenderer fontRenderer){
        Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.barHeight, DevGUI.color);
        Minecraft mc = Minecraft.getMinecraft();
        if(font) GameSenseMod.fontRenderer.drawStringWithShadow(this.category.name(), (float)(this.x + 2), (float)(this.y + 3), -1);
        else FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.category.name(), this.x + 2, this.y + 3, -1);
        if (this.open && !this.devcomponents.isEmpty()){
            for (final DevComponent devComponent : this.devcomponents){
                devComponent.renderComponent();
            }
        }
    }

    public void updatePosition(final int mouseX, final int mouseY){
        if (this.isDragging){
            this.setX(mouseX - this.dragX);
            this.setY(mouseY - this.dragY);
        }
    }

    public boolean isWithinHeader(final int x, final int y){
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight;
    }

    public void setDrag(final boolean drag){
        this.isDragging = drag;
    }

    public void setOpen(final boolean open){
        this.open = open;
    }

    public boolean isOpen(){
        return this.open;
    }

    public void refresh(){
        int off = this.barHeight;
        for (final DevComponent comp : this.devcomponents){
            comp.setOff(off);
            off += comp.getHeight();
        }
        this.height = off;
    }
}