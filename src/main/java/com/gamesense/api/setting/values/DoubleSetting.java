package com.gamesense.api.setting.values;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.settings.NumberSetting;

public class DoubleSetting extends Setting<Double> implements NumberSetting {

    private final double min;
    private final double max;

    public DoubleSetting(String name, Module module, double value, double min, double max) {
        super(value, name, module);

        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    @Override
    public double getNumber() {
        return getValue();
    }

    @Override
    public void setNumber(double value) {
        setValue(value);
    }

    @Override
    public double getMaximumValue() {
        return getMax();
    }

    @Override
    public double getMinimumValue() {
        return getMin();
    }

    @Override
    public int getPrecision() {
        return 2;
    }
}