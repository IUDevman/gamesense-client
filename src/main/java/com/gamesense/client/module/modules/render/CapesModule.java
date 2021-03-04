package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.Category;

import java.util.ArrayList;

@Module.Declaration(name = "Capes", category = Category.Render, drawn = false)
public class CapesModule extends Module {

    public ModeSetting capeMode;

    public void setup() {
        ArrayList<String> CapeType = new ArrayList<>();
        CapeType.add("Black");
        CapeType.add("White");

        capeMode = registerMode("Type", CapeType, "Black");
    }
}