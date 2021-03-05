package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.command.CommandManager;

/**
 * @Author Hoosiers on 11/05/2020
 */

@Command.Declaration(name = "Prefix", syntax = "prefix value (no letters or numbers)", alias = {"prefix", "setprefix", "cmdprefix", "commandprefix"})
public class PrefixCommand extends Command {

    public void onCommand(String command, String[] message) throws Exception {
        //this makes sure all inputs do not include letters or numbers, sets them to the default prefix instead
        String main = message[0].toUpperCase().replaceAll("[a-zA-Z0-9]", null);
        int size = message[0].length();

        //we don't want the prefix to be more than 1 character
        if (main != null && size == 1) {
            CommandManager.setCommandPrefix(main);
            MessageBus.sendCommandMessage("Prefix set: \"" + main + "\"!", true);
        } else if (size != 1) {
            MessageBus.sendCommandMessage(this.getSyntax(), true);
        }
    }
}