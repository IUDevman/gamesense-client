package com.gamesense.client.module.modules.gui;

import org.lwjgl.input.Keyboard;

import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;

public class HUDEditor extends Module {
	public HUDEditor() {
		super("HUDEditor", Category.GUI);
		setBind(Keyboard.KEY_P);
		setDrawn(false);
	}
	
	public void onEnable() {
		GameSense.getInstance().gameSenseGUI.enterHUDEditor();
		disable();
	}
}
