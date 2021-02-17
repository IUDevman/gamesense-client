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
            GameSense.getInstance().saveConfig.saveConfig();
            GameSense.getInstance().saveConfig.saveModules();
            GameSense.getInstance().saveConfig.saveEnabledModules();
            GameSense.getInstance().saveConfig.saveModuleKeybinds();
            GameSense.getInstance().saveConfig.saveDrawnModules();
            GameSense.getInstance().saveConfig.saveCommandPrefix();
            GameSense.getInstance().saveConfig.saveCustomFont();
            GameSense.getInstance().saveConfig.saveFriendsList();
            GameSense.getInstance().saveConfig.saveEnemiesList();
            GameSense.getInstance().saveConfig.saveClickGUIPositions();
            GameSense.getInstance().saveConfig.saveAutoGG();
            GameSense.getInstance().saveConfig.saveAutoReply();
            GameSense.getInstance().saveConfig.saveAutoRespawn();
            GameSense.LOGGER.info("Saved Config!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}