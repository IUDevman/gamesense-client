package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import net.minecraft.entity.player.InventoryPlayer;

import java.util.HashMap;

public class AutoGearCommand extends Command {

    private HashMap<String, String> errorMessage = new HashMap<String, String>() {
        {
            put("NoName", "I didnt found any name");
        }
    };

    public AutoGearCommand() {
        super("AutoGear");

        setCommandSyntax(Command.getCommandPrefix() + "gear set/save [name]");

        setCommandAlias(new String[]{
                "gear", "gr", "kit"
        });

    }

    public void onCommand(String command, String[] message) throws Exception {

        switch (message[0].toLowerCase()) {
            case "":
            case "help":
                MessageBus.sendCommandMessage("AutoGear message is: gear set/save [name]", true);
                break;
            case "set":
                break;
            case "save":
                InventoryPlayer prova = mc.player.inventory;
                break;
            case "debug":
                break;
        }
    }

    public static void errorMessage() {

    }

}
