package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BlockHighlight extends Module {
    public BlockHighlight() {
        super("BlockHighlight", Category.Render);
    }

    Setting.Integer w;
    Setting.Boolean shade;
	Setting.ColorSetting color;

    public void setup() {
        shade = registerBoolean("Fill", "Fill", false);
        w = registerInteger("Width", "Width", 2, 1, 10);
		color = registerColor("Color","Color");
    }

    public void onWorldRender(RenderEvent event) {
        RayTraceResult ray = mc.objectMouseOver;
        AxisAlignedBB bb;
        BlockPos pos;
		Color rgbColor=color.getValue();
        int c=new Color(rgbColor.getRed(),rgbColor.getGreen(),rgbColor.getBlue(),255).getRGB();
		int c2=new Color(rgbColor.getRed(),rgbColor.getGreen(),rgbColor.getBlue(),50).getRGB();
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            pos = ray.getBlockPos();
            bb = mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos);
            if (bb != null && pos != null && mc.world.getBlockState(pos).getMaterial() != Material.AIR) {
                GameSenseTessellator.prepareGL();
                GameSenseTessellator.drawBoundingBox(bb, w.getValue(), c);
                GameSenseTessellator.releaseGL();
                if (shade.getValue()) {
                    GameSenseTessellator.prepare(GL11.GL_QUADS);
                    GameSenseTessellator.drawBox(bb, c2, GeometryMasks.Quad.ALL);
                    GameSenseTessellator.release();
                }
            }
        }
    }
}
