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
        File file;
        FileInputStream fstream;
        DataInputStream in;
        BufferedReader br;
        String line;
        String curLine;
        String configname;
        String isOn;
        String m;
        Setting mod;
        try {
            file = new File(config.getAbsolutePath(), "Value.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(category)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndModConfig(configname, mm);

                        if (mod instanceof Setting.Integer) {
                            ((Setting.Integer) mod).setValue(java.lang.Integer.parseInt(isOn));
                        } else if (mod instanceof Setting.Double){
                            ((Setting.Double) mod).setValue(java.lang.Double.parseDouble(isOn));
                        }
                    }
                }
            }
            br.close();
        } catch (Exception var13) {
            var13.printStackTrace();
        }
        try {
            file = new File(config.getAbsolutePath(), "Boolean.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));

            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(category)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.Boolean) mod).setValue(java.lang.Boolean.parseBoolean(isOn));
                    }
                }
            }
            br.close();
        } catch (Exception var12) {
            var12.printStackTrace();
        }
        try {
            file = new File(config.getAbsolutePath(), "String.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(category)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.Mode) mod).setValue(isOn);
                    }
                }
            }
            br.close();
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }
}