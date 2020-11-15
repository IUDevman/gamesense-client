package com.gamesense.client.module.modules.gui;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSenseMod;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends Module{
	public ClickGuiModule INSTANCE;
	public ClickGuiModule(){
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
	Setting.Boolean backgroundBlur;

	public void setup(){
		backgroundBlur = registerBoolean("Blur", "Blur", false);
		opacity = registerInteger("Opacity", "Opacity", 150,50,255);
		scrollSpeed = registerInteger("Scroll Speed", "Scroll Speed", 10, 1, 20);
		outlineColor = registerColor("Outline", "Outline", new GSColor(255, 0, 0, 255));
		enabledColor =registerColor("Enabled","Enabled", new GSColor(255, 0, 0, 255));
		backgroundColor = registerColor("Background", "Background", new GSColor(0, 0, 0, 255));
		settingBackgroundColor = registerColor("Setting", "Setting", new GSColor(30, 30, 30, 255));
		fontColor = registerColor("Font", "Font", new GSColor(255, 255, 255 ,255));
		animationSpeed = registerInteger("Animation Speed", "Animation Speed", 200, 0, 1000);
	}

	/** This uses minecraft's old "super secret" shaders, which means it could be modified to be a bunch of things in the future */
	private ResourceLocation shader = new ResourceLocation("minecraft", "shaders/post/blur" + ".json");

	public void onEnable(){
		mc.displayGuiScreen(GameSenseMod.getInstance().clickGUI);

		if (backgroundBlur.getValue()) {
			mc.entityRenderer.loadShader(shader);
		}

		if(((Announcer) ModuleManager.getModuleByName("Announcer")).clickGui.getValue() && ModuleManager.isModuleEnabled("Announcer") && mc.player != null) {
			if (((Announcer) ModuleManager.getModuleByName("Announcer")).clientSide.getValue()) {
				MessageBus.sendClientPrefixMessage(Announcer.guiMessage);
			}
			else {
				MessageBus.sendServerMessage(Announcer.guiMessage);
			}
		}
	}

	public void onUpdate(){
		if (backgroundBlur.getValue() && !mc.entityRenderer.isShaderActive()){
			mc.entityRenderer.loadShader(shader);
		}

		if (!backgroundBlur.getValue() && mc.entityRenderer.isShaderActive()){
			mc.entityRenderer.stopUseShader();
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			this.disable();
		}
	}

	public void onDisable(){
		if (mc.entityRenderer.isShaderActive()) {
			mc.entityRenderer.stopUseShader();
		}
	}
}