package com.gamesense.client.command;

import java.util.ArrayList;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.misc.ClassUtil;

/**
 * @Author Hoosiers on 11/04/2020
 */

public class CommandManager {

    private static String commandPrefix = "-";
    private static final String commandPath = "com.gamesense.client.command.commands";
    public static ArrayList<Command> commands = new ArrayList<>();

    public static void init() {
        for (Class<?> clazz : ClassUtil.findClassesInPath(commandPath)) {

            if (clazz == null) continue;

            if (Command.class.isAssignableFrom(clazz)) {
                try {
                    Command command = (Command) clazz.newInstance();
                    addCommand(command);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addCommand(Command command) {
        commands.add(command);
    }

    public static ArrayList<Command> getCommands() {
        return commands;
    }

    public static String getCommandPrefix() {
        return commandPrefix;
    }

    public static void setCommandPrefix(String prefix) {
        commandPrefix = prefix;
    }

    public static boolean isValidCommand = false;

    /**
     * @Author 086 for KAMI, regex
     **/

    public static void callCommand(String input) {
        String[] split = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String command1 = split[0];
        String args = input.substring(command1.length()).trim();

        isValidCommand = false;

        commands.forEach(command -> {
            for (String string : command.getAlias()) {
                if (string.equalsIgnoreCase(command1)) {
                    isValidCommand = true;
                    try {
                        command.onCommand(args, args.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
                    } catch (Exception e) {
                        MessageBus.sendCommandMessage(command.getSyntax(), true);
                    }
                }
            }
        });

        if (!isValidCommand) {
            MessageBus.sendCommandMessage("Error! Invalid command!", true);
        }
    }
}