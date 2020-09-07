package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.world.BlockUtils;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.api.util.color.Rainbow;
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

public class VoidESP extends Module{
	public VoidESP(){
		super("VoidESP", Category.Render);
	}

	Setting.Boolean rainbow;
	Setting.Integer renderDistance;
	Setting.Integer activeYValue;
	Setting.Mode renderType;
	Setting.Mode renderMode;

	public void setup(){
		ArrayList<String> render = new ArrayList<>();
		render.add("Outline");
		render.add("Fill");
		render.add("Both");

		ArrayList<String> modes = new ArrayList<>();
		modes.add("Box");
		modes.add("Flat");

		rainbow = registerBoolean("Rainbow", "Rainbow", false);
		renderDistance = registerInteger("Distance", "Distance", 10, 1, 40);
		activeYValue = registerInteger("Activate Y", "ActivateY", 20, 0, 256);
		renderType = registerMode("Render", "Render", render, "Both");
		renderMode = registerMode("Mode", "Mode", modes, "Flat");
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
		}else{
			voidHoles.clear();
		}

		List<BlockPos> blockPosList = BlockUtils.getCircle(getPlayerPos(), 0, renderDistance.getValue(), false);

		for (BlockPos blockPos : blockPosList){
			if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.BEDROCK)){
				continue;
			}
			if (isAnyBedrock(blockPos, Offsets.center)){
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
		voidHoles.forEach(blockPos ->{
			GameSenseTessellator.prepare(GL11.GL_QUADS);
			if (renderMode.getValue().equalsIgnoreCase("Box")){
				drawBox(blockPos, 255, 255, 0);
			} else{
				drawFlat(blockPos, 255, 255, 0);
			}
			GameSenseTessellator.release();
			GameSenseTessellator.prepare(7);
			drawOutline(blockPos,1,255,255,0);
			GameSenseTessellator.release();
		});
	}

	public static BlockPos getPlayerPos(){
		return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
	}

	private boolean isAnyBedrock(BlockPos origin, BlockPos[] offset){
		for (BlockPos pos : offset){
			if (mc.world.getBlockState(origin.add(pos)).getBlock().equals(Blocks.BEDROCK)){
				return true;
			}
		} return false;
	}

	private static class Offsets{
		static final BlockPos[] center ={
				new BlockPos(0, 0, 0),
				new BlockPos(0, 1, 0),
				new BlockPos(0, 2, 0)
		};
	}

	public void drawFlat(BlockPos blockPos, int r, int g, int b){
		if (renderType.getValue().equalsIgnoreCase("Fill") || renderType.getValue().equalsIgnoreCase("Both")){
			Color color;
			Color c = Rainbow.getColor();
			AxisAlignedBB bb = mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos);
			if (renderMode.getValue().equalsIgnoreCase("Flat")){
				if (rainbow.getValue()) color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 50);
				else color = new Color(r, g, b, 50);
				GameSenseTessellator.drawBox(blockPos, color.getRGB(), GeometryMasks.Quad.DOWN);
			}
		}
	}

	private void drawBox(BlockPos blockPos, int r, int g, int b){
		if (renderType.getValue().equalsIgnoreCase("Fill") || renderType.getValue().equalsIgnoreCase("Both")){
			Color color;
			Color c = Rainbow.getColor();
			AxisAlignedBB bb = mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos);
			if (rainbow.getValue()) color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 50);
			else color = new Color(r, g, b, 50);
			GameSenseTessellator.drawBox(blockPos, color.getRGB(), GeometryMasks.Quad.ALL);
		}
	}

	public void drawOutline(BlockPos blockPos, int width, int r, int g, int b){
		if (renderType.getValue().equalsIgnoreCase("Outline") || renderType.getValue().equalsIgnoreCase("Both")){
			final float[] hue ={(System.currentTimeMillis() % (360 * 32)) / (360f * 32)};
			int rgb = Color.HSBtoRGB(hue[0], 1, 1);
			int r1 = (rgb >> 16) & 0xFF;
			int g2 = (rgb >> 8) & 0xFF;
			int b3 = rgb & 0xFF;
			hue[0] += .02f;
			if (renderMode.getValue().equalsIgnoreCase("Box")){
				if (rainbow.getValue()){
					GameSenseTessellator.drawBoundingBoxBlockPos(blockPos, width, r1, g2, b3, 255);
				} else{
					GameSenseTessellator.drawBoundingBoxBlockPos(blockPos, width, r, g, b, 255);
				}
			}
			if (renderMode.getValue().equalsIgnoreCase("Flat")){
				if (rainbow.getValue()){
					GameSenseTessellator.drawBoundingBoxBottom2(blockPos, width, r1, g2, b3, 255);
				} else{
					GameSenseTessellator.drawBoundingBoxBottom2(blockPos, width, r, g, b, 255);
				}
			}
		}
	}
}