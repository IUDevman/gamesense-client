package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

/**
 * @Author Hoosiers on 11/05/2020
 */

public class DrawnCommand extends Command {

    public DrawnCommand() {
        super("Drawn");

        setCommandSyntax(Command.getCommandPrefix() + "drawn [module]");
        setCommandAlias(new String[]{
                "drawn", "shown"
        });
    }

    public void onCommand(String command, String[] message) throws Exception {
        String main = message[0];

        Module module = ModuleManager.getModule(main);

        if (module == null) {
            MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
            return;
        }

        if (module.isDrawn()) {
            module.setDrawn(false);
            MessageBus.sendCommandMessage("Module " + module.getName() + " drawn set to: FALSE!", true);
        } else if (!module.isDrawn()) {
            module.setDrawn(true);
            MessageBus.sendCommandMessage("Module " + module.getName() + " drawn set to: TRUE!", true);
        }
    }
}