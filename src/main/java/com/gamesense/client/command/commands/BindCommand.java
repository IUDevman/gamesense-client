package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import org.lwjgl.input.Keyboard;

/**
 * @Author Hoosiers on 11/05/2020
 */

@Command.Declaration(name = "Bind", syntax = "bind [module] key", alias = {"bind", "b", "setbind", "key"})
public class BindCommand extends Command {

    public void onCommand(String command, String[] message) throws Exception {
        String main = message[0];
        String value = message[1].toUpperCase();

        for (Module module : ModuleManager.getModules()) {
            if (module.getName().equalsIgnoreCase(main)) {
                if (value.equalsIgnoreCase("none")) {
                    module.setBind(Keyboard.KEY_NONE);
                    MessageBus.sendCommandMessage("Module " + module.getName() + " bind set to: " + value + "!", true);
                }
                //keeps people from accidentally binding things such as ESC, TAB, exc.
                else if (value.length() == 1) {
                    int key = Keyboard.getKeyIndex(value);

                    module.setBind(key);
                    MessageBus.sendCommandMessage("Module " + module.getName() + " bind set to: " + value + "!", true);
                } else if (value.length() > 1) {
                    MessageBus.sendCommandMessage(this.getSyntax(), true);
                }
            }
        }
    }
}