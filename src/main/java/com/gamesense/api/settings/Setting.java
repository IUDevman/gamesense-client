package com.gamesense.api.settings;

import java.util.List;

import com.gamesense.client.module.Module;

public class Setting{

	private final String name;
	private final String configname;
	private final Module parent;
	private final Module.Category category;
	private final Type type;

	public Setting(final String name, final String configname, final Module parent, final Module.Category category, final Type type){
		this.name = name;
		this.configname = configname;
		this.parent = parent;
		this.type = type;
		this.category = category;
	}

	public String getName(){
		return this.name;
	}

	public String getConfigName(){
		return this.configname;
	}

	public Module getParent(){
		return this.parent;
	}

	public Type getType(){
		return this.type;
	}

	public Module.Category getCategory(){
		return this.category;
	}

	public enum Type{
		INT,
		DOUBLE,
		BOOLEAN,
		STRING,
		MODE
	}

	public static class Integer extends Setting{
		private int value;
		private final int min;
		private final int max;

		public Integer(final String name, final String configname, final Module parent, final Module.Category category, final int value, final int min, final int max){
			super(name, configname, parent, category, Type.INT);
			this.value = value;
			this.min = min;
			this.max = max;
		}

		public int getValue(){
			return this.value;
		}

		public void setValue(final int value){
			this.value = value;
		}

		public int getMin(){
			return this.min;
		}

		public int getMax(){
			return this.max;
		}
	}

	public static class Double extends Setting{
		private double value;
		private final double min;
		private final double max;

		public Double(final String name, final String configname, final Module parent, final Module.Category category, final double value, final double min, final double max){
			super(name, configname, parent, category, Type.DOUBLE);
			this.value = value;
			this.min = min;
			this.max = max;
		}

		public double getValue(){
			return this.value;
		}

		public void setValue(final double value){
			this.value = value;
		}

		public double getMin(){
			return this.min;
		}

		public double getMax(){
			return this.max;
		}
	}

	public static class Boolean extends Setting{
		private boolean value;

		public Boolean(final String name, final String configname, final Module parent, final Module.Category category, final boolean value){
			super(name, configname, parent, category, Type.BOOLEAN);
			this.value = value;
		}

		public boolean getValue(){
			return this.value;
		}

		public void setValue(final boolean value){
			this.value = value;
		}
	}

	public static class Mode extends Setting{
		private String value;
		private final java.util.List<String> modes;

		public Mode(final String name, final String configname, final Module parent, final Module.Category category, final java.util.List<String> modes, final String value){
			super(name, configname, parent, category, Type.MODE);
			this.value = value;
			this.modes = modes;
		}

		public String getValue(){
			return this.value;
		}

		public void setValue(final String value){
			this.value = value;
		}

		public List<String> getModes(){
			return this.modes;
		}
	}
}