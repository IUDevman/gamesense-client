package com.gamesense.client.devgui.elements;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.FontUtils;
import com.gamesense.api.util.Rainbow;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.devgui.DevComponent;
import com.gamesense.client.devgui.DevFrame;
import com.gamesense.client.devgui.DevGUI;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.ColorMain;
import com.gamesense.client.module.modules.hud.DevGuiModule;
import com.gamesense.client.module.modules.hud.HUD;
import net.minecraft.client.gui.Gui;
import java.awt.Color;
import java.util.ArrayList;

public class DevButton extends DevComponent {
    public Module mod;
    public DevFrame parent;
    public int offset;
    private boolean isHovered;
    private ArrayList<DevComponent> subcomponents;
    public boolean open;
    private int height;

    public DevButton(final Module mod, final DevFrame parent, final int offset) {
        this.mod = mod;
        this.parent = parent;
        this.offset = offset;
        this.subcomponents = new ArrayList<DevComponent>();
        this.open = false;
        this.height = 16;
        int opY = offset + 16;
        if (GameSenseMod.getInstance().settingsManager.getSettingsForMod(mod) != null && !GameSenseMod.getInstance().settingsManager.getSettingsForMod(mod).isEmpty()) {
            for (final Setting s : GameSenseMod.getInstance().settingsManager.getSettingsForMod(mod)) {
                switch (s.getType()) {
                    case MODE: {
                        this.subcomponents.add(new DevModeButton((Setting.mode)s, this, mod, opY));
                        opY += 16;
                        continue;
                    }
                    case STRING: {
                        this.subcomponents.add(new DevStringButton((Setting.s)s, this, opY));
                        opY += 16;
                        continue;
                    }
                    case BOOLEAN: {
                        this.subcomponents.add(new DevCheckBox((Setting.b)s, this, opY));
                        opY += 16;
                        continue;
                    }
                    case DOUBLE: {
                        this.subcomponents.add(new DevDoubleSlider((Setting.d)s, this, opY));
                        opY += 16;
                        continue;
                    }
                    case INT: {
                        this.subcomponents.add(new DevIntSlider((Setting.i)s, this, opY));
                        opY += 16;
                        continue;
                    }
                }
            }
        }
        this.subcomponents.add(new DevKeybind(this, opY));
    }

    @Override
    public void setOff(final int newOff) {
        this.offset = newOff;
        int opY = this.offset + 16;
        for (final DevComponent comp : this.subcomponents) {
            comp.setOff(opY);
            opY += 16;
        }
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
        Gui.drawRect(this.parent.getX(), this.parent.getY() + this.offset + 1, this.parent.getX() + this.parent.getWidth(), this.parent.getY() + 16 + this.offset, this.isHovered ? (this.mod.isEnabled() ? new Color(DevGUI.color).getRGB() : new Color(195, 195, 195, devGuiModule.opacity.getValue() -50).darker().darker().getRGB()) : (this.mod.isEnabled() ? new Color(DevGUI.color).getRGB() : new Color(195, 195, 195, devGuiModule.opacity.getValue()-50).getRGB()));
        Gui.drawRect(this.parent.getX(), this.parent.getY() + this.offset, this.parent.getX() + this.parent.getWidth(), this.parent.getY() + this.offset + 1, new Color(195, 195, 195, devGuiModule.opacity.getValue()-50).getRGB());
        FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.mod.getName(), this.parent.getX() + 2, this.parent.getY() + this.offset + 2 + 2, -1);
        if (this.subcomponents.size() > 1) {
            FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.open ? "~" : ">", this.parent.getX() + this.parent.getWidth() - 10, this.parent.getY() + this.offset + 2 + 2, -1);
        }
        if (this.open && !this.subcomponents.isEmpty()) {
            for (final DevComponent comp : this.subcomponents) {
                comp.renderComponent();
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.open) {
            return 16 * (this.subcomponents.size() + 1);
        }
        return 16;
    }

    @Override
    public void updateComponent(final int mouseX, final int mouseY) {
        this.isHovered = this.isMouseOnButton(mouseX, mouseY);
        if (!this.subcomponents.isEmpty()) {
            for (final DevComponent comp : this.subcomponents) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int button) {
        if (this.isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.mod.toggle();
        }
        if (this.isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.open = !this.open;
            this.parent.refresh();
        }
        for (final DevComponent comp : this.subcomponents) {
            comp.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(final int mouseX, final int mouseY, final int mouseButton) {
        for (final DevComponent comp : this.subcomponents) {
            comp.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(final char typedChar, final int key) {
        for (final DevComponent comp : this.subcomponents) {
            comp.keyTyped(typedChar, key);
        }
    }

    public boolean isMouseOnButton(final int x, final int y) {
        return x > this.parent.getX() && x < this.parent.getX() + this.parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 16 + this.offset;
    }
}
