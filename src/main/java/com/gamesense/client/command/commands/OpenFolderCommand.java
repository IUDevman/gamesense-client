package com.gamesense.client.command.commands;

import com.gamesense.api.config.SaveConfig;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;

import java.awt.*;
import java.io.File;

/**
 * @Author Hoosiers on 11/05/2020
 */

@Command.Declaration(name = "OpenFolder", syntax = "openfolder", alias = {"openfolder", "config", "open", "folder"})
public class OpenFolderCommand extends Command {

    public void onCommand(String command, String[] message) throws Exception {
        Desktop.getDesktop().open(new File(SaveConfig.fileName.replace("/", "")));
        MessageBus.sendCommandMessage("Opened config folder!", true);
    }
}