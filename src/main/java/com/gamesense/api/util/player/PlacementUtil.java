package com.gamesense.api.util.player;

import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystalGS;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

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
        Block block = mc.world.getBlockState(blockPos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
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

        Vec3d hitVec = getHitVec(blockPos, opposite);
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
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

        doPlace(neighbour, opposite, hand);
        mc.rightClickDelayTimer = 4;

        if (stoppedAC) {
            AutoCrystalGS.stopAC = false;
        }

        return true;
    }

    public static void doPlace(BlockPos pos, EnumFacing side, EnumHand hand) {
        NetHandlerPlayClient connection = mc.getConnection();
        if (connection == null) return;

        Vec3d hitVecOffset = getHitVecOffset(side);
        connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, side, hand, (float) hitVecOffset.x, (float) hitVecOffset.y, (float) hitVecOffset.z));
        playSound(pos, side, hand, hitVecOffset);
        mc.player.swingArm(hand);
    }

    private static Vec3d getHitVec(BlockPos pos, EnumFacing side) {
        Vec3i directionVec = side.getDirectionVec();
        return new Vec3d(
            pos.getX() + 0.5 + directionVec.getX() * 0.5,
            pos.getX() + 0.5 + directionVec.getY() * 0.5,
            pos.getX() + 0.5 + directionVec.getZ() * 0.5
        );
    }

    private static Vec3d getHitVecOffset(EnumFacing side) {
        Vec3i directionVec = side.getDirectionVec();
        return new Vec3d(
            0.5 + directionVec.getX() * 0.5,
            0.5 + directionVec.getY() * 0.5,
            0.5 + directionVec.getZ() * 0.5
        );
    }

    private static void playSound(BlockPos pos, EnumFacing side, EnumHand hand, Vec3d hitVecOffset) {
        ItemStack itemStack = mc.player.getHeldItem(hand);
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block == Blocks.AIR) return;

        int metaDate = itemStack.getMetadata();
        IBlockState blockState = block.getStateForPlacement(mc.world, pos, side, (float) hitVecOffset.x, (float) hitVecOffset.y, (float) hitVecOffset.z, metaDate, mc.player, hand);
        SoundType soundType = block.getSoundType(blockState, mc.world, pos, mc.player);

        mc.world.playSound(mc.player, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
    }
}