package com.gamesense.client.manager;

import com.gamesense.client.GameSense;
import com.gamesense.client.manager.managers.PlayerPacketManager;

import java.util.ArrayList;
import java.util.List;

public class ManagerLoader {

    private static List<Manager> managers;

    public static void init() {
        managers = new ArrayList<>();

        register(PlayerPacketManager.INSTANCE);
    }

    private static void register(Manager manager) {
        managers.add(manager);
        GameSense.EVENT_BUS.subscribe(manager);
    }

}
