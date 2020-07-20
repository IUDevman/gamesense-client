package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.GameSenseTessellator;
import com.gamesense.api.util.Rainbow;
import com.gamesense.client.module.Module;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.awt.*;

public class BlockHighlight extends Module {
    public BlockHighlight() {
        super("BlockHighlight", Category.Render);
    }

    Setting.i r;
    Setting.i g;
    Setting.i b;
    Setting.i a;
    Setting.i w;
    Setting.b rainbow;

    public void setup() {
        r = registerI("Red", 255, 0 ,255);
        g = registerI("Green", 255, 0 , 255);
        b = registerI("Blue", 255, 0 ,255);
        a = registerI("Alpha", 255, 0 ,255);
        w = registerI("Width", 2, 1, 10);
        rainbow = registerB("Rainbow", false);
    }

    public void onWorldRender(RenderEvent event) {
        RayTraceResult ray = mc.objectMouseOver;
        AxisAlignedBB bb;
        BlockPos pos;
        Color c;
        Color color = Rainbow.getColor();
        if (rainbow.getValue())
            c = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) a.getValue());
        else
            c = new Color((int) r.getValue(), (int) g.getValue(), (int) b.getValue(), (int) a.getValue());
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            pos = ray.getBlockPos();
            bb = mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos);
            if (bb != null && pos != null && mc.world.getBlockState(pos).getMaterial() != Material.AIR) {
                GameSenseTessellator.prepareGL();
                GameSenseTessellator.drawBoundingBox(bb, (int) w.getValue(), c.getRGB());
                GameSenseTessellator.releaseGL();
            }
        }
    }
}
