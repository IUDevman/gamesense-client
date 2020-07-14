package com.gamesense.client.command.commands;

import com.gamesense.client.command.Command;
import com.gamesense.api.Stopper;

public class ConfigCommand extends Command {
    @Override
    public String[] getAlias() {
        return new String[]{"saveconfig", "savecfg"};
    }

    @Override
    public String getSyntax() {
        return "saveconfig";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        Stopper.saveConfig();
        Command.sendClientMessage("Config saved");
    }
}
