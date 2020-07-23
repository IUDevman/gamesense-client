package com.gamesense.api;

import com.gamesense.client.GameSenseMod;

public class Stopper extends Thread {
    @Override
    public void run(){
        saveConfig();
    }

    public static void saveConfig(){
        GameSenseMod.getInstance().configUtils.saveMods();
        GameSenseMod.getInstance().configUtils.saveSettingsList();
        GameSenseMod.getInstance().configUtils.saveBinds();
        GameSenseMod.getInstance().configUtils.saveDrawn();
        GameSenseMod.getInstance().configUtils.saveFriends();
        GameSenseMod.getInstance().configUtils.saveDevGUI();
        GameSenseMod.getInstance().configUtils.savePrefix();
        GameSenseMod.getInstance().configUtils.saveRainbow();
        GameSenseMod.getInstance().configUtils.saveMacros();
        GameSenseMod.getInstance().configUtils.saveMsgs();
        GameSenseMod.getInstance().configUtils.saveAutoGG();
        GameSenseMod.getInstance().configUtils.saveAutoReply();
        GameSenseMod.getInstance().configUtils.saveWaypoints();
        GameSenseMod.getInstance().configUtils.saveFont();
        GameSenseMod.getInstance().configUtils.saveEnemies();
    }
}
