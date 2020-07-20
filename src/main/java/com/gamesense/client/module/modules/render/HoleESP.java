package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.GameSenseTessellator;
import com.gamesense.api.util.GeometryMasks;
import com.gamesense.api.util.Rainbow;
import com.gamesense.client.module.Module;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HoleESP extends Module {


    public HoleESP() {
        super("HoleESP", Category.Render);
    }

    public static Setting.i rangeS;
    Setting.i r;
    Setting.i g;
    Setting.i b;
    Setting.i a;
    Setting.i r2;
    Setting.i g2;
    Setting.i b2;
    Setting.b rainbow;
    Setting.mode mode;
    Setting.i width;
    Setting.b hideOwn;
    Setting.b flatOwn;

    private final BlockPos[] surroundOffset = {
            new BlockPos(0, -1, 0), // down
            new BlockPos(0, 0, -1), // north
            new BlockPos(1, 0, 0), // east
            new BlockPos(0, 0, 1), // south
            new BlockPos(-1, 0, 0) // west
    };

    public void setup(){

        rangeS = registerI("Range", 5, 1, 20);
        r = registerI("Red", 255 ,0 ,255);
        g = registerI("Green", 255, 0 ,255);
        b = registerI("Blue", 255 ,0 ,255);
        r2 = registerI("RedObby", 255, 0 ,255);
        g2 = registerI("GreenObby", 255, 0, 255);
        b2 = registerI("BlueObby", 255 ,0 ,255);
        a = registerI("Alpha", 50 , 0, 255);
        hideOwn = registerB("HideOwn", false);
        flatOwn = registerB("FlatOwn", false);
        rainbow = registerB("Rainbow", false);
        width = registerI("Width", 3 , 1, 10);

        ArrayList<String> modes = new ArrayList<>();
        modes.add("Box");

        mode = registerMode("Mode", modes, "Box");
    }

    private ConcurrentHashMap<BlockPos, Boolean> safeHoles;

    @Override
    public void onUpdate() {

        if (safeHoles == null) {
            safeHoles = new ConcurrentHashMap<>();
        } else {
            safeHoles.clear();
        }

        int range = (int) Math.ceil(rangeS.getValue());

        List<BlockPos> blockPosList = getSphere(getPlayerPos(), range, range, false, true, 0);

        for (BlockPos pos : blockPosList) {

            // block gotta be air
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            // block 1 above gotta be air
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            // block 2 above gotta be air
            if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (this.hideOwn.getValue() && pos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) {
                continue;
            }

            boolean isSafe = true;
            boolean isBedrock = true;

            for (BlockPos offset : surroundOffset) {
                Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
                if (block != Blocks.BEDROCK) {
                    isBedrock = false;
                }
                if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                    isSafe = false;
                    break;
                }
            }

            if (isSafe) {
                safeHoles.put(pos, isBedrock);
            }

        }

    }

    @Override
    public void onWorldRender(final RenderEvent event) {
        if (mc.player == null || safeHoles == null) {
            return;
        }

        if (safeHoles.isEmpty()) {
            return;
        }

        if(mode.getValue().equalsIgnoreCase("box"))
            GameSenseTessellator.prepare(GL11.GL_QUADS);

        safeHoles.forEach((blockPos, isBedrock) -> {
            if(isBedrock) {
                drawBox(blockPos, (int)r.getValue(), (int)g.getValue(), (int)b.getValue());
            } else drawBox(blockPos, (int)r2.getValue(), (int)g2.getValue(), (int)b2.getValue());

        });

        if(mode.getValue().equalsIgnoreCase("box"))
            GameSenseTessellator.release();

    }

    private void drawBox(BlockPos blockPos, int r, int g, int b) {
        Color color;
        Color c = Rainbow.getColor();
        AxisAlignedBB bb = mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos);
        if(rainbow.getValue()) color = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)a.getValue());
        else color = new Color(r, g, b, (int)a.getValue());


        if(mode.getValue().equalsIgnoreCase("box")) {
            if(this.flatOwn.getValue() && blockPos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)))
                GameSenseTessellator.drawBox(blockPos, color.getRGB(), GeometryMasks.Quad.DOWN);
            else
                GameSenseTessellator.drawBox(blockPos, color.getRGB(), GeometryMasks.Quad.ALL);
        } else {
                GameSenseTessellator.drawBoundingBox(bb, width.getValue(), color.getRGB());
        }
    }




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

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

}
