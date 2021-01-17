package com.gamesense.api.setting;

import java.awt.Color;
import java.util.List;

import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.settings.EnumSetting;
import com.lukflug.panelstudio.settings.NumberSetting;
import com.lukflug.panelstudio.settings.Toggleable;

/**
 * @author Finz0 for Osiris
 * @author Memeszz for some static class/functions for settings
 * @src (much newer) https://github.com/Memeszz/Aurora-public
 * @author Hoosiers, heavy modifications
 * @author lukflug, PanelStudio integration
 */

public abstract class Setting {

	private final String name;
	private final String configName;
	private final Module parent;
	private final Module.Category category;
	private final Type type;

	public Setting(final String name, final Module parent, final Module.Category category, final Type type) {
		this.name = name;
		this.configName = name.replace(" ", "");
		this.parent = parent;
		this.type = type;
		this.category = category;
	}

	public String getName() {
		return this.name;
	}

	public String getConfigName() {
		return this.configName;
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
		INTEGER,
		DOUBLE,
		BOOLEAN,
		MODE,
		COLOR
    }

	public static class Integer extends Setting implements NumberSetting {

		private int value;
		private final int min;
		private final int max;

		public Integer(final String name, final Module parent, final Module.Category category, final int value, final int min, final int max) {
			super(name, parent, category, Type.INTEGER);
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

		@Override
		public double getNumber() {
			return this.value;
		}

		@Override
		public void setNumber(double value) {
			this.value= (int) Math.round(value);
		}

		@Override
		public double getMaximumValue() {
			return this.max;
		}

		@Override
		public double getMinimumValue() {
			return this.min;
		}

		@Override
		public int getPrecision() {
			return 0;
		}
	}

	public static class Double extends Setting implements NumberSetting {

		private double value;
		private final double min;
		private final double max;

		public Double(final String name, final Module parent, final Module.Category category, final double value, final double min, final double max) {
			super(name, parent, category, Type.DOUBLE);
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

		@Override
		public double getNumber() {
			return this.value;
		}

		@Override
		public void setNumber(double value) {
			this.value = value;
		}

		@Override
		public double getMaximumValue() {
			return this.max;
		}

		@Override
		public double getMinimumValue() {
			return this.min;
		}

		@Override
		public int getPrecision() {
			return 2;
		}
	}

	public static class Boolean extends Setting implements Toggleable {

		private boolean value;

		public Boolean(final String name, final Module parent, final Module.Category category, final boolean value) {
			super(name, parent, category, Type.BOOLEAN);
			this.value = value;
		}

		public boolean getValue(){
			return this.value;
		}

		public void setValue(final boolean value) {
			this.value = value;
		}

		@Override
		public void toggle() {
			this.value =! this.value;
		}

		@Override
		public boolean isOn() {
			return this.value;
		}
	}

	public static class Mode extends Setting implements EnumSetting {

		private String value;
		private final java.util.List<String> modes;

		public Mode(final String name, final Module parent, final Module.Category category, final java.util.List<String> modes, final String value) {
			super(name, parent, category, Type.MODE);
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

		@Override
		public void increment() {
			int modeIndex = modes.indexOf(value);
			modeIndex = (modeIndex + 1) % modes.size();
			setValue(modes.get(modeIndex));
		}

		@Override
		public String getValueName() {
			return this.value;
		}
	}

	public static class ColorSetting extends Setting implements com.lukflug.panelstudio.settings.ColorSetting {

		private boolean rainbow;
		private GSColor value;
		
		public ColorSetting (final String name, final Module parent, final Module.Category category, boolean rainbow, final GSColor value) {
			super(name, parent, category, Type.COLOR);
			this.rainbow=rainbow;
			this.value=value;
		}
		
		public GSColor getValue() {
			if (rainbow) {
				return GSColor.fromHSB((System.currentTimeMillis()%(360*32))/(360f * 32),1,1);
			}
			return this.value;
		}
		
		public void setValue (boolean rainbow, final GSColor value) {
			this.rainbow = rainbow;
			this.value = value;
		}
		
		public int toInteger() {
			return this.value.getRGB()&0xFFFFFF+(rainbow?1:0)*0x1000000;
		}
		
		public void fromInteger (int number) {
			this.value = new GSColor(number&0xFFFFFF);
			this.rainbow = ((number&0x1000000)!=0);
		}
		
		public GSColor getColor() {
			return this.value;
		}

		@Override
		public boolean getRainbow() {
			return this.rainbow;
		}

		@Override
		public void setValue(Color value) {
			setValue(getRainbow(),new GSColor(value));
		}

		@Override
		public void setRainbow(boolean rainbow) {
			this.rainbow=rainbow;
		}
	}
}