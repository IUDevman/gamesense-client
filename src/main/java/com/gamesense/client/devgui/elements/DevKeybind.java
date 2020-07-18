package com.gamesense.client.devgui.elements;

import com.gamesense.api.util.FontUtils;
import com.gamesense.client.devgui.DevComponent;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.DevGuiModule;
import com.gamesense.client.module.modules.hud.HUD;
import org.lwjgl.input.Keyboard;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.Gui;
import java.awt.Color;

public class DevKeybind extends DevComponent{

    private boolean hovered;
    private boolean binding;
    private DevButton parent;
    private int offset;
    private int x;
    private int y;
    
    public DevKeybind(final DevButton button, final int offset) {
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }
    
    @Override
    public void setOff(final int newOff) {
        this.offset = newOff;
    }
    
    @Override
    public void renderComponent() {
        DevGuiModule devGuiModule= ((DevGuiModule) ModuleManager.getModuleByName("DevGUI"));
        Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 1, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 15, this.hovered ? new Color(195, 195, 195, devGuiModule.opacity.getValue()-50).darker().darker().getRGB() : new Color(30, 30, 30, devGuiModule.opacity.getValue()-50).getRGB());
        Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 1, new Color(195, 195, 195, devGuiModule.opacity.getValue()-50).getRGB());
        Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 15, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 16, new Color(0, 0, 0, devGuiModule.opacity.getValue()).getRGB());
        FontUtils.drawKeyStringWithShadow(HUD.customFont.getValue(), this.binding ? "Key..." : ("Key: " + ChatFormatting.GRAY + Keyboard.getKeyName(this.parent.mod.getBind())), (this.parent.parent.getX() + 2), (this.parent.parent.getY() + this.offset + 4), -1);
    }
    
    @Override
    public void updateComponent(final int mouseX, final int mouseY) {
        this.hovered = this.isMouseOnButton(mouseX, mouseY);
        this.y = this.parent.parent.getY() + this.offset;
        this.x = this.parent.parent.getX();
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int button) {
        if (this.isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
            this.binding = !this.binding;
        }
    }
    
    @Override
    public void keyTyped(final char typedChar, final int key) {
        if (this.binding) {
            if (key == 211) {
                this.parent.mod.setBind(0);
            }
            //prevents you from binding to the escape key
            if (key == Keyboard.KEY_ESCAPE){
                this.binding = false;
            }
            else {
                this.parent.mod.setBind(key);
            }
            this.binding = false;
        }
    }
    
    public boolean isMouseOnButton(final int x, final int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 16;
    }
}
