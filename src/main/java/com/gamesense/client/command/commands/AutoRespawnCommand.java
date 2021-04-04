package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.modules.misc.AutoRespawn;

@Command.Declaration(name = "AutoRespawn", syntax = "autorespawn get/set [message] (do NOT use _ for spaces)", alias = {"autorespawn", "respawn"})
public class AutoRespawnCommand extends Command {

    public void onCommand(String command, String[] message) {
        String main = message[0];

        if (main.equalsIgnoreCase("get")) {
            MessageBus.sendCommandMessage("AutoRespawn message is: " + AutoRespawn.getAutoRespawnMessages() + "!", true);
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < message.length; i++) {
            stringBuilder.append(message[i]);
            stringBuilder.append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        String value = stringBuilder.toString();

        if (main.equalsIgnoreCase("set") && !(AutoRespawn.getAutoRespawnMessages().equals(value))) {
            AutoRespawn.setAutoRespawnMessage(value);
            MessageBus.sendCommandMessage("Set AutoRespawn message to: " + value + "!", true);
        }
    }
}