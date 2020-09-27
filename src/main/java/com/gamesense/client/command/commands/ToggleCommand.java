package com.gamesense.client.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.ModuleManager;

public class ToggleCommand extends Command{
	boolean found;

	@Override
	public String[] getAlias(){
		return new String[]{"toggle", "t"};
	}

	@Override
	public String getSyntax(){
		return "toggle <module>";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception{
		found = false;
		ModuleManager.getModules().forEach(m -> {
			if (m.getName().equalsIgnoreCase(args[0])){
				if (m.isEnabled()){
					m.disable();
					found = true;
						Command.sendClientMessage(m.getName() + ChatFormatting.RED + " disabled!");
				} else if (!m.isEnabled()){
					m.enable();
					found = true;
						Command.sendClientMessage(m.getName() + ChatFormatting.GREEN + " enabled!");
				}
			}
		});
		if (!found && args.length == 1) Command.sendClientMessage(ChatFormatting.GRAY + "Module not found!");
	}
}