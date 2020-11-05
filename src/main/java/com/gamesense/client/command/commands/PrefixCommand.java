package com.gamesense.client.command.commands;

import com.gamesense.client.command.Command;
import com.gamesense.client.commands2.MessageBus;

public class PrefixCommand extends Command{

	@Override
	public String[] getAlias(){
		return new String[]{"prefix", "setprefix", "cmdprefix"};
	}

	@Override
	public String getSyntax(){
		return "prefix <character>";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception{
		com.gamesense.client.commands2.Command.setCommandPrefix(args[0]);
		MessageBus.sendClientPrefixMessage("Command prefix set to " + com.gamesense.client.commands2.Command.getCommandPrefix());
	}
}