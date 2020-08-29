package com.gamesense.api.util.config;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;

/**
 * Made by Hoosiers on 08/02/20 for GameSense, some functions were modified and ported from the original ConfigUtils
 */

public class SaveModules {

    //saves module settings for each category
    public void saveModules() {
        saveCategory (SaveConfiguration.Combat,Module.Category.Combat);
        saveCategory (SaveConfiguration.Exploits,Module.Category.Exploits);
        saveCategory (SaveConfiguration.Hud,Module.Category.HUD);
        saveCategory (SaveConfiguration.Misc,Module.Category.Misc);
        saveCategory (SaveConfiguration.Movement,Module.Category.Movement);
        saveCategory (SaveConfiguration.Render,Module.Category.Render);
    }

    // Duplicate code reduced by lukflug
    private void saveCategory (File config, Module.Category category) {
        File file;
        BufferedWriter out;
        Iterator var3;
        Setting i;
        try {
            file = new File(config.getAbsolutePath(), "Value.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(category).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.DOUBLE) {
                    out.write(i.getConfigName() + ":" +((Setting.Double) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
                if (i.getType() == Setting.Type.INT) {
                    out.write(i.getConfigName() + ":" +((Setting.Integer) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var7) {
        }
        try {
            file = new File(config.getAbsolutePath(), "Boolean.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(category).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.BOOLEAN) {
                    out.write(i.getConfigName() + ":" + ((Setting.Boolean) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var6) {
        }
        try {
            file = new File(config.getAbsolutePath(), "String.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(category).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.MODE) {
                    out.write(i.getConfigName() + ":" + ((Setting.Mode) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var5) {
        }
    }
}