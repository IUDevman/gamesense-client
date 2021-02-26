package com.gamesense.api.util.player;

import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystalGS;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlacementUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static int placementConnections = 0;
    private static boolean isSneaking = false;

    public static void onEnable() {
        placementConnections++;
    }

    public static void onDisable() {
        placementConnections--;
        if (placementConnections == 0) {
            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }
        }
    }

    public static boolean placeBlock(BlockPos blockPos, EnumHand hand, boolean rotate, Class<? extends Block> blockToPlace) {
        int oldSlot = mc.player.inventory.currentItem;
        int newSlot = InventoryUtil.findFirstBlockSlot(blockToPlace, 0, 8);

        if (newSlot == -1) {
            return false;
        }

        mc.player.inventory.currentItem = newSlot;
        boolean output = place(blockPos, hand, rotate);
        mc.player.inventory.currentItem = oldSlot;

        return output;
    }

    public static boolean placeItem(BlockPos blockPos, EnumHand hand, boolean rotate, Class<? extends Item> itemToPlace) {
        int oldSlot = mc.player.inventory.currentItem;
        int newSlot = InventoryUtil.findFirstItemSlot(itemToPlace, 0, 8);

        if (newSlot == -1) {
            return false;
        }

        mc.player.inventory.currentItem = newSlot;
        boolean output = place(blockPos, hand, rotate);
        mc.player.inventory.currentItem = oldSlot;

        return output;
    }

    public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate) {
        EntityPlayerSP player = mc.player;
        WorldClient world = mc.world;
        PlayerControllerMP playerController = mc.playerController;

        if (player == null || world == null || playerController == null) return false;

        if (!world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return false;
        }

        EnumFacing side = BlockUtil.getPlaceableSide(blockPos);

        if (side == null) {
            return false;
        }

        BlockPos neighbour = blockPos.offset(side);
        EnumFacing opposite = side.getOpposite();

        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = world.getBlockState(neighbour).getBlock();

        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        boolean stoppedAC = false;

        if (ModuleManager.isModuleEnabled(AutoCrystalGS.class)) {
            AutoCrystalGS.stopAC = true;
            stoppedAC = true;
        }

        if (rotate) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }

        EnumActionResult action = playerController.processRightClickBlock(player, world, neighbour, opposite, hitVec, hand);
        if (action == EnumActionResult.SUCCESS) {
            player.swingArm(hand);
            mc.rightClickDelayTimer = 4;
        }

        if (stoppedAC) {
            AutoCrystalGS.stopAC = false;
        }

        return action == EnumActionResult.SUCCESS;
    }
}