package com.gamesense.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @Author Hoosiers on 10/15/2020
 */

public class LoadConfig {

    public LoadConfig(){
        try {
            loadConfig();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    String fileName = "GameSense3/";
    String moduleName = "Modules/";
    String mainName = "Main/";
    String miscName = "Misc/";

    public void loadConfig() throws IOException {
    	loadModules();
        loadEnabledModules();
    }

    public void loadModules() throws IOException {
        String moduleLocation = fileName + moduleName;

        for (Module module : ModuleManager.getModules()){
            if (!Files.exists(Paths.get(moduleLocation + module.getName() + ".json"))){
                return;
            }

            InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + module.getName() + ".json"));
            JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

            if (moduleObject.get("Module") == null){
                return;
            }

            JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();
            for (Setting setting : GameSenseMod.getInstance().settingsManager.getSettingsForMod(module)){
            	JsonElement dataObject=settingObject.get(setting.getConfigName());
            	
                if (dataObject!=null && dataObject.isJsonPrimitive()) {

                    //JsonObject dataObject = configObject.getAsJsonObject().get(setting.getType().toString()).getAsJsonObject();

                    switch (setting.getType()){
                        case BOOLEAN:
                            ((Setting.Boolean) setting).setValue(dataObject.getAsBoolean());
                            break;
                        case INT:
                            ((Setting.Integer) setting).setValue(dataObject.getAsInt());
                            break;
                        case DOUBLE:
                            ((Setting.Double) setting).setValue(dataObject.getAsDouble());
                            break;
                        case COLOR:
                            ((Setting.ColorSetting) setting).fromInteger(dataObject.getAsInt());
                            break;
                        case MODE:
                            ((Setting.Mode) setting).setValue(dataObject.getAsString());
                            break;
                    }
                }
            }
            inputStream.close();
        }
    }

    public void loadEnabledModules() throws IOException {
        String enabledLocation = fileName + mainName;

        if (!Files.exists(Paths.get(enabledLocation + "Toggle" + ".json"))){
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(enabledLocation + "Toggle" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null){
            return;
        }

        JsonObject enabledObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.getModules()){
            if (module.getName() == enabledObject.toString()){
                module.setEnabled(enabledObject.getAsBoolean());
            }
        }
        inputStream.close();
    }
}