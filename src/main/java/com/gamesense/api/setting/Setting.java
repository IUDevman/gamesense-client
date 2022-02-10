package com.gamesense.api.setting;

import java.util.function.Supplier;

import com.gamesense.client.module.Module;

public abstract class Setting<T> {

    private T value;
    private final String name;
    private final String configName;
    private final Module module;
    private final Supplier<Boolean> isVisible;

    public Setting(T value, String name, String configName, Module module, Supplier<Boolean> isVisible) {
        this.value = value;
        this.name = name;
        this.configName = configName;
        this.module = module;
        this.isVisible = isVisible;
    }
    
    public Setting(T value, String name, Module module) {
        this(value,name,name.replace(" ",""),module,()->true);
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
    
    public boolean isVisible() {
    	return isVisible.get();
    }
}