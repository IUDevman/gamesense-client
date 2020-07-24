package com.gamesense.client.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.client.command.commands.*;

import java.util.ArrayList;

public class CommandManager {
    private static ArrayList<Command> commands;
    boolean b;

    public static void initCommands(){
        commands = new ArrayList<>();
        addCommand(new BindCommand());
        addCommand(new ToggleCommand());
        addCommand(new DrawnCommand());
        addCommand(new CmdsCommand());
        addCommand(new ModsCommand());
        addCommand(new PrefixCommand());
        addCommand(new FriendCommand());
        addCommand(new MacroCommand());
        addCommand(new ConfigCommand());
        addCommand(new ClientMsgsCommand());
        addCommand(new AutoGgCommand());
        addCommand(new OpenFolderCommand());
        addCommand(new LoadSpammerCommand());
        addCommand(new AutoReplyCommand());
        addCommand(new MiddleXCommand());
        addCommand(new WaypointCommand());
        addCommand(new FontCommand());
        addCommand(new EnemyCommand());
        addCommand(new VanishCommand());
    }

    public static void addCommand(Command c){
        commands.add(c);
    }

    public static ArrayList<Command> getCommands(){
        return commands;
    }

    public void callCommand(String input){
        String[] split = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by every space if it isn't surrounded by quotes // credit 086/KAMI
        String command = split[0];
        String args = input.substring(command.length()).trim();
        b = false;
        commands.forEach(c ->{
            for(String s : c.getAlias()) {
                if (s.equalsIgnoreCase(command)) {
                    b = true;
                    try {
                        c.onCommand(args, args.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
                    } catch (Exception e) {
                        Command.sendClientMessage(ChatFormatting.DARK_RED + c.getSyntax());
                    }
                }
            }
        });
        if(!b) Command.sendClientMessage(ChatFormatting.DARK_RED + "Unknown command!");
    }
}
