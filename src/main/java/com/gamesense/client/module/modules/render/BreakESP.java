package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.client.module.Module;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

/**
 * @author Hoosiers
 * @since 12/13/2020
 */

public class BreakESP extends Module {

    public BreakESP() {
        super("BreakESP", Category.Render);
    }

    Setting.Mode renderType;
    Setting.ColorSetting color;
    Setting.Integer range;
    Setting.Integer lineWidth;
    public static Setting.Boolean cancelAnimation;

    public void setup() {
        ArrayList<String> renderTypes = new ArrayList<>();
        renderTypes.add("Outline");
        renderTypes.add("Fill");
        renderTypes.add("Both");

        renderType = registerMode("Render", renderTypes, "Both");
        lineWidth = registerInteger("Width", 1, 0, 5);
        range = registerInteger("Range", 100, 1, 200);
        cancelAnimation = registerBoolean("No Animation", true);
        color = registerColor("Color", new GSColor(0, 255, 0, 255));
    }

    public void onWorldRender(RenderEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        mc.renderGlobal.damagedBlocks.forEach((integer, destroyBlockProgress) -> {
            if (destroyBlockProgress != null) {

                BlockPos blockPos = destroyBlockProgress.getPosition();

                if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR) {
                    return;
                }

                if (blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= range.getValue()) {

                    int progress = destroyBlockProgress.getPartialBlockDamage();
                    AxisAlignedBB axisAlignedBB = mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos);

                    renderESP(axisAlignedBB, progress, color.getValue());
                }
            }
        });
    }

    private void renderESP(AxisAlignedBB axisAlignedBB, int progress, GSColor color) {
        GSColor fillColor = new GSColor(color, 50);
        GSColor outlineColor = new GSColor(color, 255);

        double centerX = axisAlignedBB.minX + ((axisAlignedBB.maxX - axisAlignedBB.minX) / 2);
        double centerY = axisAlignedBB.minY + ((axisAlignedBB.maxY - axisAlignedBB.minY) / 2);
        double centerZ = axisAlignedBB.minZ + ((axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2);
        double progressValX = progress * ((axisAlignedBB.maxX - centerX) / 10);
        double progressValY = progress * ((axisAlignedBB.maxY - centerY) / 10);
        double progressValZ = progress * ((axisAlignedBB.maxZ - centerZ) / 10);

        AxisAlignedBB axisAlignedBB1 = new AxisAlignedBB(centerX - progressValX, centerY - progressValY, centerZ - progressValZ, centerX + progressValX, centerY + progressValY, centerZ + progressValZ);

        switch (renderType.getValue()) {
            case "Fill" : {
                RenderUtil.drawBox(axisAlignedBB1, true, 0, fillColor, GeometryMasks.Quad.ALL);
                break;
            }
            case "Outline" : {
                RenderUtil.drawBoundingBox(axisAlignedBB1, lineWidth.getValue(), outlineColor);
                break;
            }
            case "Both" : {
                RenderUtil.drawBox(axisAlignedBB1, true, 0, fillColor, GeometryMasks.Quad.ALL);
                RenderUtil.drawBoundingBox(axisAlignedBB1, lineWidth.getValue(), outlineColor);
                break;
            }
        }
    }
}