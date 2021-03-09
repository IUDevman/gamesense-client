package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.combat.DamageUtil;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
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

@Module.Declaration(name = "Offhand", category = Category.Combat)
public class OffHand extends Module {

    ModeSetting defaultItem = registerMode("Default", Arrays.asList("Totem", "Crystal", "Gapple", "Plates", "Obby", "Pot", "Exp"), "Totem");
    ModeSetting nonDefaultItem = registerMode("Non Default", Arrays.asList("Totem", "Crystal", "Gapple", "Plates", "Obby", "Pot", "Exp"), "Crystal");
    ModeSetting noPlayerItem = registerMode("No Player", Arrays.asList("Totem", "Crystal", "Gapple", "Plates", "Obby", "Pot", "Exp"), "Gapple");
    ModeSetting potionChoose = registerMode("Potion", Arrays.asList("first", "strength", "swiftness"), "first");
    IntegerSetting healthSwitch = registerInteger("Health Switch", 14, 0, 36);
    IntegerSetting tickDelay = registerInteger("Tick Delay", 0, 0, 20);
    IntegerSetting fallDistance = registerInteger("Fall Distance", 12, 0, 30);
    IntegerSetting maxSwitchPerSecond = registerInteger("Max Switch", 6, 2, 10);
    DoubleSetting biasDamage = registerDouble("Bias Damage", 1, 0, 3);
    DoubleSetting playerDistance = registerDouble("Player Distance", 0, 0, 30);
    BooleanSetting pickObby = registerBoolean("Pick Obby", false);
    BooleanSetting pickObbyShift = registerBoolean("Pick Obby On Shift", false);
    BooleanSetting crystObby = registerBoolean("Cryst Shift Obby", false);
    BooleanSetting leftGap = registerBoolean("Left Click Gap", false);
    BooleanSetting shiftPot = registerBoolean("Shift Pot", false);
    BooleanSetting swordCheck = registerBoolean("Only Sword", true);
    BooleanSetting fallDistanceBol = registerBoolean("Fall Distance", true);
    BooleanSetting crystalCheck = registerBoolean("Crystal Check", false);
    BooleanSetting noHotBar = registerBoolean("No HotBar", false);
    BooleanSetting onlyHotBar = registerBoolean("Only HotBar", false);
    BooleanSetting antiWeakness = registerBoolean("AntiWeakness", false);
    BooleanSetting hotBarTotem = registerBoolean("HotBar Totem", false);
    BooleanSetting chatMsg = registerBoolean("Chat Msg", true);

    int prevSlot,
            tickWaited,
            totems;
    boolean returnBack,
            stepChanging;
    private static boolean activeT = false;
    private static int forceObby;
    private ArrayList<Long> switchDone = new ArrayList<>();
    private final ArrayList<Item> ignoreNoSword = new ArrayList<Item>() {
        {
            add(Items.GOLDEN_APPLE);
            add(Items.EXPERIENCE_BOTTLE);
            add(Items.BOW);
            add(Items.POTIONITEM);
        }
    };

    public static boolean isActive() {
        return activeT;
    }

    public static void requestObsidian() {
        forceObby++;
    }

    public static void removeObsidian() {
        if (forceObby != 0) forceObby--;
    }

    // Create maps of allowed items
    Map<String, Item> allowedItemsItem = new HashMap<String, Item>() {{
        put("Totem", Items.TOTEM_OF_UNDYING);
        put("Crystal", Items.END_CRYSTAL);
        put("Gapple", Items.GOLDEN_APPLE);
        put("Pot", Items.POTIONITEM);
        put("Exp", Items.EXPERIENCE_BOTTLE);
    }};
    // Create maps of allowed blocks
    Map<String, net.minecraft.block.Block> allowedItemsBlock = new HashMap<String, net.minecraft.block.Block>() {
        {
            put("Plates", Blocks.WOODEN_PRESSURE_PLATE);
            put("Obby", Blocks.OBSIDIAN);
        }
    };

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
        if (mc.currentScreen instanceof GuiContainer) return;
        // If we are changing
        if (stepChanging)
            // Check if we have to wait
            if (tickWaited++ >= tickDelay.getValue()) {
                // If we are fine, finish
                tickWaited = 0;
                stepChanging = false;
                // Change
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                switchDone.add(System.currentTimeMillis());
                // If yes, return
            } else return;

        // Get number of totems
        totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();

        // If e had an item before that we have to return back
        if (returnBack) {
            // If we have to wait
            if (tickWaited++ >= tickDelay.getValue()) {
                changeBack();
            } else return;
        }

        String itemCheck = getItem();

