package com.gamesense.client.module.modules.render;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;

import java.util.ArrayList;

public class CapesModule extends Module {
    public CapesModule() {
        super("Capes", Category.Render);
        setEnabled(true);
        setDrawn(false);
    }

    public Setting.mode CapeMode;

    public void setup() {
        ArrayList<String> CapeType = new ArrayList<>();
        CapeType.add("Black");
        CapeType.add("White");

        CapeMode = this.registerMode("Type", "Type", CapeType, "Black");
    }
}
