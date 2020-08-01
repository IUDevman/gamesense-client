package com.gamesense.client.command.commands;

import com.gamesense.client.GameSenseMod;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.client.command.Command;
import com.gamesense.client.macro.Macro;
import org.lwjgl.input.Keyboard;

public class MacroCommand extends Command {
    @Override
    public String[] getAlias() {
        return new String[]{"macro", "macros"};
    }

    @Override
    public String getSyntax() {
        return "macro <add | del> <key> <value>";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        if(args[0].equalsIgnoreCase("add")){
            GameSenseMod.getInstance().macroManager.delMacro(GameSenseMod.getInstance().macroManager.getMacroByKey(Keyboard.getKeyIndex(args[1])));
            GameSenseMod.getInstance().macroManager.addMacro(new Macro(Keyboard.getKeyIndex(args[1].toUpperCase()), args[2].replace("_", " ")));
            Command.sendClientMessage(ChatFormatting.GREEN + "Added" + ChatFormatting.GRAY + " macro for key \"" + args[1].toUpperCase() + "\" with value \"" + args[2].replace("_", " ") + "\".");
        }
        if(args[0].equalsIgnoreCase("del")){
            if(GameSenseMod.getInstance().macroManager.getMacros().contains(GameSenseMod.getInstance().macroManager.getMacroByKey(Keyboard.getKeyIndex(args[1].toUpperCase())))) {
                GameSenseMod.getInstance().macroManager.delMacro(GameSenseMod.getInstance().macroManager.getMacroByKey(Keyboard.getKeyIndex(args[1].toUpperCase())));
                Command.sendClientMessage(ChatFormatting.RED + "Removed " + ChatFormatting.GRAY + "macro for key \"" + args[1].toUpperCase() + "\".");
            }else {
                Command.sendClientMessage(ChatFormatting.GRAY + "That macro doesn't exist!");
            }
        }
    }
}
