package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

import static com.gamesense.api.util.player.SpoofRotationUtil.ROTATION_UTIL;

/**
 * @Author TechAle
 * Ported and modified from AutoTrap.java,
 * Ported Crystal Break from AutoCrystal.java
 */

@Module.Declaration(name = "Blocker", category = Category.Combat)
public class Blocker extends Module {

    BooleanSetting rotate = registerBoolean("Rotate", true);
    BooleanSetting anvilBlocker = registerBoolean("Anvil", true);
    BooleanSetting offHandObby = registerBoolean("Off Hand Obby", true);
    BooleanSetting pistonBlocker = registerBoolean("Piston", true);
    BooleanSetting antiFacePlace = registerBoolean("Shift AntiFacePlace", true);
    IntegerSetting BlocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 10);
    ModeSetting blockPlaced = registerMode("Block Place", Arrays.asList("Pressure", "String"), "String");
    IntegerSetting tickDelay = registerInteger("Tick Delay", 5, 0, 10);

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
        if (!anvilBlocker.getValue() && !pistonBlocker.getValue() && !antiFacePlace.getValue()) {
            noActive = true;
            disable();
        }

        noObby = false;
    }

    public void onDisable() {
        ROTATION_UTIL.onDisable();
        PlacementUtil.onDisable();

        if (mc.player == null) {
            return;
        }

        if (noActive) setDisabledMessage("Nothing is active... Blocker turned OFF!");
        else if (noObby) setDisabledMessage("Obsidian not found... Blocker turned OFF!");
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

            if (antiFacePlace.getValue() && mc.gameSettings.keyBindSneak.isPressed()) {
                antiFacePlace();
            }

        }

    }

    private void antiFacePlace() {
        int blocksPlaced = 0;
        Block temp;
        for(Vec3d surround : new Vec3d[] {
                new Vec3d(1,1, 0),
                new Vec3d(-1, 1, 0),
                new Vec3d(0, 1, 1),
                new Vec3d(0, 1, -1)
        }) {
            BlockPos pos = new BlockPos(mc.player.posX + surround.x, mc.player.posY , mc.player.posZ + surround.z);
            if ((temp = BlockUtil.getBlock(pos)) instanceof BlockObsidian ||
                    temp == Blocks.BEDROCK) {
                if (blocksPlaced++ == 0) {
                    AntiCrystal.getHotBarPressure(blockPlaced.getValue());
                }

                PlacementUtil.placeItem(new BlockPos(pos.getX(), pos.getY() + surround.y, pos.getZ()), EnumHand.MAIN_HAND, rotate.getValue(), Items.STRING.getClass());

                if (blocksPlaced == BlocksPerTick.getValue())
                    return;
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
                    MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "AutoAnvil detected... Anvil Blocked!");
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
                                MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "PistonCrystal detected... Destroyed crystal!");
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

        PlacementUtil.place(pos, handSwing, rotate.getValue(), true);
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