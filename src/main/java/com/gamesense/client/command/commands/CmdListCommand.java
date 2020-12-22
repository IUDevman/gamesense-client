package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.command.CommandManager;

/**
 * @Author Hoosiers on 11/05/2020
 */

public class CmdListCommand extends Command {

    public CmdListCommand() {
        super("Commands");

        setCommandSyntax(Command.getCommandPrefix() + "commands");
        setCommandAlias(new String[]{
                "commands", "cmd", "command", "commandlist", "help"
        });
    }

    public void onCommand(String command, String[] message) throws Exception {
        for (Command command1 : CommandManager.getCommands()) {
            MessageBus.sendCommandMessage(command1.getCommandName() + ": " + "\"" + command1.getCommandSyntax() + "\"!", true);
        }
    }
}