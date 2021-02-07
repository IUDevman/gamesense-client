package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.combat.DamageUtil;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author TechAle and Hossier
 * Ported and modified from the ex module AutoTotem
 * Crystal Damage calculation autoCrystal
 */
/*
 Fix: SwordCheck was inverted
*/

public class OffHand extends Module {

    public static Setting.Mode  defaultItem,
                                nonDefaultItem,
                                noPlayerItem;
    Setting.Mode    potionChoose;
    Setting.Integer healthSwitch,
                    tickDelay,
                    fallDistance;
    Setting.Double  biasDamage,
                    playerDistance;
    Setting.Boolean pickObby,
                    leftGap,
                    shiftPot,
                    swordCheck,
                    fallDistanceBol,
                    antiWeakness,
                    chatMsg,
                    noHotBar,
                    crystObby,
                    pickObbyShift,
                    onlyHotBar,
                    crystalCheck,
                    hotBarTotem;

    int prevSlot,
        tickWaited,
        totems;
    boolean returnBack,
            stepChanging;
    private static boolean activeT;
    private static int forceObby;

    public static boolean isActive() {
        return activeT;
    }

    public static void requestObsidian() {
        forceObby++;
    }

    public static void removeObsidian() { if (forceObby != 0 ) forceObby--; }

    // Create maps of allowed items
    Map<String, Item> allowedItemsItem = new HashMap<String, Item>() {{
        put("Totem", Items.TOTEM_OF_UNDYING);
        put("Crystal", Items.END_CRYSTAL);
        put("Gapple", Items.GOLDEN_APPLE);
        put("Pot", Items.POTIONITEM);
    }};
    // Create maps of allowed blocks
    Map<String, net.minecraft.block.Block> allowedItemsBlock = new HashMap<String, net.minecraft.block.Block>() {
        {
            put("Plates", Blocks.WOODEN_PRESSURE_PLATE);
            put("Obby", Blocks.OBSIDIAN);
        }
    };


    public OffHand() {
        super("Offhand", Category.Combat);
    }

    @Override
    public void setup() {
        // At start, offHand is not active
        activeT = false;
        // Default items
        String[] allowedItems = {"Totem", "Crystal", "Gapple", "Plates", "Obby", "Pot"};
        String[] allowedPotions = {"first", "strength", "swiftness"};
        /// Initialize values
        // Default items
        ArrayList<String> defaultItems = new ArrayList<>(Arrays.asList(allowedItems));
        // Default potions
        ArrayList<String> defaultPotions = new ArrayList<>(Arrays.asList(allowedPotions));
        /// Add to settings
        // Default
        defaultItem = registerMode("Default", defaultItems, "Totem");
        // Non-Default
        nonDefaultItem = registerMode("Non Default", defaultItems, "Crystal");
        // Non-Player items
        noPlayerItem = registerMode("No Player", defaultItems, "Gapple");
        // Potions
        potionChoose = registerMode("Potion", defaultPotions, "first");
        // HealthSwitch
        healthSwitch = registerInteger("Health Switch", 14, 0, 36);
        // TickDelay
        tickDelay = registerInteger("Tick Delay", 0, 0, 20);
        // Fall distance
        fallDistance = registerInteger("Fall Distance", 12, 0, 30);
        // Bias Damage
        biasDamage = registerDouble("Bias Damage", 1, 0, 3);
        // Distance player
        playerDistance = registerDouble("Player Distance", 0, 0, 30);
        // obby
        pickObby = registerBoolean("Pick Obby", false);
        // Enable pickObby only in shift
        pickObbyShift = registerBoolean("Pick Obby On Shift", false);
        // If you want crystal to offHand
        crystObby = registerBoolean("Cryst Shift Obby", false);
        // Gapple
        leftGap = registerBoolean("Left Click Gap", false);
        // Potion
        shiftPot = registerBoolean("Shift Pot", false);
        // Sword check
        swordCheck = registerBoolean("Only Sword", true);
        // Fall Distance
        fallDistanceBol = registerBoolean("Fall Distance", true);
        // Crystal Check
        crystalCheck = registerBoolean("Crystal Check", false);
        // NoHotbar
        noHotBar = registerBoolean("No HotBar", false);
        // OnlyHotBar
        onlyHotBar = registerBoolean("Only HotBar", false);
        // Anti Weakness
        antiWeakness = registerBoolean("AntiWeakness", false);
        // HotBar totem
        hotBarTotem = registerBoolean("HotBar Totem", false);
        // Chat
        chatMsg = registerBoolean("Chat Msg", true);
    }

    @Override
    public void onEnable() {
        // Enable it
        activeT = true;
        // If they are gonna force us obby
        forceObby = 0;

        returnBack = false;

        if (chatMsg.getValue()) {
            PistonCrystal.printChat("OffHand enabled", false);
        }

    }

