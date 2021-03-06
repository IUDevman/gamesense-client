package com.gamesense.api.config;

import com.gamesense.client.GameSense;

import java.io.IOException;

public class ConfigStopper extends Thread {

    @Override
    public void run() {
        saveConfig();
    }

    public static void saveConfig() {
        try {
            GameSense.INSTANCE.saveConfig.saveConfig();
            GameSense.INSTANCE.saveConfig.saveModules();
            GameSense.INSTANCE.saveConfig.saveEnabledModules();
            GameSense.INSTANCE.saveConfig.saveModuleKeybinds();
            GameSense.INSTANCE.saveConfig.saveDrawnModules();
            GameSense.INSTANCE.saveConfig.saveCommandPrefix();
            GameSense.INSTANCE.saveConfig.saveCustomFont();
            GameSense.INSTANCE.saveConfig.saveFriendsList();
            GameSense.INSTANCE.saveConfig.saveEnemiesList();
            GameSense.INSTANCE.saveConfig.saveClickGUIPositions();
            GameSense.INSTANCE.saveConfig.saveAutoGG();
            GameSense.INSTANCE.saveConfig.saveAutoReply();
            GameSense.INSTANCE.saveConfig.saveAutoRespawn();
            GameSense.LOGGER.info("Saved Config!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}