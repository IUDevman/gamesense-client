package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.*;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static com.gamesense.api.util.world.BlockUtil.faceVectorPacketInstant;

/**
 * @Author Hoosiers on 09/23/20
 * Ported and modified from Surround.java
 */

public class SelfWeb extends Module {

    public SelfWeb() {
        super("SelfWeb", Category.Combat);
    }

    Setting.Boolean chatMsg;
    Setting.Boolean shiftOnly;
    Setting.Boolean singleWeb;
    Setting.Boolean rotate;
    Setting.Boolean disableNone;
    Setting.Integer tickDelay;
    Setting.Integer blocksPerTick;
    Setting.Mode placeType;

    public void setup() {
        ArrayList<String> placeModes = new ArrayList<>();
        placeModes.add("Single");
        placeModes.add("Double");

        placeType = registerMode("Place", placeModes, "Single");
        shiftOnly = registerBoolean("Shift Only", false);
        singleWeb = registerBoolean("One Place", false);
        disableNone = registerBoolean("Disable No Web", true);
        rotate = registerBoolean("Rotate", true);
        tickDelay = registerInteger("Tick Delay", 5, 0, 10);
        blocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 8);
        chatMsg = registerBoolean("Chat Msgs", true);
    }

    private boolean noWeb = false;
    private boolean isSneaking = false;
    private boolean firstRun = false;

    private int blocksPlaced;
    private int delayTimeTicks = 0;
    private final int playerYLevel = 0;
    private int offsetSteps = 0;
    private int oldSlot = -1;

    public void onEnable() {
        if (mc.player == null) {
            disable();
            return;
        }

        if (chatMsg.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "SelfWeb turned ON!");
        }

        oldSlot = mc.player.inventory.currentItem;

        if (findWebSlot() != -1) {
            mc.player.inventory.currentItem = findWebSlot();
        }
    }

    public void onDisable() {
        if (mc.player == null) {
            return;
        }

        if (chatMsg.getValue()) {
            if (noWeb) {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "No web detected... SelfWeb turned OFF!");
            }
            else {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "SelfWeb turned OFF!");
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

        noWeb = false;
        firstRun = true;
        AutoCrystalGS.stopAC = false;
    }

    public void onUpdate() {
        if (mc.player == null) {
            disable();
            return;
        }

        if (disableNone.getValue() && noWeb) {
            disable();
            return;
        }

        if (mc.player.posY <= 0) {
            return;
        }

        if (singleWeb.getValue() && blocksPlaced >= 1) {
            blocksPlaced = 0;
            disable();
            return;
        }

        if (firstRun) {
            firstRun = false;
            if (findWebSlot() == -1) {
                noWeb = true;
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

        blocksPlaced = 0;

        while (blocksPlaced <= blocksPerTick.getValue()) {
            Vec3d[] offsetPattern;
            int maxSteps;

            if (placeType.getValue().equalsIgnoreCase("Double")) {
                offsetPattern = Offsets.DOUBLE;
                maxSteps = Offsets.DOUBLE.length;
            }
            else {
                offsetPattern = Offsets.SINGLE;
                maxSteps = Offsets.SINGLE.length;
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

    private int findWebSlot() {
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockWeb) {
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

        int webSlot = findWebSlot();

        if (mc.player.inventory.currentItem != webSlot && webSlot != -1) {
            mc.player.inventory.currentItem = webSlot;
        }

        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        if (webSlot == -1) {
            noWeb = true;
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

    private static class Offsets {
        private static final Vec3d[] SINGLE = {
                new Vec3d(0, 0, 0)
        };

        private static final Vec3d[] DOUBLE = {
                new Vec3d(0, 0, 0),
                new Vec3d(0, 1, 0)
        };
    }
}