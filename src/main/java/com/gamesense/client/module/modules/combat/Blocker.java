package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.combat.CrystalUtil;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import static com.gamesense.api.util.player.RotationUtil.ROTATION_UTIL;

/**
 * @Author TechAle on (date)
 * Ported and modified from AutoTrap.java,
 * Ported Crystal Break from AutoCrystal.java
 */

@Module.Declaration(name = "Blocker", category = Category.Combat)
public class Blocker extends Module {

    BooleanSetting chatMsg;
    BooleanSetting rotate;
    BooleanSetting anvilBlocker;
    BooleanSetting pistonBlocker;
    BooleanSetting offHandObby;
    IntegerSetting tickDelay;

    public void setup() {
        rotate = registerBoolean("Rotate", true);
        anvilBlocker = registerBoolean("Anvil", true);
        offHandObby = registerBoolean("Off Hand Obby", true);
        pistonBlocker = registerBoolean("Piston", true);
        tickDelay = registerInteger("Tick Delay", 5, 0, 10);
        chatMsg = registerBoolean("Chat Msgs", true);
    }

    private int delayTimeTicks = 0;
    private boolean noObby;
    private boolean noActive;
    private boolean activedBefore;

    public void onEnable() {
        ROTATION_UTIL.onEnable();
        PlacementUtil.onEnable();
        if (mc.player == null) {
            disable();
            return;
        }

        if (chatMsg.getValue()) {

            String output = "";

            if (anvilBlocker.getValue())
                output += "Anvil ";
            if (pistonBlocker.getValue())
                output += " Piston ";

            if (!output.equals("")) {
                noActive = false;
                MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + output + " turned ON!");
            } else {
                noActive = true;
                disable();
            }
        }
        noObby = false;
    }

    public void onDisable() {
        ROTATION_UTIL.onDisable();
        PlacementUtil.onDisable();
        if (mc.player == null) {
            return;
        }
        if (chatMsg.getValue()) {
            if (noActive) {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "Nothing is active... Blocker turned OFF!");
            } else if (noObby)
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "Obsidian not found... Blocker turned OFF!");
            else
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "Blocker turned OFF!");
        }

    }

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

            if (anvilBlocker.getValue()) {
                blockAnvil();
            }
            if (pistonBlocker.getValue()) {
                blockPiston();
            }

        }

    }

    private void blockAnvil() {
        boolean found = false;
        // Iterate for everything
        for (Entity t : mc.world.loadedEntityList) {
            // If it's a falling block
            if (t instanceof EntityFallingBlock) {
                Block ex = ((EntityFallingBlock) t).fallTile.getBlock();
                // If it's anvil
                if (ex instanceof BlockAnvil
                        // If coords are the same as us
                        && (int) t.posX == (int) mc.player.posX && (int) t.posZ == (int) mc.player.posZ
                        && BlockUtil.getBlock(mc.player.posX, mc.player.posY + 2, mc.player.posZ) instanceof BlockAir) {
                    // Place the block
                    placeBlock(new BlockPos(mc.player.posX, mc.player.posY + 2, mc.player.posZ));
                    MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "AutoAnvil detected... Anvil Blocked!");
                    found = true;
                }
            }
        }
        if (!found) {
            if (activedBefore) {
                activedBefore = false;
                OffHand.removeObsidian();
            }
        }
    }

    private void blockPiston() {
        // Iterate for everything
        for (Entity t : mc.world.loadedEntityList) {
            // If it's an ecrystal and it's near us
            if (t instanceof EntityEnderCrystal
                    && t.posX >= mc.player.posX - 1.5 && t.posX <= mc.player.posX + 1.5
                    && t.posZ >= mc.player.posZ - 1.5 && t.posZ <= mc.player.posZ + 1.5) {
                // Check if it's near
                for (int i = -2; i < 3; i++) {
                    for (int j = -2; j < 3; j++) {
                        if (i == 0 || j == 0) {
                            // If it's a piston
                            if (BlockUtil.getBlock(t.posX + i, t.posY, t.posZ + j) instanceof BlockPistonBase) {
                                // Break
                                breakCrystalPiston(t);
                                MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "PistonCrystal detected... Destroyed crystal!");
                            }
                        }
                    }
                }
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;

        int obsidianSlot = InventoryUtil.findObsidianSlot(offHandObby.getValue(), activedBefore);

        if (obsidianSlot == -1) {
            noObby = true;
            return;
        }

        if (obsidianSlot == 9) {
            activedBefore = true;
            if (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian) {
                // We can continue
                handSwing = EnumHand.OFF_HAND;
            } else return;
        }

        if (mc.player.inventory.currentItem != obsidianSlot && obsidianSlot != 9) {
            mc.player.inventory.currentItem = obsidianSlot;
        }

        PlacementUtil.place(pos, handSwing, rotate.getValue());
    }

    private void breakCrystalPiston(Entity crystal) {
        // If rotate
        if (rotate.getValue()) {
            ROTATION_UTIL.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
        }
        CrystalUtil.breakCrystal(crystal);
        // Rotate
        if (rotate.getValue())
            ROTATION_UTIL.resetRotation();
    }
}