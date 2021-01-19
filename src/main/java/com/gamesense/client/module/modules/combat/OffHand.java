package com.gamesense.client.module.modules.combat;

/**
 * @Author TechAle and Hossier
 * Ported and modified from the ex module AutoTotem
 * TODO: add gapple if block sword
 * TODO: add strenght if block pick
 * TODO: add crystalCheck
 * TODO: add fallCheck
 */

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OffHand extends Module {

    Setting.Mode    defaultItem;
    Setting.Mode    nonDefaultItem;
    Setting.Integer healthSwitch;
    Setting.Integer tickDelay;
    Setting.Boolean chatMsg;

    int prevSlot,
        tickWaited,
        totems;
    boolean returnBack,
            stepChanging;


    // Create maps of allowed items
    Map<String, Item> allowedItemsItem = new HashMap<String, Item>() {{
        put("Totem", Items.TOTEM_OF_UNDYING);
        put("Crystal", Items.END_CRYSTAL);
        put("Gapple", Items.GOLDEN_APPLE);
    }};
    // Create maps of allowed blocks
    Map<String, net.minecraft.block.Block> allowedItemsBlock = new HashMap<String, net.minecraft.block.Block>() {
        {
            put("Plates", Blocks.WOODEN_PRESSURE_PLATE);
        }
    };


    public OffHand() {
        super("Offhand", Category.Combat);
    }

    @Override
    public void setup() {
        String[] allowedItems = {"Totem", "Crystal", "Gapple", "Plates"};
        /// Initialize values
        // Default items
        ArrayList<String> defaultItems = new ArrayList<>(Arrays.asList(allowedItems));
        /// Add to settings
        // Default
        defaultItem = registerMode("default", defaultItems, "Totem");
        // Non-Default
        nonDefaultItem = registerMode("nonDefault", defaultItems, "Crystal");
        // HealthSwitch
        healthSwitch = registerInteger("HealthSwitch", 15, 0, 36);
        // TickDelay
        tickDelay = registerInteger("TickDelay", 0, 0, 20);
        // Chat
        chatMsg = registerBoolean("Chat Msg", true);
    }

    @Override
    public void onEnable() {

        returnBack = false;

        if (chatMsg.getValue()) {
            PistonCrystal.printChat("OffHand enabled", false);
        }

    }

    @Override
    public void onDisable() {

        if (chatMsg.getValue()) {
            PistonCrystal.printChat("OffHand disabled", true);
        }

    }

    @Override
    public void onUpdate() {
        // If we are changing
        if (stepChanging)
            // Check if we have to wait
            if(tickWaited++ >= tickDelay.getValue()) {
                // If we are fine, finish
                tickWaited = 0;
                stepChanging = false;
                // Change
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            // If yes, return
            }else return;

        // Get number of totems
        totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();

        // If he has a guy opened that is not the inventory, return
        if (mc.currentScreen instanceof GuiContainer && !(net.minecraft.client.Minecraft.getMinecraft().currentScreen instanceof GuiInventory))
            return;

        // If e had an item before that we have to return back
        if (returnBack) {
            // If we have to wait
            if(tickWaited++ >= tickDelay.getValue()) {
                // Change
                mc.playerController.windowClick(0, prevSlot < 9 ? prevSlot + 36 : prevSlot, 0, ClickType.PICKUP, mc.player);
                // Set to false
                returnBack = false;
                tickWaited = 0;
            }else return;
        }

        // Get item to check based from the health
        String itemCheck = getItemToCheck();

        // If our offhand is okay
        if (offHandSame(itemCheck)) {
            int t = getInventorySlot(itemCheck);
            // If nothing found
            if (t == -1) return;
            // Change
            toOffHand(t);
        }



    }

    private boolean offHandSame(String itemCheck) {
        // Check if e have arleady that item
        Item offHandItem = mc.player.getHeldItemOffhand().getItem();
        if (allowedItemsBlock.containsKey(itemCheck)) {
            Block item = allowedItemsBlock.get(itemCheck);
            if (offHandItem instanceof ItemBlock)
                // Check if it's the block we have
                return ((ItemBlock) offHandItem).getBlock() != item;
        }
        else {
            Item item = allowedItemsItem.get(itemCheck);
            return item != offHandItem;
        }
        return true;
    }

    private String getItemToCheck() {
        return (mc.player.getHealth() + mc.player.getAbsorptionAmount() > healthSwitch.getValue()) ? nonDefaultItem.getValue() : defaultItem.getValue();
    }

    private int getInventorySlot(String itemName) {
        // Get if it's a block or an item
        Object item;
        boolean blockBool = false;
        if (allowedItemsItem.containsKey(itemName)) {
            item = allowedItemsItem.get(itemName);
        }
        else {
            item = allowedItemsBlock.get(itemName);
            blockBool = true;
        }
        // Temporany variable
        Item temp;
        // Iterate
        for (int i = 44; i > -1; i--) {
            // Get item
            temp = mc.player.inventory.getStackInSlot(i).getItem();

            // If we have to check if it's a block
            if (blockBool) {
                // Check if it's what we want
                if (temp instanceof ItemBlock) {
                    if (((ItemBlock) temp).getBlock() == item)
                        return i;
                }

            // If we have to check if it's an item
            } else {
                // Check if it's what we want
                if (item == temp)
                    return i;
            }

        }
        return -1;

    }

    private void toOffHand(int t) {

        // Lets check if we have arleady an item
        if (!mc.player.getHeldItemOffhand().isEmpty()) {
            // After we have to return this
            prevSlot = t;
            returnBack = true;
        }

        // Move the item
        mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
        stepChanging = true;
        tickWaited = 0;
    }

    @Override
    public String getHudInfo() {
        return "[" + ChatFormatting.WHITE + totems + ChatFormatting.GRAY + "]";
    }

}
