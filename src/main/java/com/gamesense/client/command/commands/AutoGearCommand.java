package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;

public class AutoGearCommand extends Command {

    final private String pathSave = "GameSense/Misc/AutoGear.json";

    private HashMap<String, String> errorMessage = new HashMap<String, String>() {
        {
            put("NoPar", "Not enough parameters");
            put("Exist", "This kit arleady exist");
            put("Saving", "Error saving the file");
            put("NoEx", "Kit not found");
        }
    };

    public AutoGearCommand() {
        super("AutoGear");

        setCommandSyntax(Command.getCommandPrefix() + "gear set/save [name]");

        setCommandAlias(new String[]{
                "gear", "gr", "kit"
        });

    }

    public void onCommand(String command, String[] message) throws Exception {

        switch (message[0].toLowerCase()) {
            case "":
            case "help":
                MessageBus.sendCommandMessage("AutoGear message is: gear set/save/del [name]", true);
                break;
            case "set":
                if (message.length == 2) {
                    set(message[1]);
                }else errorMessage("NoPar");
                break;
            case "save":
                if (message.length == 2) {
                    save(message[1]);
                }else errorMessage("NoPar");
                break;
            case "del":
                if (message.length == 2) {
                    delete(message[1]);
                }else errorMessage("NoPar");
                break;
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
                // Save
                saveFile(completeJson, name, "deleted");
            }else errorMessage("NoEx");

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
            }else errorMessage("NoEx");

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
            if (completeJson.get(name) != null) {
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
        for(ItemStack item : mc.player.inventory.mainInventory) {
            // Add everything
            jsonInventory.append(item.getItem().getRegistryName()).append(" ");
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
            PistonCrystal.printChat("Kit " + name + " " + operation, false);
        } catch (IOException e) {
            errorMessage("Saving");
        }
    }

    private void errorMessage(String e) {
        PistonCrystal.printChat("Error: " + errorMessage.get(e), true);
    }

}
