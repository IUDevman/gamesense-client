package com.gamesense.client.command.commands;

import com.gamesense.api.config.ConfigStopper;
import com.gamesense.client.command.Command;

public class SaveConfigCommand extends Command{

	@Override
	public String[] getAlias(){
		return new String[]{"saveconfig", "savecfg"};
	}

	@Override
	public String getSyntax(){
		return "saveconfig";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception{
		ConfigStopper.saveConfig();
		Command.sendClientMessage("Config saved!");
	}
}