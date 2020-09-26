package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.Module;
import com.gamesense.api.settings.Setting;

public class CustomFOV extends Module {
    public CustomFOV() {
        super("Custom FOV", Category.Render);
    }

    Setting.Integer FOV;

    public void setup(){
    FOV = registerInteger("FOV", "FOV", 90, 0, 180);
    }
    public void onUpdate() {
        mc.gameSettings.fovSetting = (float)FOV.getValue();
    }
}


