package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.GameSenseTessellator;
import com.gamesense.client.module.Module;
import net.minecraft.tileentity.*;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

public class StorageESP extends Module {
    public StorageESP() {
        super("StorageESP", Category.Render);
    }

    Setting.i a;
    Setting.i w;

    ConcurrentHashMap<TileEntity, String> chests = new ConcurrentHashMap<>();

    public void setup(){
        a = registerI("Alpha", 150 , 0 ,255);
        w = registerI("Width", 1 , 1 ,10);

    }

    public void onUpdate(){
        mc.world.loadedTileEntityList.forEach(e->{
            chests.put(e, "");
        });
    }

    public void onWorldRender(RenderEvent event){
        Color c1 = new Color(200, 100, 0, (int)a.getValue());
        Color c2 = new Color(200, 0, 200, (int)a.getValue());
        Color c3 = new Color(150, 150, 150, (int)a.getValue());
        if(chests != null && chests.size() > 0){
            GameSenseTessellator.prepareGL();
            chests.forEach((c, t)->{
                if(mc.world.loadedTileEntityList.contains(c)) {
                    if(c instanceof TileEntityChest
                            || c instanceof TileEntityShulkerBox)
                        //OsirisTessellator.drawBox(c.getPos(), c1.getRGB(), GeometryMasks.Quad.ALL);
                        GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(c.getPos()).getSelectedBoundingBox(mc.world, c.getPos()), (float)w.getValue(), c1.getRGB());
                    if(c instanceof TileEntityEnderChest)
                        //OsirisTessellator.drawBox(c.getPos(), c2.getRGB(), GeometryMasks.Quad.ALL);
                        GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(c.getPos()).getSelectedBoundingBox(mc.world, c.getPos()), (float)w.getValue(), c2.getRGB());
                    if(c instanceof TileEntityDispenser
                            || c instanceof TileEntityFurnace
                            || c instanceof TileEntityHopper)
                        //OsirisTessellator.drawBox(c.getPos(), c3.getRGB(), GeometryMasks.Quad.ALL);
                        GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(c.getPos()).getSelectedBoundingBox(mc.world, c.getPos()), (float)w.getValue(), c3.getRGB());
                }
            });
            GameSenseTessellator.releaseGL();
        }
    }
}
