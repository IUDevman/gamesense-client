package dev.gamesense.misc.setting.settings;

import dev.gamesense.misc.GSColor;
import dev.gamesense.misc.setting.Setting;

/**
 * @author IUDevman
 * @since 02-13-2022
 */

public final class ColorSetting implements Setting<GSColor> {

    private final String name;
    private final GSColor defaultValue;
    private GSColor value;
    private boolean rainbow = false;

    public ColorSetting(String name, GSColor value) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public GSColor getDefaultValue() {
        return null; //todo
    }

    @Override
    public GSColor getValue() {
        return null; //todo
    }

    @Override
    public void setValue(GSColor value) {
        //todo
    }

    @Override
    public void reset() {
        setRainbow(false);
    }

    public boolean isRainbow() {
        return this.rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }
}
