package com.gamesense.api;

import com.gamesense.client.GameSenseMod;

public class Stopper extends Thread {

    @Override
    public void run(){
        saveConfig();
    }

    public static void saveConfig(){

        GameSenseMod.getInstance().saveModules.saveModules();

        GameSenseMod.getInstance().saveConfiguration.saveAutoGG();
        GameSenseMod.getInstance().saveConfiguration.saveAutoReply();
        GameSenseMod.getInstance().saveConfiguration.saveBinds();
        GameSenseMod.getInstance().saveConfiguration.saveDrawn();
        GameSenseMod.getInstance().saveConfiguration.saveEnabled();
        GameSenseMod.getInstance().saveConfiguration.saveEnemies();
        GameSenseMod.getInstance().saveConfiguration.saveFont();
        GameSenseMod.getInstance().saveConfiguration.saveFriends();
        GameSenseMod.getInstance().saveConfiguration.saveGUI();
        GameSenseMod.getInstance().saveConfiguration.saveMacros();
        GameSenseMod.getInstance().saveConfiguration.saveMessages();
        GameSenseMod.getInstance().saveConfiguration.savePrefix();
    }
}
