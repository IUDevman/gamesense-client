package com.gamesense.client.module.modules.combat;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * @author linustouchtips
 * wip module
 */

public class Quiver extends Module {
    public Quiver() {super("Quiver", Category.Combat);}

    public Setting.Boolean strength;
    public Setting.Boolean speed;

    public boolean hasSpeed = false;
    public boolean hasStrength = false;

    @Override
    public void setup(){
        strength = registerBoolean("Strength", "Strength", true);
        speed = registerBoolean("Speed", "Speed", true);
    }

    private int findBowInHotbar() {
        int slot = 0;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.BOW) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    private boolean ifArrowInInventory() {
        boolean inInv = false;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TIPPED_ARROW) {
                inInv = true;
                break;
            }
        }
        return inInv;
    }

    @Override
    public void onUpdate() {
        PotionEffect speedEffect = mc.player.getActivePotionEffect(Potion.getPotionById(1));
        PotionEffect strengthEffect = mc.player.getActivePotionEffect(Potion.getPotionById(5));

        if (speedEffect != null) {
            hasSpeed = true;
        }

        if (strengthEffect != null) {
            hasStrength = true;
        }

        if (strength.getValue()) {
            if (hasStrength == false && ifArrowInInventory() == true) {
                mc.player.inventory.currentItem = findBowInHotbar();
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, 90, true));
                if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.getItemInUseMaxCount() >= 3) {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem());
                }
            }

            if (speed.getValue()) {
                if (hasSpeed == false && ifArrowInInventory() == true) {
                    mc.player.inventory.currentItem = findBowInHotbar();
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, 90, true));
                    if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.getItemInUseMaxCount() >= 3) {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem());
                    }
                }

            }

        }


    }
}
