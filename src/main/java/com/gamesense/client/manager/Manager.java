package com.gamesense.client.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public interface Manager {

    default Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    default EntityPlayerSP getPlayer() {
        return getMinecraft().player;
    }
}