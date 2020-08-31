package com.gamesense.api.util.config;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

import java.io.*;

/**
 * Made by Hoosiers on 08/02/20 for GameSense, some functions were modified and ported from the original ConfigUtils
 */

public class LoadModules {

    //loads all functions for modules
    public LoadModules() {
        loadCategory(SaveConfiguration.Combat, Module.Category.Combat);
        loadCategory(SaveConfiguration.Exploits, Module.Category.Exploits);
        loadCategory(SaveConfiguration.Hud, Module.Category.HUD);
        loadCategory(SaveConfiguration.Misc, Module.Category.Misc);
        loadCategory(SaveConfiguration.Movement, Module.Category.Movement);
        loadCategory(SaveConfiguration.Render, Module.Category.Render);
    }

    /**
     * @Author Lukflug, removed excess code
     */

	private void loadCategory (File config, Module.Category category) {
        loadSettings(config,category,"Value.json",Setting.Type.INT);
        loadSettings(config,category,"Boolean.json",Setting.Type.BOOLEAN);
        loadSettings(config,category,"String.json",Setting.Type.MODE);
		//loadSettings(config,category,"Color.json",Setting.Type.COLOR);
    }
	
	private void loadSettings (File config, Module.Category category, String filename, Setting.Type type) {
		try {
            File file = new File(config.getAbsolutePath(),filename);
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			String curLine;
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                String configname = curLine.split(":")[0];
                String isOn = curLine.split(":")[1];
                String m = curLine.split(":")[2];
                for (Module mm: ModuleManager.getModulesInCategory(category)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        Setting mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname,mm);
						if (mod.getType()==type || (type==Setting.Type.INT && mod.getType()==Setting.Type.DOUBLE)) {
							switch (type) {
							case INT:
								((Setting.Integer) mod).setValue(java.lang.Integer.parseInt(isOn));
								break;
							case DOUBLE:
								((Setting.Double) mod).setValue(java.lang.Double.parseDouble(isOn));
								break;
							case BOOLEAN:
								((Setting.Boolean) mod).setValue(java.lang.Boolean.parseBoolean(isOn));
								break;
							case MODE:
								((Setting.Mode) mod).setValue(isOn);
								break;
							/*case COLOR:
								((Setting.ColorSetting)mod).fromInteger(java.lang.Integer.parseInt(isOn));
								break;*/
							}
						}
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}