package com.gamesense.client.command.commands;

import com.gamesense.api.config.ConfigStopper;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;

/**
 * @author Hoosiers
 * @since 1/1/2020
 */

public class SaveConfigCommand extends Command {

    public SaveConfigCommand() {
        super("SaveConfig");

        setCommandSyntax(Command.getCommandPrefix() + "saveconfig");
        setCommandAlias(new String[]{
                "saveconfig", "reloadconfig", "config", "saveconfiguration"
        });
    }

    public void onCommand(String command, String[] message) {
        ConfigStopper.saveConfig();
        MessageBus.sendCommandMessage("Config saved!", true);
    }
}