package com.gamesense.client.clickgui.frame;

import com.gamesense.api.util.font.FontUtils;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.clickgui.ClickGUI;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

public class Frames {
    public ArrayList<Component> guicomponents;
    public Module.Category category;
    private final int width;
    private final int barHeight;
    private int height = 16;
    public int x;
    public int y;
    public int dragX;
    public int dragY;
    private boolean isDragging;
    public boolean open;
    boolean font;

    public Frames(final Module.Category catg, int posX, int posY, int width){
        this.guicomponents = new ArrayList<Component>();
        this.category = catg;
        this.open = true;
        this.isDragging = false;
        this.x = posX;
        this.y = posY;
        this.dragX = 0;
        this.width = width;
        this.barHeight = 16;
        int tY = this.barHeight;

        for (final Module mod : ModuleManager.getModulesInCategory(catg)){
            final Buttons devmodButton = new Buttons(mod, this, tY);
            this.guicomponents.add(devmodButton);
            tY += 16;
        }
        //this.guicomponents.add(new FrameCap(this, tY));
        this.refresh();
    }

    public ArrayList<Component> getComponents() {
        return this.guicomponents;
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

    public void renderGUIFrame(){
        Renderer.drawCategoryBox(this.x, this.y, this.x + this.width, this.y + this.barHeight, Renderer.getEnabledColor(false));
        if(font) GameSenseMod.fontRenderer.drawStringWithShadow(this.category.name(), (float)(this.x + 2), (float)(this.y + 4), Renderer.getFontColor());
        else FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), this.category.name(), this.x + 2, this.y + 4, Renderer.getFontColor());
        if (this.open && !this.guicomponents.isEmpty()){
            for (final Component component : this.guicomponents){
                component.renderComponent();
            }
            Gui.drawRect(this.getX(), this.getY() + this.height, this.getX() + this.getWidth(), this.getY() + 1 + this.height, ClickGuiModule.outlineColor.getValue().getRGB());
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
        for (final Component comp : this.guicomponents){
            comp.setOff(off);
            off += comp.getHeight();
        }
        this.height = off;
    }

    public void updateMouseWheel() {
        int scrollWheel = Mouse.getDWheel();
        for (final Frames frames : ClickGUI.frames) {
            if (scrollWheel < 0) {
                frames.setY(frames.getY() - ClickGuiModule.scrollSpeed.getValue());
            }
            else if (scrollWheel > 0) {
                frames.setY(frames.getY() + ClickGuiModule.scrollSpeed.getValue());
            }
        }
    }
}