package com.gamesense.client.command.commands;

import com.gamesense.api.util.font.CFontRenderer;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.GameSense;
import com.gamesense.client.command.Command;

import java.awt.*;

/**
 * @Author Hoosiers on 11/05/2020
 */

@Command.Declaration(name = "Font", syntax = "font [name] size (use _ for spaces)", alias = {"font", "setfont", "customfont", "fonts", "chatfont"})
public class FontCommand extends Command {

    public void onCommand(String command, String[] message) {
        String main = message[0].replace("_", " ");
        int value = Integer.parseInt(message[1]);

        if (value >= 21 || value <= 15) {
            value = 18;
        }

        GameSense.INSTANCE.cFontRenderer = new CFontRenderer(new Font(main, Font.PLAIN, value), true, true);
        GameSense.INSTANCE.cFontRenderer.setFontName(main);
        GameSense.INSTANCE.cFontRenderer.setFontSize(value);

        MessageBus.sendCommandMessage("Font set to: " + main.toUpperCase() + ", size " + value + "!", true);
    }
}