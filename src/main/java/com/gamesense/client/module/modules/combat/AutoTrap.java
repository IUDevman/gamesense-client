package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author Hoosiers on 09/19/20
 * Ported and modified from Surround.java
 */
/*
    Added new mode: target. this allow to choose in which way he is going to choose the target
    2 modes: nearest (who is cloosest), looking (who you are looking)
    Now some modules are in common (closestTarget, lookingAt)
 */

public class AutoTrap extends Module {

    public AutoTrap() {
        super("AutoTrap", Category.Combat);
    }

    Setting.Mode trapType;
    Setting.Mode target;
    Setting.Boolean chatMsg;
    Setting.Boolean rotate;
    Setting.Boolean disableNone;
    Setting.Integer enemyRange;
    Setting.Integer tickDelay;
    Setting.Integer blocksPerTick;

    public void setup() {
        ArrayList<String> trapTypes = new ArrayList<>();
        trapTypes.add("Normal");
        trapTypes.add("No Step");
        trapTypes.add("Air");
        ArrayList<String> targetChoose = new ArrayList<>();
        targetChoose.add("Nearest");
        targetChoose.add("Looking");

        trapType = registerMode("Mode", trapTypes, "Normal");
        target = registerMode("Target", targetChoose, "Nearest");
        disableNone = registerBoolean("Disable No Obby", true);
        rotate = registerBoolean("Rotate", true);
        tickDelay = registerInteger("Tick Delay", 5, 0, 10);
        blocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 8);
        enemyRange = registerInteger("Range",4, 0, 6);
        chatMsg = registerBoolean("Chat Msgs", true);
    }

    private boolean noObby = false;
    private boolean isSneaking = false;
    private boolean firstRun = false;
    private int oldSlot = -1;

    private int blocksPlaced;
    private int delayTimeTicks = 0;
    private int offsetSteps = 0;

    private EntityPlayer aimTarget;

    public void onEnable() {
        if (mc.player == null) {
            disable();
            return;
        }

        if (chatMsg.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "AutoTrap turned ON!");
        }

        mc.player.inventory.currentItem = oldSlot;
        if (findObsidianSlot() != -1) {
            mc.player.inventory.currentItem = findObsidianSlot();
        }
    }

    public void onDisable() {
        if (mc.player == null) {
            return;
        }

        if (chatMsg.getValue()) {
            if (noObby) {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "No obsidian detected... AutoTrap turned OFF!");
            }
            else {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "AutoTrap turned OFF!");
            }
        }

        if (oldSlot != mc.player.inventory.currentItem && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        noObby = false;
        firstRun = true;
        AutoCrystalGS.stopAC = false;
    }

    public void onUpdate() {
        if (mc.player == null) {
            disable();
            return;
        }

        if (disableNone.getValue() && noObby) {
            disable();
            return;
        }

        if (target.getValue().equals("Nearest"))
            aimTarget = PistonCrystal.findClosestTarget(enemyRange.getValue(), aimTarget);
        else if(target.getValue().equals("Looking"))
            aimTarget = PistonCrystal.findLookingPlayer(enemyRange.getValue());

        if (aimTarget == null) {
            return;
        }

        if (firstRun) {
            firstRun = false;
            if (findObsidianSlot() == -1) {
                noObby = true;
                disable();
            }
        }
        else {
            if (delayTimeTicks < tickDelay.getValue()) {
                delayTimeTicks++;
                return;
            }
            else {
                delayTimeTicks = 0;
            }
        }

        blocksPlaced = 0;

        while (blocksPlaced <= blocksPerTick.getValue()) {

            List<Vec3d> placeTargets = new ArrayList<>();
            int maxSteps;

            if (trapType.getValue().equalsIgnoreCase("Normal")) {
                Collections.addAll(placeTargets, Offsets.TRAP);
                maxSteps = AutoTrap.Offsets.TRAP.length;
            }
            else if (trapType.getValue().equalsIgnoreCase("Air")) {
                Collections.addAll(placeTargets, Offsets.AIR);
                maxSteps = AutoTrap.Offsets.AIR.length;
            }
            else {
                Collections.addAll(placeTargets, Offsets.TRAPFULLROOF);
                maxSteps = AutoTrap.Offsets.TRAPFULLROOF.length;
            }

            if (offsetSteps >= maxSteps) {
                offsetSteps = 0;
                break;
            }

            BlockPos offsetPos = new BlockPos(placeTargets.get(offsetSteps));
            BlockPos targetPos = new BlockPos(aimTarget.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

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

    private int findObsidianSlot() {
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockObsidian) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    private boolean placeBlock(BlockPos pos, int range) {
        Block block = mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }

        EnumFacing side = BlockUtil.getPlaceableSide(pos);

        if (side == null) {
            return false;
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        if (mc.player.getPositionVector().distanceTo(hitVec) > range) {
            return false;
        }

        int obsidianSlot = findObsidianSlot();

        if (mc.player.inventory.currentItem != obsidianSlot && obsidianSlot != -1) {
            mc.player.inventory.currentItem = obsidianSlot;
        }

        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        if (obsidianSlot == -1) {
            noObby = true;
            return false;
        }

        boolean stoppedAC = false;

        if (ModuleManager.isModuleEnabled("AutoCrystalGS")) {
            AutoCrystalGS.stopAC = true;
            stoppedAC = true;
        }

        if (rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec);
        }

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;

        if (stoppedAC) {
            AutoCrystalGS.stopAC = false;
            stoppedAC = false;
        }

        return true;
    }


    private static class Offsets {
        private static final Vec3d[] TRAP = {
                new Vec3d(0, -1, -1),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, 0,-1),
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
                new Vec3d(0, 0,-1),
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