package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.player.*;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.OffHand;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSkull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static com.gamesense.api.util.player.SpoofRotationUtil.ROTATION_UTIL;

/**
 * @author TechAle last edit 06/04/21
 * Ported and modified from Blocker.java
 */

@Module.Declaration(name = "AutoSkull", category = Category.Combat)
public class AutoSkull extends Module {

    BooleanSetting rotate = registerBoolean("Rotate", true);
    BooleanSetting offHandSkull = registerBoolean("OffHand Skull", false);
    BooleanSetting onShift = registerBoolean("On Shift", false);
    BooleanSetting instaActive = registerBoolean("Insta Active", true);
    BooleanSetting disableAfter = registerBoolean("Disable After", true);
    BooleanSetting forceRotation = registerBoolean("Force Rotation", false);
    BooleanSetting noUp = registerBoolean("No Up", false);
    BooleanSetting onlyHoles = registerBoolean("Only Holes", false);
    IntegerSetting tickDelay = registerInteger("Tick Delay", 5, 0, 10);
    IntegerSetting preSwitch = registerInteger("Pre Switch", 0, 0, 20);
    IntegerSetting afterSwitch = registerInteger("After Switch", 0, 0, 20);
    DoubleSetting playerDistance = registerDouble("Player Distance", 0, 0, 6);
    BooleanSetting autoTrap = registerBoolean("AutoTrap", false);
    IntegerSetting BlocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 10);

    private static final Vec3d[] AIR = {
            // Supports
            new Vec3d(-1, -1, -1),
            new Vec3d(-1, 0, -1),
            new Vec3d(-1, 1, -1),
            // Start circle
            new Vec3d(-1, 2, -1),
            new Vec3d(-1, 2, 0),
            new Vec3d(0, 2, -1),
            new Vec3d(1, 2, -1),
            new Vec3d(1, 2, 0),
            new Vec3d(1, 2, 1),
            new Vec3d(0, 2, 1),
    };

    private int delayTimeTicks = 0;
    private boolean noObby;
    private boolean activedBefore;
    private int oldSlot;
    private Vec3d lastHitVec = new Vec3d(-1, -1, -1);
    private int preRotationTick;
    private int afterRotationTick;
    private boolean alrPlaced;
    public void onEnable() {
        ROTATION_UTIL.onEnable();
        PlacementUtil.onEnable();
        if (mc.player == null) {
            disable();
            return;
        }
        noObby = firstShift = alrPlaced = false;
        lastHitVec = null;
        preRotationTick = afterRotationTick = 0;
    }

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<>(event -> {
        if (event.getPhase() != Phase.PRE || !rotate.getValue() || lastHitVec == null || !forceRotation.getValue()) return;
        Vec2f rotation = RotationUtil.getRotationTo(lastHitVec);
        PlayerPacket packet = new PlayerPacket(this, rotation);
        PlayerPacketManager.INSTANCE.addPacket(packet);
    });

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
            delayTimeTicks = 0;

            if (onlyHoles.getValue() && HoleUtil.isHole(EntityUtil.getPosition(mc.player), true, true).getType() == HoleUtil.HoleType.NONE)
                return;

            ROTATION_UTIL.shouldSpoofAngles(true);

            if (autoTrap.getValue() && BlockUtil.getBlock(new BlockPos(mc.player.getPosition().add(0, .4, 0))) instanceof BlockSkull) {
                EntityPlayer closest = PlayerUtil.findClosestTarget(2, null);
                if (closest != null && (int) closest.posX == (int) mc.player.posX && (int) closest.posZ == (int) mc.player.posZ && closest.posY > mc.player.posY && closest.posY < mc.player.posY + 2) {
                    int blocksPlaced = 0;
                    int offsetSteps = 0;
                    while (blocksPlaced <= BlocksPerTick.getValue() && offsetSteps < 10) {
                        BlockPos offsetPos = new BlockPos(AIR[offsetSteps]);
                        BlockPos targetPos = mc.player.getPosition().add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
                        if (placeBlock(targetPos))
                            blocksPlaced++;
                        offsetSteps++;
                    }
                }
            }

            if (instaActive.getValue()) {
                placeBlock();
                return;
            }

            if (onShift.getValue() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                if (!firstShift)
                    placeBlock();
                return;
            } else if(firstShift && !mc.gameSettings.keyBindSneak.isKeyDown()) firstShift = false;

            if (playerDistance.getValue() != 0) {
                if ( PlayerUtil.findClosestTarget(playerDistance.getValue(), null) != null) {
                    placeBlock();
                    return;
                }
            }
        }
    }

    private boolean placeBlock(BlockPos pos) {

        EnumHand handSwing = EnumHand.MAIN_HAND;

        int obsidianSlot = InventoryUtil.findObsidianSlot(false, false);

        if (mc.player.inventory.currentItem != obsidianSlot && obsidianSlot != 9) {
            mc.player.inventory.currentItem = obsidianSlot;
        }

        return PlacementUtil.place(pos, handSwing, rotate.getValue(), true);
    }

    private final ArrayList<EnumFacing> exd = new ArrayList<EnumFacing>() {
        {
            add(EnumFacing.DOWN);
            add(EnumFacing.UP);
        }
    };

    private void placeBlock() {

        if (mc.player.onGround) {
            BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY + .4, mc.player.posZ);
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


                if (preSwitch.getValue() > 0 && preRotationTick++ == preSwitch.getValue()) {
                    lastHitVec = new Vec3d(pos.x, pos.y, pos.z);
                    return;
                }


                if (alrPlaced || (noUp.getValue() ? (PlacementUtil.place(pos, handSwing, rotate.getValue(), exd) || PlacementUtil.place(pos, handSwing, rotate.getValue()))
                        : PlacementUtil.place(pos, handSwing, rotate.getValue()))) {
                    alrPlaced = true;
                    if (afterSwitch.getValue() > 0 && afterRotationTick++ == afterSwitch.getValue()) {
                        lastHitVec = new Vec3d(pos.x, pos.y, pos.z);
                        return;
                    }

                    if (oldSlot != -1) {
                        mc.player.inventory.currentItem = oldSlot;
                        oldSlot = -1;
                    }
                    firstShift = true;
                    activedBefore = alrPlaced = false;
                    if (offHandSkull.getValue())
                        OffHand.removeSkull();

                    if (disableAfter.getValue()) {
                        disable();
                    }
                    preRotationTick = afterRotationTick = 0;
                    lastHitVec = null;
                } else lastHitVec = null;
            }
        }
    }
}
