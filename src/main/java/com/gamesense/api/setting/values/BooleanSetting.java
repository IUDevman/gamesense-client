package com.gamesense.api.setting.values;

import java.util.function.Supplier;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, Module module, boolean value) {
        super(value, name, module);
    }
    
    public BooleanSetting(String name, String configName, Module module, Supplier<Boolean> isVisible, boolean value) {
        super(value, name, configName, module, isVisible);
    }
}