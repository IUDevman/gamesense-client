package com.gamesense.api.config;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.setting.values.*;
import com.gamesense.api.util.player.social.Enemy;
import com.gamesense.api.util.player.social.Friend;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.client.GameSense;
import com.gamesense.client.clickgui.GuiConfig;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.gamesense.client.module.modules.misc.AutoReply;
import com.gamesense.client.module.modules.misc.AutoRespawn;
import com.google.gson.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Hoosiers
 * @since 10/15/2020
 */

public class SaveConfig {

    public static final String fileName = "GameSense/";
    private static final String moduleName = "Modules/";
    private static final String mainName = "Main/";
    private static final String miscName = "Misc/";

    public static void init() {
        try {
            saveConfig();
            saveModules();
            saveEnabledModules();
            saveModuleKeybinds();
            saveDrawnModules();
            saveToggleMessagesModules();
            saveCommandPrefix();
            saveCustomFont();
            saveFriendsList();
            saveEnemiesList();
            saveClickGUIPositions();
            saveAutoGG();
            saveAutoReply();
            saveAutoRespawn();
        } catch (IOException e) {
            e.printStackTrace();
        }
        GameSense.LOGGER.info("Saved Config!");
    }

    private static void saveConfig() throws IOException {
        if (!Files.exists(Paths.get(fileName))) {
            Files.createDirectories(Paths.get(fileName));
        }
        if (!Files.exists(Paths.get(fileName + moduleName))) {
            Files.createDirectories(Paths.get(fileName + moduleName));
        }
        if (!Files.exists(Paths.get(fileName + mainName))) {
            Files.createDirectories(Paths.get(fileName + mainName));
        }
        if (!Files.exists(Paths.get(fileName + miscName))) {
            Files.createDirectories(Paths.get(fileName + miscName));
        }
    }

    private static void registerFiles(String location, String name) throws IOException {
        if (Files.exists(Paths.get(fileName + location + name + ".json"))) {
            File file = new File(fileName + location + name + ".json");

            file.delete();

        }
        Files.createFile(Paths.get(fileName + location + name + ".json"));
    }

    private static void saveModules() throws IOException {
        for (Module module : ModuleManager.getModules()) {
            try {
                saveModuleDirect(module);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveModuleDirect(Module module) throws IOException {
        registerFiles(moduleName, module.getName());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + moduleName + module.getName() + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject settingObject = new JsonObject();
        moduleObject.add("Module", new JsonPrimitive(module.getName()));

        for (Setting setting : SettingsManager.getSettingsForModule(module)) {
            if (setting instanceof BooleanSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((BooleanSetting) setting).getValue()));
            } else if (setting instanceof IntegerSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((IntegerSetting) setting).getValue()));
            } else if (setting instanceof DoubleSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((DoubleSetting) setting).getValue()));
            } else if (setting instanceof ColorSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((ColorSetting) setting).toInteger()));
            } else if (setting instanceof ModeSetting) {
                settingObject.add(setting.getConfigName(), new JsonPrimitive(((ModeSetting) setting).getValue()));
            }
        }
        moduleObject.add("Settings", settingObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveEnabledModules() throws IOException {

        registerFiles(mainName, "Toggle");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + mainName + "Toggle" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject enabledObject = new JsonObject();

        for (Module module : ModuleManager.getModules()) {

            enabledObject.add(module.getName(), new JsonPrimitive(module.isEnabled()));
        }
        moduleObject.add("Modules", enabledObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveModuleKeybinds() throws IOException {

        registerFiles(mainName, "Bind");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + mainName + "Bind" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject bindObject = new JsonObject();

        for (Module module : ModuleManager.getModules()) {

            bindObject.add(module.getName(), new JsonPrimitive(module.getBind()));
        }
        moduleObject.add("Modules", bindObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveDrawnModules() throws IOException {

        registerFiles(mainName, "Drawn");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + mainName + "Drawn" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject drawnObject = new JsonObject();

        for (Module module : ModuleManager.getModules()) {

            drawnObject.add(module.getName(), new JsonPrimitive(module.isDrawn()));
        }
        moduleObject.add("Modules", drawnObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveToggleMessagesModules() throws IOException {

        registerFiles(mainName, "ToggleMessages");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + mainName + "ToggleMessages" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject toggleMessagesObject = new JsonObject();

        for (Module module : ModuleManager.getModules()) {

            toggleMessagesObject.add(module.getName(), new JsonPrimitive(module.isToggleMsg()));
        }
        moduleObject.add("Modules", toggleMessagesObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveCommandPrefix() throws IOException {

        registerFiles(mainName, "CommandPrefix");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + mainName + "CommandPrefix" + ".json"), StandardCharsets.UTF_8);
        JsonObject prefixObject = new JsonObject();

        prefixObject.add("Prefix", new JsonPrimitive(CommandManager.getCommandPrefix()));
        String jsonString = gson.toJson(new JsonParser().parse(prefixObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveCustomFont() throws IOException {

        registerFiles(miscName, "CustomFont");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + miscName + "CustomFont" + ".json"), StandardCharsets.UTF_8);
        JsonObject fontObject = new JsonObject();

        fontObject.add("Font Name", new JsonPrimitive(GameSense.INSTANCE.cFontRenderer.getFontName()));
        fontObject.add("Font Size", new JsonPrimitive(GameSense.INSTANCE.cFontRenderer.getFontSize()));
        String jsonString = gson.toJson(new JsonParser().parse(fontObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveFriendsList() throws IOException {

        registerFiles(miscName, "Friends");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + miscName + "Friends" + ".json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();
        JsonArray friendArray = new JsonArray();

        for (Friend friend : SocialManager.getFriends()) {
            friendArray.add(friend.getName());
        }
        mainObject.add("Friends", friendArray);
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveEnemiesList() throws IOException {

        registerFiles(miscName, "Enemies");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + miscName + "Enemies" + ".json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();
        JsonArray enemyArray = new JsonArray();

        for (Enemy enemy : SocialManager.getEnemies()) {
            enemyArray.add(enemy.getName());
        }
        mainObject.add("Enemies", enemyArray);
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveClickGUIPositions() throws IOException {
        registerFiles(mainName, "ClickGUI");
        GameSense.INSTANCE.gameSenseGUI.gui.saveConfig(new GuiConfig(fileName + mainName));
    }

    private static void saveAutoGG() throws IOException {

        registerFiles(miscName, "AutoGG");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + miscName + "AutoGG" + ".json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();
        JsonArray messageArray = new JsonArray();

        for (String autoGG : AutoGG.getAutoGgMessages()) {
            messageArray.add(autoGG);
        }
        mainObject.add("Messages", messageArray);
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveAutoReply() throws IOException {

        registerFiles(miscName, "AutoReply");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + miscName + "AutoReply" + ".json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();
        JsonObject messageObject = new JsonObject();

        messageObject.add("Message", new JsonPrimitive(AutoReply.getReply()));
        mainObject.add("AutoReply", messageObject);
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveAutoRespawn() throws IOException {

        registerFiles(miscName, "AutoRespawn");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName + miscName + "AutoRespawn" + ".json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();

        mainObject.add("Message", new JsonPrimitive(AutoRespawn.getAutoRespawnMessages()));
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
}