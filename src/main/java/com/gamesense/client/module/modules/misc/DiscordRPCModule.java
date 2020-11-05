package com.gamesense.client.module.modules.misc;

import com.gamesense.api.util.misc.Discord;
import com.gamesense.client.commands2.MessageBus;
import com.gamesense.client.module.Module;

public class DiscordRPCModule extends Module {
    public DiscordRPCModule(){
        super("DiscordRPC", Category.Misc);
        setDrawn(false);
    }

    public void onEnable(){
        Discord.startRPC();
        if (mc.player != null || mc.world != null){
            MessageBus.sendClientPrefixMessage("Discord RPC started!");
        }
    }

    public void onDisable(){
        Discord.stopRPC();
        if (mc.player != null || mc.world != null) {
            MessageBus.sendClientPrefixMessage("Discord RPC stopped!");
        }
    }
}