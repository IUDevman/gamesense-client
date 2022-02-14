package dev.gamesense.misc.setting.settings;

import dev.gamesense.misc.setting.Setting;

public final class EnumSetting implements Setting<Enum<?>> {

    private final String name;
    private final Enum<?> defaultValue;
    private Enum<?> value;

    public EnumSetting(String name, Enum<?> value) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Enum<?> getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public Enum<?> getValue() {
        return this.value;
    }

    @Override
    public void setValue(Enum<?> value) {
        this.value = value;
    }

    public void increment() {
        Enum<?>[] array = getValue().getDeclaringClass().getEnumConstants();
        int index = getValue().ordinal() + 1;

        if (index >= array.length) index = 0;

        setValue(array[index]);
    }
}
