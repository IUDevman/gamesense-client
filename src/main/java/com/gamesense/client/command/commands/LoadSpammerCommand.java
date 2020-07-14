package com.gamesense.client.command.commands;

import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
//import com.gamesense.client.module.modules.chat.Spammer;

public class LoadSpammerCommand extends Command {
    @Override
    public String[] getAlias() {
        return new String[]{"loadspammer"};
    }

    @Override
    public String getSyntax() {
        return "loadspammer";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
    //    Spammer.text.clear();
    //    GameSenseMod.getInstance().configUtils.loadSpammer();
        Command.sendClientMessage("Loaded Spammer File");
    }
}
