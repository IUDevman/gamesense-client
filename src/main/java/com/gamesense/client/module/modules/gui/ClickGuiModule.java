package com.gamesense.client.module.modules.gui;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends Module {

	public ClickGuiModule INSTANCE;

	public ClickGuiModule() {
		super("ClickGUI", Category.GUI);
		setBind(Keyboard.KEY_O);
		setDrawn(false);
		INSTANCE = this;
	}

	public static Setting.Integer scrollSpeed;
	public static Setting.Integer opacity;
	public static Setting.ColorSetting enabledColor;
	public static Setting.ColorSetting outlineColor;
	public static Setting.ColorSetting backgroundColor;
	public static Setting.ColorSetting settingBackgroundColor;
	public static Setting.ColorSetting fontColor;
	public static Setting.Integer animationSpeed;
	public static Setting.Mode scrolling;
	public static Setting.Boolean showHUD;

	public void setup() {
		ArrayList<String> models=new ArrayList<>();
		models.add("Screen");
		models.add("Container");

		opacity = registerInteger("Opacity", 150,50,255);
		scrollSpeed = registerInteger("Scroll Speed", 10, 1, 20);
		outlineColor = registerColor("Outline", new GSColor(255, 0, 0, 255));
		enabledColor =registerColor("Enabled", new GSColor(255, 0, 0, 255));
		backgroundColor = registerColor("Background", new GSColor(0, 0, 0, 255));
		settingBackgroundColor = registerColor("Setting", new GSColor(30, 30, 30, 255));
		fontColor = registerColor("Font", new GSColor(255, 255, 255 ,255));
		animationSpeed = registerInteger("Animation Speed", 200, 0, 1000);
		scrolling = registerMode("Scrolling",models,"Screen");
		showHUD = registerBoolean("Show HUD Panels",false);
	}

	public void onEnable() {
		GameSense.getInstance().gameSenseGUI.enterGUI();

		if(((Announcer) ModuleManager.getModuleByName("Announcer")).clickGui.getValue() && ModuleManager.isModuleEnabled("Announcer") && mc.player != null) {
			if (((Announcer) ModuleManager.getModuleByName("Announcer")).clientSide.getValue()) {
				MessageBus.sendClientPrefixMessage(Announcer.guiMessage);
			}
			else {
				MessageBus.sendServerMessage(Announcer.guiMessage);
			}
		}
		disable();
	}
}