package com.gamesense.api.util.config;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;

public class SaveModules {

    //saves module settings for each category
    public void saveModules(){
        saveCombat();
        saveExploits();
        saveHud();
        saveMisc();
        saveMovement();
        saveRender();
    }

    //saves combat-related modules
    public void saveCombat(){
        File file;
        BufferedWriter out;
        Iterator var3;
        Setting i;
        try {
            file = new File(SaveConfiguration.Combat.getAbsolutePath(), "Value.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Combat).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.DOUBLE) {
                    out.write(i.getConfigName() + ":" +((Setting.d) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
                if (i.getType() == Setting.Type.INT) {
                    out.write(i.getConfigName() + ":" +((Setting.i) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var7) {
        }
        try {
            file = new File(SaveConfiguration.Combat.getAbsolutePath(), "Boolean.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Combat).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.BOOLEAN) {
                    out.write(i.getConfigName() + ":" + ((Setting.b) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var6) {
        }
        try {
            file = new File(SaveConfiguration.Combat.getAbsolutePath(), "String.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Combat).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.MODE) {
                    out.write(i.getConfigName() + ":" + ((Setting.mode) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var5) {
        }
    }

    //saves exploit-related modules
    public void saveExploits(){
        File file;
        BufferedWriter out;
        Iterator var3;
        Setting i;
        try {
            file = new File(SaveConfiguration.Exploits.getAbsolutePath(), "Value.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Exploits).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.DOUBLE) {
                    out.write(i.getConfigName() + ":" +((Setting.d) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
                if (i.getType() == Setting.Type.INT) {
                    out.write(i.getConfigName() + ":" +((Setting.i) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var7) {
        }
        try {
            file = new File(SaveConfiguration.Exploits.getAbsolutePath(), "Boolean.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Exploits).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.BOOLEAN) {
                    out.write(i.getConfigName() + ":" + ((Setting.b) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var6) {
        }
        try {
            file = new File(SaveConfiguration.Exploits.getAbsolutePath(), "String.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Exploits).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.MODE) {
                    out.write(i.getConfigName() + ":" + ((Setting.mode) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var5) {
        }
    }

    //saves hud-related modules
    public void saveHud(){
        File file;
        BufferedWriter out;
        Iterator var3;
        Setting i;
        try {
            file = new File(SaveConfiguration.Hud.getAbsolutePath(), "Value.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.HUD).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.DOUBLE) {
                    out.write(i.getConfigName() + ":" +((Setting.d) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
                if (i.getType() == Setting.Type.INT) {
                    out.write(i.getConfigName() + ":" +((Setting.i) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var7) {
        }
        try {
            file = new File(SaveConfiguration.Hud.getAbsolutePath(), "Boolean.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.HUD).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.BOOLEAN) {
                    out.write(i.getConfigName() + ":" + ((Setting.b) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var6) {
        }
        try {
            file = new File(SaveConfiguration.Hud.getAbsolutePath(), "String.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.HUD).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.MODE) {
                    out.write(i.getConfigName() + ":" + ((Setting.mode) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var5) {
        }
    }

    //saves misc-related modules
    public void saveMisc(){
        File file;
        BufferedWriter out;
        Iterator var3;
        Setting i;
        try {
            file = new File(SaveConfiguration.Misc.getAbsolutePath(), "Value.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Misc).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.DOUBLE) {
                    out.write(i.getConfigName() + ":" +((Setting.d) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
                if (i.getType() == Setting.Type.INT) {
                    out.write(i.getConfigName() + ":" +((Setting.i) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var7) {
        }
        try {
            file = new File(SaveConfiguration.Misc.getAbsolutePath(), "Boolean.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Misc).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.BOOLEAN) {
                    out.write(i.getConfigName() + ":" + ((Setting.b) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var6) {
        }
        try {
            file = new File(SaveConfiguration.Misc.getAbsolutePath(), "String.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Misc).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.MODE) {
                    out.write(i.getConfigName() + ":" + ((Setting.mode) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var5) {
        }
    }

    //saves movement-related modules
    public void saveMovement(){
        File file;
        BufferedWriter out;
        Iterator var3;
        Setting i;
        try {
            file = new File(SaveConfiguration.Movement.getAbsolutePath(), "Value.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Movement).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.DOUBLE) {
                    out.write(i.getConfigName() + ":" +((Setting.d) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
                if (i.getType() == Setting.Type.INT) {
                    out.write(i.getConfigName() + ":" +((Setting.i) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var7) {
        }
        try {
            file = new File(SaveConfiguration.Movement.getAbsolutePath(), "Boolean.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Movement).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.BOOLEAN) {
                    out.write(i.getConfigName() + ":" + ((Setting.b) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var6) {
        }
        try {
            file = new File(SaveConfiguration.Movement.getAbsolutePath(), "String.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Movement).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.MODE) {
                    out.write(i.getConfigName() + ":" + ((Setting.mode) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var5) {
        }
    }

    //saves render-related modules
    public void saveRender(){
        File file;
        BufferedWriter out;
        Iterator var3;
        Setting i;
        try {
            file = new File(SaveConfiguration.Render.getAbsolutePath(), "Value.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Render).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.DOUBLE) {
                    out.write(i.getConfigName() + ":" +((Setting.d) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
                if (i.getType() == Setting.Type.INT) {
                    out.write(i.getConfigName() + ":" +((Setting.i) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var7) {
        }
        try {
            file = new File(SaveConfiguration.Render.getAbsolutePath(), "Boolean.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Render).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.BOOLEAN) {
                    out.write(i.getConfigName() + ":" + ((Setting.b) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var6) {
        }
        try {
            file = new File(SaveConfiguration.Render.getAbsolutePath(), "String.json");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettingsByCategory(Module.Category.Render).iterator();
            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.MODE) {
                    out.write(i.getConfigName() + ":" + ((Setting.mode) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var5) {
        }
    }
}