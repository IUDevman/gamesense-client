package com.gamesense.client.command.commands;

import com.gamesense.api.util.font.CFontRenderer;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import java.awt.Font;

/**
 * @Author Hoosiers on 11/05/2020
 */

public class FontCommand extends Command {

    public FontCommand(){
        super("Font");

        setCommandSyntax(Command.getCommandPrefix() + "font [name] size (use _ for spaces)");
        setCommandAlias(new String[]{
                "font", "setfont", "customfont", "fonts", "chatfont"
        });
    }

    public void onCommand(String command, String[] message){
        String main = message[0].replace("_", " ");
        int value = Integer.parseInt(message[1]);

        if (value >= 21 || value <= 15){
            value = 18;
        }

        GameSenseMod.fontRenderer = new CFontRenderer(new Font(main, Font.PLAIN, value), true, true);
        GameSenseMod.fontRenderer.setFontName(main);
        GameSenseMod.fontRenderer.setFontSize(value);

        MessageBus.sendClientPrefixMessage("Font set to: " + main.toUpperCase() + ", size " + value + "!");
    }
}