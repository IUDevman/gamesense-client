package com.gamesense.client.module.modules.misc;

import com.gamesense.api.util.misc.Discord;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Module.Declaration(name = "DiscordRPC", category = Category.Misc, drawn = false)
public class DiscordRPCModule extends Module {

    public void onEnable() {
        Discord.startRPC();
    }

    public void onDisable() {
        Discord.stopRPC();
    }
}