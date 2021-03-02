package com.gamesense.api.setting;

import com.gamesense.client.module.Module;

public abstract class Setting<T> {

    private T value;
    private final String name;
    private final String configName;
    private final Module module;

    public Setting(T value, String name, Module module) {
        this.value = value;
        this.name = name;
        this.configName = name.replace(" ", "");
        this.module = module;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getConfigName() {
        return this.configName;
    }

    public Module getModule() {
        return this.module;
    }
}
