package com.gamesense.client.module.modules.gui;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

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
	public static Setting.Mode theme;

	public void setup() {
		ArrayList<String> models=new ArrayList<>();
		models.add("Screen");
		models.add("Container");
		ArrayList<String> themes=new ArrayList<>();
		themes.add("2.2");
		themes.add("2.1.2");
		themes.add("2.0");

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
		theme = registerMode("Skin",themes,"2.2");
	}

	public void onEnable() {
		GameSense.getInstance().gameSenseGUI.enterGUI();
		Announcer announcer = ModuleManager.getModule(Announcer.class);

		if (announcer.clickGui.getValue() && announcer.isEnabled() && mc.player != null) {
			if (announcer.clientSide.getValue()) {
				MessageBus.sendClientPrefixMessage(Announcer.guiMessage);
			} else {
				MessageBus.sendServerMessage(Announcer.guiMessage);
			}
		}
		disable();
	}
}