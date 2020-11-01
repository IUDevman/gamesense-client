package com.gamesense.client.module;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSenseMod;
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

	public Module(String n, Category c) {
		name = n;
		category = c;
		bind = Keyboard.KEY_NONE;
		enabled = false;
		drawn = true;
		setup();
	}

	public String getName(){
		return name;
	}

	public void setName(String n){
		name = n;
	}

	public Category getCategory(){
		return category;
	}

	public void setCategory(Category c){
		category = c;
	}

	public int getBind(){
		return bind;
	}

	public void setBind(int b){
		bind = b;
	}

	protected void onEnable(){ }

	protected void onDisable(){ }

	public void onUpdate(){}

	public void onRender(){}

	public void onWorldRender(RenderEvent event) {}

	public boolean isEnabled(){
		return enabled;
	}

	public void setEnabled(boolean e){
		enabled = e;
	}

	public void enable(){
		setEnabled(true);
		onEnable();
	}

	public void disable(){
		setEnabled(false);
		onDisable();
	}

	public void toggle(){
		if(isEnabled()) {
			disable();
		}
		else if(!isEnabled()){
			enable();
		}
	}

	public String getHudInfo(){
		return "";
	}

	public void setup(){}

	public boolean isDrawn(){
		return drawn;
	}

	public void setDrawn(boolean d){
		drawn = d;
	}

	/** Check Setting.java */

	protected Setting.Integer registerInteger(final String name, final String configName, final int value, final int min, final int max) {
		final Setting.Integer setting = new Setting.Integer(name, configName, this, getCategory(), value, min, max);
		GameSenseMod.getInstance().settingsManager.addSetting(setting);
		return setting;
	}

	protected Setting.Double registerDouble(final String name, final String configName, final double value, final double min, final double max) {
		final Setting.Double setting = new Setting.Double(name, configName, this, getCategory(), value, min, max);
		GameSenseMod.getInstance().settingsManager.addSetting(setting);
		return setting;
	}

	protected Setting.Boolean registerBoolean(final String name, final String configName, final boolean value) {
		final Setting.Boolean setting = new Setting.Boolean(name, configName, this, getCategory(), value);
		GameSenseMod.getInstance().settingsManager.addSetting(setting);
		return setting;
	}

	protected Setting.Mode registerMode(final String name, final String configName, final List<String> modes, final String value) {
		final Setting.Mode setting = new Setting.Mode(name, configName, this, getCategory(), modes, value);
		GameSenseMod.getInstance().settingsManager.addSetting(setting);
		return setting;
	}
	
	protected Setting.ColorSetting registerColor (final String name, final String configName, GSColor color) {
		final Setting.ColorSetting setting = new Setting.ColorSetting(name, configName, this, getCategory(), false, color);
		GameSenseMod.getInstance().settingsManager.addSetting(setting);
		return setting;
	}
	
	protected Setting.ColorSetting registerColor (final String name, final String configName) {
		return registerColor(name,configName,new GSColor(90,145,240));
	}

	public enum Category{
		Combat,
		Exploits,
		Movement,
		Misc,
		Render,
		HUD,
		GUI
	}
	
	@Override
	public boolean isRunning() {
		return enabled;
	}
	
	@Override
	public int getKey() {
		return bind;
	}
	
	@Override
	public void setKey(int key) {
		bind=key;
	}
    
	@Override
	public String getKeyName() {
		return Keyboard.getKeyName(bind);
	}
}