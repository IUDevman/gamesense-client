package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.modules.misc.AutoRespawn;

public class AutoRespawnCommand extends Command{

    public AutoRespawnCommand() {
        super("AutoRespawn");

        setCommandSyntax(Command.getCommandPrefix() + "autorespawn get/set [message] (use _ for spaces)");
        setCommandAlias(new String[]{
                "autorespawn", "respawn"
        });
    }

    public void onCommand(String command, String[] message) throws Exception {
        String main = message[0];

        if (main.equalsIgnoreCase("get")) {
            MessageBus.sendCommandMessage("AutoRespawn message is: " + AutoRespawn.getAutoRespawnMessages() + "!", true);
            return;
        }

        String value = message[1].replace("_", " ");

        if (main.equalsIgnoreCase("set") && !(AutoRespawn.getAutoRespawnMessages().equals(value))) {
            AutoRespawn.setAutoRespawnMessage(value);
            MessageBus.sendCommandMessage("Set AutoRespawn message to: " + value + "!", true);
        }
    }
}
