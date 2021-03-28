package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import io.netty.util.internal.ConcurrentSet;
import java.util.Arrays;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

/**
 * @Author: Hoosiers on 08/14/20
 */

@Module.Declaration(name = "VoidESP", category = Category.Render)
public class VoidESP extends Module {

    IntegerSetting renderDistance = registerInteger("Distance", 10, 1, 40);
    IntegerSetting activeYValue = registerInteger("Activate Y", 20, 0, 256);
    ModeSetting renderType = registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
    ModeSetting renderMode = registerMode("Mode", Arrays.asList("Box", "Flat"), "Flat");
    IntegerSetting width = registerInteger("Width", 1, 1, 10);
    ColorSetting color = registerColor("Color", new GSColor(255, 255, 0));

    private ConcurrentSet<BlockPos> voidHoles;

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public void onUpdate() {
        if (mc.player.dimension == 1) {
            return;
        }
        if (mc.player.getPosition().getY() > activeYValue.getValue()) {
            return;
        }
        if (voidHoles == null) {
            voidHoles = new ConcurrentSet<>();
        } else {
            voidHoles.clear();
        }

        List<BlockPos> blockPosList = BlockUtil.getCircle(getPlayerPos(), 0, renderDistance.getValue(), false);

        for (BlockPos blockPos : blockPosList) {
            if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.BEDROCK)) {
                continue;
            }
            if (isAnyBedrock(blockPos, Offsets.center)) {
                continue;
            }
            voidHoles.add(blockPos);
        }
    }

    public void onWorldRender(RenderEvent event) {
        if (mc.player == null || voidHoles == null) {
            return;
        }
        if (mc.player.getPosition().getY() > activeYValue.getValue()) {
            return;
        }
        if (voidHoles.isEmpty()) {
            return;
        }
        voidHoles.forEach(blockPos -> {
            if (renderMode.getValue().equalsIgnoreCase("Box")) {
                drawBox(blockPos);
            } else {
                drawFlat(blockPos);
            }
            drawOutline(blockPos, width.getValue());
        });
    }

    private boolean isAnyBedrock(BlockPos origin, BlockPos[] offset) {
        for (BlockPos pos : offset) {
            if (mc.world.getBlockState(origin.add(pos)).getBlock().equals(Blocks.BEDROCK)) {
                return true;
            }
        }
        return false;
    }

    private void drawFlat(BlockPos blockPos) {
        if (renderType.getValue().equalsIgnoreCase("Fill") || renderType.getValue().equalsIgnoreCase("Both")) {
            GSColor c = new GSColor(color.getValue(), 50);
            if (renderMode.getValue().equalsIgnoreCase("Flat")) {
                RenderUtil.drawBox(blockPos, 1, c, GeometryMasks.Quad.DOWN);
            }
        }
    }

    private void drawBox(BlockPos blockPos) {
        if (renderType.getValue().equalsIgnoreCase("Fill") || renderType.getValue().equalsIgnoreCase("Both")) {
            GSColor c = new GSColor(color.getValue(), 50);
            RenderUtil.drawBox(blockPos, 1, c, GeometryMasks.Quad.ALL);
        }
    }

    private void drawOutline(BlockPos blockPos, int width) {
        if (renderType.getValue().equalsIgnoreCase("Outline") || renderType.getValue().equalsIgnoreCase("Both")) {
            if (renderMode.getValue().equalsIgnoreCase("Box")) {
                RenderUtil.drawBoundingBox(blockPos, 1, width, color.getValue());
            }
            if (renderMode.getValue().equalsIgnoreCase("Flat")) {
                RenderUtil.drawBoundingBoxWithSides(blockPos, width, color.getValue(), GeometryMasks.Quad.DOWN);
            }
        }
    }

    private static class Offsets {
        static final BlockPos[] center = {
                new BlockPos(0, 0, 0),
                new BlockPos(0, 1, 0),
                new BlockPos(0, 2, 0)
        };
    }
}