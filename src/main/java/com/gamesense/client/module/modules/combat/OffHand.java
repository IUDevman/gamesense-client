package com.gamesense.client.module.modules.combat;

/**
 * @Author TechAle and Hossier
 * Ported and modified from the ex module AutoTotem
 * Crystal Damage calculation autoCrystal
 */

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OffHand extends Module {

    Setting.Mode    defaultItem;
    Setting.Mode    nonDefaultItem;
    Setting.Mode    potionChoose;
    Setting.Integer healthSwitch;
    Setting.Integer tickDelay;
    Setting.Integer fallDistance;
    Setting.Double biasDamage;
    Setting.Boolean crystalObby;
    Setting.Boolean leftGap;
    Setting.Boolean shiftPot;
    Setting.Boolean swordCheck;
    Setting.Boolean fallDistanceBol;
    Setting.Boolean crystalCheck;
    Setting.Boolean antiWeakness;
    Setting.Boolean chatMsg;
    Setting.Boolean noHotBar;

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
        String[] allowedItems = {"Totem", "Crystal", "Gapple", "Plates", "Obby", "Pot"};
        String[] allowedPotions = {"first", "strength", "swiftness"};
        /// Initialize values
        // Default items
        ArrayList<String> defaultItems = new ArrayList<>(Arrays.asList(allowedItems));
        // Default potions
        ArrayList<String> defaultPotions = new ArrayList<>(Arrays.asList(allowedPotions));
        /// Add to settings
        // Default
        defaultItem = registerMode("default", defaultItems, "Totem");
        // Non-Default
        nonDefaultItem = registerMode("Non Default", defaultItems, "Crystal");
        // Potions
        potionChoose = registerMode("Potion", defaultPotions, "first");
        // HealthSwitch
        healthSwitch = registerInteger("Health Switch", 15, 0, 36);
        // TickDelay
        tickDelay = registerInteger("Tick Delay", 0, 0, 20);
        // Fall distance
        fallDistance = registerInteger("Fall Distance", 8, 0, 30);
        // Bias Damage
        biasDamage = registerDouble("Bias Damage", 1, 0, 3);
        // obby
        crystalObby = registerBoolean("shiftCrystObby", true);
        // Gapple
        leftGap = registerBoolean("leftClickGap", true);
        // Potion
        shiftPot = registerBoolean("ShiftPot", true);
        // Sword check
        swordCheck = registerBoolean("swordCheck", true);
        // Fall Distance
        fallDistanceBol = registerBoolean("Fall Distance", true);
        // Crystal Check
        crystalCheck = registerBoolean("Crystal Check", true);
        // Anti Weakness
        antiWeakness = registerBoolean("AntiWeakness", true);
        // NoHotbar
        noHotBar = registerBoolean("noHotBar", false);
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

        // If weakness
        if (normalOffHand && antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            normalOffHand = false;
            itemCheck = "Crystal";
        }

        // If obby
        if(normalOffHand && crystalObby.getValue() && mc.gameSettings.keyBindSneak.isKeyDown()
                && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            itemCheck = "Obby";
            normalOffHand = false;
        }

        if (normalOffHand && mc.gameSettings.keyBindUseItem.isKeyDown() && (swordCheck.getValue() || mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD)) {
            if(mc.gameSettings.keyBindSneak.isKeyDown()) {
                if(shiftPot.getValue()) {
                    itemCheck = "Pot";
                    normalOffHand = false;
                }
            }else
            if (leftGap.getValue() ) {
                itemCheck = "Gapple";
                normalOffHand = false;
            }
        }

        // Get item to check based from the health
        itemCheck = getItemToCheck(itemCheck);

        // If our offhand is okay
        if (offHandSame(itemCheck)) {
            int t = getInventorySlot(itemCheck);
            // If nothing found
            if (t == -1) return;
            // Change
            toOffHand(t);
        }



    }

    private boolean crystalDamage() {
        double ris2 = 0;
        // Check if the crystal exist
        for(Entity t : mc.world.loadedEntityList) {
            // If it's a crystal
            if (t instanceof EntityEnderCrystal && mc.player.getDistance(t) <= 12) {
                if ((ris2 = calculateDamage(t.posX, t.posY, t.posZ, mc.player) * biasDamage.getValue()) >= mc.player.getHealth()) {
                    return true;
                }
            }
        }
        return false;
    }


    public static float calculateDamage (double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;

        double l_Distance = entity.getDistance(posX, posY, posZ);


        double distancedsize = l_Distance / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = (double) entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (int)((v * v + v) / 2.0D * 7.0D * doubleExplosionSize + 1.0D);
        double finald = 1.0D;

        if (entity instanceof EntityLivingBase)
        {
            try {
                finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage),
                        new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
            }catch(NullPointerException e) {
                finald = 1.0D;
            }
        }
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(),
                    (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;

            if (entity.isPotionActive(Potion.getPotionById(11)))
            {
                damage -= damage / 4;
            }
            // damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0F);
            return damage;
        }

        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(),
                (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
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
        for (int i = 35; i > (noHotBar.getValue() ? 9 : -1); i--) {
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
