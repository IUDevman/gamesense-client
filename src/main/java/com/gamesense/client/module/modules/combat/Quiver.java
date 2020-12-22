package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

/**
 * @author linustouchtips
 * @since 11/23/2020
 * @edited by Hoosiers on 11/24/2020
 */

public class Quiver extends Module {
    public Quiver() {
        super("Quiver", Category.Combat);
    }

    Setting.Boolean strength;
    Setting.Boolean speed;
    Setting.Boolean shootOne;

    boolean hasSpeed = false;
    boolean hasStrength = false;

    public void setup(){
        strength = registerBoolean("Strength", "Strength", true);
        speed = registerBoolean("Speed", "Speed", true);
        shootOne = registerBoolean("Disable", "Disable", true);
    }

    private int randomVariation;

    public void onUpdate() {
        PotionEffect speedEffect = mc.player.getActivePotionEffect(Potion.getPotionById(1));
        PotionEffect strengthEffect = mc.player.getActivePotionEffect(Potion.getPotionById(5));

        if (speedEffect != null) {
            hasSpeed = true;
        } else {
            hasSpeed = false;
        }

        if (strengthEffect != null) {
            hasStrength = true;
        } else {
            hasStrength = false;
        }

        if (strength.getValue() && !hasStrength) {
            if (mc.player.inventory.getCurrentItem().getItem() == Items.BOW && isArrowInInventory("Arrow of Strength")) {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, -90, true));
                if (mc.player.getItemInUseMaxCount() >= getBowCharge()) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                    mc.player.stopActiveHand();
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    mc.player.setActiveHand(EnumHand.MAIN_HAND);
                } else if (mc.player.getItemInUseMaxCount() == 0) {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    mc.player.setActiveHand(EnumHand.MAIN_HAND);
                }
            }
        }
        if (speed.getValue() && !hasSpeed) {
            if (mc.player.inventory.getCurrentItem().getItem() == Items.BOW && isArrowInInventory("Arrow of Speed")) {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, -90, true));
                if (mc.player.getItemInUseMaxCount() >= getBowCharge()) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                    mc.player.stopActiveHand();
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    mc.player.setActiveHand(EnumHand.MAIN_HAND);
                } else if (mc.player.getItemInUseMaxCount() == 0) {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    mc.player.setActiveHand(EnumHand.MAIN_HAND);
                }
            }
        }

        if (shootOne.getValue()){
            disable();
        }
    }

    private boolean isArrowInInventory(String type) {
        boolean inInv = false;
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.TIPPED_ARROW) {
                if (itemStack.getDisplayName().equalsIgnoreCase(type)){
                    inInv = true;
                    switchArrow(i);
                    break;
                }
            }
        }
        return inInv;
    }

    //this should work pretty well to make sure that the desired arrow to be shot is first. -Hoosiers
    private void switchArrow(int oldSlot){
        int bowSlot = mc.player.inventory.currentItem;
        int placeSlot = bowSlot +1;

        if (placeSlot > 8){
            placeSlot = 1;
        }

        if (placeSlot != oldSlot){
            if (mc.currentScreen instanceof GuiContainer){
                return;
            }

            mc.playerController.windowClick(0, oldSlot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, placeSlot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, oldSlot, 0, ClickType.PICKUP, mc.player);
            MessageBus.sendClientPrefixMessage("Placed arrow at pos " + placeSlot);
        }
    }

    private int getBowCharge() {
        if (randomVariation == 0) {
            randomVariation = 1;
        }
        return 3 + randomVariation;
    }
}