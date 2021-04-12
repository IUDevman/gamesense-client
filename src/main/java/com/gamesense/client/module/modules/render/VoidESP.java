package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.api.util.world.Offsets;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

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

    private final ConcurrentSet<BlockPos> voidHoles = new ConcurrentSet<>();

    public void onEnable() {
        voidHoles.clear();
    }

    public void onUpdate() {
        if (mc.player.dimension == 1) return;

        if (mc.player.getPosition().getY() > activeYValue.getValue()) return;

        List<BlockPos> blockPosList = BlockUtil.getCircle(mc.player.getPosition(), 0, renderDistance.getValue(), false);

        for (BlockPos blockPos : blockPosList) {
            if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.BEDROCK)) continue;

            if (isBedrock(blockPos)) continue;

            voidHoles.add(blockPos);
        }
    }

    public void onWorldRender(RenderEvent event) {
        if (mc.player.getPosition().getY() > activeYValue.getValue()) return;

        if (voidHoles.isEmpty()) return;

        voidHoles.forEach(this::renderESP);
    }

    private boolean isBedrock(BlockPos blockPos) {
        for (Vec3d vec3d : Offsets.BURROW_TRIPLE) {
            if (mc.world.getBlockState(blockPos.add(new BlockPos(vec3d))).getBlock().equals(Blocks.BEDROCK)) {
                return true;
            }
        }

        return false;
    }

    private void renderESP(BlockPos blockPos) {
        GSColor fillColor = new GSColor(color.getValue(), 50);
        GSColor outlineColor = new GSColor(color.getValue(), 255);

        int sides = renderMode.getValue().equalsIgnoreCase("Flat") ? GeometryMasks.Quad.DOWN : GeometryMasks.Quad.ALL;

        switch (renderType.getValue()) {
            case "Outline" : {
                renderOutline(blockPos, width.getValue(), outlineColor, sides);
                break;
            }
            case "Fill" : {
                RenderUtil.drawBox(blockPos, 1, fillColor, sides);
                break;
            }
            default: {
                RenderUtil.drawBox(blockPos, 1, fillColor, sides);
                renderOutline(blockPos, width.getValue(), outlineColor, sides);
                break;
            }
        }
    }

    private void renderOutline(BlockPos blockPos, int lineWidth, GSColor color, int sides) {
        if (sides == GeometryMasks.Quad.ALL) {
            RenderUtil.drawBoundingBox(blockPos, 1, lineWidth, color);
        } else {
            RenderUtil.drawBoundingBoxWithSides(blockPos, lineWidth, color, sides);
        }
    }
}