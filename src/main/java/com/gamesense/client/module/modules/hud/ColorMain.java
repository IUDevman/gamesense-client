package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;

public class ColorMain extends Module {
    public ColorMain() {
        super("Colors", Category.HUD);
        setDrawn(false);
    }

    public static Setting.b Rainbow;
    public static Setting.i Red;
    public static Setting.i Blue;
    public static Setting.i Green;

    public void setup() {
        Rainbow = this.registerB("Rainbow", false);
        Red = this.registerI("Red", 255, 0, 255);
        Green = this.registerI("Green", 26, 0, 255);
        Blue = this.registerI("Blue", 42, 0, 255);
    }

    public void onDisable(){
        this.enable();
    }
}
