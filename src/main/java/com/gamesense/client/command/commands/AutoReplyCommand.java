package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.modules.misc.AutoReply;

/**
 * @Author Hoosiers on 11/04/2020
 */

public class AutoReplyCommand extends Command {

    public AutoReplyCommand() {
        super("AutoReply");

        setCommandSyntax(Command.getCommandPrefix() + "autoreply set [message] (use _ for spaces)");
        setCommandAlias(new String[]{
                "autoreply", "reply"
        });
    }

    public void onCommand(String command, String[] message) throws Exception {
        String main = message[0];
        String value = message[1].replace("_", " ");

        if (main.equalsIgnoreCase("set")) {
            AutoReply.setReply(value);
            MessageBus.sendClientPrefixMessage("Set AutoReply message: " + value + "!");
        }
    }
}