package com.gamesense.api.setting.values;

import java.util.List;
import java.util.function.Supplier;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;

    public ModeSetting(String name, Module module, String value, List<String> modes) {
        super(value, name, module);
        this.modes = modes;
    }
    
    public ModeSetting(String name, String configName, Module module, String value, Supplier<Boolean> isVisible, List<String> modes) {
        super(value, name, configName, module, isVisible);
        this.modes = modes;
    }

    public List<String> getModes() {
        return this.modes;
    }

    public void increment() {
        int modeIndex = modes.indexOf(getValue());
        modeIndex = (modeIndex + 1) % modes.size();
        setValue(modes.get(modeIndex));
    }
    
    public void decrement() {
        int modeIndex = modes.indexOf(getValue());
        modeIndex-=1;
        if (modeIndex<0) modeIndex=modes.size()-1;
        setValue(modes.get(modeIndex));
    }
}