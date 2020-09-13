package com.gamesense.client.module.modules.render;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;

import java.util.ArrayList;

public class CapesModule extends Module {
    public Setting.Mode capeMode;

    public CapesModule() {
        super("Capes", Category.Render);
        setDrawn(false);
    }

    public void setup() {
        ArrayList<String> CapeType = new ArrayList<>();
        CapeType.add("Black");
        CapeType.add("White");

        capeMode = registerMode("Type", "Type", CapeType, "Black");
    }
}
