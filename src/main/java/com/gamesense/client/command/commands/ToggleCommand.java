package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

/**
 * @Author Hoosiers on 11/05/2020
 */

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("Toggle");

        setCommandSyntax(Command.getCommandPrefix() + "toggle [module]");
        setCommandAlias(new String[]{
                "toggle", "t", "enable", "disable"
        });
    }

    public void onCommand(String command, String[] message) throws Exception {
        String main = message[0];

        for (Module module : ModuleManager.getModules()) {
            if (module.getName().equalsIgnoreCase(main) && !module.isEnabled()) {
                module.enable();
                MessageBus.sendCommandMessage("Module " + module.getName() + " set to: ENABLED!", true);
            }
            else if (module.getName().equalsIgnoreCase(main) && module.isEnabled()) {
                module.disable();
                MessageBus.sendCommandMessage("Module " + module.getName() + " set to: DISABLED!", true);
            }
        }

        if (main == null || ModuleManager.getModuleByName(main) == null) {
            MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
        }
    }
}