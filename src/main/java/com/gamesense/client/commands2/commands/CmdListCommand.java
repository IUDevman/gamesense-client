package com.gamesense.client.commands2.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.commands2.Command;
import com.gamesense.client.commands2.CommandManager;

/**
 * @Author Hoosiers on 11/05/2020
 */

public class CmdListCommand extends Command {

    public CmdListCommand(){
        super("Commands");

        setCommandSyntax(Command.getCommandPrefix() + "commands");
        setCommandAlias(new String[]{
                "commands", "cmd", "command", "commandlist"
        });
    }

    public void onCommand(String command, String[] message) throws Exception{
        for (Command command1 : CommandManager.getCommands()){
            MessageBus.sendClientPrefixMessage(command1.getCommandName() + ": " + "\"" + command1.getCommandSyntax() + "\"!");
        }
    }
}