package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.world.BlockUtil;
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
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static com.gamesense.api.util.world.BlockUtil.faceVectorPacketInstant;

/**
 * @Author Hoosiers on 09/19/20
 * Ported and modified from Surround.java
 */

public class SelfTrap extends Module {

    public SelfTrap() {
        super("SelfTrap", Category.Combat);
    }

    Setting.Mode trapType;
    Setting.Boolean shiftOnly;
    Setting.Boolean chatMsg;
    Setting.Boolean rotate;
    Setting.Boolean disableNone;
    Setting.Boolean offHandObby;
    Setting.Boolean centerPlayer;
    Setting.Integer tickDelay;
    Setting.Integer blocksPerTick;

    public void setup() {
        ArrayList<String> trapTypes = new ArrayList<>();
        trapTypes.add("Normal");
        trapTypes.add("No Step");
        trapTypes.add("Simple");

        trapType = registerMode("Mode", trapTypes, "Normal");
        shiftOnly = registerBoolean("Shift Only", false);
        disableNone = registerBoolean("Disable No Obby", true);
        rotate = registerBoolean("Rotate", true);
        offHandObby = registerBoolean("Off Hand Obby", false);
        centerPlayer = registerBoolean("Center Player", false);
        tickDelay = registerInteger("Tick Delay", 5, 0, 10);
        blocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 8);
        chatMsg = registerBoolean("Chat Msgs", true);
    }

    private boolean noObby = false;
    private boolean isSneaking = false;
    private boolean firstRun = false;
    private boolean activedOff;

    private int delayTimeTicks = 0;
    private final int playerYLevel = 0;
    private int offsetSteps = 0;
    private int oldSlot = -1;

    private Vec3d centeredBlock = Vec3d.ZERO;

    public void onEnable() {
        if (mc.player == null) {
            disable();
            return;
        }

        if (chatMsg.getValue()) {
            MessageBus.printChat("SelfTrap turned ON!", false);
        }

        if (centerPlayer.getValue() && mc.player.onGround) {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        centeredBlock = BlockUtil.getCenterOfBlock(mc.player.posX, mc.player.posY, mc.player.posY);

        oldSlot = mc.player.inventory.currentItem;

    }

    public void onDisable() {
        if (mc.player == null) {
            return;
        }

        if (chatMsg.getValue()) {
            if (noObby) {
                MessageBus.printChat("No obsidian detected... SelfTrap turned OFF!", true);
            }
            else {
                MessageBus.printChat("SelfTrap turned OFF!", true);
            }
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        if (oldSlot != mc.player.inventory.currentItem && oldSlot != -1 && oldSlot != 9) {
            mc.player.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }

        centeredBlock = Vec3d.ZERO;

        noObby = false;
        firstRun = true;
        AutoCrystalGS.stopAC = false;
        if (offHandObby.getValue() && OffHand.isActive() && activedOff) {
            OffHand.removeObsidian();
            activedOff = false;
        }
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

        if (mc.player.posY <= 0) {
            return;
        }

        if (firstRun || noObby) {
            firstRun = false;
            if (InventoryUtil.findObsidianSlot(offHandObby.getValue(), activedOff) == -1) {
                noObby = true;
                return;
            }else {
                noObby = false;
                activedOff = true;
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

        if (shiftOnly.getValue() && !mc.player.isSneaking()){
            return;
        }

        if (centerPlayer.getValue() && centeredBlock != Vec3d.ZERO && mc.player.onGround) {

            double xDeviation = Math.abs(centeredBlock.x - mc.player.posX);
            double zDeviation = Math.abs(centeredBlock.z - mc.player.posZ);

            if (xDeviation <= 0.1 && zDeviation <= 0.1) {
                centeredBlock = Vec3d.ZERO;
            }
            else {
                double newX;
                double newZ;
                if (mc.player.posX > Math.round(mc.player.posX)) {
                    newX = Math.round(mc.player.posX) + 0.5;
                }
                else if (mc.player.posX < Math.round(mc.player.posX)) {
                    newX = Math.round(mc.player.posX) - 0.5;
                }
                else {
                    newX = mc.player.posX;
                }

                if (mc.player.posZ > Math.round(mc.player.posZ)) {
                    newZ = Math.round(mc.player.posZ) + 0.5;
                }
                else if (mc.player.posZ < Math.round(mc.player.posZ)) {
                    newZ = Math.round(mc.player.posZ) - 0.5;
                }
                else {
                    newZ = mc.player.posZ;
                }

                mc.player.connection.sendPacket(new CPacketPlayer.Position(newX, mc.player.posY, newZ, true));
                mc.player.setPosition(newX, mc.player.posY, newZ);
            }
        }

        int blocksPlaced = 0;

        while (blocksPlaced <= blocksPerTick.getValue()) {

            Vec3d[] offsetPattern;
            int maxSteps;

            if (trapType.getValue().equalsIgnoreCase("Normal")) {
                offsetPattern = Offsets.TRAP;
                maxSteps = Offsets.TRAP.length;
            }
            else if (trapType.getValue().equalsIgnoreCase("No Step")) {
                offsetPattern = Offsets.TRAPFULLROOF;
                maxSteps = Offsets.TRAPFULLROOF.length;
            }
            else {
                offsetPattern = Offsets.TRAPSIMPLE;
                maxSteps = Offsets.TRAPSIMPLE.length;
            }

            if (offsetSteps >= maxSteps) {
                offsetSteps = 0;
                break;
            }

            BlockPos offsetPos = new BlockPos(offsetPattern[offsetSteps]);
            BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

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

            if (tryPlacing && placeBlock(targetPos)) {
                blocksPlaced++;
            }

            offsetSteps++;

            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }
        }
    }


    private boolean placeBlock(BlockPos pos) {
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
        EnumHand handSwing = EnumHand.MAIN_HAND;
        int obsidianSlot = InventoryUtil.findObsidianSlot(offHandObby.getValue(), activedOff);
        if (obsidianSlot == 9) {
            activedOff = true;
            if (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian) {
                // We can continue
                handSwing = EnumHand.OFF_HAND;
            }else return false;
        }else

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
            faceVectorPacketInstant(hitVec);
        }

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, handSwing);
        mc.player.swingArm(handSwing);
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

        private static final Vec3d[] TRAPSIMPLE = {
                new Vec3d(-1, -1, 0),
                new Vec3d(1, -1,0),
                new Vec3d(0,-1,-1),
                new Vec3d(0,-1,1),
                new Vec3d(1, 0,0),
                new Vec3d(0,0,-1),
                new Vec3d(0,0,1),
                new Vec3d(-1, 0, 0),
                new Vec3d(-1, 1, 0),
                new Vec3d(-1, 2, 0),
                new Vec3d(0, 2, 0)
        };
    }
}