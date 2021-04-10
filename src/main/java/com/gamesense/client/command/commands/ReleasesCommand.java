package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Command.Declaration(name = "Releases", syntax = "releases", alias = {"releases", "release", "updateversion"})
public class ReleasesCommand extends Command {

    @Override
    public void onCommand(String command, String[] message) {

        try {
            URL url = new URL("https://github.com/IUDevman/gamesense-client/releases");
            try {
                Desktop.getDesktop().browse(url.toURI());
                MessageBus.sendCommandMessage("Opened a link to the releases page!", true);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                MessageBus.sendCommandMessage("Failed to pen a link to the releases page!", true);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            MessageBus.sendCommandMessage("Failed to pen a link to the releases page!", true);
        }
    }
}