package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.command.commands.AutoGearCommand;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import scala.Int;

import java.util.*;

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
                sortItems = getInventorySort(inventoryCopy, aimInventory, maxValue);
                PistonCrystal.printChat("ciao", false);
            }
        }else openedBefore = false;

    }

    // This give the inventory to sort
    private ArrayList<Integer> getInventorySort(HashMap<Integer, String> copyInventory, HashMap<Integer, String> planInventoryCopy, int startValues) {
        // Plan to move
        ArrayList<Integer> planMove = new ArrayList<>();
        // The copy of the inventory
        // Lets get nItems
        HashMap<String, Integer> nItemsCopy = new HashMap<>();
        // Lets add everything to nItems
        for(String value : planInventoryCopy.values()) {
            if (nItemsCopy.containsKey(value)) {
                nItemsCopy.put(value, nItemsCopy.get(value) + 1);
            }else {
                nItemsCopy.put(value, 1);
            }
        }
        // Ignore values
        ArrayList<Integer> ignoreValues = new ArrayList<>();

        // Iterate and check if we are ok for certain items
        int[] listValue = new int[planInventoryCopy.size()];
        // Lets add everything
        int id = 0;
        for(int idx : planInventoryCopy.keySet()) {
            listValue[id++] = idx;
        }


        for(int item : listValue) {
            if (copyInventory.get(item).equals(planInventoryCopy.get(item))) {
                // Add a value to ignore later
                ignoreValues.add(item);
                // Update the value in nItemsCopy
                nItemsCopy.put(planInventoryCopy.get(item), nItemsCopy.get(planInventoryCopy.get(item)) - 1);
                // If it's == 0, just remove it
                if (nItemsCopy.get(planInventoryCopy.get(item)) == 0)
                    nItemsCopy.remove(planInventoryCopy.get(item));
                // Lets remove it on planInventory
                planInventoryCopy.remove(item);
            }
        }

        String pickedItem = null;


        // Try to sort
        int i;
        for (i = startValues; i < startValues + copyInventory.size(); i++) {
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

                    copyInventory.put(i, copyInventory.get(aimKey));
                    copyInventory.put(aimKey, itemCheck);

                    // Check if that determinated item is empty or not
                    if (!copyInventory.get(aimKey).equals("minecraft:air0")) {
                        // If it's not air, in this case we'll have an item in our pick hand.
                        // We have to do not incr i
                        // And then, lets add this value to pickedItem
                        if (i >=  startValues + copyInventory.size())
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
                        copyInventory.put(i, pickedItem);
                        // Reset pickedItem
                        pickedItem = null;
                    }
                }
            }

        }


        // Print all path
        if (debugMode.getValue()) {
            // Print every values
            for(int valuePath : planMove) {
                PistonCrystal.printChat(Integer.toString(valuePath),  false);
            }
        }

        return planMove;
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
