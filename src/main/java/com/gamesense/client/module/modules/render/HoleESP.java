package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.client.module.Module;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HoleESP extends Module {
	public HoleESP(){
		super("HoleESP", Category.Render);
	}

	public static Setting.Integer rangeS;
	Setting.Boolean hideOwn;
	Setting.Boolean flatOwn;
	Setting.Boolean renderBurrow;
	Setting.Mode mode;
	Setting.Mode type;
	Setting.Double slabHeight;
	Setting.Integer width;
	Setting.ColorSetting bedrockColor;
	Setting.ColorSetting otherColor;
	Setting.ColorSetting burrowColor;

	public void setup(){
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

		rangeS = registerInteger("Range", "Range", 5, 1, 20);
		renderBurrow = registerBoolean("Burrow", "Burrow", true);
		hideOwn = registerBoolean("Hide Own", "HideOwn", false);
		flatOwn = registerBoolean("Flat Own", "FlatOwn", false);
		type = registerMode("Render", "Render", render, "Both");
		mode = registerMode("Mode", "Mode", modes, "Air");
		slabHeight = registerDouble("Slab Height", "SlabHeight", 0.5, 0.1, 1.5);
		width = registerInteger("Width","Width",1,1,10);
		bedrockColor = registerColor("Bedrock Color","BedrockColor", new GSColor(0,255,0));
		otherColor = registerColor("Obsidian Color","ObsidianColor", new GSColor(255,0,0));
		burrowColor = registerColor("Burrow Color", "BurrowColor", new GSColor(255, 255, 0));
	}

	private ConcurrentHashMap<BlockPos, GSColor> renderHoles;

	//defines the render borders
	private final BlockPos[] surroundOffset ={
			new BlockPos(0, -1, 0), // down
			new BlockPos(0, 0, -1), // north
			new BlockPos(1, 0, 0), // east
			new BlockPos(0, 0, 1), // south
			new BlockPos(-1, 0, 0) // west
	};

	//defines the area for the client to search
	public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y){
		List<BlockPos> circleblocks = new ArrayList<>();
		int cx = loc.getX();
		int cy = loc.getY();
		int cz = loc.getZ();
		for (int x = cx - (int) r; x <= cx + r; x++){
			for (int z = cz - (int) r; z <= cz + r; z++){
				for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++){
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
					if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))){
						BlockPos l = new BlockPos(x, y + plus_y, z);
						circleblocks.add(l);
					}
				}
			}
		}
		return circleblocks;
	}

	//gets the players location
	public static BlockPos getPlayerPos(){
		return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
	}

	public void onUpdate(){
		if (mc.player == null || mc.world == null){
			return;
		}

		if (renderHoles == null){
			renderHoles = new ConcurrentHashMap<>();
		}
		else {
			renderHoles.clear();
		}

		int range = (int) Math.ceil(rangeS.getValue());

		List<BlockPos> blockPosList = getSphere(getPlayerPos(), range, range, false, true, 0);
		for (BlockPos pos : blockPosList){

			if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)){
				continue;
			}
			if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)){
				continue;
			}
			if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)){
				continue;
			}
			if (this.hideOwn.getValue() && pos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))){
				continue;
			}

			boolean isSafe = true;
			GSColor color = new GSColor(bedrockColor.getValue(), 255);

			for (BlockPos offset : surroundOffset){
				Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
				if (block != Blocks.BEDROCK){
					color = new GSColor(otherColor.getValue(), 255);
				}
				if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL){
					isSafe = false;
					break;
				}
			}
			if (isSafe){
				renderHoles.put(pos, color);
			}
		}

		if (renderBurrow.getValue()) {
			mc.world.playerEntities.stream().forEach(entityPlayer -> {
				if (entityPlayer == mc.player) {
					return;
				}

				BlockPos blockPos = new BlockPos(roundValueToCenter(mc.player.posX), mc.player.posY, roundValueToCenter(mc.player.posZ));

				if (blockPos == mc.player.getPosition()) {
					return;
				}

				if (blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= rangeS.getValue()) {

					if (mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockPos).getBlock() == Blocks.ENDER_CHEST) {
						renderHoles.put(blockPos, new GSColor(burrowColor.getValue(), 255));
					}
				}
			});
		}
	}

	public void onWorldRender(RenderEvent event){
		if (mc.player == null || mc.world == null || renderHoles == null || renderHoles.isEmpty()){
			return;
		}

		renderHoles.forEach(((blockPos, color) -> {
			renderHoles(blockPos, color);
		}));
	}

	private void renderHoles(BlockPos blockPos, GSColor color){
		switch (type.getValue()){
			case "Outline": {
				renderOutline(blockPos, color);
				break;
			}
			case "Fill": {
				renderFill(blockPos, color);
				break;
			}
			case "Both": {
				renderOutline(blockPos, color);
				renderFill(blockPos, color);
				break;
			}
		}
	}

	private void renderFill(BlockPos blockPos, GSColor color){
		GSColor fillColor = new GSColor(color, 50);

		switch (mode.getValue()){
			case "Air": {
				if (flatOwn.getValue() && blockPos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) {
					GameSenseTessellator.drawBox(blockPos, 1, fillColor, GeometryMasks.Quad.DOWN);
				}
				else {
					GameSenseTessellator.drawBox(blockPos, 1, fillColor, GeometryMasks.Quad.ALL);
				}
				break;
			}
			case "Ground": {
				GameSenseTessellator.drawBox(blockPos.down(), 1, fillColor, GeometryMasks.Quad.ALL);
				break;
			}
			case "Flat": {
				GameSenseTessellator.drawBox(blockPos, 1, fillColor, GeometryMasks.Quad.DOWN);
				break;
			}
			case "Slab": {
				if (flatOwn.getValue() && blockPos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) {
					GameSenseTessellator.drawBox(blockPos, 1, fillColor, GeometryMasks.Quad.DOWN);
				}
				else {
					GameSenseTessellator.drawBox(blockPos, slabHeight.getValue(), fillColor, GeometryMasks.Quad.ALL);
				}
				break;
			}
			case "Double": {
				if (flatOwn.getValue() && blockPos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) {
					GameSenseTessellator.drawBox(blockPos, 1, fillColor, GeometryMasks.Quad.DOWN);
				}
				else {
					GameSenseTessellator.drawBox(blockPos, 2, fillColor, GeometryMasks.Quad.ALL);
				}
				break;
			}
		}
	}

	private void renderOutline(BlockPos blockPos, GSColor color){
		GSColor outlineColor = new GSColor(color, 255);

		switch (mode.getValue()){
			case "Air": {
				if (flatOwn.getValue() && blockPos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) {
					GameSenseTessellator.drawBoundingBoxWithSides(blockPos, width.getValue(), outlineColor, GeometryMasks.Quad.DOWN);
				}
				else {
					GameSenseTessellator.drawBoundingBox(blockPos, 1, width.getValue(), outlineColor);
				}
				break;
			}
			case "Ground": {
				GameSenseTessellator.drawBoundingBox(blockPos.down(), 1, width.getValue(), outlineColor);
				break;
			}
			case "Flat": {
				GameSenseTessellator.drawBoundingBoxWithSides(blockPos, width.getValue(), outlineColor, GeometryMasks.Quad.DOWN);
				break;
			}
			case "Slab": {
				if (this.flatOwn.getValue() && blockPos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) {
					GameSenseTessellator.drawBoundingBoxWithSides(blockPos, width.getValue(), outlineColor, GeometryMasks.Quad.DOWN);
				}
				else {
					GameSenseTessellator.drawBoundingBox(blockPos, slabHeight.getValue(), width.getValue(), outlineColor);
				}
				break;
			}
			case "Double": {
				if (this.flatOwn.getValue() && blockPos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) {
					GameSenseTessellator.drawBoundingBoxWithSides(blockPos, width.getValue(), outlineColor, GeometryMasks.Quad.DOWN);
				}
				else {
					GameSenseTessellator.drawBoundingBox(blockPos, 2, width.getValue(), outlineColor);
				}
				break;
			}
		}
	}

	private double roundValueToCenter(double inputVal) {
		double roundVal = Math.round(inputVal);

		if (roundVal > inputVal) {
			roundVal -= 0.5;
		}
		else if (roundVal <= inputVal) {
			roundVal += 0.5;
		}

		return roundVal;
	}
}