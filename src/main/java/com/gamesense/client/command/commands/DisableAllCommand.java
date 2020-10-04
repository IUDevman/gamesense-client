package com.gamesense.client.command.commands;

import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

/**
 * @Author Hoosiers on 10/03/20
 */

public class DisableAllCommand extends Command {

    @Override
    public String[] getAlias(){
        return new String[]{
                "disableall", "stopall", "disablemodules"
        };
    }

    @Override
    public String getSyntax(){
        return "disableall";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception{
        for (Module module : ModuleManager.getModules()){
            if (ModuleManager.isModuleEnabled(module)){
                module.disable();
            }
        }
    }
}