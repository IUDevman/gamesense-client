package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

/**
 * @Author Hoosiers on 11/05/2020
 */

@Command.Declaration(name = "Drawn", syntax = "drawn [module]", alias = {"drawn", "shown"})
public class DrawnCommand extends Command {

    public void onCommand(String command, String[] message) throws Exception {
        String main = message[0];

        Module module = ModuleManager.getModule(main);

        if (module == null) {
            MessageBus.sendCommandMessage(this.getSyntax(), true);
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