        // If our offhand is okay
        if (offHandSame(itemCheck)) {

            // If the inventory is opened, close it
            boolean done = false;
            if (hotBarTotem.getValue() && itemCheck.equals("Totem")) {
                done = switchItemTotemHot();
            }
            if (!done) {
                switchItemNormal(itemCheck);
            }

        }

    }

    private void changeBack() {
        /// Change
        // Check if the slot is not air
        if (!mc.player.inventory.getStackInSlot(prevSlot).isEmpty() || prevSlot == -1)
            prevSlot = findEmptySlot();
        // If it's air
        if (prevSlot != -1) {
            mc.playerController.windowClick(0, prevSlot < 9 ? prevSlot + 36 : prevSlot, 0, ClickType.PICKUP, mc.player);
        } else
            PistonCrystal.printChat("Your inventory is full. the item that was on your offhand is going to be dropped. Open your inventory and choose where to put it", true);
        // Set to false
        returnBack = false;
        tickWaited = 0;
    }

    private boolean switchItemTotemHot() {
        // Get totem
        int slot = InventoryUtil.findTotemSlot(0, 8);
        // If we have found one
        if (slot != -1) {
            // Switch
            if (mc.player.inventory.currentItem != slot)
                mc.player.inventory.currentItem = slot;
            return true;
        }
        return false;
    }

    private void switchItemNormal(String itemCheck) {
        // Get slot
        int t = getInventorySlot(itemCheck);
        // If nothing found
        if (t == -1) return;
        // Change
        if (!itemCheck.equals("Totem") && canSwitch())
            return;
        toOffHand(t);
    }

    private String getItem() {
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
        if ((fallDistanceBol.getValue() && mc.player.fallDistance >= fallDistance.getValue() && mc.player.prevPosY != mc.player.posY && !mc.player.isElytraFlying())
                || (crystalCheck.getValue() && crystalDamage())) {
            normalOffHand = false;
            itemCheck = "Totem";
        }

        // If crystal obby
        Item mainHandItem = mc.player.getHeldItemMainhand().getItem();
        if (forceObby > 0
                || (normalOffHand && (
                (crystObby.getValue() && mc.gameSettings.keyBindSneak.isKeyDown()
                        && mainHandItem == Items.END_CRYSTAL)
                        || (pickObby.getValue() && mainHandItem == Items.DIAMOND_PICKAXE && (!pickObbyShift.getValue() || mc.gameSettings.keyBindSneak.isKeyDown()))))) {
            itemCheck = "Obby";
            normalOffHand = false;
        }

        // Gap + Pot
        if (normalOffHand && mc.gameSettings.keyBindUseItem.isKeyDown() && (!swordCheck.getValue() || mainHandItem == Items.DIAMOND_SWORD)) {
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                if (shiftPot.getValue()) {
                    itemCheck = "Pot";
                    normalOffHand = false;
                }
            } else if (leftGap.getValue() && !ignoreNoSword.contains(mainHandItem)) {
                itemCheck = "Gapple";
                normalOffHand = false;
            }
        }

        // If weakness
        if (normalOffHand && antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            normalOffHand = false;
            itemCheck = "Crystal";
        }

        // If no player
        if (normalOffHand && !nearPlayer()) {
            normalOffHand = false;
            itemCheck = noPlayerItem.getValue();
        }


        // Get item to check based from the health
        itemCheck = getItemToCheck(itemCheck);
        return itemCheck;
    }

    private boolean canSwitch() {
        boolean result = false;
        long now = System.currentTimeMillis();

        // Lets remove every old one
        for (int i = 0; i < switchDone.size(); i++) {
            if (now - switchDone.get(i) > 1000)
                switchDone.remove(i);
            else
                break;

        }

        if (switchDone.size() / 2 >= maxSwitchPerSecond.getValue()) {
            return true;
        }
        switchDone.add(now);
        return false;
    }

    private boolean nearPlayer() {
        if (playerDistance.getValue().intValue() == 0)
            return true;
        for (EntityPlayer pl : mc.world.playerEntities) {
            if (pl != mc.player && mc.player.getDistance(pl) < playerDistance.getValue())
                return true;
        }
        return false;
    }

    private boolean crystalDamage() {
        double ris2 = 0;
        // Check if the crystal exist
        for (Entity t : mc.world.loadedEntityList) {
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
        } else {
            Item item = allowedItemsItem.get(itemCheck);
            return item != offHandItem;
        }
        return true;
    }

    private String getItemToCheck(String str) {


        return (mc.player.getHealth() + mc.player.getAbsorptionAmount() > healthSwitch.getValue())
                ? (str.equals("")
                ? nonDefaultItem.getValue()
                : str
        )
                : defaultItem.getValue();

    }

    private int getInventorySlot(String itemName) {
        // Get if it's a block or an item
        Object item;
        boolean blockBool = false;
        if (allowedItemsItem.containsKey(itemName)) {
            item = allowedItemsItem.get(itemName);
        } else {
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
                    if (itemName.equals("Pot") && !(potionChoose.getValue().equalsIgnoreCase("first") || mc.player.inventory.getStackInSlot(i).stackTagCompound.toString().split(":")[2].contains(potionChoose.getValue())))
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
        } else prevSlot = -1;

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
