package com.gamesense.client.command.commands;

import com.gamesense.client.commands2.MessageBus;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.ModuleManager;

public class DrawnCommand extends Command{
	boolean found;

	@Override
	public String[] getAlias(){
		return new String[]{"drawn", "visible", "d", "seen"};
	}

	@Override
	public String getSyntax(){
		return "drawn <module>";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception{
		found = false;
		ModuleManager.getModules().forEach(m -> {
			if (m.getName().equalsIgnoreCase(args[0])){
				if (m.isDrawn()){
					m.setDrawn(false);
					found = true;
					MessageBus.sendClientPrefixMessage(m.getName() + ChatFormatting.RED + " drawn = false");
				} else if (!m.isDrawn()){
					m.setDrawn(true);
					found = true;
					MessageBus.sendClientPrefixMessage(m.getName() + ChatFormatting.GREEN + " drawn = true");
				}
			}
		});
		if (!found && args.length == 1) MessageBus.sendClientPrefixMessage(ChatFormatting.GRAY + "Module not found!");
	}
}