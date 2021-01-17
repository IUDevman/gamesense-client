package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.player.friends.Friends;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.client.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Author Hoosiers on 10/20/2020
 */

public class CityESP extends Module {

    public CityESP() {
        super("CityESP", Category.Render);
    }

    Setting.Mode targetMode;
    Setting.Mode selectMode;
    Setting.Mode renderMode;
    Setting.Integer range;
    Setting.Integer width;
    Setting.ColorSetting color;

    public void setup() {
        ArrayList<String> targetModes = new ArrayList<>();
        targetModes.add("Single");
        targetModes.add("All");

        ArrayList<String> selectModes = new ArrayList<>();
        selectModes.add("Closest");
        selectModes.add("All");

        ArrayList<String> renderModes = new ArrayList<>();
        renderModes.add("Outline");
        renderModes.add("Fill");
        renderModes.add("Both");

        range = registerInteger("Range", 20, 1, 30);
        targetMode = registerMode("Target", targetModes, "Single");
        selectMode = registerMode("Select", selectModes, "Closest");
        renderMode = registerMode("Render", renderModes, "Both");
        width = registerInteger("Width", 1, 1, 10);
        color = registerColor("Color", new GSColor(102,51,153));
    }

    public void onWorldRender(RenderEvent event) {
        if (mc.player != null || mc.world != null) {
            mc.world.playerEntities.stream()
                    .filter(entityPlayer -> entityPlayer.getDistance(mc.player) <= range.getValue())
                    .filter(entityPlayer -> entityPlayer != mc.player)
                    .filter(entityPlayer ->  !Friends.isFriend(entityPlayer.getName()))
                    .forEach(entityPlayer -> {

                        if (entityPlayer == mc.player) {
                            return;
                        }

                        if (isTrapped(entityPlayer)) {
                            List<BlockPos> renderBlocks = new ArrayList<>();
                            renderBlocks.addAll(getBlocksToRender(entityPlayer));

                            if (renderBlocks != null) {
                                renderBox(renderBlocks);
                            }

                            if (targetMode.getValue().equalsIgnoreCase("All")) {
                                return;
                            }
                        }
                    });
        }
    }

    private boolean isTrapped(EntityPlayer entityPlayer) {
        BlockPos blockPos = new BlockPos(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);

        //minimum amount of blocks needed to be "trapped"
        return mc.world.getBlockState(blockPos.east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(blockPos.west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(blockPos.north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(blockPos.south()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(blockPos.up(2)).getBlock() != Blocks.AIR
                && mc.world.getBlockState(blockPos.down()).getBlock() != Blocks.AIR;
    }

    //this doesn't check if there is a block below the target block, might add it later if people want it
    private List<BlockPos> getBlocksToRender(EntityPlayer entityPlayer) {
        NonNullList<BlockPos> blockPosList = NonNullList.create();
        BlockPos blockPos = new BlockPos(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);

        if (mc.world.getBlockState(blockPos.east()).getBlock() != Blocks.BEDROCK) {
            blockPosList.add(blockPos.east());
        }
        if (mc.world.getBlockState(blockPos.west()).getBlock() != Blocks.BEDROCK) {
            blockPosList.add(blockPos.west());
        }
        if (mc.world.getBlockState(blockPos.north()).getBlock() != Blocks.BEDROCK) {
            blockPosList.add(blockPos.north());
        }
        if (mc.world.getBlockState(blockPos.south()).getBlock() != Blocks.BEDROCK) {
            blockPosList.add(blockPos.south());
        }

        return blockPosList;
    }

    private void renderBox(List<BlockPos> blockPosList) {
        switch (selectMode.getValue()) {
            case "Closest": {
                BlockPos renderPos = blockPosList.stream().sorted(Comparator.comparing(blockPos -> blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ))).findFirst().orElse(null);

                if (renderPos != null) {
                    renderBox2(renderPos);
                }
                break;
            }
            case "All": {
                for (BlockPos blockPos : blockPosList) {
                    renderBox2(blockPos);
                }
                break;
            }
        }
    }

    private void renderBox2(BlockPos blockPos) {
        GSColor gsColor1 = new GSColor(color.getValue(), 255);
        GSColor gsColor2 = new GSColor(color.getValue(), 50);

        switch (renderMode.getValue()) {
            case "Both": {
                RenderUtil.drawBox(blockPos, 1, gsColor2, GeometryMasks.Quad.ALL);
                RenderUtil.drawBoundingBox(blockPos, 1, width.getValue(), gsColor1);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(blockPos, 1, width.getValue(), gsColor1);
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(blockPos, 1, gsColor2, GeometryMasks.Quad.ALL);
                break;
            }
        }
    }
}