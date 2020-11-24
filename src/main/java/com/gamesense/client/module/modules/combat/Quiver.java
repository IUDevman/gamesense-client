package com.gamesense.client.module.modules.combat;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.init.Items;
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
 */

public class Quiver extends Module {
    public Quiver() {
        super("Quiver", Category.Combat);
    }

    public Setting.Boolean strength;
    public Setting.Boolean speed;

    public boolean hasSpeed = false;
    public boolean hasStrength = false;

    @Override
    public void setup(){
        strength = registerBoolean("Strength", "Strength", true);
        speed = registerBoolean("Speed", "Speed", true);
    }

    @Override
    public void onUpdate() {
        ++tick;

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

        if (strength.getBVal() == true && !hasStrength) {
            if (mc.player.inventory.getCurrentItem().getItem() == Items.BOW && ifArrowInHotbar()) {
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

            if (speed.getBVal() == true && !hasSpeed) {
                if (mc.player.inventory.getCurrentItem().getItem() == Items.BOW && ifArrowInHotbar()) {
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

        }
    }

    private boolean ifArrowInHotbar() {
        boolean inInv = false;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TIPPED_ARROW) {
                inInv = true;
                break;
            }
        }
        return inInv;
    }

    private int getBowCharge() {
        if (randomVariation == 0) {
            randomVariation = 1;
        } return 3 + randomVariation;
    }
}
