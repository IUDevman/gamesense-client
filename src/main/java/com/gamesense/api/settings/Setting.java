package com.gamesense.api.settings;

import java.util.List;

import com.gamesense.client.module.Module;

public class Setting {

	private final String name;
	private final String configname;
	private final Module parent;
	private final Type type;
	private boolean bval;
	private double dval;
	private boolean onlyint = false;

	public Setting(final String name, final String configname, final Module parent, final Type type) {
		this.name = name;
		this.configname = configname;
		this.parent = parent;
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public String getConfigName(){
		return this.configname;
	}

	public Module getParent() {
		return this.parent;
	}

	public Type getType() {
		return this.type;
	}

	public enum Type
	{
		INT,
		DOUBLE,
		BOOLEAN,
		STRING,
		MODE;
	}

	public double getValDouble(){
		if(this.onlyint){
			this.dval = (int)dval;
		}
		return this.dval;
	}

	public boolean getValBoolean(){
		return this.bval;
	}

	public static class i extends Setting
	{
		private int value;
		private final int min;
		private final int max;

		public i(final String name, final String configname, final Module parent, final int value, final int min, final int max) {
			super(name, configname, parent, Type.INT);
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

	public static class d extends Setting
	{
		private double value;
		private final double min;
		private final double max;

		public d(final String name, final String configname, final Module parent, final double value, final double min, final double max) {
			super(name, configname, parent, Type.DOUBLE);
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

	public static class b extends Setting
	{
		private boolean value;

		public b(final String name, final String configname, final Module parent, final boolean value) {
			super(name, configname, parent, Type.BOOLEAN);
			this.value = value;
		}

		public boolean getValue() {
			return this.value;
		}

		public void setValue(final boolean value) {
			this.value = value;
		}
	}

	public static class s extends Setting
	{
		private String value;

		public s(final String name, final String configname, final Module parent, final String value) {
			super(name, configname, parent, Type.STRING);
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	public static class mode extends Setting
	{
		private String value;
		private final java.util.List<String> modes;

		public mode(final String name, final String configname, final Module parent, final java.util.List<String> modes, final String value) {
			super(name, configname, parent, Type.MODE);
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
}

