package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * @Author Hoosiers on 09/19/20
 * Ported and modified from Surround.java
 */

@Module.Declaration(name = "AutoTrap", category = Category.Combat)
public class AutoTrap extends Module {

    ModeSetting trapType = registerMode("Mode", Arrays.asList("Normal", "No Step", "Air"), "Normal");
    ModeSetting target = registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
    BooleanSetting disableNone = registerBoolean("Disable No Obby", true);
    BooleanSetting rotate = registerBoolean("Rotate", true);
    BooleanSetting offHandObby = registerBoolean("Off Hand Obby", false);
    IntegerSetting tickDelay = registerInteger("Tick Delay", 5, 0, 10);
    IntegerSetting blocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 8);
    IntegerSetting enemyRange = registerInteger("Range", 4, 0, 6);

    private boolean noObby = false;
    private boolean isSneaking = false;
    private boolean firstRun = false;
    private boolean activedOff;
    private final int oldSlot = -1;

    private int delayTimeTicks = 0;
    private int offsetSteps = 0;

    private EntityPlayer aimTarget;

    public void onEnable() {
        PlacementUtil.onEnable();
        activedOff = false;
        if (mc.player == null) {
            disable();
            return;
        }
    }

    public void onDisable() {
        PlacementUtil.onDisable();
        if (mc.player == null) {
            return;
        }

        if (noObby) setDisabledMessage("No obsidian detected... AutoTrap turned OFF!");

        if (oldSlot != mc.player.inventory.currentItem && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        noObby = false;
        firstRun = true;
        AutoCrystalGS.stopAC = false;
        if (offHandObby.getValue() && OffHand.isActive()) {
            OffHand.removeObsidian();
            activedOff = false;
        }
    }

    public void onUpdate() {
        if (mc.player == null) {
            disable();
            return;
        }

        if (target.getValue().equals("Nearest"))
            aimTarget = PlayerUtil.findClosestTarget(enemyRange.getValue(), aimTarget);
        else if (target.getValue().equals("Looking"))
            aimTarget = PlayerUtil.findLookingPlayer(enemyRange.getValue());

        if (aimTarget == null) {
            return;
        }

        if (firstRun || noObby) {
            firstRun = false;
            if (InventoryUtil.findObsidianSlot(offHandObby.getValue(), activedOff) == -1) {
                noObby = true;
                return;
            } else {
                noObby = false;
                activedOff = true;
            }
        } else {

            if (delayTimeTicks < tickDelay.getValue()) {
                delayTimeTicks++;
                return;
            } else {
                delayTimeTicks = 0;
            }
        }

        if (disableNone.getValue() && noObby) {
            disable();
            return;
        }

        int blocksPlaced = 0;
        if (!noObby)
            while (blocksPlaced <= blocksPerTick.getValue()) {

                List<Vec3d> placeTargets = new ArrayList<>();
                int maxSteps;

                if (trapType.getValue().equalsIgnoreCase("Normal")) {
                    Collections.addAll(placeTargets, Offsets.TRAP);
                    maxSteps = AutoTrap.Offsets.TRAP.length;
                } else if (trapType.getValue().equalsIgnoreCase("Air")) {
                    Collections.addAll(placeTargets, Offsets.AIR);
                    maxSteps = AutoTrap.Offsets.AIR.length;
                } else {
                    Collections.addAll(placeTargets, Offsets.TRAPFULLROOF);
                    maxSteps = AutoTrap.Offsets.TRAPFULLROOF.length;
                }

                if (offsetSteps >= maxSteps) {
                    offsetSteps = 0;
                    break;
                }

                BlockPos offsetPos = new BlockPos(placeTargets.get(offsetSteps));
                BlockPos targetPos = new BlockPos(aimTarget.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

                if (aimTarget.posY % 1 > .2) {
                    targetPos = new BlockPos(targetPos.getX(), targetPos.getY() + 1, targetPos.getZ());
                }

                boolean tryPlacing = true;

                if (!mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
                    tryPlacing = false;
                }

                for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))) {
                    if (entity instanceof EntityPlayer) {
                        tryPlacing = false;
                        break;
                    }
                }

                if (tryPlacing && placeBlock(targetPos, enemyRange.getValue())) {
                    blocksPlaced++;
                }

                offsetSteps++;

                if (isSneaking) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    isSneaking = false;
                }
            }
    }

    private boolean placeBlock(BlockPos pos, int range) {
        if (mc.player.getDistanceSq(pos) > range * range) {
            return false;
        }

        EnumHand handSwing = EnumHand.MAIN_HAND;

        int obsidianSlot = InventoryUtil.findObsidianSlot(offHandObby.getValue(), activedOff);

        if (obsidianSlot == -1) {
            noObby = true;
            return false;
        }

        if (obsidianSlot == 9) {
            activedOff = true;
            if (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian) {
                // We can continue
                handSwing = EnumHand.OFF_HAND;
            } else return false;
        }

        if (mc.player.inventory.currentItem != obsidianSlot && obsidianSlot != 9) {
            mc.player.inventory.currentItem = obsidianSlot;
        }

        return PlacementUtil.place(pos, handSwing, rotate.getValue(), true);
    }

    private static class Offsets {
        private static final Vec3d[] TRAP = {
                new Vec3d(0, -1, -1),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, 0, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 1, -1),
                new Vec3d(1, 1, 0),
                new Vec3d(0, 1, 1),
                new Vec3d(-1, 1, 0),
                new Vec3d(0, 2, -1),
                new Vec3d(0, 2, 0)
        };

        private static final Vec3d[] TRAPFULLROOF = {
                new Vec3d(0, -1, -1),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, 0, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 1, -1),
                new Vec3d(1, 1, 0),
                new Vec3d(0, 1, 1),
                new Vec3d(-1, 1, 0),
                new Vec3d(0, 2, -1),
                new Vec3d(0, 2, 0),
                new Vec3d(0, 3, 0)
        };

        private static final Vec3d[] AIR = {
                new Vec3d(0, -1, -1),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, 0, -1),
                new Vec3d(0, 1, -1),
                new Vec3d(0, 2, -1),
                new Vec3d(0, 2, 0),
                new Vec3d(1, 2, 0),
                new Vec3d(1, 1, 0),
                new Vec3d(-1, 2, 0),
                new Vec3d(-1, 1, 0),
                new Vec3d(0, 2, 1),
                new Vec3d(0, 1, 1)
        };
    }
}