package com.gamesense.client.module;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.modules.Category;
import com.lukflug.panelstudio.settings.KeybindSetting;
import com.lukflug.panelstudio.settings.Toggleable;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

public abstract class Module implements Toggleable, KeybindSetting {

	protected static final Minecraft mc = Minecraft.getMinecraft();

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Declaration {
		String name();
		Category category();
		int priority() default 0;
		int bind() default Keyboard.KEY_NONE;
		boolean enabled() default false;
		boolean drawn() default true;
	}

	private final String name = getDeclaration().name();
	private final Category category = getDeclaration().category();
	private final int priority = getDeclaration().priority();
	private int bind = getDeclaration().bind();
	private boolean enabled = getDeclaration().enabled();
	private boolean drawn = getDeclaration().drawn();

	public Module() {
		setup();
	}

	private Declaration getDeclaration() {
		return getClass().getAnnotation(Declaration.class);
	}

	public void setup() {

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
		GameSense.EVENT_BUS.subscribe(this);
		onEnable();
	}

	public void disable() {
		setEnabled(false);
		GameSense.EVENT_BUS.unsubscribe(this);
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

	public String getName() {
		return this.name;
	}

	public Category getCategory() {
		return this.category;
	}

	public int getPriority() {
		return priority;
	}

	public int getBind() {
		return this.bind;
	}

	public void setBind(int bind){
		if (bind >= 0 && bind <= 255) {
			this.bind = bind;
		}
	}

	public String getHudInfo() {
		return "";
	}

	public boolean isDrawn() {
		return this.drawn;
	}

	public void setDrawn(boolean drawn) {
		this.drawn = drawn;
	}

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

	@Override
	public boolean isOn() {
		return this.enabled;
	}
	
	@Override
	public int getKey() {
		return this.getBind();
	}
	
	@Override
	public void setKey(int key) {
		setBind(key);
	}
    
	@Override
	public String getKeyName() {
		if (this.bind <= 0 || this.bind > 255) {
			return "NONE";
		} else {
			return Keyboard.getKeyName(this.bind);
		}
	}
}