package com.gamesense.client.command.commands;

import com.gamesense.client.GameSenseMod;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.command.Command;
import com.gamesense.api.util.misc.MessageBus;

/**
 * @Author Hoosiers on 10/03/20
 */

public class FixGuiCommand extends Command {

    @Override
    public String[] getAlias(){
        return new String[] {
                "fixgui", "fixhud", "fix"
        };
    }

    @Override
    public String getSyntax(){
        return "fixgui";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception{
        try {
            //resets ClickGui to default positions
            GameSenseMod.getInstance().clickGUI = new GameSenseGUI();
            MessageBus.sendClientPrefixMessage("ClickGui positions reset!");
        }
        catch (Exception e){
            MessageBus.sendClientPrefixMessage("There was an error in resetting ClickGui positions!");
        }
    }
}