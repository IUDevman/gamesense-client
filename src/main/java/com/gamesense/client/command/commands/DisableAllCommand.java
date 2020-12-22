package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

/**
 * @Author Hoosiers on 11/05/2020
 */

public class DisableAllCommand extends Command {

    public DisableAllCommand() {
        super("DisableAll");

        setCommandSyntax(Command.getCommandPrefix() + "disableall");
        setCommandAlias(new String[]{
                "disableall", "stop"
        });
    }

    public void onCommand(String command, String[] message) throws Exception {
        int count = 0;

        for (Module module : ModuleManager.getModules()) {
            if (module.isEnabled()) {
                module.disable();
                count++;
            }
        }

        MessageBus.sendCommandMessage("Disabled " + count + " modules!", true);
    }
}