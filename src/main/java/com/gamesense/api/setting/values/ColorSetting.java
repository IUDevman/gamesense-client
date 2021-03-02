package com.gamesense.api.setting.values;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;

import java.awt.*;

public class ColorSetting extends Setting<GSColor> implements com.lukflug.panelstudio.settings.ColorSetting {

    private boolean rainbow;

    public ColorSetting(String name, Module module, boolean rainbow, GSColor value) {
        super(value, name, module);

        this.rainbow = rainbow;
    }

    @Override
    public GSColor getValue() {
        if (rainbow) return GSColor.fromHSB((System.currentTimeMillis() % (360 * 32)) / (360f * 32), 1, 1);
        else return super.getValue();
    }

    public int toInteger() {
        return getValue().getRGB() & 0xFFFFFF + (this.rainbow ? 1 : 0) * 0x1000000;
    }

    public void fromInteger(int number) {
        this.rainbow = ((number & 0x1000000) != 0);

        super.setValue(this.rainbow ? GSColor.fromHSB((System.currentTimeMillis() % (360 * 32)) / (360f * 32), 1, 1) : new GSColor(number & 0xFFFFFF));
    }

    @Override
    public void setValue(Color value) {
        super.setValue(this.rainbow ? GSColor.fromHSB((System.currentTimeMillis() % (360 * 32)) / (360f * 32), 1, 1) : new GSColor(value));
    }

    @Override
    public Color getColor() {
        return super.getValue();
    }

    @Override
    public boolean getRainbow() {
        return this.rainbow;
    }

    @Override
    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }
}