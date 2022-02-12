package dev.gamesense.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

/**
 * @author IUDevman
 * @since 02-11-2022
 */

public interface Global {

    default Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    default EntityPlayerSP getPlayer() {
        return getMinecraft().player;
    }

    default WorldClient getWorld() {
        return getMinecraft().world;
    }

    default boolean isNull() {
        return getPlayer() == null || getWorld() == null;
    }
}
