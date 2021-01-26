package com.gamesense.client.module.modules.misc;
/*
    TODO: Read perfectly your inventory
    TODO: Create a json
    TODO: Read the json
    TODO: Create line-config
    TODO: Read perfectly a shulker/chest
    TODO: Take chest/shulker items to inventory
    TODO: Sort inventory
 */
import com.gamesense.api.setting.Setting;
import com.gamesense.client.command.commands.AutoGearCommand;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AutoGear extends Module {

    public AutoGear() {
        super("AutoGear", Category.Misc);
    }

    Setting.Boolean chatMsg;
    Setting.Integer tickDelay;

    // Config variables
    private String curConfigName;
    private String inventoryConfig;
    // Our inventory variables
    private HashMap<Integer, String> planInventory = new HashMap<>();
    private ArrayList<Integer> emptySlots = new ArrayList<>();
    private HashMap<String, Integer> nItems = new HashMap<>();
    // Sort item
    private ArrayList<ArrayList<Integer>> sortItems = new ArrayList<>();

    // Tickets
    private int delayTimeTicks;
    // If we had opened before a chest/inventory
    private boolean openedBefore;

    @Override
    public void setup() {
        chatMsg = registerBoolean("Chat Msg", true);
        tickDelay = registerInteger("Tick Delay", 0, 0, 20);
    }

    @Override
    public void onEnable() {
        // Msg
        if (chatMsg.getValue())
            PistonCrystal.printChat("AutoGear Turned On!", false);
        // Get name of the config
        curConfigName = AutoGearCommand.getCurrentSet();
        if (curConfigName.equals("")) {
            disable();
            return;
        }
        if (chatMsg.getValue())
            PistonCrystal.printChat("Config " + curConfigName + " actived", false);
        // Get everything
        inventoryConfig = AutoGearCommand.getInventoryKit(curConfigName);
        if (inventoryConfig.equals("")) {
            disable();
            return;
        }
        // Split it into array
        String[] inventoryDivided = inventoryConfig.split(" ");
        // Reset variables
        planInventory = new HashMap<>();
        nItems = new HashMap<>();
        emptySlots = new ArrayList<>();
        // Iterate
        for(int i = 0; i < inventoryDivided.length; i++) {
            // Add to planInventory if it's not air
            if (!inventoryDivided[i].contains("air")) {
                // Add it
                planInventory.put(i, inventoryDivided[i]);
                // Lets add it to our list
                if (nItems.containsKey(inventoryDivided[i]))
                    // If it exist, incr of 1
                    nItems.put(inventoryDivided[i], nItems.get(inventoryDivided[i]) + 1);
                else
                    // If it doesnt exist, add it with value 1
                    nItems.put(inventoryDivided[i], 1);
            }
            else
                // It's air, add it
                emptySlots.add(i);
        }
        // Reset tickdelay
        delayTimeTicks = 0;
        // Reset opened
        openedBefore = false;
    }

    @Override
    public void onDisable() {
        if (chatMsg.getValue())
            PistonCrystal.printChat("AutoGear Turned Off!", true);
    }

    @Override
    public void onUpdate() {
        if (delayTimeTicks < tickDelay.getValue()) {
            delayTimeTicks++;
            return;
        }
        else {
            delayTimeTicks = 0;
        }
        if (planInventory.size() == 0)
            disable();
        // Check if your inventory is open
        if ( mc.currentScreen instanceof GuiInventory) {
            // Get what's missing in your inventory and which slots are not usefull
            if (!openedBefore) {
                PistonCrystal.printChat("a", false);
                sortItems = getInventorySort();
                openedBefore = true;
            }
        }else
        // Check if a shulker / check is open
        if (mc.player.openContainer instanceof ContainerChest || mc.player.openContainer instanceof ContainerShulkerBox) {
            if (!openedBefore) {
                int maxValue =  mc.player.openContainer instanceof ContainerChest ? ((ContainerChest) mc.player.openContainer).getLowerChestInventory().getSizeInventory()
                                : 27;
                // Iterate for every value
                for(int i = 0; i < maxValue; i++) {
                    // TODO: make the shulker thing. first make the sort inventory
                }
                openedBefore = true;
            }
        }else openedBefore = false;

    }

    private ArrayList<ArrayList<Integer>> getInventorySort() {
        ArrayList<ArrayList<Integer>> tempToSort = new ArrayList<>();

        // Lets copy planInventory
        HashMap<Integer, String> planInventoryCopy = (HashMap<Integer, String>) planInventory.clone();
        // Lets copy nItems
        HashMap<String, Integer> nItemsCopy = (HashMap<String, Integer>) nItems.clone();
        // Ignore values for after
        ArrayList<Integer> ignoreValues = new ArrayList<>();
        int value;
        // Iterate and check if we are ok for certain items
        for(int i = 0; i < planInventory.size(); i++) {
            value = (Integer) planInventory.keySet().toArray()[i];
            // If the item we are looking is arleady fine
            if (Objects.requireNonNull(mc.player.inventory.getStackInSlot(value).getItem().getRegistryName()).toString().equals(planInventoryCopy.get(value))) {
                // Add a value to ignore later
                ignoreValues.add(value);
                // Update the value in nItemsCopy
                nItemsCopy.put(planInventoryCopy.get(value), nItemsCopy.get(planInventoryCopy.get(value)) - 1);
                // If it's == 0, just remove it
                if (nItemsCopy.get(planInventoryCopy.get(value)) == 0)
                    nItemsCopy.remove(planInventoryCopy.get(value));
                // Lets remove it on planInventory
                planInventoryCopy.remove(value);
            }
        }
        // Iterate for the whole inventory and lets try to check if it's what we want
        PistonCrystal.printChat("t", false);
        return tempToSort;
    }


    /*
    nItems.get(mc.player.inventory.getStackInSlot(0).getItem().registryName.toString())
     */


}