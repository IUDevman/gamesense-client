package com.gamesense.api.setting.values;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.settings.EnumSetting;

import java.util.List;

public class ModeSetting extends Setting<String> implements EnumSetting {

    private final List<String> modes;

    public ModeSetting(String name, Module module, String value, List<String> modes) {
        super(value, name, module);

        this.modes = modes;
    }

    public List<String> getModes() {
        return this.modes;
    }

    @Override
    public void increment() {
        int modeIndex = modes.indexOf(getValue());
        modeIndex = (modeIndex + 1) % modes.size();
        setValue(modes.get(modeIndex));
    }

    @Override
    public String getValueName() {
        return getValue();
    }
}