package com.gamesense.client.commands2.commands;

import com.gamesense.client.commands2.Command;

/**
 * @Author Hoosiers on 11/04/2020
 */

public class AutoReply extends Command {

    public AutoReply(){
        super("AutoReply");

        setCommandSyntax(Command.getCommandPrefix() + "autoreply add/del/list [message] (use _ for spaces)");
        setCommandAlias(new String[]{
                "autoreply", "reply"
        });
    }
}