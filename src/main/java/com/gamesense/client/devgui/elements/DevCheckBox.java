package com.gamesense.client.devgui.elements;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.FontUtils;
import com.gamesense.api.util.Rainbow;
import com.gamesense.client.devgui.DevComponent;
import com.gamesense.client.devgui.DevGUI;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.ColorMain;
import com.gamesense.client.module.modules.hud.DevGuiModule;
import com.gamesense.client.module.modules.hud.HUD;
import net.minecraft.client.gui.Gui;
import java.awt.Color;

public class DevCheckBox extends DevComponent{

    private boolean hovered;
    private final Setting.b op;
    private final DevButton parent;
    private int offset;
    private int x;
    private int y;


    public DevCheckBox(final Setting.b option, final DevButton button, final int offset) {
        this.op = option;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }
    
    @Override
    public void renderComponent() {
        if (ColorMain.Rainbow.getValue()){
            DevGUI.color = Rainbow.getColorWithOpacity(DevGuiModule.opacity.getValue()).getRGB();
        }
        else {
            DevGUI.color = new Color(ColorMain.Red.getValue(), ColorMain.Green.getValue(), ColorMain.Blue.getValue(), DevGuiModule.opacity.getValue()).getRGB();
        }
        Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 1, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 16, this.hovered ? (this.op.getValue() ? DevGUI.color : new Color(195, 195, 195, DevGuiModule.opacity.getValue()-50).darker().darker().getRGB()) : (this.op.getValue() ? DevGUI.color : new Color(195, 195, 195, DevGuiModule.opacity.getValue()-50).getRGB()));
        Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 1, new Color(195, 195, 195, DevGuiModule.opacity.getValue()-50).getRGB());
        FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.op.getName(),this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset + 4, -1);
    }

    @Override
    public void setOff(final int newOff) {
        this.offset = newOff;
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
            this.op.setValue(!this.op.getValue());
        }
    }
    
    public boolean isMouseOnButton(final int x, final int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 16;
    }
}
