package com.gamesense.client.module.modules.gui;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;
import org.lwjgl.input.Keyboard;

public class HUDEditor extends Module {
	public HUDEditor() {
		super("HUDEditor", Category.GUI);
		setBind(Keyboard.KEY_P);
		setDrawn(false);
	}

	public void onEnable() {
		GameSense.getInstance().gameSenseGUI.enterHUDEditor();
		Announcer announcer = ModuleManager.getModule(Announcer.class);

		if (announcer.clickGui.getValue() && announcer.isEnabled() && mc.player != null) {
			if (announcer.clientSide.getValue()) {
				MessageBus.sendClientPrefixMessage(Announcer.guiMessage);
			} else {
				MessageBus.sendServerMessage(Announcer.guiMessage);
			}
		}
		disable();
	}
}
