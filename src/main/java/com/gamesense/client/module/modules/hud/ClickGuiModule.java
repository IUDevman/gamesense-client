package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends Module{
	public ClickGuiModule INSTANCE;
	public ClickGuiModule(){
		super("ClickGUI", Category.HUD);
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

	public void setup(){
		opacity = registerInteger("Opacity", "Opacity", 150,50,255);
		scrollSpeed = registerInteger("Scroll Speed", "Scroll Speed", 10, 1, 20);
		outlineColor = registerColor("Outline", "Outline", new GSColor(255, 0, 0, 255));
		enabledColor =registerColor("Enabled","Enabled", new GSColor(255, 0, 0, 255));
		backgroundColor = registerColor("Background", "Background", new GSColor(0, 0, 0, 255));
		settingBackgroundColor = registerColor("Setting", "Setting", new GSColor(30, 30, 30, 255));
		fontColor = registerColor("Font", "Font", new GSColor(255, 255, 255 ,255));
	}

	public void onEnable(){
		mc.displayGuiScreen(GameSenseMod.getInstance().clickGUI);
		if(((Announcer) ModuleManager.getModuleByName("Announcer")).clickGui.getValue() && ModuleManager.isModuleEnabled("Announcer") && mc.player != null)
			if(((Announcer)ModuleManager.getModuleByName("Announcer")).clientSide.getValue()){
				Command.sendClientMessage(Announcer.guiMessage);
			} else {
				mc.player.sendChatMessage(Announcer.guiMessage);
			}
		this.disable();
	}
}
