package com.gamesense.client.command.commands;

import com.gamesense.client.command.Command;
import com.gamesense.client.GameSenseMod;

public class LoadAnnouncerCommand extends Command {
    @Override
    public String[] getAlias() {
        return new String[]{
                "loadannouncer"
        };
    }

    @Override
    public String getSyntax() {
        return "loadannouncer";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        GameSenseMod.getInstance().configUtils.loadAnnouncer();
        sendClientMessage("Loaded Announcer file");
    }
}
