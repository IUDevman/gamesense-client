package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.util.Arrays;

/**
 * @author Hoosiers
 * @since 10/10/2020
 */

@Module.Declaration(name = "BlockHighlight", category = Category.Render)
public class BlockHighlight extends Module {

    ModeSetting renderLook = registerMode("Render", Arrays.asList("Block", "Side"), "Block");
    ModeSetting renderType = registerMode("Type", Arrays.asList("Outline", "Fill", "Both"), "Outline");
    IntegerSetting lineWidth = registerInteger("Width", 1, 1, 5);
    ColorSetting renderColor = registerColor("Color", new GSColor(255, 0, 0, 255));

    public void onWorldRender(RenderEvent event) {
        RayTraceResult rayTraceResult = mc.objectMouseOver;

        if (rayTraceResult == null) return;

        EnumFacing enumFacing = mc.objectMouseOver.sideHit;

        if (enumFacing == null) return;

        GSColor colorWithOpacity = new GSColor(renderColor.getValue(), 50);

        if (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {

            BlockPos blockPos = rayTraceResult.getBlockPos();
            AxisAlignedBB axisAlignedBB = mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos);
            int lookInt = renderLook.getValue().equalsIgnoreCase("Side") ? findRenderingSide(enumFacing) : GeometryMasks.Quad.ALL;

            if (mc.world.getBlockState(blockPos).getMaterial() != Material.AIR) {
                switch (renderType.getValue()) {
                    case "Outline": {
                        renderOutline(axisAlignedBB, lineWidth.getValue(), renderColor.getValue(), lookInt);
                        break;
                    }
                    case "Fill": {
                        RenderUtil.drawBox(axisAlignedBB, true, 1, colorWithOpacity, lookInt);
                        break;
                    }
                    default: {
                        renderOutline(axisAlignedBB, lineWidth.getValue(), renderColor.getValue(), lookInt);
                        RenderUtil.drawBox(axisAlignedBB, true, 1, colorWithOpacity, lookInt);
                        break;
                    }
                }
            }
        }
    }

    private void renderOutline(AxisAlignedBB axisAlignedBB, int lineWidth, GSColor color, int lookInt) {
        if (lookInt == GeometryMasks.Quad.ALL) {
            RenderUtil.drawBoundingBox(axisAlignedBB, lineWidth, color);
        } else {
            RenderUtil.drawBoundingBoxWithSides(axisAlignedBB, lineWidth, color, lookInt);
        }
    }

    private int findRenderingSide(EnumFacing enumFacing) {

        switch (enumFacing) {
            case EAST: {
                return GeometryMasks.Quad.EAST;
            }
            case WEST: {
                return GeometryMasks.Quad.WEST;
            }
            case NORTH: {
                return GeometryMasks.Quad.NORTH;
            }
            case SOUTH: {
                return GeometryMasks.Quad.SOUTH;
            }
            case UP: {
                return GeometryMasks.Quad.UP;
            }
            default: {
                return GeometryMasks.Quad.DOWN;
            }
        }
    }
}