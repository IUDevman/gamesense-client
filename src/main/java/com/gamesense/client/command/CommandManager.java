package com.gamesense.client.command;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.commands.*;

import java.util.ArrayList;

/**
 * @Author Hoosiers on 11/04/2020
 */

public class CommandManager {

    private static String commandPrefix = "-";
    public static ArrayList<Command> commands = new ArrayList<>();

    public static void init() {
        addCommand(new AutoGearCommand());
        addCommand(new AutoGGCommand());
        addCommand(new AutoReplyCommand());
        addCommand(new AutoRespawnCommand());
        addCommand(new BackupConfigCommand());
        addCommand(new BindCommand());
        addCommand(new CmdListCommand());
        addCommand(new DisableAllCommand());
        addCommand(new DrawnCommand());
        addCommand(new EnemyCommand());
        addCommand(new FixGUICommand());
        addCommand(new FixHUDCommand());
        addCommand(new FontCommand());
        addCommand(new FriendCommand());
        addCommand(new ModulesCommand());
        addCommand(new OpenFolderCommand());
        addCommand(new PrefixCommand());
        addCommand(new SaveConfigCommand());
        addCommand(new SetCommand());
        addCommand(new ToggleCommand());
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

    boolean isValidCommand = false;

    /**
     * @Author 086 for KAMI, regex
     **/

    public void callCommand(String input) {
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