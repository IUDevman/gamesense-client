package dev.gamesense.client.command;

import dev.gamesense.misc.Global;

/**
 * @author IUDevman
 * @since 02-12-2022
 */

public interface Command extends Global {

    String getName();

    String getMarker();

    String getSyntax();

    int getID();

    void onCommand(String[] message);
}
