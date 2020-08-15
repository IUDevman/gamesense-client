package com.gamesense.client.devgui.elements;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.FontUtils;
import com.gamesense.client.devgui.DevComponent;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.DevGuiModule;
import com.gamesense.client.module.modules.hud.HUD;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.Gui;
import java.awt.Color;

public class DevStringButton extends DevComponent{

    private final Setting.s op;
    private final DevButton parent;
    private int offset;
    
    public DevStringButton(final Setting.s option, final DevButton button, final int offset) {
        this.op = option;
        this.parent = button;
        this.offset = offset;
    }
    
    @Override
    public void renderComponent() {
        DevGuiModule devGuiModule= ((DevGuiModule) ModuleManager.getModuleByName("DevGUI"));
        Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 1, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 16, new Color(195, 195, 195, DevGuiModule.opacity.getValue()-50).getRGB());
        Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 1, new Color(195, 195, 195, DevGuiModule.opacity.getValue()-50).getRGB());
        FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.op.getName() + " " + ChatFormatting.GRAY + "-set", this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset + 4, -1);
    }
    
    @Override
    public void setOff(final int newOff) {
        this.offset = newOff;
    }
}
