package com.gamesense.client.module.modules.render;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.api.util.world.BlockUtils;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.client.module.Module;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

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
	Setting.Integer width;
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
		width=registerInteger("Width","Width",1,1,10);
		color=registerColor("Color","Color",new GSColor(255,255,0));
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
				drawBox(blockPos);
			} else {
				drawFlat(blockPos);
			}
			GameSenseTessellator.release();
			GameSenseTessellator.prepare(7);
			drawOutline(blockPos,width.getValue());
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

	private void drawFlat(BlockPos blockPos) {
		if (renderType.getValue().equalsIgnoreCase("Fill") || renderType.getValue().equalsIgnoreCase("Both")) {
			GSColor c=new GSColor(color.getValue(),50);
			//AxisAlignedBB bb = mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos);
			if (renderMode.getValue().equalsIgnoreCase("Flat")) {
				GameSenseTessellator.drawBox(blockPos, c, GeometryMasks.Quad.DOWN);
			}
		}
	}

	private void drawBox(BlockPos blockPos) {
		if (renderType.getValue().equalsIgnoreCase("Fill") || renderType.getValue().equalsIgnoreCase("Both")) {
			GSColor c=new GSColor(color.getValue(),50);
			//AxisAlignedBB bb = mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos);
			GameSenseTessellator.drawBox(blockPos, c, GeometryMasks.Quad.ALL);
		}
	}

	private void drawOutline(BlockPos blockPos, int width) {
		if (renderType.getValue().equalsIgnoreCase("Outline") || renderType.getValue().equalsIgnoreCase("Both")) {
			if (renderMode.getValue().equalsIgnoreCase("Box")) {
				GameSenseTessellator.drawBoundingBoxBlockPos(blockPos, width, color.getValue());
			}
			if (renderMode.getValue().equalsIgnoreCase("Flat")) {
				GameSenseTessellator.drawBoundingBoxBottom2(blockPos, width, color.getValue());
			}
		}
	}
}