package com.gamesense.client.module.modules.gui;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.modules.misc.Announcer;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

@Module.Declaration(name = "ClickGUI", category = Category.GUI, bind = Keyboard.KEY_O, drawn = false)
public class ClickGuiModule extends Module {

    public static IntegerSetting scrollSpeed;
    public static IntegerSetting opacity;
    public static ColorSetting enabledColor;
    public static ColorSetting outlineColor;
    public static ColorSetting backgroundColor;
    public static ColorSetting settingBackgroundColor;
    public static ColorSetting fontColor;
    public static IntegerSetting animationSpeed;
    public static ModeSetting scrolling;
    public static BooleanSetting showHUD;
    public static ModeSetting theme;

    public void setup() {
        ArrayList<String> models = new ArrayList<>();
        models.add("Screen");
        models.add("Container");
        ArrayList<String> themes = new ArrayList<>();
        themes.add("2.2");
        themes.add("2.1.2");
        themes.add("2.0");

        opacity = registerInteger("Opacity", 150, 50, 255);
        scrollSpeed = registerInteger("Scroll Speed", 10, 1, 20);
        outlineColor = registerColor("Outline", new GSColor(255, 0, 0, 255));
        enabledColor = registerColor("Enabled", new GSColor(255, 0, 0, 255));
        backgroundColor = registerColor("Background", new GSColor(0, 0, 0, 255));
        settingBackgroundColor = registerColor("Setting", new GSColor(30, 30, 30, 255));
        fontColor = registerColor("Font", new GSColor(255, 255, 255, 255));
        animationSpeed = registerInteger("Animation Speed", 200, 0, 1000);
        scrolling = registerMode("Scrolling", models, "Screen");
        showHUD = registerBoolean("Show HUD Panels", false);
        theme = registerMode("Skin", themes, "2.2");
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