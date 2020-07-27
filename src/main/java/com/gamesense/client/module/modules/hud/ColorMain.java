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
        Rainbow = this.registerB("Rainbow", "CMRainbow", false);
        Red = this.registerI("Red", "CMRed", 255, 0, 255);
        Green = this.registerI("Green", "CMGreen", 26, 0, 255);
        Blue = this.registerI("Blue", "CMBlue", 42, 0, 255);
    }

    public void onEnable(){
        this.disable();
    }
}
