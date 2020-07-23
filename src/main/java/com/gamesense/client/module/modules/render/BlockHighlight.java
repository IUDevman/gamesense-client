package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.GameSenseTessellator;
import com.gamesense.api.util.Rainbow;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.ColorMain;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.awt.*;

public class BlockHighlight extends Module {
    public BlockHighlight() {
        super("BlockHighlight", Category.Render);
    }

    Setting.i w;
    Setting.i opacity;
    int c;

    public void setup() {
        w = registerI("Width", 2, 1, 10);
        opacity = registerI("Alpha", 50 , 0, 255);
    }

    public void onWorldRender(RenderEvent event) {
        RayTraceResult ray = mc.objectMouseOver;
        AxisAlignedBB bb;
        BlockPos pos;
        ColorMain colorMain = ((ColorMain) ModuleManager.getModuleByName("Colors"));
        if (colorMain.Rainbow.getValue()){
            c = Rainbow.getColorWithOpacity(opacity.getValue()).getRGB();
        }
        else {
            c = new Color(colorMain.Red.getValue(), colorMain.Green.getValue(), colorMain.Blue.getValue(), opacity.getValue()).getRGB();
        }
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            pos = ray.getBlockPos();
            bb = mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos);
            if (bb != null && pos != null && mc.world.getBlockState(pos).getMaterial() != Material.AIR) {
                GameSenseTessellator.prepareGL();
                GameSenseTessellator.drawBoundingBox(bb, (int) w.getValue(), c);
                GameSenseTessellator.releaseGL();
            }
        }
    }
}
