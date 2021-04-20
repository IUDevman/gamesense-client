package com.gamesense.client.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.profiler.Profiler;

public interface Manager {

    default Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    default EntityPlayerSP getPlayer() {
        return getMinecraft().player;
    }

    default WorldClient getWorld() {
        return getMinecraft().world;
    }

    default Profiler getProfiler() {
        return getMinecraft().profiler;
    }
}