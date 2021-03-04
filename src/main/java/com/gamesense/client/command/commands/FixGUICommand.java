package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.GameSense;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.command.Command;

/**
 * @Author Hoosiers on 11/05/2020
 */

@Command.Declaration(name = "FixGUI", syntax = "fixgui", alias = {"fixgui", "gui", "resetgui"})
public class FixGUICommand extends Command {

    public void onCommand(String command, String[] message) throws Exception {
        GameSense.getInstance().gameSenseGUI = new GameSenseGUI();
        MessageBus.sendCommandMessage("ClickGUI positions reset!", true);
    }
}