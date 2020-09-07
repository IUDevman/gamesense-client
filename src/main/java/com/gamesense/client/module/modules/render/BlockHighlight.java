package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.api.util.color.Rainbow;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.ColorMain;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BlockHighlight extends Module{
	public BlockHighlight(){
		super("BlockHighlight", Category.Render);
	}

	Setting.Integer w;
	Setting.Boolean shade;
	int c; //outline
	int c2; //fill

	public void setup(){
		shade = registerBoolean("Fill", "Fill", false);
		w = registerInteger("Width", "Width", 2, 1, 10);
	}

	public void onWorldRender(RenderEvent event){
		RayTraceResult ray = mc.objectMouseOver;
		AxisAlignedBB bb;
		BlockPos pos;
		ColorMain colorMain = ((ColorMain) ModuleManager.getModuleByName("Colors"));
		if (ColorMain.rainbow.getValue()){
			c = Rainbow.getColorWithOpacity(255).getRGB();
			c2 = Rainbow.getColorWithOpacity(50).getRGB();
		}
		else{
			c = new Color(ColorMain.Red.getValue(), ColorMain.Green.getValue(), ColorMain.Blue.getValue(), 255).getRGB();
			c2 = new Color(ColorMain.Red.getValue(), ColorMain.Green.getValue(), ColorMain.Blue.getValue(), 50).getRGB();
		}
		if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK){
			pos = ray.getBlockPos();
			bb = mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos);
			if (bb != null && pos != null && mc.world.getBlockState(pos).getMaterial() != Material.AIR){
				GameSenseTessellator.prepareGL();
				GameSenseTessellator.drawBoundingBox(bb, w.getValue(), c);
				GameSenseTessellator.releaseGL();
				if (shade.getValue()){
					GameSenseTessellator.prepare(GL11.GL_QUADS);
					GameSenseTessellator.drawBox(bb, c2, GeometryMasks.Quad.ALL);
					GameSenseTessellator.release();
				}
			}
		}
	}
}
