package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.Pair;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.client.module.Module;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GameSense client for original code (actual author unknown)
 * @source https://github.com/IUDevman/gamesense-client/blob/2.2.5/src/main/java/com/gamesense/client/module/modules/render/HoleESP.java
 * @reworked by 0b00101010 on 14/01/2021
 */
public class HoleESP extends Module {

    public HoleESP() {
        super("HoleESP", Category.Render);
    }

    public static Setting.Integer rangeS;
    Setting.Boolean hideOwn;
    Setting.Boolean flatOwn;
    Setting.Mode customHoles;
    Setting.Mode mode;
    Setting.Mode type;
    Setting.Double slabHeight;
    Setting.Integer width;
    Setting.ColorSetting bedrockColor;
    Setting.ColorSetting obsidianColor;
    Setting.ColorSetting customColor;

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
        width = registerInteger("Width",1,1,10);
        bedrockColor = registerColor("Bedrock Color", new GSColor(0,255,0));
        obsidianColor = registerColor("Obsidian Color", new GSColor(255,0,0));
        customColor = registerColor("Custom Color", new GSColor(0,0,255));
    }

    private ConcurrentHashMap<AxisAlignedBB, GSColor> holes;

    // defines the area for the client to search
    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    // gets the entities location
    public static BlockPos getEntityPos(Entity entity) {
        return new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ));
    }

    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (holes == null) {
            holes = new ConcurrentHashMap<>();
        }
        else {
            holes.clear();
        }

        int range = (int) Math.ceil(rangeS.getValue());

        // hashSets are easier to navigate
        HashSet<BlockPos> possibleFullHoles = Sets.newHashSet();
        HashMap<BlockPos, Pair<BlockOffset, GSColor>> possibleWideHoles = new HashMap<>();
        List<BlockPos> blockPosList = getSphere(getEntityPos(mc.player), range, range, false, true, 0);

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
                possibleFullHoles.add(pos);
            }
        }

        possibleFullHoles.forEach(pos -> {
            GSColor color = new GSColor(bedrockColor.getValue(), 255);

            HashMap<BlockOffset, BlockSafety> unsafeSides = getUnsafeSides(pos);

            if (unsafeSides.containsKey(BlockOffset.DOWN)) {
                if (unsafeSides.remove(BlockOffset.DOWN, BlockSafety.BREAKABLE)) {
                    return;
                }
            }

            int size = unsafeSides.size();

            unsafeSides.entrySet().removeIf(entry -> entry.getValue() == BlockSafety.RESISTANT);

            // size has changed so must have weak side
            if (unsafeSides.size() != size)
                color = new GSColor(obsidianColor.getValue(), 255);

            size = unsafeSides.size();

            // is it a perfect hole
            if (size == 0) {
                holes.put(new AxisAlignedBB(pos), color);

            }
            // have one open side
            if (size == 1) {
                possibleWideHoles.put(pos, new Pair<>(unsafeSides.keySet().stream().findFirst().get(), color));
            }
        });

        // two wide and/or custom holes is enabled
        // we can guarantee all holes in possibleWideHoles
        // have only one open side
        String customHoleMode = customHoles.getValue();
        if (!customHoleMode.equalsIgnoreCase("Single")) {
            possibleWideHoles.forEach((pos, pair) -> {
                GSColor color = pair.getValue();
                BlockPos unsafePos = pair.getKey().offset(pos);

                // Custom allows hole in floor for second side
                boolean allowCustom = customHoleMode.equalsIgnoreCase("Custom");
                HashMap<BlockOffset, BlockSafety> unsafeSides = getUnsafeSides(unsafePos);

                int size = unsafeSides.size();

                unsafeSides.entrySet().removeIf(entry -> entry.getValue() == BlockSafety.RESISTANT);

                // size has changed so must have weak side
                if (unsafeSides.size() != size)
                    color = new GSColor(obsidianColor.getValue(), 255);

                if (allowCustom) {
                    if (unsafeSides.containsKey(BlockOffset.DOWN))
                        color = new GSColor(customColor.getValue(), 255);
                    unsafeSides.remove(BlockOffset.DOWN);
                }

                // is it a safe hole
                if (unsafeSides.size() >  1)
                    return;

                // it is
                double minX = Math.min(pos.x, unsafePos.x);
                double maxX = Math.max(pos.x, unsafePos.x) + 1;
                double minZ = Math.min(pos.z, unsafePos.z);
                double maxZ = Math.max(pos.z, unsafePos.z) + 1;

                holes.put(new AxisAlignedBB(minX, pos.y, minZ, maxX, pos.y + 1, maxZ), color);
            });

        }
    }

    private BlockSafety isBlockSafe(Block block) {
        if (block == Blocks.BEDROCK) {
            return BlockSafety.UNBREAKABLE;
        }
        if (block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST || block == Blocks.ANVIL) {
            return BlockSafety.RESISTANT;
        }
        return BlockSafety.BREAKABLE;
    }

    private HashMap<BlockOffset, BlockSafety> getUnsafeSides(BlockPos pos) {
        HashMap<BlockOffset, BlockSafety> output = new HashMap<>();
        BlockSafety temp;

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.DOWN.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.DOWN, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.NORTH.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.NORTH, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.SOUTH.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.SOUTH, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.EAST.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.EAST, temp);

        temp = isBlockSafe(mc.world.getBlockState(BlockOffset.WEST.offset(pos)).getBlock());
        if (temp != BlockSafety.UNBREAKABLE)
            output.put(BlockOffset.WEST, temp);

        return output;
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

        if (hideOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) return;

        switch (mode.getValue()) {
            case "Air": {
                if (flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1, fillColor, GeometryMasks.Quad.DOWN);
                }
                else {
                    RenderUtil.drawBox(hole, true, 1, fillColor, GeometryMasks.Quad.ALL);
                }
                break;
            }
            case "Ground": {
                RenderUtil.drawBox(hole.offset(0, -1, 0), true, 1, fillColor, GeometryMasks.Quad.ALL);
                break;
            }
            case "Flat": {
                RenderUtil.drawBox(hole, true, 1, fillColor, GeometryMasks.Quad.DOWN);
                break;
            }
            case "Slab": {
                if (flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1, fillColor, GeometryMasks.Quad.DOWN);
                }
                else {
                    RenderUtil.drawBox(hole, false, slabHeight.getValue(), fillColor, GeometryMasks.Quad.ALL);
                }
                break;
            }
            case "Double": {
                if (flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1, fillColor, GeometryMasks.Quad.DOWN);
                }
                else {
                    RenderUtil.drawBox(hole.setMaxY(hole.maxY + 1), true, 2, fillColor, GeometryMasks.Quad.ALL);
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
                    RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, GeometryMasks.Quad.DOWN);
                }
                else {
                    RenderUtil.drawBoundingBox(hole, width.getValue(), outlineColor);
                }
                break;
            }
            case "Ground": {
                RenderUtil.drawBoundingBox(hole.offset(0, -1, 0), width.getValue(), outlineColor);
                break;
            }
            case "Flat": {
                RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, GeometryMasks.Quad.DOWN);
                break;
            }
            case "Slab": {
                if (this.flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, GeometryMasks.Quad.DOWN);
                }
                else {
                    RenderUtil.drawBoundingBox(hole.setMaxY(hole.minY + slabHeight.getValue()), width.getValue(), outlineColor);
                }
                break;
            }
            case "Double": {
                if (this.flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, GeometryMasks.Quad.DOWN);
                }
                else {
                    RenderUtil.drawBoundingBox(hole.setMaxY(hole.maxY + 1), width.getValue(), outlineColor);
                }
                break;
            }
        }
    }

    private enum BlockSafety {
        UNBREAKABLE,
        RESISTANT,
        BREAKABLE
    }

    private enum BlockOffset {
        DOWN(0, -1, 0),
        UP(0, 1, 0),
        NORTH(0, 0, -1),
        SOUTH(0, 0, 1),
        WEST(-1, 0, 0),
        EAST(1, 0, 0);

        private final int x;
        private final int y;
        private final int z;

        BlockOffset(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public BlockPos offset(BlockPos pos) {
            return pos.add(x, y, z);
        }
    }
}