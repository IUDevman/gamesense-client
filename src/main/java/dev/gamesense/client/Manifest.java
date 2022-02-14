package dev.gamesense.client;

import dev.gamesense.client.command.Command;
import dev.gamesense.client.module.Module;

import java.util.ArrayList;

/**
 * @author IUDevman
 * @since 02-14-2022
 */

public final class Manifest {

    public static ArrayList<Module> getModulesToLoad() {
        ArrayList<Module> modules = new ArrayList<>();

        return modules;
    }

    public static ArrayList<Command> getCommandsToLoad() {
        ArrayList<Command> commands = new ArrayList<>();

        return commands;
    }
}
