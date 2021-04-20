package com.gamesense.api.setting.values;

import java.util.function.Supplier;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;

public class DoubleSetting extends Setting<Double> {

    private final double min;
    private final double max;

    public DoubleSetting(String name, Module module, double value, double min, double max) {
        super(value, name, module);
        this.min = min;
        this.max = max;
    }

    public DoubleSetting(String name, String configName, Module module, Supplier<Boolean> isVisible, double value, double min, double max) {
    	super(value,name,configName,module,isVisible);
        this.min = min;
        this.max = max;
    }
    
    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }
}