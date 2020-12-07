package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

/**
 * @Author Hoosiers on 11/05/2020
 */

public class DrawnCommand extends Command {

    public DrawnCommand(){
        super("Drawn");

        setCommandSyntax(Command.getCommandPrefix() + "drawn [module]");
        setCommandAlias(new String[]{
                "drawn", "shown"
        });
    }

    public void onCommand(String command, String[] message) throws Exception{
        String main = message[0];

        if (ModuleManager.getModules().stream().filter(module -> module.getName() == main).findFirst().orElse(null) == null) {
            MessageBus.sendClientPrefixMessage(this.getCommandSyntax());
            return;
        }

        for (Module module : ModuleManager.getModules()){
            if (module.getName().equalsIgnoreCase(main)){
                if (module.isDrawn()){
                    module.setDrawn(false);
                    MessageBus.sendClientPrefixMessage("Module " + module.getName() + "drawn set to: FALSE!");
                }
                else if (!module.isDrawn()){
                    module.setDrawn(true);
                    MessageBus.sendClientPrefixMessage("Module " + module.getName() + "drawn set to: TRUE!");
                }
            }
        }
    }
}