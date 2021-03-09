package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.BlockWeb;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

/**
 * @Author Hoosiers on 09/23/20
 * Ported and modified from Surround.java
 */

@Module.Declaration(name = "SelfWeb", category = Category.Combat)
public class SelfWeb extends Module {

    ModeSetting placeType = registerMode("Place", Arrays.asList("Single", "Double"), "Single");
    BooleanSetting shiftOnly = registerBoolean("Shift Only", false);
    BooleanSetting singleWeb = registerBoolean("One Place", false);
    BooleanSetting disableNone = registerBoolean("Disable No Web", true);
    BooleanSetting rotate = registerBoolean("Rotate", true);
    IntegerSetting tickDelay = registerInteger("Tick Delay", 5, 0, 10);
    IntegerSetting blocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 8);
    BooleanSetting chatMsg = registerBoolean("Chat Msgs", true);

    private boolean noWeb = false;
    private boolean isSneaking = false;
    private boolean firstRun = false;

    private int blocksPlaced;
    private int delayTimeTicks = 0;
    private int offsetSteps = 0;
    private int oldSlot = -1;

    public void onEnable() {
        PlacementUtil.onEnable();
        if (mc.player == null) {
            disable();
            return;
        }

        if (chatMsg.getValue()) {
            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "SelfWeb turned ON!");
        }

        oldSlot = mc.player.inventory.currentItem;

        int newSlot = InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8);
        if (newSlot != -1) {
            mc.player.inventory.currentItem = newSlot;
        }
    }

    public void onDisable() {
        PlacementUtil.onDisable();
        if (mc.player == null) {
            return;
        }

        if (chatMsg.getValue()) {
            if (noWeb) {
                MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "No web detected... SelfWeb turned OFF!");
            } else {
                MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "SelfWeb turned OFF!");
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
            if (InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8) == -1) {
                noWeb = true;
                disable();
            }
        } else {
            if (delayTimeTicks < tickDelay.getValue()) {
                delayTimeTicks++;
                return;
            } else {
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
            } else {
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

            if (tryPlacing && PlacementUtil.placeBlock(targetPos, EnumHand.MAIN_HAND, rotate.getValue(), BlockWeb.class)) {
                blocksPlaced++;
            } else {
                if (InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8) == -1) {
                    noWeb = true;
                    disable();
                }
            }

            offsetSteps++;

            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }
        }
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