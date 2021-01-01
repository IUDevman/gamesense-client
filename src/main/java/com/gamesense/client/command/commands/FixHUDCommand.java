package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.HUDModule;

public class FixHUDCommand extends Command {
	public FixHUDCommand() {
		super("FixHUD");
		setCommandSyntax(Command.getCommandPrefix() + "fixhud");
        setCommandAlias(new String[]{
                "fixhud", "hud", "resethud"
        });
	}

	@Override
	public void onCommand(String command, String[] message) throws Exception {
		for (Module module: ModuleManager.getModules()) {
			if (module instanceof HUDModule) {
				((HUDModule)module).resetPosition();
			}
		}
        MessageBus.sendCommandMessage("HUD positions reset!", true);
	}

}
