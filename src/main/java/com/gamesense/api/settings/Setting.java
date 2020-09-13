package com.gamesense.api.settings;

import com.gamesense.api.util.GSColor;
import com.gamesense.client.module.Module;

import java.util.List;

public abstract class Setting {

    private final String name;
    private final String configname;
    private final Module parent;
    private final Module.Category category;
    private final Type type;

    public Setting(final String name, final String configname, final Module parent, final Module.Category category, final Type type) {
        this.name = name;
        this.configname = configname;
        this.parent = parent;
        this.type = type;
        this.category = category;
    }

    public String getName() {
        return this.name;
    }

    public String getConfigName() {
        return this.configname;
    }

    public Module getParent() {
        return this.parent;
    }

    public Type getType() {
        return this.type;
    }

    public Module.Category getCategory() {
        return this.category;
    }

    public enum Type {
        INT,
        DOUBLE,
        BOOLEAN,
        MODE,
        COLOR
    }

    public static class Integer extends Setting {
        private final int min;
        private final int max;
        private int value;

        public Integer(final String name, final String configname, final Module parent, final Module.Category category, final int value, final int min, final int max) {
            super(name, configname, parent, category, Type.INT);
            this.value = value;
            this.min = min;
            this.max = max;
        }

        public int getValue() {
            return this.value;
        }

        public void setValue(final int value) {
            this.value = value;
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }
    }

    public static class Double extends Setting {
        private final double min;
        private final double max;
        private double value;

        public Double(final String name, final String configname, final Module parent, final Module.Category category, final double value, final double min, final double max) {
            super(name, configname, parent, category, Type.DOUBLE);
            this.value = value;
            this.min = min;
            this.max = max;
        }

        public double getValue() {
            return this.value;
        }

        public void setValue(final double value) {
            this.value = value;
        }

        public double getMin() {
            return this.min;
        }

        public double getMax() {
            return this.max;
        }
    }

    public static class Boolean extends Setting {
        private boolean value;

        public Boolean(final String name, final String configname, final Module parent, final Module.Category category, final boolean value) {
            super(name, configname, parent, category, Type.BOOLEAN);
            this.value = value;
        }

        public boolean getValue() {
            return this.value;
        }

        public void setValue(final boolean value) {
            this.value = value;
        }
    }

    public static class Mode extends Setting {
        private final java.util.List<String> modes;
        private String value;

        public Mode(final String name, final String configname, final Module parent, final Module.Category category, final java.util.List<String> modes, final String value) {
            super(name, configname, parent, category, Type.MODE);
            this.value = value;
            this.modes = modes;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(final String value) {
            this.value = value;
        }

        public List<String> getModes() {
            return this.modes;
        }
    }

    // Color config added by lukflug
    public static class ColorSetting extends Setting {
        private boolean rainbow;
        private GSColor value;

        public ColorSetting(final String name, final String configname, final Module parent, final Module.Category category, boolean rainbow, final GSColor value) {
            super(name, configname, parent, category, Type.COLOR);
            this.rainbow = rainbow;
            this.value = value;
        }

        public GSColor getValue() {
            if (rainbow) {
                return GSColor.fromHSB((System.currentTimeMillis() % (360 * 32)) / (360f * 32), 1, 1);
            }
            return value;
        }

        public void setValue(boolean rainbow, final GSColor value) {
            this.rainbow = rainbow;
            this.value = value;
        }

        public int toInteger() {
            return value.getRGB() & 0xFFFFFF + (rainbow ? 1 : 0) * 0x1000000;
        }

        public void fromInteger(int number) {
            value = new GSColor(number & 0xFFFFFF);
            rainbow = ((number & 0x1000000) != 0);
        }

        public GSColor getColor() {
            return value;
        }

        public boolean getRainbow() {
            return rainbow;
        }
    }
}