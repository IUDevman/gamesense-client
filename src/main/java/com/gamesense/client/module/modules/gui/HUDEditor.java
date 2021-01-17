package com.gamesense.client.module.modules.gui;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;
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

		if(((Announcer) ModuleManager.getModuleByName("Announcer")).clickGui.getValue() && ModuleManager.isModuleEnabled("Announcer") && mc.player != null) {
			if (((Announcer) ModuleManager.getModuleByName("Announcer")).clientSide.getValue()) {
				MessageBus.sendClientPrefixMessage(Announcer.guiMessage);
			}
			else {
				MessageBus.sendServerMessage(Announcer.guiMessage);
			}
		}
		disable();
	}
}
