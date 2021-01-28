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
import com.gamesense.client.GameSense;
import com.gamesense.client.command.commands.AutoGearCommand;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.Int;

import java.util.*;
import java.util.stream.Collectors;

public class AutoGear extends Module {

    public AutoGear() {
        super("AutoGear", Category.Misc);
    }

    Setting.Boolean chatMsg;
    Setting.Boolean debugMode;
    Setting.Boolean activeSort;
    Setting.Integer tickDelay;
    Setting.Integer trySort;

    // Config variables
    private String curConfigName;
    private String inventoryConfig;
    // Our inventory variables
    private HashMap<Integer, String> planInventory = new HashMap<>();
    private ArrayList<Integer> emptySlots = new ArrayList<>();
    private HashMap<String, Integer> nItems = new HashMap<>();
    // Sort item
    private ArrayList<Integer> sortItems = new ArrayList<>();

    // Tickets
    private int delayTimeTicks,
                stepNow;
    // If we had opened before a chest/inventory
    private boolean openedBefore,
                    finishSort;

    @Override
    public void setup() {
        chatMsg = registerBoolean("Chat Msg", true);
        debugMode = registerBoolean("Debug Mode", true);
        activeSort = registerBoolean("Active Sort", false);
        tickDelay = registerInteger("Tick Delay", 0, 0, 20);
        trySort = registerInteger("Try Sort", 3, 1, 5);
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
        if (chatMsg.getValue() && planInventory.size() > 0)
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
                if (chatMsg.getValue())
                    PistonCrystal.printChat("Start sorting inventory...", false);
                sortItems = getInventorySort();
                if (sortItems.size() == 0) {
                    finishSort = false;
                    if (chatMsg.getValue())
                        PistonCrystal.printChat("Inventory arleady sorted...", true);
                }else {
                    finishSort = true;
                    stepNow = 0;
                }
                openedBefore = true;
            } else if (finishSort) {

                int slotChange = sortItems.get(stepNow++);
                // Sort the inventory
                if (activeSort.getValue())
                    mc.playerController.windowClick(0, slotChange < 9 ? slotChange + 36 : slotChange, 0, ClickType.PICKUP, mc.player);
                if (stepNow == sortItems.size()) {
                    finishSort = false;
                    if (chatMsg.getValue()) {
                        PistonCrystal.printChat("Inventory sorted", false);
                    }
                }
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

    private ArrayList<Integer> getInventorySort() {
        ArrayList<Integer> planMove = new ArrayList<>();
        ArrayList<String> copyInventory = getInventoryCopy();

        for(int tryCount = 0; tryCount < trySort.getValue(); tryCount++) {
            // Lets copy planInventory
            HashMap<Integer, String> planInventoryCopy = (HashMap<Integer, String>) planInventory.clone();
            // Lets copy nItems
            HashMap<String, Integer> nItemsCopy = (HashMap<String, Integer>) nItems.clone();
            // Ignore values for after
            ArrayList<Integer> ignoreValues = new ArrayList<>();
            int value;
            // Iterate and check if we are ok for certain items
            for (int i = 0; i < planInventory.size(); i++) {
                value = (Integer) planInventory.keySet().toArray()[i];
                // If the item we are looking is arleady fine
                if ((copyInventory.get(i)).equals(planInventoryCopy.get(value))) {
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
            String pickedItem = null;

            // Try to sort
            for (int i = 0; i < copyInventory.size(); i++) {
                try {
                    // Check if the i is in the ignoreList
                    if (!ignoreValues.contains(i)) {

                        // Lets check if it's one of the items we have
                        String itemCheck = copyInventory.get(i);
                        // Get the first possibilities
                        Optional<Map.Entry<Integer, String>> momentAim = planInventoryCopy.entrySet().stream().filter(x -> x.getValue().equals(itemCheck)).findFirst();
                        // Check if we found something (this should be always true, but because i fear NullPointerExceptor, i add this
                        if (momentAim.isPresent()) {
                            /// add values
                            // Lets start with the beginning. If pickedItem is null, that means our hand is empty
                            if (pickedItem == null)
                                planMove.add(i);
                            // Get end key
                            int aimKey = momentAim.get().getKey();
                            planMove.add(aimKey);
                            // Ignore the end key
                            ignoreValues.add(aimKey);
                            /// We also have to update the list of item we need
                            // Update the value in nItemsCopy
                            nItemsCopy.put(itemCheck, nItemsCopy.get(itemCheck) - 1);
                            // If it's == 0, just remove it
                            if (nItemsCopy.get(itemCheck) == 0)
                                nItemsCopy.remove(itemCheck);

                            copyInventory.set(i, copyInventory.get(aimKey));
                            copyInventory.set(aimKey, itemCheck);

                            // Check if that determinated item is empty or not
                            if (!copyInventory.get(aimKey).equals("minecraft:air0")) {
                                // If it's not air, in this case we'll have an item in our pick hand.
                                // We have to do not incr i
                                // And then, lets add this value to pickedItem
                                pickedItem = copyInventory.get(i + 1);
                                i--;
                            } else {
                                // Else, it means we are placing on air. Lets remove pickedItem
                                pickedItem = null;
                            }
                            // Lets remove it on planInventory
                            planInventoryCopy.remove(aimKey);
                        } else {
                            // If we found nothing, lets check if we have something in the pick
                            if (pickedItem != null) {
                                // In this case, lets place this item in i
                                planMove.add(i);
                                copyInventory.set(i, pickedItem);
                                // Reset pickedItem
                                pickedItem = null;
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    PistonCrystal.printChat("a", false);
                } catch (IndexOutOfBoundsException e) {
                    PistonCrystal.printChat("b", false);
                }

            }
            break;
        }

        if (debugMode.getValue()) {
            // Print every values
            for(int valuePath : planMove) {
                PistonCrystal.printChat(Integer.toString(valuePath),  false);
            }
        }

        return planMove;
    }

    private ArrayList<String> getInventoryCopy() {
        ArrayList<String> output = new ArrayList<>();
        for(ItemStack i : mc.player.inventory.mainInventory) {
            output.add(Objects.requireNonNull(i.getItem().getRegistryName()).toString() + i.getMetadata());
        }
        return output;
    }

}