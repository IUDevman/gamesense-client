package com.gamesense.client.command;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.commands.*;

import java.util.ArrayList;

/**
 * @Author Hoosiers on 11/04/2020
 */

public class CommandManager {

    public static ArrayList<Command> commands = new ArrayList<>();

    public static void registerCommands() {
        addCommand(new AutoGearCommand());
        addCommand(new AutoGGCommand());
        addCommand(new AutoReplyCommand());
        addCommand(new AutoRespawnCommand());
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

    public static Command getCommandByName(String name) {
        for (Command command : commands) {
            if (command.getCommandName() == name) {
                return command;
            }
        }
        return null;
    }

    boolean isValidCommand = false;

    /** @Author 086 for KAMI, regex is a bitch **/
    public void callCommand(String input) {
        String[] split = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String command1 = split[0];
        String args = input.substring(command1.length()).trim();

        isValidCommand = false;

        commands.forEach(command -> {
            for (String string : command.getCommandAlias()) {
                if (string.equalsIgnoreCase(command1)) {
                    isValidCommand = true;
                    try {
                        command.onCommand(args, args.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
                    }
                    catch (Exception e) {
                        MessageBus.sendCommandMessage(command.getCommandSyntax(), true);
                    }
                }
            }
        });

        if (!isValidCommand) {
            MessageBus.sendCommandMessage("Error! Invalid command!", true);
        }
    }
}