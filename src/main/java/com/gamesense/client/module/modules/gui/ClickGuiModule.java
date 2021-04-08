package com.gamesense.client.module.modules.gui;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

@Module.Declaration(name = "ClickGUI", category = Category.GUI, bind = Keyboard.KEY_O, drawn = false)
public class ClickGuiModule extends Module {

    public IntegerSetting opacity = registerInteger("Opacity", 150, 50, 255);
    public IntegerSetting scrollSpeed = registerInteger("Scroll Speed", 10, 1, 20);
    public ColorSetting outlineColor = registerColor("Outline", new GSColor(255, 0, 0, 255));
    public ColorSetting enabledColor = registerColor("Enabled", new GSColor(255, 0, 0, 255));
    public ColorSetting backgroundColor = registerColor("Background", new GSColor(0, 0, 0, 255));
    public ColorSetting  settingBackgroundColor = registerColor("Setting", new GSColor(30, 30, 30, 255));
    public ColorSetting fontColor = registerColor("Font", new GSColor(255, 255, 255, 255));
    public IntegerSetting animationSpeed = registerInteger("Animation Speed", 200, 0, 1000);
    public ModeSetting scrolling = registerMode("Scrolling", Arrays.asList("Screen", "Container"), "Screen");
    public BooleanSetting showHUD = registerBoolean("Show HUD Panels", false);
    public ModeSetting theme = registerMode("Skin", Arrays.asList("2.2", "2.1.2", "2.0"), "2.2");

    public void onEnable() {
        GameSense.INSTANCE.gameSenseGUI.enterGUI();
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