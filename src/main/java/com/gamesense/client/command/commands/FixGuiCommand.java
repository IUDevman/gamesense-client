package com.gamesense.client.command.commands;

import com.gamesense.client.GameSenseMod;
import com.gamesense.client.clickgui.ClickGUI;
import com.gamesense.client.command.Command;

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
            GameSenseMod.getInstance().clickGUI = new ClickGUI();
            Command.sendClientMessage("ClickGui positions reset!");
        }
        catch (Exception e){
            Command.sendClientMessage("There was an error in resetting ClickGui positions!");
        }
    }
}