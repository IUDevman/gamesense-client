package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.command.commands.AutoGearCommand;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import java.util.*;

/**
 * @Author TechAle
 */
/*
    TODO: Read perfectly your inventory
    TODO: Create a json
    TODO: Read the json
    TODO: Create line-config
    TODO: Read perfectly a shulker/chest
    TODO: Take chest/shulker items to inventory
    TODO: Sort inventory
 */
/*
    INVENTORY SORTING ALGORITHM
    Lets start from the beginning, the inventory on the json.
    This is composed in crescent values, so the first item is the first item in our inventory
    And all the names are this: [name item][meta data]
    I need to specify also the meta data because i want to also sort different types of wool.
    The first step we make is to delete every values that are the same between our inventory and the inventory we want.
    If after all the loop we have 0 item left, that means our inventory is fine and we have to do nothing.
    If not,
    We start iterate for every item in the list of items that are not the same and are not air.
    For every item, we search if that determinated item is one we need.
    If yes, lets search in which slot it has to go.
    If that slot is empty, just switch, if it's not empty, save the item and switch and do not continue with the loop.
    If that determinated item is one we need, search where it has to go, else, just put it where the last item was.
    This should be a recursive problem but, for now i decided to not making it recursive. I'll make it recursive after i've done everything
    for now this is enough, this algo is good enough, with a recursive algo it would be only 10% faster.
    I added double check because, so the end is going to be perfect in anyway
 */

@Module.Declaration(name = "SortInventory", category = Category.Misc)
public class SortInventory extends Module {

    IntegerSetting tickDelay = registerInteger("Tick Delay", 0, 0, 20);
    BooleanSetting confirmSort = registerBoolean("Confirm Sort", true);
    BooleanSetting chatMsg = registerBoolean("Chat Msg", true);
    BooleanSetting instaSort = registerBoolean("Insta Sort", false);
    BooleanSetting closeAfter = registerBoolean("Close After", false);
    BooleanSetting debugMode = registerBoolean("Debug Mode", false);

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
        for (int i = 0; i < inventoryDivided.length; i++) {
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
        // If we have instaSort, open inventory so it start sorting
        if (instaSort.getValue())
            mc.displayGuiScreen(new GuiInventory(mc.player));
    }

    public void onDisable() {
        if (chatMsg.getValue() && planInventory.size() > 0)
            PistonCrystal.printChat("AutoSort Turned Off!", true);
    }

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
        // Check if your inventory is open
        if (mc.currentScreen instanceof GuiInventory) {
            // In that case, sort the inventory
            sortInventoryAlgo();
        } else openedBefore = false;

    }

    // This function sort the entire inventory
    private void sortInventoryAlgo() {
        // If we have just started
        if (!openedBefore) {
            // Print
            if (chatMsg.getValue() && !doneBefore)
                PistonCrystal.printChat("Start sorting inventory...", false);
            // Get the plan to create
            sortItems = getInventorySort();
            // Check some errors / doubleCheck
            if (sortItems.size() == 0 && !doneBefore) {
                finishSort = false;
                // Print
                if (chatMsg.getValue())
                    PistonCrystal.printChat("Inventory arleady sorted...", true);
                // If we are using instaSort, close
                if (instaSort.getValue() || closeAfter.getValue()) {
                    mc.player.closeScreen();
                    if (instaSort.getValue())
                        disable();
                }

            } else {
                finishSort = true;
                stepNow = 0;
            }
            openedBefore = true;
            // if we have to start sorting
        } else if (finishSort) {
            int slotChange;
            // This is the sort area
            if (sortItems.size() != 0) {
                // Get where we are now
                slotChange = sortItems.get(stepNow++);
                // Sort the inventory
                mc.playerController.windowClick(0, slotChange < 9 ? slotChange + 36 : slotChange, 0, ClickType.PICKUP, mc.player);
            }
            // If we have at the limit
            if (stepNow == sortItems.size()) {
                // If confirm sort but we have not done yet
                if (confirmSort.getValue()) {
                    if (!doneBefore) {
                        // Reset
                        openedBefore = false;
                        finishSort = false;
                        doneBefore = true;
                        // The last item sometimes fuck up. This reduce the possibilites
                        checkLastItem();
                        return;
                    }
                }

                finishSort = false;
                // Print
                if (chatMsg.getValue()) {
                    PistonCrystal.printChat("Inventory sorted", false);
                }
                // Check if the last slot has been placed
                checkLastItem();
                doneBefore = false;
                // If we are using instaSort or closeAfter, close
                if (instaSort.getValue() || closeAfter.getValue()) {
                    mc.player.closeScreen();
                    if (instaSort.getValue())
                        disable();
                }

            }
        }
    }

    // This is for checking the last item
    private void checkLastItem() {
        if (sortItems.size() != 0) {
            // Get last
            int slotChange = sortItems.get(sortItems.size() - 1);
            // Check if it's empty
            if (mc.player.inventory.getStackInSlot(slotChange).isEmpty()) {
                // If yes, change
                mc.playerController.windowClick(0, slotChange < 9 ? slotChange + 36 : slotChange, 0, ClickType.PICKUP, mc.player);
            }
        }
    }

    // This give the inventory to sort
    private ArrayList<Integer> getInventorySort() {
        // Plan to move
        ArrayList<Integer> planMove = new ArrayList<>();
        // The copy of the inventory
        ArrayList<String> copyInventory = getInventoryCopy();

        // Lets copy planInventory
        HashMap<Integer, String> planInventoryCopy = (HashMap<Integer, String>) planInventory.clone();
        // Lets copy nItems
        HashMap<String, Integer> nItemsCopy = (HashMap<String, Integer>) nItems.clone();
        // Ignore values
        ArrayList<Integer> ignoreValues = new ArrayList<>();
        int value;
        // Iterate and check if we are ok for certain items
        for (int i = 0; i < planInventory.size(); i++) {
            value = (Integer) planInventory.keySet().toArray()[i];
            // If the item we are looking is arleady fine
            if ((copyInventory.get(value)).equals(planInventoryCopy.get(value))) {
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
        int i;
        // Try to sort
        for (i = 0; i < copyInventory.size(); i++) {
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
                    if (pickedItem == null || !pickedItem.equals(itemCheck))
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
                        if (i >= copyInventory.size())
                            // Somehow, sometimes i go over the size of our inventory. I dunno how since the for cicle should
                            // Stop it, but ok
                            continue;
                        pickedItem = copyInventory.get(i);
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
        }

        if (planMove.size() != 0 && planMove.get(planMove.size() - 1).equals(planMove.get(planMove.size() - 2))) {
            planMove.remove(planMove.size() - 1);
        }

        // Print all path
        if (debugMode.getValue()) {
            // Print every values
            for (int valuePath : planMove) {
                PistonCrystal.printChat(Integer.toString(valuePath), false);
            }
        }

        return planMove;
    }

    // This give a copy of our inventory
    private ArrayList<String> getInventoryCopy() {
        ArrayList<String> output = new ArrayList<>();
        for (ItemStack i : mc.player.inventory.mainInventory) {
            output.add(Objects.requireNonNull(i.getItem().getRegistryName()).toString() + i.getMetadata());
        }
        return output;
    }
}