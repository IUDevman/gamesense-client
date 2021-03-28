package com.gamesense.client.command;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.commands.AutoGGCommand;
import com.gamesense.client.command.commands.AutoGearCommand;
import com.gamesense.client.command.commands.AutoReplyCommand;
import com.gamesense.client.command.commands.AutoRespawnCommand;
import com.gamesense.client.command.commands.BackupConfigCommand;
import com.gamesense.client.command.commands.BindCommand;
import com.gamesense.client.command.commands.CmdListCommand;
import com.gamesense.client.command.commands.DisableAllCommand;
import com.gamesense.client.command.commands.DrawnCommand;
import com.gamesense.client.command.commands.EnemyCommand;
import com.gamesense.client.command.commands.FixGUICommand;
import com.gamesense.client.command.commands.FixHUDCommand;
import com.gamesense.client.command.commands.FontCommand;
import com.gamesense.client.command.commands.FriendCommand;
import com.gamesense.client.command.commands.ModulesCommand;
import com.gamesense.client.command.commands.MsgsCommand;
import com.gamesense.client.command.commands.OpenFolderCommand;
import com.gamesense.client.command.commands.PrefixCommand;
import com.gamesense.client.command.commands.SaveConfigCommand;
import com.gamesense.client.command.commands.SetCommand;
import com.gamesense.client.command.commands.ToggleCommand;
import java.util.ArrayList;

/**
 * @Author Hoosiers on 11/04/2020
 */

public class CommandManager {

    public static ArrayList<Command> commands = new ArrayList<>();
    private static String commandPrefix = "-";
    boolean isValidCommand = false;

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
        addCommand(new MsgsCommand());
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