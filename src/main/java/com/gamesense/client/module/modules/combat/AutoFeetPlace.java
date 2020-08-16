package com.gamesense.client.module.modules.combat;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.world.BlockUtils;
import com.gamesense.client.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static com.gamesense.api.util.world.BlockUtils.canBeClicked;
import static com.gamesense.api.util.world.BlockUtils.faceVectorPacketInstant;

public class AutoFeetPlace extends Module {
    public AutoFeetPlace() {super("AutoFeetPlace", Category.Combat);}

    Setting.Boolean triggerable;
    Setting.Integer timeoutTicks;
    Setting.Boolean disableNone;
    Setting.Integer tickDelay;
    Setting.Boolean rotate;
    Setting.Integer blocksPerTick;
    Setting.Boolean disableOnJump;

    public void setup() {
        triggerable = registerBoolean("Triggerable", "Troggerable", false);
        disableNone = registerBoolean("Disable No Obby", "DisableNoObby", false);
        disableOnJump = registerBoolean("Disable on Jump", "DisableOnJump", false);
        rotate = registerBoolean("Rotate","Rotate", false);
        tickDelay = registerInteger("Tick Delay", "TickDelay", 5, 0, 10);
        timeoutTicks = registerInteger("Timeout Ticks", "TimeoutTicks", 40, 1, 100);
        blocksPerTick = registerInteger("Blocks Per Tick", "BlocksPerTick", 4, 0, 8);
    }

    private int offsetStep = 0;
    private int delayStep = 0;

    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private boolean isSneaking = false;

    private int totalTicksRunning = 0;
    private boolean firstRun;
    private boolean missingObiDisable = false;

    private static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);

            if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                continue;
            }

            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (!blockState.getMaterial().isReplaceable()) {
                return side;
            }
        }
        return null;
    }

    @Override
    protected void onEnable() {
        if (mc.player == null) {
            disable();
            return;
        }

        firstRun = true;

        // save initial player hand
        playerHotbarSlot = mc.player.inventory.currentItem;
        lastHotbarSlot = -1;
    }

    @Override
    protected void onDisable() {
        if (mc.player == null) return;

        // load initial player hand
        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            mc.player.inventory.currentItem = playerHotbarSlot;
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        playerHotbarSlot = -1;
        lastHotbarSlot = -1;

        missingObiDisable = false;
    }

    @Override
    public void onUpdate() {

        if (!(mc.player.onGround) && disableOnJump.getValue()) return;

        if (mc.player == null) {
            return;
        }

        if (triggerable.getValue() && totalTicksRunning >= timeoutTicks.getValue()) {
            totalTicksRunning = 0;
            disable();
            return;
        }

        if (!firstRun) {
            if (delayStep < tickDelay.getValue()) {
                delayStep++;
                return;
            } else {
                delayStep = 0;
            }
        }

        if (firstRun) {
            firstRun = false;
            if (findObiInHotbar() == -1) {
                missingObiDisable = true;
            }
        }

        Vec3d[] offsetPattern = new Vec3d[0];
        int maxSteps = 0;
        offsetPattern = Offsets.SURROUND;
        maxSteps = Offsets.SURROUND.length;


        int blocksPlaced = 0;

        while (blocksPlaced < blocksPerTick.getValue()) {
            if (offsetStep >= maxSteps) {
                offsetStep = 0;
                break;
            }

            BlockPos offsetPos = new BlockPos(offsetPattern[offsetStep]);
            BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

            if (placeBlock(targetPos)) {
                blocksPlaced++;
            }

            offsetStep++;
        }

        if (blocksPlaced > 0) {
            if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
                mc.player.inventory.currentItem = playerHotbarSlot;
                lastHotbarSlot = playerHotbarSlot;
            }

            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }
        }

        totalTicksRunning++;

        if (missingObiDisable && disableNone.getValue()) {
            missingObiDisable = false;
            disable();
        }
    }

    private boolean placeBlock(BlockPos pos) {
        // check if block is already placed
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }

        // check if entity blocks placing
        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                return false;
            }
        }

        EnumFacing side = getPlaceableSide(pos);

        // check if we have a block adjacent to blockpos to click at
        if (side == null) {
            return false;
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        // check if neighbor can be right clicked
        if (!canBeClicked(neighbour)) {
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        int obiSlot = findObiInHotbar();

        if (obiSlot == -1) {
            missingObiDisable = true;
            return false;
        }

        if (lastHotbarSlot != obiSlot) {
            mc.player.inventory.currentItem = obiSlot;
            lastHotbarSlot = obiSlot;
        }

        if (!isSneaking && BlockUtils.blackList.contains(neighbourBlock) || BlockUtils.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        if (rotate.getValue()) {
            faceVectorPacketInstant(hitVec);
        }

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;


        return true;
    }

    private int findObiInHotbar() {
        // search blocks in hotbar
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            // filter out non-block items
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
