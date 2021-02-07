package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.command.commands.AutoGearCommand;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.item.ItemStack;
import scala.Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AutoGear extends Module {

    public AutoGear() {
        super("AutoGear", Category.Misc);
    }

    Setting.Boolean chatMsg,
                    debugMode,
                    enderChest;
    Setting.Integer tickDelay;

    // Our inventory variables
    private HashMap<Integer, String> planInventory = new HashMap<>();
    private HashMap<String, Integer> nItems = new HashMap<>();
    // Sort item
    private ArrayList<Integer> sortItems = new ArrayList<>();

    // Tickets
    private int delayTimeTicks,
            stepNow;
    // If we had opened before a chest/inventory
    private boolean openedBefore,
            finishSort,
            doneBefore;

    @Override
    public void setup() {

        tickDelay = registerInteger("Tick Delay", 0, 0, 20);
        chatMsg = registerBoolean("Chat Msg", true);
        enderChest = registerBoolean("EnderChest", false);
        debugMode = registerBoolean("Debug Mode", false);

    }

    @Override
    public void onEnable() {
        // Msg
        if (chatMsg.getValue())
            PistonCrystal.printChat("AutoSort Turned On!", false);
        // Get name of the config
        // Config variables
        String curConfigName = AutoGearCommand.getCurrentSet();
        // If none, exit
        if (curConfigName.equals("")) {
            disable();
            return;
        }
        // Print the config
        if (chatMsg.getValue())
            PistonCrystal.printChat("Config " + curConfigName + " actived", false);
        // Get the inventory
        String inventoryConfig = AutoGearCommand.getInventoryKit(curConfigName);
        // If none, exit
        if (inventoryConfig.equals("")) {
            disable();
            return;
        }
        // Split it into array
        String[] inventoryDivided = inventoryConfig.split(" ");
        // Reset variables
        planInventory = new HashMap<>();
        nItems = new HashMap<>();
        // Iterate for creating planInventory and nItems
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
        }
        // Reset tickdelay
        delayTimeTicks = 0;
        // Reset opened
        openedBefore = doneBefore = false;
    }

    @Override
    public void onDisable() {
        if (chatMsg.getValue() && planInventory.size() > 0)
            PistonCrystal.printChat("AutoSort Turned Off!", true);
    }

    @Override
    public void onUpdate() {
        // Wait
        if (delayTimeTicks < tickDelay.getValue()) {
            delayTimeTicks++;
            return;
        } else {
            delayTimeTicks = 0;
        }

        // Since this is in the misc category, it did not turn off. This can cause some problems, so i have to turn it off manually with this
        if (planInventory.size() == 0)
            disable();

        if (((mc.player.openContainer instanceof ContainerChest && (enderChest.getValue() || !((ContainerChest) mc.player.openContainer).getLowerChestInventory().getDisplayName().getUnformattedText().equals("Ender Chest")))
            || mc.player.openContainer instanceof ContainerShulkerBox)
            ) {
            if (!openedBefore) {
                int maxValue = mc.player.openContainer instanceof ContainerChest ? ((ContainerChest) mc.player.openContainer).getLowerChestInventory().getSizeInventory()
                        : 27;
                // Iterate for every value
                for (int i = 0; i < maxValue; i++) {
                    // TODO: make the shulker thing. first make the sort inventory
                }
                openedBefore = true;
                //  mc.playerController.windowClick(0, 0, 0, ClickType.PICKUP, mc.player);
                HashMap<Integer, String> inventoryCopy = getInventoryCopy(maxValue);
                HashMap<Integer, String> aimInventory = getInventoryCopy(maxValue, planInventory);
                PistonCrystal.printChat("ciao", false);
            }
        }else openedBefore = false;

    }

    // This give a copy of our inventory
    private HashMap<Integer, String> getInventoryCopy(int startPoint) {
        HashMap<Integer, String> output = new HashMap<>();
        int sizeInventory = mc.player.inventory.mainInventory.size();

        for(int i = 0; i < sizeInventory; i++) {
            ItemStack item = mc.player.inventory.getStackInSlot(i);
            output.put(i + startPoint + (i < 9 ? sizeInventory - 9 : -9), Objects.requireNonNull(item.getItem().getRegistryName()).toString() + item.getMetadata());
        }

        return output;
    }

    // This give a copy of our inventory
    private HashMap<Integer, String> getInventoryCopy(int startPoint, HashMap<Integer, String> inventory) {
        HashMap<Integer, String> output = new HashMap<>();
        int sizeInventory = mc.player.inventory.mainInventory.size();
        String outputString = "";
        for(int val : inventory.keySet()) {
            output.put(val + startPoint + (val < 9 ? sizeInventory - 9 : -9), inventory.get(val));
        }

        return output;
    }



}
