package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * @Author Hoosiers on 09/18/20
 */
public class Surround extends Module {

    public Surround() {
        super("Surround", Category.Combat);
    }

    Setting.Boolean chatMsg,
                    triggerSurround,
                    shiftOnly,
                    rotate,
                    disableNone,
                    disableOnJump,
                    offHandObby,
                    cityBlocker,
                    centerPlayer;
    Setting.Integer tickDelay,
                    timeOutTicks,
                    blocksPerTick;

    public void setup() {
        triggerSurround = registerBoolean("Triggerable", false);
        shiftOnly = registerBoolean("Shift Only", false);
        cityBlocker = registerBoolean("City Blocker", false);
        disableNone = registerBoolean("Disable No Obby", true);
        disableOnJump = registerBoolean("Disable On Jump", false);
        rotate = registerBoolean("Rotate", true);
        offHandObby = registerBoolean("Off Hand Obby", false);
        centerPlayer = registerBoolean("Center Player", false);
        tickDelay = registerInteger("Tick Delay", 5, 0, 10);
        timeOutTicks = registerInteger("Timeout Ticks", 40, 1, 100);
        blocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 8);
        chatMsg = registerBoolean("Chat Msgs", true);
    }

    private boolean noObby = false;
    private boolean isSneaking = false;
    private boolean firstRun = false;
    private boolean activedOff;

    private int oldSlot = -1;

    private int runTimeTicks = 0;
    private int delayTimeTicks = 0;
    private int offsetSteps = 0;

    private Vec3d centeredBlock = Vec3d.ZERO;

    public void onEnable() {
        PlacementUtil.onEnable();
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

        centeredBlock = BlockUtil.getCenterOfBlock(mc.player.posX, mc.player.posY, mc.player.posY);

        oldSlot = mc.player.inventory.currentItem;
    }

    public void onDisable() {
        PlacementUtil.onDisable();
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

        if (oldSlot != mc.player.inventory.currentItem && oldSlot != -1 && oldSlot != 9) {
            mc.player.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }

        centeredBlock = Vec3d.ZERO;

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

        if (disableNone.getValue() && noObby) {
            disable();
            return;
        }

        if (mc.player.posY <= 0) {
            return;
        }

        if (firstRun) {
            firstRun = false;
            if (InventoryUtil.findObsidianSlot(offHandObby.getValue(), activedOff) == -1) {
                noObby = true;
                disable();
            }else activedOff = true;
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

        int blocksPlaced = 0;

        while (blocksPlaced <= blocksPerTick.getValue()) {
            int maxSteps;
            Vec3d[] offsetPattern;
            if (cityBlocker.getValue()) {
                offsetPattern = Offsets.CITY;
                maxSteps = Offsets.CITY.length;
            }else {
                offsetPattern = Surround.Offsets.SURROUND;
                maxSteps = Surround.Offsets.SURROUND.length;
            }

            if (offsetSteps >= maxSteps) {
                offsetSteps = 0;
                break;
            }

            BlockPos offsetPos = new BlockPos(offsetPattern[offsetSteps]);
            BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

            boolean tryPlacing = true;

            // Lets check if we are on a enderchest
            if ( mc.player.posY % 1 > .2 ) {
                targetPos = new BlockPos(targetPos.getX(), targetPos.getY() + 1, targetPos.getZ());
            }

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

    private boolean placeBlock(BlockPos pos) {
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

        return PlacementUtil.place(pos, handSwing, rotate.getValue());
    }

    private static class Offsets {
        private static final Vec3d[] SURROUND = {
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, -1)
        };
        private static final Vec3d[] CITY = {
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, -1),
                new Vec3d(2, 0, 0),
                new Vec3d(-2, 0, 0),
                new Vec3d(0, 0, 2),
                new Vec3d(0, 0, -2),
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, -1)
        };
    }
}