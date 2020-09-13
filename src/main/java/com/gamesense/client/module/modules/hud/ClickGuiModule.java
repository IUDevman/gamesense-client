package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ClickGuiModule extends Module {
    public static Setting.Integer scrollSpeed;
    public static Setting.Integer opacity;
    public static Setting.Mode icon;
    public static Setting.Mode backgroundColor;
    public static Setting.ColorSetting guiColor;
    public ClickGuiModule INSTANCE;
    public Setting.Boolean customFont;

    public ClickGuiModule() {
        super("ClickGUI", Category.HUD);
        setBind(Keyboard.KEY_O);
        setDrawn(false);
        INSTANCE = this;
    }

    public void setup() {
        ArrayList<String> icons = new ArrayList<>();
        icons.add("Font");
        icons.add("Image");
        ArrayList<String> background = new ArrayList<>();
        background.add("Black");
        background.add("Silver");
        background.add("Gray");
        opacity = registerInteger("Opacity", "Opacity", 200, 50, 255);
        scrollSpeed = registerInteger("Scroll Speed", "Scroll Speed", 10, 1, 20);
        icon = registerMode("Icon", "Icons", icons, "Image");
        backgroundColor = registerMode("Background", "Background", background, "Gray");
        guiColor = registerColor("Color", "Color");
    }

    public void onEnable() {
        mc.displayGuiScreen(GameSenseMod.getInstance().clickGUI);
        if (((Announcer) ModuleManager.getModuleByName("Announcer")).clickGui.getValue() && ModuleManager.isModuleEnabled("Announcer") && mc.player != null)
            if (((Announcer) ModuleManager.getModuleByName("Announcer")).clientSide.getValue()) {
                Command.sendClientMessage(Announcer.guiMessage);
            } else {
                mc.player.sendChatMessage(Announcer.guiMessage);
            }
        this.disable();
    }
}
