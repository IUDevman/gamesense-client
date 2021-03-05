package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.*;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.Category;
import com.google.common.collect.Sets;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @reworked by 0b00101010 on 14/01/2021
 */

@Module.Declaration(name = "HoleESP", category = Category.Render)
public class HoleESP extends Module {

    public static IntegerSetting rangeS;
    BooleanSetting hideOwn;
    BooleanSetting flatOwn;
    ModeSetting customHoles;
    ModeSetting mode;
    ModeSetting type;
    DoubleSetting slabHeight;
    IntegerSetting width;
    ColorSetting bedrockColor;
    ColorSetting obsidianColor;
    ColorSetting customColor;
    IntegerSetting ufoAlpha;

    public void setup() {
        ArrayList<String> holes = new ArrayList<>();
        holes.add("Single");
        // https://github.com/IUDevman/gamesense-client/issues/57
        holes.add("Double");
        /*
         * This refers to two wide holes with one down block being blast resistant
         * and the other being air or a breakable block
         *
         * This is technically a safe hole as putting in that block or switching it
         * to a blast resistant one makes it a two wide hole
         *
         * CAUTION: standing over the air gap can cause you to be crystallized which
         * is why I gave it a separate mode
         */
        holes.add("Custom");

        ArrayList<String> render = new ArrayList<>();
        render.add("Outline");
        render.add("Fill");
        render.add("Both");

        ArrayList<String> modes = new ArrayList<>();
        modes.add("Air");
        modes.add("Ground");
        modes.add("Flat");
        modes.add("Slab");
        modes.add("Double");

        rangeS = registerInteger("Range", 5, 1, 20);
        customHoles = registerMode("Show", holes, "Single");
        type = registerMode("Render", render, "Both");
        mode = registerMode("Mode", modes, "Air");
        hideOwn = registerBoolean("Hide Own", false);
        flatOwn = registerBoolean("Flat Own", false);
        slabHeight = registerDouble("Slab Height", 0.5, 0.1, 1.5);
        width = registerInteger("Width", 1, 1, 10);
        bedrockColor = registerColor("Bedrock Color", new GSColor(0, 255, 0));
        obsidianColor = registerColor("Obsidian Color", new GSColor(255, 0, 0));
        customColor = registerColor("Custom Color", new GSColor(0, 0, 255));
        ufoAlpha = registerInteger("UFOAlpha", 255, 0, 255);
    }

    private ConcurrentHashMap<AxisAlignedBB, GSColor> holes;

    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (holes == null) {
            holes = new ConcurrentHashMap<>();
        } else {
            holes.clear();
        }

        int range = (int) Math.ceil(rangeS.getValue());

        // hashSets are easier to navigate
        HashSet<BlockPos> possibleHoles = Sets.newHashSet();
        List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), range, range, false, true, 0);

        // find all holes
        for (BlockPos pos : blockPosList) {

            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            // if air below, we are wasting our time and hashset space
            // we do not remove check from surround offset as potentially a weak block
            if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                possibleHoles.add(pos);
            }
        }

        possibleHoles.forEach(pos -> {
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {
                // We have a hole!
                HoleUtil.BlockSafety holeSafety = holeInfo.getSafety();
                AxisAlignedBB centreBlocks = holeInfo.getCentre();

                if (centreBlocks == null)
                    return;

                GSColor colour;
                // get Colour
                if (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE) {
                    colour = new GSColor(bedrockColor.getValue(), 255);
                } else {
                    colour = new GSColor(obsidianColor.getValue(), 255);
                }
                if (holeType == HoleUtil.HoleType.CUSTOM) {
                    colour = new GSColor(customColor.getValue(), 255);
                }

                String mode = customHoles.getValue();
                if (mode.equalsIgnoreCase("Custom") && (holeType == HoleUtil.HoleType.CUSTOM || holeType == HoleUtil.HoleType.DOUBLE)) {
                    holes.put(centreBlocks, colour);
                } else if (mode.equalsIgnoreCase("Double") && holeType == HoleUtil.HoleType.DOUBLE) {
                    holes.put(centreBlocks, colour);
                } else if (holeType == HoleUtil.HoleType.SINGLE) {
                    holes.put(centreBlocks, colour);
                }
            }
        });
    }

    public void onWorldRender(RenderEvent event) {
        if (mc.player == null || mc.world == null || holes == null || holes.isEmpty()) {
            return;
        }

        holes.forEach(this::renderHoles);
    }

    private void renderHoles(AxisAlignedBB hole, GSColor color) {
        switch (type.getValue()) {
            case "Outline": {
                renderOutline(hole, color);
                break;
            }
            case "Fill": {
                renderFill(hole, color);
                break;
            }
            case "Both": {
                renderOutline(hole, color);
                renderFill(hole, color);
                break;
            }
        }
    }

    private void renderFill(AxisAlignedBB hole, GSColor color) {
        GSColor fillColor = new GSColor(color, 50);
        int ufoAlpha = (this.ufoAlpha.getValue() * 50) / 255;

        if (hideOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) return;

        switch (mode.getValue()) {
            case "Air": {
                if (flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.DOWN);
                } else {
                    RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.ALL);
                }
                break;
            }
            case "Ground": {
                RenderUtil.drawBox(hole.offset(0, -1, 0), true, 1, new GSColor(fillColor, ufoAlpha), fillColor.getAlpha(), GeometryMasks.Quad.ALL);
                break;
            }
            case "Flat": {
                RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.DOWN);
                break;
            }
            case "Slab": {
                if (flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.DOWN);
                } else {
                    RenderUtil.drawBox(hole, false, slabHeight.getValue(), fillColor, ufoAlpha, GeometryMasks.Quad.ALL);
                }
                break;
            }
            case "Double": {
                if (flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.DOWN);
                } else {
                    RenderUtil.drawBox(hole.setMaxY(hole.maxY + 1), true, 2, fillColor, ufoAlpha, GeometryMasks.Quad.ALL);
                }
                break;
            }
        }
    }

    private void renderOutline(AxisAlignedBB hole, GSColor color) {
        GSColor outlineColor = new GSColor(color, 255);

        if (hideOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) return;

        switch (mode.getValue()) {
            case "Air": {
                if (flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, ufoAlpha.getValue(), GeometryMasks.Quad.DOWN);
                } else {
                    RenderUtil.drawBoundingBox(hole, width.getValue(), outlineColor, ufoAlpha.getValue());
                }
                break;
            }
            case "Ground": {
                RenderUtil.drawBoundingBox(hole.offset(0, -1, 0), width.getValue(), new GSColor(outlineColor, ufoAlpha.getValue()), outlineColor.getAlpha());
                break;
            }
            case "Flat": {
                RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, ufoAlpha.getValue(), GeometryMasks.Quad.DOWN);
                break;
            }
            case "Slab": {
                if (this.flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, ufoAlpha.getValue(), GeometryMasks.Quad.DOWN);
                } else {
                    RenderUtil.drawBoundingBox(hole.setMaxY(hole.minY + slabHeight.getValue()), width.getValue(), outlineColor, ufoAlpha.getValue());
                }
                break;
            }
            case "Double": {
                if (this.flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, ufoAlpha.getValue(), GeometryMasks.Quad.DOWN);
                } else {
                    RenderUtil.drawBoundingBox(hole.setMaxY(hole.maxY + 1), width.getValue(), outlineColor, ufoAlpha.getValue());
                }
                break;
            }
        }
    }
}
