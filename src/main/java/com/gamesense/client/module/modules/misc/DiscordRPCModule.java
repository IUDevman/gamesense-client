package com.gamesense.client.module.modules.misc;

import com.gamesense.api.util.misc.Discord;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.Category;

@Module.Declaration(name = "DiscordRPC", category = Category.Misc, drawn = false)
public class DiscordRPCModule extends Module {

    public void onEnable() {
        Discord.startRPC();
        if (mc.player != null || mc.world != null) {
            MessageBus.sendClientPrefixMessage("Discord RPC started!");
        }
    }

    public void onDisable() {
        Discord.stopRPC();
        if (mc.player != null || mc.world != null) {
            MessageBus.sendClientPrefixMessage("Discord RPC stopped!");
        }
    }
}