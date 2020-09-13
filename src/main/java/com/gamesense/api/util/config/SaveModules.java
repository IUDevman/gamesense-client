package com.gamesense.api.util.config;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;

// Made by Hoosiers on 08/02/20 for GameSense, some functions were modified and ported from the original ConfigUtils

public class SaveModules {

    //saves module settings for each category
    public void saveModules() {
        saveCategory(SaveConfiguration.Combat, Module.Category.Combat);
        saveCategory(SaveConfiguration.Exploits, Module.Category.Exploits);
        saveCategory(SaveConfiguration.Hud, Module.Category.HUD);
        saveCategory(SaveConfiguration.Misc, Module.Category.Misc);
        saveCategory(SaveConfiguration.Movement, Module.Category.Movement);
        saveCategory(SaveConfiguration.Render, Module.Category.Render);
    }

    // @Author Lukflug, removed excess code

    private void saveCategory(File config, Module.Category category) {
        saveSettings(config, category, "Value.json", Setting.Type.INT);
        saveSettings(config, category, "Boolean.json", Setting.Type.BOOLEAN);
        saveSettings(config, category, "String.json", Setting.Type.MODE);
        saveSettings(config, category, "Color.json", Setting.Type.COLOR);
    }

    private void saveSettings(File config, Module.Category category, String filename, Setting.Type type) {
        try {
            File file = new File(config.getAbsolutePath(), filename);
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator iter = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(category).iterator();
            while (iter.hasNext()) {
                Setting mod = (Setting) iter.next();
                if (mod.getType() == type || (type == Setting.Type.INT && mod.getType() == Setting.Type.DOUBLE)) {
                    switch (mod.getType()) {
                        case INT:
                            out.write(mod.getConfigName() + ":" + ((Setting.Integer) mod).getValue() + ":" + mod.getParent().getName() + "\r\n");
                            break;
                        case DOUBLE:
                            out.write(mod.getConfigName() + ":" + ((Setting.Double) mod).getValue() + ":" + mod.getParent().getName() + "\r\n");
                            break;
                        case BOOLEAN:
                            out.write(mod.getConfigName() + ":" + ((Setting.Boolean) mod).getValue() + ":" + mod.getParent().getName() + "\r\n");
                            break;
                        case MODE:
                            out.write(mod.getConfigName() + ":" + ((Setting.Mode) mod).getValue() + ":" + mod.getParent().getName() + "\r\n");
                            break;
                        case COLOR:
                            out.write(mod.getConfigName() + ":" + ((Setting.ColorSetting) mod).toInteger() + ":" + mod.getParent().getName() + "\r\n");
                            break;
                    }
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}