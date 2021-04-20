package com.gamesense.api.util.misc;

import com.gamesense.client.GameSense;
import com.gamesense.client.command.CommandManager;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/**
 * @author Hoosiers
 * @since 12/15/2020
 */

public class VersionChecker {

    public static String joinMessage = "None";

    public static void init() {
        checkVersion(GameSense.MODVER);
    }

    private static void checkVersion(String version) {
        boolean isLatest = true;
        String newVersion = "null";

        if (version.startsWith("d")) {
            return;
        }

        try {
            URL url = new URL("https://raw.githubusercontent.com/IUDevman/gamesense-assets/main/files/versioncontrol.txt");
            Scanner scanner = new Scanner(url.openStream());

            String grabbedVersion = scanner.next();

            if (!version.equalsIgnoreCase(grabbedVersion)) {
                isLatest = false;
                newVersion = grabbedVersion;
            }
        } catch (IOException e) {
            e.printStackTrace();
            isLatest = true;
        }

        if (!isLatest) {
            joinMessage = "Version (" + version + ") is outdated! Download the latest version (" + newVersion + ") by typing " + CommandManager.getCommandPrefix() + "releases!";
        }
    }
}