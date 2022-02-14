package dev.gamesense.misc.setting.settings;

import dev.gamesense.misc.setting.Setting;

/**
 * @author IUDevman
 * @since 02-13-2022
 */

public final class BooleanSetting implements Setting<Boolean> {

    private final String name;
    private final boolean defaultValue;
    private boolean value;

    public BooleanSetting(String name, boolean value) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Boolean getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public Boolean getValue() {
        return this.value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }
}
