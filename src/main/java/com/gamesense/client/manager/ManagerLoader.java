package com.gamesense.client.manager;

import java.util.ArrayList;
import java.util.List;

import com.gamesense.client.GameSense;
import com.gamesense.client.manager.managers.ClientEventManager;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.manager.managers.TotemPopManager;

import net.minecraftforge.common.MinecraftForge;

public class ManagerLoader {

    private static final List<Manager> managers = new ArrayList<>();

    public static void init() {
        register(ClientEventManager.INSTANCE);
        register(PlayerPacketManager.INSTANCE);
        register(TotemPopManager.INSTANCE);
    }

    private static void register(Manager manager) {
        managers.add(manager);
        GameSense.EVENT_BUS.subscribe(manager);
        MinecraftForge.EVENT_BUS.register(manager);
    }
}