    @Override
    public void onDisable() {
        activeT = false;
        forceObby = 0;

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
                /// Change
                // Check if the slot is not air
                if (!mc.player.inventory.getStackInSlot(prevSlot).isEmpty())
                    prevSlot = findEmptySlot();
                // If it's air
                if (prevSlot != -1)
                    mc.playerController.windowClick(0, prevSlot < 9 ? prevSlot + 36 : prevSlot, 0, ClickType.PICKUP, mc.player);
                else
                    PistonCrystal.printChat("Your inventory is full. the item that was on your offhand is going to be dropped. Open your inventory and choose where to put it", true);
                // Set to false
                returnBack = false;
                tickWaited = 0;
            }else return;
        }

        // This is going to contain the item
        String itemCheck = "";
        // If we have to check
        boolean normalOffHand = true;

        /*
            FallDistance and:
            1) his posY is changing (else probably he is packet flying)
            2) he has not an elytra
            or crystalCheck
         */
        if ( (fallDistanceBol.getValue() && mc.player.fallDistance >= fallDistance.getValue() && mc.player.prevPosY != mc.player.posY && !mc.player.isElytraFlying())
            || (crystalCheck.getValue() && crystalDamage())) {
            normalOffHand = false;
            itemCheck = "Totem";
        }

        // If no player
        if (!nearPlayer()) {
            normalOffHand = false;
            itemCheck = noPlayerItem.getValue();
        }

        // If weakness
        if (normalOffHand && antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            normalOffHand = false;
            itemCheck = "Crystal";
        }

        Item mainHandItem = mc.player.getHeldItemMainhand().getItem();

        // If crystal obby
        if( forceObby > 0
            || (normalOffHand && (
            (crystObby.getValue() && mc.gameSettings.keyBindSneak.isKeyDown()
            && mainHandItem == Items.END_CRYSTAL)
            || (pickObby.getValue() && mainHandItem == Items.DIAMOND_PICKAXE && (!pickObbyShift.getValue() || mc.gameSettings.keyBindSneak.isKeyDown()))))) {
            itemCheck = "Obby";
            normalOffHand = false;
        }

        // Gap + Pot
        if (normalOffHand && mc.gameSettings.keyBindUseItem.isKeyDown() && (!swordCheck.getValue() || mainHandItem == Items.DIAMOND_SWORD)) {
            if(mc.gameSettings.keyBindSneak.isKeyDown()) {
                if(shiftPot.getValue()) {
                    itemCheck = "Pot";
                    normalOffHand = false;
                }
            }else
            if (leftGap.getValue() && mainHandItem != Items.GOLDEN_APPLE && mainHandItem != Items.EXPERIENCE_BOTTLE) {
                itemCheck = "Gapple";
                normalOffHand = false;
            }
        }

        // Get item to check based from the health
        itemCheck = getItemToCheck(itemCheck);

        // If our offhand is okay
        if (offHandSame(itemCheck)) {
            boolean done = false;
            if (hotBarTotem.getValue() && itemCheck.equals("Totem")) {
                int slot = InventoryUtil.findTotemSlot(0, 8);
                if (slot != -1) {
                    if (mc.player.inventory.currentItem != slot)
                        mc.player.inventory.currentItem = slot;
                    done = true;
                }
            }
            if (!done) {
                int t = getInventorySlot(itemCheck);
                // If nothing found
                if (t == -1) return;
                // Change
                toOffHand(t);
            }

        }



    }

    private boolean nearPlayer() {
        for(EntityPlayer pl : mc.world.playerEntities) {
            if (pl != mc.player && mc.player.getDistance(pl) < playerDistance.getValue())
                return true;
        }
        return false;
    }

    private boolean crystalDamage() {
        double ris2 = 0;
        // Check if the crystal exist
        for(Entity t : mc.world.loadedEntityList) {
            // If it's a crystal
            if (t instanceof EntityEnderCrystal && mc.player.getDistance(t) <= 12) {
                if ((ris2 = DamageUtil.calculateDamage(t.posX, t.posY, t.posZ, mc.player) * biasDamage.getValue()) >= mc.player.getHealth()) {
                    return true;
                }
            }
        }
        return false;
    }

    private int findEmptySlot() {
        // Iterate all the inv
        for (int i = 35; i > -1; i--) {
            // If empty, return
            if (mc.player.inventory.getStackInSlot(i).isEmpty())
                return i;
        }
        // If full, -1
        return -1;
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

    private String getItemToCheck(String str) {
        return !str.equals("") ? str
                : mc.player.getHealth() + mc.player.getAbsorptionAmount() > healthSwitch.getValue()
                    ? nonDefaultItem.getValue()
                    : defaultItem.getValue();

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
        for (int i = (onlyHotBar.getValue() ? 8 : 35); i > (noHotBar.getValue() ? 9 : -1); i--) {
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
                if (item == temp) {
                    // If it's a potion
                    if (itemName.equals("Pot") && !(potionChoose.getValue().equals("first") || mc.player.inventory.getStackInSlot(i).stackTagCompound.toString().split(":")[2].contains(potionChoose.getValue())))
                        continue;
                    return i;
                }
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
