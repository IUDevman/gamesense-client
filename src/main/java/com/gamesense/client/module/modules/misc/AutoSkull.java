package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.OffHand;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemSkull;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import static com.gamesense.api.util.player.SpoofRotationUtil.ROTATION_UTIL;

/**
 * @Author TechAle
 * Ported and modified from Blocker.java
 */

@Module.Declaration(name = "AutoSkull", category = Category.Misc)
public class AutoSkull extends Module {

    BooleanSetting rotate = registerBoolean("Rotate", true);
    BooleanSetting offHandSkull = registerBoolean("OffHand Skull", false);
    BooleanSetting onShift = registerBoolean("On Shift", false);
    BooleanSetting instaActive = registerBoolean("Insta Active", true);
    BooleanSetting disableAfter = registerBoolean("Disable After", true);
    IntegerSetting tickDelay = registerInteger("Tick Delay", 5, 0, 10);
    DoubleSetting playerDistance = registerDouble("Player Distance", 0, 0, 6);

    private int delayTimeTicks = 0;
    private boolean noObby;
    private boolean activedBefore;
    private int oldSlot;
    public void onEnable() {
        ROTATION_UTIL.onEnable();
        PlacementUtil.onEnable();
        if (mc.player == null) {
            disable();
            return;
        }
        noObby = firstShift = false;
    }

    public void onDisable() {
        ROTATION_UTIL.onDisable();
        PlacementUtil.onDisable();

        if (mc.player == null) {
            return;
        }

        if (noObby) setDisabledMessage("Skull not found... Blocker turned OFF!");
        if (offHandSkull.getValue()) OffHand.removeSkull();
    }

    private boolean firstShift;

    public void onUpdate() {
        if (mc.player == null) {
            disable();
            return;
        }

        if (noObby) {
            disable();
            return;
        }

        if (delayTimeTicks < tickDelay.getValue()) {
            delayTimeTicks++;
        } else {
            ROTATION_UTIL.shouldSpoofAngles(true);
            delayTimeTicks = 0;

            if (instaActive.getValue()) {
                placeBlock();
                return;
            }

            if (onShift.getValue() && mc.gameSettings.keyBindSneak.isKeyDown() && mc.player.onGround) {
                if (!firstShift)
                    placeBlock();
                return;
            } else if(firstShift && !mc.gameSettings.keyBindSneak.isKeyDown()) firstShift = false;

            if (playerDistance.getValue() != 0) {
                if ( PlayerUtil.findClosestTarget(playerDistance.getValue(), null) != null) {
                    placeBlock();
                }
            }


        }

    }



    private void placeBlock() {
        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (BlockUtil.getBlock(pos) instanceof BlockAir) {
            EnumHand handSwing = EnumHand.MAIN_HAND;

            int skullSlot = InventoryUtil.findSkullSlot(offHandSkull.getValue(), activedBefore);

            if (skullSlot == -1) {
                noObby = true;
                return;
            }

            if (skullSlot == 9) {
                activedBefore = true;
                if (mc.player.getHeldItemOffhand().getItem() instanceof ItemSkull) {
                    // We can continue
                    handSwing = EnumHand.OFF_HAND;
                } else return;
            }

            if (mc.player.inventory.currentItem != skullSlot && skullSlot != 9) {
                oldSlot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = skullSlot;
            }

            if (PlacementUtil.place(pos, handSwing, rotate.getValue(), true)) {
                if (oldSlot != -1) {
                    mc.player.inventory.currentItem = oldSlot;
                    oldSlot = -1;
                }
                firstShift = true;
                activedBefore = false;
                if (offHandSkull.getValue())
                    OffHand.removeSkull();

                if (disableAfter.getValue()) {
                    disable();
                }
            }
        }


    }

}