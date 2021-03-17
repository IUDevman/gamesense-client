package com.gamesense.api.setting.values;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.settings.NumberSetting;

public class IntegerSetting extends Setting<Integer> implements NumberSetting {

    private final int min;
    private final int max;

    public IntegerSetting(String name, Module module, int value, int min, int max) {
        super(value, name, module);

        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    @Override
    public double getNumber() {
        return getValue();
    }

    @Override
    public void setNumber(double value) {
        setValue((int) Math.round(value));
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
        return 0;
    }
}
