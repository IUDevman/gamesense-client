package com.gamesense.api;

import com.gamesense.api.util.config.SaveConfiguration;
import com.gamesense.client.GameSenseMod;

public class Stopper extends Thread{

	@Override
	public void run(){
		saveConfig();
	}

	public static void saveConfig(){

		//GameSenseMod.getInstance().saveModules.saveModules();

		SaveConfiguration.saveAutoGG();
		SaveConfiguration.saveAutoReply();
		SaveConfiguration.saveBinds();
		SaveConfiguration.saveDrawn();
		SaveConfiguration.saveEnabled();
		SaveConfiguration.saveEnemies();
		SaveConfiguration.saveFont();
		SaveConfiguration.saveFriends();
		SaveConfiguration.saveGUI();
		SaveConfiguration.saveMacros();
		SaveConfiguration.saveMessages();
		SaveConfiguration.savePrefix();
	}
}
