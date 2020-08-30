package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.world.BlockUtils;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.client.module.Module;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Hoosiers on 08/14/20
 */

public class VoidESP extends Module {
    public VoidESP(){
        super("VoidESP", Category.Render);
    }

    Setting.Integer renderDistance;
    Setting.Integer activeYValue;
    Setting.Mode renderType;
    Setting.Mode renderMode;
	Setting.ColorSetting color;

    public void setup(){
        ArrayList<String> render = new ArrayList<>();
        render.add("Outline");
        render.add("Fill");
        render.add("Both");

        ArrayList<String> modes = new ArrayList<>();
        modes.add("Box");
        modes.add("Flat");

        renderDistance = registerInteger("Distance", "Distance", 10, 1, 40);
        activeYValue = registerInteger("Activate Y", "ActivateY", 20, 0, 256);
        renderType = registerMode("Render", "Render", render, "Both");
        renderMode = registerMode("Mode", "Mode", modes, "Flat");
		color=registerColor("Color","Color");
    }

    private ConcurrentSet<BlockPos> voidHoles;

    @Override
    public void onUpdate(){
        if (mc.player.dimension == 1){
            return;
        }
        if (mc.player.getPosition().getY() > activeYValue.getValue()){
            return;
        }
        if (voidHoles == null){
            voidHoles = new ConcurrentSet<>();
        }else {
            voidHoles.clear();
        }

        List<BlockPos> blockPosList = BlockUtils.getCircle(getPlayerPos(), 0, renderDistance.getValue(), false);

        for (BlockPos blockPos : blockPosList){
            if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.BEDROCK)) {
                continue;
            }
            if (isAnyBedrock(blockPos, Offsets.center)) {
                continue;
            }
            voidHoles.add(blockPos);
        }
    }

    @Override
    public void onWorldRender(RenderEvent event){
        if (mc.player == null || voidHoles == null){
            return;
        }
        if (mc.player.getPosition().getY() > activeYValue.getValue()){
            return;
        }
        if (voidHoles.isEmpty()){
            return;
        }
        voidHoles.forEach(blockPos -> {
            GameSenseTessellator.prepare(GL11.GL_QUADS);
            if (renderMode.getValue().equalsIgnoreCase("Box")){
                drawBox(blockPos, 255, 255, 0);
            } else {
                drawFlat(blockPos, 255, 255, 0);
            }
            GameSenseTessellator.release();
            GameSenseTessellator.prepare(7);
            drawOutline(blockPos,1,255,255,0);
            GameSenseTessellator.release();
        });
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    private boolean isAnyBedrock(BlockPos origin, BlockPos[] offset) {
        for (BlockPos pos : offset) {
            if (mc.world.getBlockState(origin.add(pos)).getBlock().equals(Blocks.BEDROCK)) {
                return true;
            }
        } return false;
    }

    private static class Offsets {
        static final BlockPos[] center = {
                new BlockPos(0, 0, 0),
                new BlockPos(0, 1, 0),
                new BlockPos(0, 2, 0)
        };
    }

    public void drawFlat(BlockPos blockPos, int r, int g, int b) {
        if (renderType.getValue().equalsIgnoreCase("Fill") || renderType.getValue().equalsIgnoreCase("Both")) {
            Color c=new Color(color.getValue().getRed(),color.getValue().getGreen(),color.getValue().getBlue(),50);
            AxisAlignedBB bb = mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos);
            if (renderMode.getValue().equalsIgnoreCase("Flat")) {
                GameSenseTessellator.drawBox(blockPos, c.getRGB(), GeometryMasks.Quad.DOWN);
            }
        }
    }

    private void drawBox(BlockPos blockPos, int r, int g, int b) {
        if (renderType.getValue().equalsIgnoreCase("Fill") || renderType.getValue().equalsIgnoreCase("Both")) {
            Color c=new Color(color.getValue().getRed(),color.getValue().getGreen(),color.getValue().getBlue(),50);
            AxisAlignedBB bb = mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos);
            GameSenseTessellator.drawBox(blockPos, c.getRGB(), GeometryMasks.Quad.ALL);
        }
    }

    public void drawOutline(BlockPos blockPos, int width, int r, int g, int b) {
        if (renderType.getValue().equalsIgnoreCase("Outline") || renderType.getValue().equalsIgnoreCase("Both")) {
			int r1=color.getValue().getRed();
			int g1=color.getValue().getGreen();
			int b1=color.getValue().getBlue();
            if (renderMode.getValue().equalsIgnoreCase("Box")) {
                GameSenseTessellator.drawBoundingBoxBlockPos(blockPos, width, r1, g1, b1, 255);
            }
            if (renderMode.getValue().equalsIgnoreCase("Flat")) {
                GameSenseTessellator.drawBoundingBoxBottom2(blockPos, width, r1, g1, b1, 255);
            }
        }
    }
}