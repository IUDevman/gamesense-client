package com.gamesense.client.commands2.commands;

import com.gamesense.client.commands2.Command;

/**
 * @Author Hoosiers on 11/04/2020
 */

public class AutoGG extends Command {

    public AutoGG(){
        super("AutoGG");

        setCommandSyntax(Command.getCommandPrefix() + "autogg add/del/list [message] (use _ for spaces)");
        setCommandAlias(new String[]{
                "autogg", "gg"
        });
    }
}
