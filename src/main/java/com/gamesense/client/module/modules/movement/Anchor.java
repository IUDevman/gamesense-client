package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

/**
 * @Author Hoosiers on 09/28/20
 */

public class Anchor extends Module {

    public Anchor() {
        super("Anchor", Category.Movement);
    }

    Setting.Integer activateYLevel;

    public void setup() {
        activateYLevel = registerInteger("Activate Y", 20, 0, 256);
    }

    BlockPos playerPos;

    public void onUpdate() {
        if (mc.player == null) {
            return;
        }

        if (mc.player.posY < 0) {
            return;
        }

        if (mc.player.posY > activateYLevel.getValue()) {
            return;
        }

        double newX;
        double newZ;

        //specifies the x and z coordinates to be centered- should prevent people from getting stuck up on side blocks
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

        playerPos = new BlockPos(newX, mc.player.posY, newZ);

        if (mc.world.getBlockState(playerPos).getBlock() != Blocks.AIR) {
            return;
        }

        //looks to see if the block below the player is "surrounded"
        if (mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.AIR //1 block
                && mc.world.getBlockState(playerPos.down().east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down().west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down().north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down().south()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2)).getBlock() != Blocks.AIR) {

            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
        else if (mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.AIR //2 block
                && mc.world.getBlockState(playerPos.down(2)).getBlock() == Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2).east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2).west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2).north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(2).south()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(playerPos.down(3)).getBlock() != Blocks.AIR) {

            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
    }
}