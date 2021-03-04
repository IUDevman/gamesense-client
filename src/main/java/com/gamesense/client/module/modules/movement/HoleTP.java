package com.gamesense.client.module.modules.movement;

import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.Category;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * Edited by 0b00101010
 *
 * @since 25/01/21
 */

@Module.Declaration(name = "HoleTP", category = Category.Movement)
public class HoleTP extends Module {

    private int packets;
    private boolean jumped;
    private final double[] oneblockPositions = new double[]{0.42, 0.75};

    public void onUpdate() {
        if (mc.world == null || mc.player == null || ModuleManager.isModuleEnabled(Speed.class)) {
            return;
        }
        if (!mc.player.onGround) {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                this.jumped = true;
            }
        } else {
            this.jumped = false;
        }
        if (!this.jumped && mc.player.fallDistance < 0.5 && this.isInHole() && mc.player.posY - this.getNearestBlockBelow() <= 1.125 && mc.player.posY - this.getNearestBlockBelow() <= 0.95 && !this.isOnLiquid() && !this.isInLiquid()) {
            if (!mc.player.onGround) {
                this.packets++;
            }
            if (!mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.player.isOnLadder() && this.packets > 0) {
                final BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
                for (final double position : this.oneblockPositions) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(blockPos.getX() + 0.5f, mc.player.posY - position, blockPos.getZ() + 0.5f, true));
                }
                mc.player.setPosition(blockPos.getX() + 0.5f, this.getNearestBlockBelow() + 0.1, blockPos.getZ() + 0.5f);
                this.packets = 0;
            }
        }
    }

    private boolean isInHole() {
        final BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        final IBlockState blockState = mc.world.getBlockState(blockPos);
        return this.isBlockValid(blockState, blockPos);
    }

    private double getNearestBlockBelow() {
        for (int y = (int) Math.floor(mc.player.posY); y > 0.0; y--) {
            if (!(mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock() instanceof BlockSlab) && mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock().getDefaultState().getCollisionBoundingBox(mc.world, new BlockPos(0, 0, 0)) != null) {
                return y + 1;
            }
        }
        return -1.0;
    }

    private boolean isBlockValid(final IBlockState blockState, final BlockPos blockPos) {
        return blockState.getBlock() == Blocks.AIR && mc.player.getDistanceSq(blockPos) >= 1.0 && mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(blockPos.up(2)).getBlock() == Blocks.AIR && this.isSafeHole(blockPos);
    }

    private boolean isSafeHole(BlockPos blockPos) {
        return HoleUtil.isHole(blockPos, true, false).getType() != HoleUtil.HoleType.NONE;
    }

    private boolean isOnLiquid() {
        final double y = mc.player.posY - 0.03;
        for (int x = MathHelper.floor(mc.player.posX); x < MathHelper.ceil(mc.player.posX); x++) {
            for (int z = MathHelper.floor(mc.player.posZ); z < MathHelper.ceil(mc.player.posZ); z++) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInLiquid() {
        final double y = mc.player.posY + 0.01;
        for (int x = MathHelper.floor(mc.player.posX); x < MathHelper.ceil(mc.player.posX); x++) {
            for (int z = MathHelper.floor(mc.player.posZ); z < MathHelper.ceil(mc.player.posZ); z++) {
                final BlockPos pos = new BlockPos(x, (int) y, z);
                if (mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }
}