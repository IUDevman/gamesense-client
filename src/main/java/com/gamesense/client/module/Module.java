package com.gamesense.client.module;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.lukflug.panelstudio.settings.KeybindSetting;
import com.lukflug.panelstudio.settings.Toggleable;

import net.minecraft.client.Minecraft;

public abstract class Module implements Toggleable,KeybindSetting {

	protected static final Minecraft mc = Minecraft.getMinecraft();

	String name;
	Category category;
	int bind;
	boolean enabled;
	boolean drawn;

	public Module(String name, Category category) {
		this.name = name;
		this.category = category;
		this.bind = Keyboard.KEY_NONE;
		this.enabled = false;
		this.drawn = true;
		setup();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getBind() {
		return this.bind;
	}

	public void setBind(int bind){
		this.bind = bind;
	}

	protected void onEnable() {

	}

	protected void onDisable() {

	}

	public void onUpdate() {

	}

	public void onRender() {

	}

	public void onWorldRender(RenderEvent event) {

	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void enable() {
		setEnabled(true);
		onEnable();
	}

	public void disable() {
		setEnabled(false);
		onDisable();
	}

	public void toggle() {
		if(isEnabled()) {
			disable();
		}
		else if(!isEnabled()) {
			enable();
		}
	}

	public String getHudInfo() {
		return "";
	}

	public void setup() {

	}

	public boolean isDrawn() {
		return this.drawn;
	}

	public void setDrawn(boolean drawn) {
		this.drawn = drawn;
	}

	/** Check Setting.java */

	protected Setting.Integer registerInteger(final String name, final int value, final int min, final int max) {
		final Setting.Integer setting = new Setting.Integer(name, this, getCategory(), value, min, max);
		GameSense.getInstance().settingsManager.addSetting(setting);
		return setting;
	}

	protected Setting.Double registerDouble(final String name, final double value, final double min, final double max) {
		final Setting.Double setting = new Setting.Double(name, this, getCategory(), value, min, max);
		GameSense.getInstance().settingsManager.addSetting(setting);
		return setting;
	}

	protected Setting.Boolean registerBoolean(final String name, final boolean value) {
		final Setting.Boolean setting = new Setting.Boolean(name, this, getCategory(), value);
		GameSense.getInstance().settingsManager.addSetting(setting);
		return setting;
	}

	protected Setting.Mode registerMode(final String name, final List<String> modes, final String value) {
		final Setting.Mode setting = new Setting.Mode(name, this, getCategory(), modes, value);
		GameSense.getInstance().settingsManager.addSetting(setting);
		return setting;
	}
	
	protected Setting.ColorSetting registerColor (final String name, GSColor color) {
		final Setting.ColorSetting setting = new Setting.ColorSetting(name, this, getCategory(), false, color);
		GameSense.getInstance().settingsManager.addSetting(setting);
		return setting;
	}
	
	protected Setting.ColorSetting registerColor (final String name) {
		return registerColor(name, new GSColor(90,145,240));
	}

	public enum Category {
		Combat,
		Exploits,
		Movement,
		Misc,
		Render,
		HUD,
		GUI
	}
	
	@Override
	public boolean isOn() {
		return this.enabled;
	}
	
	@Override
	public int getKey() {
		return this.bind;
	}
	
	@Override
	public void setKey(int key) {
		this.bind = key;
	}
    
	@Override
	public String getKeyName() {
		return Keyboard.getKeyName(this.bind);
	}
}