package com.gamesense.client.commands2.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.commands2.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import org.lwjgl.input.Keyboard;

/**
 * @Author Hoosiers on 11/05/2020
 */

public class BindCommand extends Command {

    public BindCommand(){
        super("Bind");

        setCommandSyntax(Command.getCommandPrefix() + "bind [module] key");
        setCommandAlias(new String[]{
                "bind", "b", "setbind", "key"
        });
    }

    public void onCommand(String command, String[] message) throws Exception{
        String main = message[0];
        String value = message[1].toUpperCase();

        for (Module module : ModuleManager.getModules()){
            if (module.getName().equalsIgnoreCase(main)){
                if (value.equalsIgnoreCase("none")){
                    module.setBind(Keyboard.KEY_NONE);
                    MessageBus.sendClientPrefixMessage("Module " + module.getName() + " bind set to: " + value + "!");
                }
                else if (value.length() == 1){
                    int key = Keyboard.getKeyIndex(value);

                    module.setBind(key);
                    MessageBus.sendClientPrefixMessage("Module " + module.getName() + " bind set to: " + value + "!");
                }
            }
        }
    }
}