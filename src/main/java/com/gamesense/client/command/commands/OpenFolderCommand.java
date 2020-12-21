package com.gamesense.client.command.commands;

import com.gamesense.api.config.SaveConfig;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;

import java.awt.Desktop;
import java.io.File;

/**
 * @Author Hoosiers on 11/05/2020
 */

public class OpenFolderCommand extends Command {

    public OpenFolderCommand() {
        super("OpenFolder");

        setCommandSyntax(Command.getCommandPrefix() + "openfolder");
        setCommandAlias(new String[]{
                "openfolder", "config", "open", "folder"
        });
    }

    public void onCommand(String command, String[] message) throws Exception {
        Desktop.getDesktop().open(new File(SaveConfig.fileName.replace("/", "")));
        MessageBus.sendClientPrefixMessage("Opened config folder!");
    }
}