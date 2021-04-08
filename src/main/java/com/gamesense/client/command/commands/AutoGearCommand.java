package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.ItemStack;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

@Command.Declaration(name = "AutoGear", syntax = "gear set/save/del/list [name]", alias = {"gear", "gr", "kit"})
public class AutoGearCommand extends Command {

    final static private String pathSave = "GameSense/Misc/AutoGear.json";

    private static final HashMap<String, String> errorMessage = new HashMap<String, String>() {
        {
            put("NoPar", "Not enough parameters");
            put("Exist", "This kit arleady exist");
            put("Saving", "Error saving the file");
            put("NoEx", "Kit not found");
        }
    };

    public void onCommand(String command, String[] message) {

        switch (message[0].toLowerCase()) {
            case "list":

                if (message.length == 1) {
                    listMessage();
                } else errorMessage("NoPar");
                break;
            case "set":
                if (message.length == 2) {
                    set(message[1]);
                } else errorMessage("NoPar");
                break;
            case "save":
            case "add":
            case "create":
                if (message.length == 2) {
                    save(message[1]);
                } else errorMessage("NoPar");
                break;
            case "del":
                if (message.length == 2) {
                    delete(message[1]);
                } else errorMessage("NoPar");
                break;
            case "":
            case "help":
            default:
                MessageBus.sendCommandMessage("AutoGear message is: gear set/save/del/list [name]", true);
                break;
        }
    }

    private void listMessage() {
        JsonObject completeJson = new JsonObject();
        try {
            // Read json
            completeJson = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject();
            int lenghtJson = completeJson.entrySet().size();
            for (int i = 0; i < lenghtJson; i++) {
                String item = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject().entrySet().toArray()[i].toString().split("=")[0];
                if (!item.equals("pointer"))
                    PistonCrystal.printDebug("Kit avaible: " + item, false);
            }

        } catch (IOException e) {
            // Case not found, reset
            errorMessage("NoEx");
        }
    }

    private void delete(String name) {
        JsonObject completeJson = new JsonObject();
        try {
            // Read json
            completeJson = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                // Delete
                completeJson.remove(name);
                // Check if it's setter
                if (completeJson.get("pointer").getAsString().equals(name))
                    completeJson.addProperty("pointer", "none");
                // Save
                saveFile(completeJson, name, "deleted");
            } else errorMessage("NoEx");

        } catch (IOException e) {
            // Case not found, reset
            errorMessage("NoEx");
        }
    }

    private void set(String name) {
        JsonObject completeJson = new JsonObject();
        try {
            // Read json
            completeJson = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                // Change the value
                completeJson.addProperty("pointer", name);
                // Save
                saveFile(completeJson, name, "selected");
            } else errorMessage("NoEx");

        } catch (IOException e) {
            // Case not found, reset
            errorMessage("NoEx");
        }
    }

    private void save(String name) {
        JsonObject completeJson = new JsonObject();
        try {
            // Read json
            completeJson = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                errorMessage("Exist");
                return;
            }
            // We can continue

        } catch (IOException e) {
            // Case not found, reset
            completeJson.addProperty("pointer", "none");
        }

        // String that is going to be our inventory
        StringBuilder jsonInventory = new StringBuilder();
        for (ItemStack item : mc.player.inventory.mainInventory) {
            // Add everything
            jsonInventory.append(item.getItem().getRegistryName().toString() + item.getMetadata()).append(" ");
        }
        // Add to the json
        completeJson.addProperty(name, jsonInventory.toString());
        // Save json
        saveFile(completeJson, name, "saved");
    }

    private void saveFile(JsonObject completeJson, String name, String operation) {
        // Save the json
        try {
            // Open
            BufferedWriter bw = new BufferedWriter(new FileWriter(pathSave));
            // Write
            bw.write(completeJson.toString());
            // Save
            bw.close();
            // Chat message
            PistonCrystal.printDebug("Kit " + name + " " + operation, false);
        } catch (IOException e) {
            errorMessage("Saving");
        }
    }

    private static void errorMessage(String e) {
        PistonCrystal.printDebug("Error: " + errorMessage.get(e), true);
    }

    public static String getCurrentSet() {

        JsonObject completeJson = new JsonObject();
        try {
            // Read json
            completeJson = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject();
            if (!completeJson.get("pointer").getAsString().equals("none"))
                return completeJson.get("pointer").getAsString();


        } catch (IOException e) {
            // Case not found, reset
        }
        errorMessage("NoEx");
        return "";
    }

    public static String getInventoryKit(String kit) {
        JsonObject completeJson = new JsonObject();
        try {
            // Read json
            completeJson = new JsonParser().parse(new FileReader(pathSave)).getAsJsonObject();
            return completeJson.get(kit).getAsString();


        } catch (IOException e) {
            // Case not found, reset
        }
        errorMessage("NoEx");
        return "";
    }
}