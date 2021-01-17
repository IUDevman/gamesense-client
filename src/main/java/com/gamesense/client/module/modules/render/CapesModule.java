package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;

import java.util.ArrayList;

public class CapesModule extends Module {

	public CapesModule() {
		super("Capes", Category.Render);
		setDrawn(false);
	}

	public Setting.Mode capeMode;

	public void setup() {
		ArrayList<String> CapeType = new ArrayList<>();
		CapeType.add("Black");
		CapeType.add("White");

		capeMode = registerMode("Type", CapeType, "Black");
	}
}