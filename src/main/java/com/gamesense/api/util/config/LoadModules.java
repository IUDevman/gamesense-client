package com.gamesense.api.util.config;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

import java.io.*;

public class LoadModules {

    //loads all functions for modules
    public LoadModules(){
        loadCombat();
        loadExploits();
        loadHud();
        loadMisc();
        loadMovement();
        loadRender();
    }

    //loads combat-related configs
    public void loadCombat(){
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
            file = new File(SaveConfiguration.Combat.getAbsolutePath(), "Value.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Combat)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndModConfig(configname, mm);

                        if (mod instanceof Setting.i) {
                            ((Setting.i) mod).setValue(Integer.parseInt(isOn));
                        } else if (mod instanceof Setting.d){
                            ((Setting.d) mod).setValue(Double.parseDouble(isOn));
                        }
                    }
                }
            }
            br.close();
        } catch (Exception var13) {
            var13.printStackTrace();
        }
        try {
            file = new File(SaveConfiguration.Combat.getAbsolutePath(), "Boolean.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));

            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Combat)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.b) mod).setValue(Boolean.parseBoolean(isOn));
                    }
                }
            }
            br.close();
        } catch (Exception var12) {
            var12.printStackTrace();
        }
        try {
            file = new File(SaveConfiguration.Combat.getAbsolutePath(), "String.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Combat)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.mode) mod).setValue(isOn);
                    }
                }
            }
            br.close();
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    //loads exploit-related configs
    public void loadExploits(){
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
            file = new File(SaveConfiguration.Exploits.getAbsolutePath(), "Value.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Exploits)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndModConfig(configname, mm);

                        if (mod instanceof Setting.i) {
                            ((Setting.i) mod).setValue(Integer.parseInt(isOn));
                        } else if (mod instanceof Setting.d){
                            ((Setting.d) mod).setValue(Double.parseDouble(isOn));
                        }
                    }
                }
            }
            br.close();
        } catch (Exception var13) {
            var13.printStackTrace();
        }
        try {
            file = new File(SaveConfiguration.Exploits.getAbsolutePath(), "Boolean.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));

            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Exploits)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.b) mod).setValue(Boolean.parseBoolean(isOn));
                    }
                }
            }
            br.close();
        } catch (Exception var12) {
            var12.printStackTrace();
        }
        try {
            file = new File(SaveConfiguration.Exploits.getAbsolutePath(), "String.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Exploits)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.mode) mod).setValue(isOn);
                    }
                }
            }
            br.close();
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    //loads hud-related configs
    public void loadHud(){
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
            file = new File(SaveConfiguration.Hud.getAbsolutePath(), "Value.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.HUD)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndModConfig(configname, mm);

                        if (mod instanceof Setting.i) {
                            ((Setting.i) mod).setValue(Integer.parseInt(isOn));
                        } else if (mod instanceof Setting.d){
                            ((Setting.d) mod).setValue(Double.parseDouble(isOn));
                        }
                    }
                }
            }
            br.close();
        } catch (Exception var13) {
            var13.printStackTrace();
        }
        try {
            file = new File(SaveConfiguration.Hud.getAbsolutePath(), "Boolean.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));

            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.HUD)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.b) mod).setValue(Boolean.parseBoolean(isOn));
                    }
                }
            }
            br.close();
        } catch (Exception var12) {
            var12.printStackTrace();
        }
        try {
            file = new File(SaveConfiguration.Hud.getAbsolutePath(), "String.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.HUD)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.mode) mod).setValue(isOn);
                    }
                }
            }
            br.close();
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    //loads misc-related configs
    public void loadMisc(){
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
            file = new File(SaveConfiguration.Misc.getAbsolutePath(), "Value.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Misc)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndModConfig(configname, mm);

                        if (mod instanceof Setting.i) {
                            ((Setting.i) mod).setValue(Integer.parseInt(isOn));
                        } else if (mod instanceof Setting.d){
                            ((Setting.d) mod).setValue(Double.parseDouble(isOn));
                        }
                    }
                }
            }
            br.close();
        } catch (Exception var13) {
            var13.printStackTrace();
        }
        try {
            file = new File(SaveConfiguration.Misc.getAbsolutePath(), "Boolean.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));

            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Misc)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.b) mod).setValue(Boolean.parseBoolean(isOn));
                    }
                }
            }
            br.close();
        } catch (Exception var12) {
            var12.printStackTrace();
        }
        try {
            file = new File(SaveConfiguration.Misc.getAbsolutePath(), "String.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Misc)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.mode) mod).setValue(isOn);
                    }
                }
            }
            br.close();
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    //loads movement-related configs
    public void loadMovement(){
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
            file = new File(SaveConfiguration.Movement.getAbsolutePath(), "Value.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Movement)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndModConfig(configname, mm);

                        if (mod instanceof Setting.i) {
                            ((Setting.i) mod).setValue(Integer.parseInt(isOn));
                        } else if (mod instanceof Setting.d){
                            ((Setting.d) mod).setValue(Double.parseDouble(isOn));
                        }
                    }
                }
            }
            br.close();
        } catch (Exception var13) {
            var13.printStackTrace();
        }
        try {
            file = new File(SaveConfiguration.Movement.getAbsolutePath(), "Boolean.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));

            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Movement)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.b) mod).setValue(Boolean.parseBoolean(isOn));
                    }
                }
            }
            br.close();
        } catch (Exception var12) {
            var12.printStackTrace();
        }
        try {
            file = new File(SaveConfiguration.Movement.getAbsolutePath(), "String.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Movement)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.mode) mod).setValue(isOn);
                    }
                }
            }
            br.close();
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    //loads render-related configs
    public void loadRender(){
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
            file = new File(SaveConfiguration.Render.getAbsolutePath(), "Value.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Render)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndModConfig(configname, mm);

                        if (mod instanceof Setting.i) {
                            ((Setting.i) mod).setValue(Integer.parseInt(isOn));
                        } else if (mod instanceof Setting.d){
                            ((Setting.d) mod).setValue(Double.parseDouble(isOn));
                        }
                    }
                }
            }
            br.close();
        } catch (Exception var13) {
            var13.printStackTrace();
        }
        try {
            file = new File(SaveConfiguration.Render.getAbsolutePath(), "Boolean.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));

            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Render)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.b) mod).setValue(Boolean.parseBoolean(isOn));
                    }
                }
            }
            br.close();
        } catch (Exception var12) {
            var12.printStackTrace();
        }
        try {
            file = new File(SaveConfiguration.Render.getAbsolutePath(), "String.json");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            while((line = br.readLine()) != null) {
                curLine = line.trim();
                configname = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModulesInCategory(Module.Category.Render)) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(configname, mm);
                        ((Setting.mode) mod).setValue(isOn);
                    }
                }
            }
            br.close();
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }
}
