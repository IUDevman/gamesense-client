package com.gamesense.client.devgui.elements;

import java.math.RoundingMode;
import java.math.BigDecimal;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.FontUtils;
import com.gamesense.api.util.Rainbow;
import com.gamesense.client.devgui.DevComponent;
import com.gamesense.client.devgui.DevGUI;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.ColorMain;
import com.gamesense.client.module.modules.hud.DevGuiModule;
import com.gamesense.client.module.modules.hud.HUD;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.Gui;
import java.awt.Color;

public class DevDoubleSlider extends DevComponent{

    private boolean hovered;
    private Setting.d set;
    private DevButton parent;
    private int offset;
    private int x;
    private int y;
    private boolean dragging;
    private double renderWidth;
    
    public DevDoubleSlider(final Setting.d value, final DevButton button, final int offset) {
        this.dragging = false;
        this.set = value;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }
    
    @Override
    public void renderComponent() {
        DevGuiModule devGuiModule= ((DevGuiModule) ModuleManager.getModuleByName("DevGUI"));
        ColorMain colorMain = ((ColorMain) ModuleManager.getModuleByName("Colors"));
        if (colorMain.Rainbow.getValue()){
            DevGUI.color = Rainbow.getColor().getRGB();
        }
        else {
            DevGUI.color = new Color(colorMain.Red.getValue(), colorMain.Green.getValue(), colorMain.Blue.getValue(), devGuiModule.opacity.getValue()).getRGB();
        }
        Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 1, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 16, this.hovered ? new Color(195, 195, 195, devGuiModule.opacity.getValue()-50).darker().darker().getRGB() : new Color(195, 195, 195, devGuiModule.opacity.getValue()-50).getRGB());
        final int drag = (int)(this.set.getValue() / this.set.getMax() * this.parent.parent.getWidth());
        Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 1, this.parent.parent.getX() + (int)this.renderWidth, this.parent.parent.getY() + this.offset + 16, this.hovered ? new Color(DevGUI.color).getRGB() : new Color(DevGUI.color).getRGB());
        Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 1, new Color(195, 195, 195, devGuiModule.opacity.getValue()-50).getRGB());
        FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.set.getName() + " " + ChatFormatting.GRAY + this.set.getValue(), (int)(this.parent.parent.getX() + 2), this.parent.parent.getY() + this.offset + 4, -1);
    }
    
    @Override
    public void setOff(final int newOff) {
        this.offset = newOff;
    }
    
    @Override
    public void updateComponent(final int mouseX, final int mouseY) {
        this.hovered = (this.isMouseOnButtonD(mouseX, mouseY) || this.isMouseOnButtonI(mouseX, mouseY));
        this.y = this.parent.parent.getY() + this.offset;
        this.x = this.parent.parent.getX();
        final double diff = Math.min(100, Math.max(0, mouseX - this.x));
        final double min = this.set.getMin();
        final double max = this.set.getMax();
        this.renderWidth = 100.0 * (this.set.getValue() - min) / (max - min);
        if (this.dragging) {
            if (diff == 0.0) {
                this.set.setValue(this.set.getMin());
            }
            else {
                final double newValue = roundToPlace(diff / 100.0 * (max - min) + min, 2);
                this.set.setValue(newValue);
            }
        }
    }
    
    private static double roundToPlace(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int button) {
        if (this.isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open) {
            this.dragging = true;
        }
        if (this.isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.open) {
            this.dragging = true;
        }
    }
    
    @Override
    public void mouseReleased(final int mouseX, final int mouseY, final int mouseButton) {
        this.dragging = false;
    }
    
    public boolean isMouseOnButtonD(final int x, final int y) {
        return x > this.x && x < this.x + (this.parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 16;
    }
    
    public boolean isMouseOnButtonI(final int x, final int y) {
        return x > this.x + this.parent.parent.getWidth() / 2 && x < this.x + this.parent.parent.getWidth() && y > this.y && y < this.y + 16;
    }
}
