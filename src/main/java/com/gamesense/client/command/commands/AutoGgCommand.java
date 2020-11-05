package com.gamesense.client.command.commands;

import com.gamesense.client.command.Command;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.modules.misc.AutoGG;

public class AutoGgCommand extends Command{

	@Override
	public String[] getAlias(){
		return new String[]{"autogg", "autoez"};
	}

	@Override
	public String getSyntax(){
		return "autogg <add | del> <message> (use \"{name}\" for the player's name, use \"_\" for spaces)";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception{
		String s = args[1].replace("_", " ");
		if (args[0].equalsIgnoreCase("add")){
			if (!AutoGG.getAutoGgMessages().contains(s)){
				AutoGG.addAutoGgMessage(s);
				MessageBus.sendClientPrefixMessage("Added AutoGG message: " + s);
			} else{
				MessageBus.sendClientPrefixMessage("AutoGG list doesn't contain " + s);
			}
		} else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove")){
			AutoGG.getAutoGgMessages().remove(s);
			MessageBus.sendClientPrefixMessage("Removed AutoGG message: " + s);
		}
		else{
			MessageBus.sendClientPrefixMessage(getSyntax());
		}
	}
}