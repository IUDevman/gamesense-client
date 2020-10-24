package com.gamesense.api.config;

import com.gamesense.client.GameSenseMod;

import java.io.IOException;

public class ConfigStopper extends Thread {

    @Override
    public void run(){
        saveConfig();
    }

    public static void saveConfig(){
        try {
            GameSenseMod.getInstance().saveConfig.saveConfig();
            GameSenseMod.getInstance().saveConfig.saveModules();
            GameSenseMod.getInstance().saveConfig.saveEnabledModules();
            GameSenseMod.getInstance().saveConfig.saveModuleKeybinds();
            GameSenseMod.getInstance().saveConfig.saveDrawnModules();
            GameSenseMod.getInstance().saveConfig.saveCommandPrefix();
            GameSenseMod.getInstance().saveConfig.saveCustomFont();
            GameSenseMod.getInstance().saveConfig.saveFriendsList();
            GameSenseMod.getInstance().saveConfig.saveEnemiesList();
            GameSenseMod.getInstance().saveConfig.saveClickGUIPositions();
            GameSenseMod.getInstance().saveConfig.saveAutoGG();
            GameSenseMod.getInstance().saveConfig.saveAutoReply();
            GameSenseMod.log.info("Saved Config!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}