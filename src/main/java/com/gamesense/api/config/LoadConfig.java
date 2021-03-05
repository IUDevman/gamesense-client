package com.gamesense.api.config;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.setting.values.*;
import com.gamesense.api.util.font.CFontRenderer;
import com.gamesense.api.util.player.enemy.Enemies;
import com.gamesense.api.util.player.friend.Friends;
import com.gamesense.client.GameSense;
import com.gamesense.client.clickgui.GuiConfig;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.gamesense.client.module.modules.misc.AutoReply;
import com.gamesense.client.module.modules.misc.AutoRespawn;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Hoosiers
 * @since 10/15/2020
 */

public class LoadConfig {

    public LoadConfig() {
        try {
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String fileName = "GameSense/";
    String moduleName = "Modules/";
    String mainName = "Main/";
    String miscName = "Misc/";

    public void loadConfig() throws IOException {
        loadModules();
        loadEnabledModules();
        loadModuleKeybinds();
        loadDrawnModules();
        loadCommandPrefix();
        loadCustomFont();
        loadFriendsList();
        loadEnemiesList();
        loadClickGUIPositions();
        loadAutoGG();
        loadAutoReply();
        loadAutoRespawn();
    }

    //big shoutout to lukflug for helping/fixing this
    public void loadModules() {
        String moduleLocation = fileName + moduleName;

        for (Module module : ModuleManager.getModules()) {
            try {
                loadModuleDirect(moduleLocation, module);
            } catch (IOException e) {
                System.out.println(module.getName());
                e.printStackTrace();
            }
        }
    }

    public void loadModuleDirect(String moduleLocation, Module module) throws IOException {
        if (!Files.exists(Paths.get(moduleLocation + module.getName() + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + module.getName() + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Module") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();
        for (Setting setting : SettingsManager.getSettingsForModule(module)) {
            JsonElement dataObject = settingObject.get(setting.getConfigName());
            try {
                if (dataObject != null && dataObject.isJsonPrimitive()) {
                    if (setting instanceof BooleanSetting) {
                        setting.setValue(dataObject.getAsBoolean());
                    } else if (setting instanceof IntegerSetting) {
                        setting.setValue(dataObject.getAsInt());
                    } else if (setting instanceof DoubleSetting) {
                        setting.setValue(dataObject.getAsDouble());
                    } else if (setting instanceof ColorSetting) {
                        ((ColorSetting) setting).fromInteger(dataObject.getAsInt());
                    } else if (setting instanceof ModeSetting) {
                        setting.setValue(dataObject.getAsString());
                    }
                }
            } catch (java.lang.NumberFormatException e) {
                System.out.println(setting.getConfigName() + " " + module.getName());
                System.out.println(dataObject);
            }
        }
        inputStream.close();
    }

    public void loadEnabledModules() throws IOException {
        String enabledLocation = fileName + mainName;

        if (!Files.exists(Paths.get(enabledLocation + "Toggle" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(enabledLocation + "Toggle" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.getModules()) {
            JsonElement dataObject = settingObject.get(module.getName());

            if (dataObject != null && dataObject.isJsonPrimitive()) {
                if (dataObject.getAsBoolean()) {
                    try {
                        module.enable();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        inputStream.close();
    }

    public void loadModuleKeybinds() throws IOException {
        String bindLocation = fileName + mainName;

        if (!Files.exists(Paths.get(bindLocation + "Bind" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(bindLocation + "Bind" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.getModules()) {
            JsonElement dataObject = settingObject.get(module.getName());

            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setBind(dataObject.getAsInt());
            }
        }
        inputStream.close();
    }

    public void loadDrawnModules() throws IOException {
        String drawnLocation = fileName + mainName;

        if (!Files.exists(Paths.get(drawnLocation + "Drawn" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(drawnLocation + "Drawn" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null) {
            return;
        }

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : ModuleManager.getModules()) {
            JsonElement dataObject = settingObject.get(module.getName());

            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setDrawn(dataObject.getAsBoolean());
            }
        }
        inputStream.close();
    }

    public void loadCommandPrefix() throws IOException {
        String prefixLocation = fileName + mainName;

        if (!Files.exists(Paths.get(prefixLocation + "CommandPrefix" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(prefixLocation + "CommandPrefix" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Prefix") == null) {
            return;
        }

        JsonElement prefixObject = mainObject.get("Prefix");

        if (prefixObject != null && prefixObject.isJsonPrimitive()) {
            CommandManager.setCommandPrefix(prefixObject.getAsString());
        }
        inputStream.close();
    }

    public void loadCustomFont() throws IOException {
        String fontLocation = fileName + miscName;

        if (!Files.exists(Paths.get(fontLocation + "CustomFont" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(fontLocation + "CustomFont" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Font Name") == null || mainObject.get("Font Size") == null) {
            return;
        }

        JsonElement fontNameObject = mainObject.get("Font Name");

        String name = null;

        if (fontNameObject != null && fontNameObject.isJsonPrimitive()) {
            name = fontNameObject.getAsString();
        }

        JsonElement fontSizeObject = mainObject.get("Font Size");

        int size = -1;

        if (fontSizeObject != null && fontSizeObject.isJsonPrimitive()) {
            size = fontSizeObject.getAsInt();
        }

        if (name != null && size != -1) {
            GameSense.getInstance().cFontRenderer = new CFontRenderer(new Font(name, Font.PLAIN, size), true, true);
            GameSense.getInstance().cFontRenderer.setFont(new Font(name, Font.PLAIN, size));
            GameSense.getInstance().cFontRenderer.setAntiAlias(true);
            GameSense.getInstance().cFontRenderer.setFractionalMetrics(true);
            GameSense.getInstance().cFontRenderer.setFontName(name);
            GameSense.getInstance().cFontRenderer.setFontSize(size);
        }
        inputStream.close();
    }

    public void loadFriendsList() throws IOException {
        String friendLocation = fileName + miscName;

        if (!Files.exists(Paths.get(friendLocation + "Friends" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(friendLocation + "Friends" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Friends") == null) {
            return;
        }

        JsonArray friendObject = mainObject.get("Friends").getAsJsonArray();

        friendObject.forEach(object -> Friends.addFriend(object.getAsString()));
        inputStream.close();
    }

    public void loadEnemiesList() throws IOException {
        String enemyLocation = fileName + miscName;

        if (!Files.exists(Paths.get(enemyLocation + "Enemies" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(enemyLocation + "Enemies" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Enemies") == null) {
            return;
        }

        JsonArray enemyObject = mainObject.get("Enemies").getAsJsonArray();

        enemyObject.forEach(object -> Enemies.addEnemy(object.getAsString()));
        inputStream.close();
    }

    public void loadClickGUIPositions() {
        GameSense.getInstance().gameSenseGUI.gui.loadConfig(new GuiConfig(fileName + mainName));
    }

    public void loadAutoGG() throws IOException {
        String fileLocation = fileName + miscName;

        if (!Files.exists(Paths.get(fileLocation + "AutoGG" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoGG" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Messages") == null) {
            return;
        }

        JsonArray messageObject = mainObject.get("Messages").getAsJsonArray();

        messageObject.forEach(object -> AutoGG.addAutoGgMessage(object.getAsString()));
        inputStream.close();
    }

    public void loadAutoReply() throws IOException {
        String fileLocation = fileName + miscName;

        if (!Files.exists(Paths.get(fileLocation + "AutoReply" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoReply" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("AutoReply") == null) {
            return;
        }

        JsonObject arObject = mainObject.get("AutoReply").getAsJsonObject();
        JsonElement dataObject = arObject.get("Message");
        if (dataObject != null && dataObject.isJsonPrimitive()) {
            AutoReply.setReply(dataObject.getAsString());
        }
        inputStream.close();
    }

    public void loadAutoRespawn() throws IOException {
        String fileLocation = fileName + miscName;

        if (!Files.exists(Paths.get(fileLocation + "AutoRespawn" + ".json"))) {
            return;
        }

        InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoRespawn" + ".json"));
        JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Message") == null) {
            return;
        }

        JsonElement dataObject = mainObject.get("Message");
        if (dataObject != null && dataObject.isJsonPrimitive()) {
            AutoRespawn.setAutoRespawnMessage(dataObject.getAsString());
        }

        inputStream.close();
    }
}