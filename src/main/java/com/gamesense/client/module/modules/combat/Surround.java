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
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static com.gamesense.api.util.world.BlockUtil.faceVectorPacketInstant;

/**
 * @Author Hoosiers on 09/18/20
 */

public class Surround extends Module {

    public Surround() {
        super("Surround", Category.Combat);
    }

    Setting.Boolean chatMsg;
    Setting.Boolean triggerSurround;
    Setting.Boolean shiftOnly;
    Setting.Boolean rotate;
    Setting.Boolean disableNone;
    Setting.Boolean disableOnJump;
    Setting.Boolean centerPlayer;
    Setting.Integer tickDelay;
    Setting.Integer timeOutTicks;
    Setting.Integer blocksPerTick;

    public void setup() {
        triggerSurround = registerBoolean("Triggerable", false);
        shiftOnly = registerBoolean("Shift Only", false);
        disableNone = registerBoolean("Disable No Obby", true);
        disableOnJump = registerBoolean("Disable On Jump", false);
        rotate = registerBoolean("Rotate", true);
        centerPlayer = registerBoolean("Center Player", false);
        tickDelay = registerInteger("Tick Delay", 5, 0, 10);
        timeOutTicks = registerInteger("Timeout Ticks", 40, 1, 100);
        blocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 8);
        chatMsg = registerBoolean("Chat Msgs", true);
    }

    private boolean noObby = false;
    private boolean isSneaking = false;
    private boolean firstRun = false;

    private int oldSlot = -1;

    private int blocksPlaced;
    private int runTimeTicks = 0;
    private int delayTimeTicks = 0;
    private final int playerYLevel = 0;
    private int offsetSteps = 0;

    private Vec3d centeredBlock = Vec3d.ZERO;

    public void onEnable() {
        if (mc.player == null) {
            disable();
            return;
        }

        if (chatMsg.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "Surround turned ON!");
        }

        if (centerPlayer.getValue() && mc.player.onGround) {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        centeredBlock = getCenterOfBlock(mc.player.posX, mc.player.posY, mc.player.posY);

        oldSlot = mc.player.inventory.currentItem;

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
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "No obsidian detected... Surround turned OFF!");
            }
            else {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "Surround turned OFF!");
            }
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        if (oldSlot != mc.player.inventory.currentItem && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }

        centeredBlock = Vec3d.ZERO;

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

        if (mc.player.posY <= 0) {
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

        if (shiftOnly.getValue() && !mc.player.isSneaking()) {
            return;
        }

        if (disableOnJump.getValue() && !(mc.player.onGround) && !(mc.player.isInWeb)) {
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

        if (triggerSurround.getValue() && runTimeTicks >= timeOutTicks.getValue()) {
            runTimeTicks = 0;
            disable();
            return;
        }

        blocksPlaced = 0;

        while (blocksPlaced <= blocksPerTick.getValue()) {
            Vec3d[] offsetPattern;
            offsetPattern = Surround.Offsets.SURROUND;
            int maxSteps = Surround.Offsets.SURROUND.length;

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
        runTimeTicks++;
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
            faceVectorPacketInstant(hitVec);
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

    private Vec3d getCenterOfBlock(double playerX, double playerY, double playerZ) {

        double newX = Math.floor(playerX) + 0.5;
        double newY = Math.floor(playerY);
        double newZ = Math.floor(playerZ) + 0.5;

        return new Vec3d(newX, newY, newZ);
    }

    private static class Offsets {
        private static final Vec3d[] SURROUND = {
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, -1),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, -1)
        };
    }
}