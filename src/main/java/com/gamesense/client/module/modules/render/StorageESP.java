package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.client.module.Module;
import net.minecraft.tileentity.*;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

public class StorageESP extends Module {
    public StorageESP() {
        super("StorageESP", Category.Render);
    }

    Setting.Integer w;
	Setting.ColorSetting cl1;
	Setting.ColorSetting cl2;
	Setting.ColorSetting cl3;
	Setting.ColorSetting cl4;
    ConcurrentHashMap<TileEntity, String> chests = new ConcurrentHashMap<>();

    public void setup(){
        w = registerInteger("Width", "Width", 2 , 1 ,10);
		cl1=registerColor("Chest Color","ChestColor",new Color(255,255,0));
		cl2=registerColor("Ender Chest Color","EnderChestColor",new Color(180,70,200));
		cl3=registerColor("Shulker Box Color","ShulkerBoxColor",new Color(150,150,150));
		cl4=registerColor("Other Container Color","OtherColor",new Color(255,0,0));
    }

    public void onUpdate(){
        mc.world.loadedTileEntityList.forEach(e->{
            chests.put(e, "");
        });
    }

    public void onWorldRender(RenderEvent event){
        Color c1 = new Color(cl1.getValue().getRed(),cl1.getValue().getGreen(),cl1.getValue().getBlue(),255);
		Color c2 = new Color(cl2.getValue().getRed(),cl2.getValue().getGreen(),cl2.getValue().getBlue(),255);
		Color c3 = new Color(cl3.getValue().getRed(),cl3.getValue().getGreen(),cl3.getValue().getBlue(),255);
		Color c4 = new Color(cl4.getValue().getRed(),cl4.getValue().getGreen(),cl4.getValue().getBlue(),255);
        if(chests != null && chests.size() > 0){
            GameSenseTessellator.prepareGL();
            chests.forEach((c, t)->{
                if(mc.world.loadedTileEntityList.contains(c)) {
                    if(c instanceof TileEntityChest)
                            GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(c.getPos()).getSelectedBoundingBox(mc.world, c.getPos()), (float)w.getValue(), c1.getRGB());
                    if(c instanceof TileEntityEnderChest)
                        GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(c.getPos()).getSelectedBoundingBox(mc.world, c.getPos()), (float)w.getValue(), c2.getRGB());
                    if(c instanceof TileEntityShulkerBox)
                        GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(c.getPos()).getSelectedBoundingBox(mc.world, c.getPos()), (float)w.getValue(), c4.getRGB());
                    if(c instanceof TileEntityDispenser
                            || c instanceof TileEntityFurnace
                            || c instanceof TileEntityHopper)
                        GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(c.getPos()).getSelectedBoundingBox(mc.world, c.getPos()), (float)w.getValue(), c3.getRGB());
                }
            });
            GameSenseTessellator.releaseGL();
        }
    }
}
