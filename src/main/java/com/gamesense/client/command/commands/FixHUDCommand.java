package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

@Command.Declaration(name = "FixHUD", syntax = "fixhud", alias = {"fixhud", "hud", "resethud"})
public class FixHUDCommand extends Command {

    @Override
    public void onCommand(String command, String[] message) throws Exception {
        for (Module module : ModuleManager.getModules()) {
            if (module instanceof HUDModule) {
                ((HUDModule) module).resetPosition();
            }
        }
        MessageBus.sendCommandMessage("HUD positions reset!", true);
    }
}