package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.api.util.GSColor;
import com.gamesense.client.module.Module;
import net.minecraft.tileentity.*;

import java.util.concurrent.ConcurrentHashMap;

public class StorageESP extends Module{
	public StorageESP(){
		super("StorageESP", Category.Render);
	}

	Setting.Integer w;
	Setting.ColorSetting c1;
	Setting.ColorSetting c2;
	Setting.ColorSetting c3;
	Setting.ColorSetting c4;
	ConcurrentHashMap<TileEntity, String> chests = new ConcurrentHashMap<>();

	public void setup(){
		w = registerInteger("Width", "Width", 2 , 1 ,10);
		c1=registerColor("Chest Color","ChestColor",new GSColor(255,255,0));
		c2=registerColor("EnderChest Color","EnderChestColor",new GSColor(180,70,200));
		c3=registerColor("Shulker Color","ShulkerBoxColor",new GSColor(255,0,0));
		c4=registerColor("Other Color","OtherColor",new GSColor(150,150,150));
	}

	public void onUpdate(){
		mc.world.loadedTileEntityList.forEach(e -> {
			chests.put(e, "");
		});
	}

	public void onWorldRender(RenderEvent event){
		if(chests != null && chests.size() > 0){
			GameSenseTessellator.prepareGL();
			chests.forEach((c, t)->{
				if(mc.world.loadedTileEntityList.contains(c)) {
					if(c instanceof TileEntityChest)
							GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(c.getPos()).getSelectedBoundingBox(mc.world, c.getPos()), (float)w.getValue(), c1.getValue());
					if(c instanceof TileEntityEnderChest)
						GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(c.getPos()).getSelectedBoundingBox(mc.world, c.getPos()), (float)w.getValue(), c2.getValue());
					if(c instanceof TileEntityShulkerBox)
						GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(c.getPos()).getSelectedBoundingBox(mc.world, c.getPos()), (float)w.getValue(), c3.getValue());
					if(c instanceof TileEntityDispenser
							|| c instanceof TileEntityFurnace
							|| c instanceof TileEntityHopper)
						GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(c.getPos()).getSelectedBoundingBox(mc.world, c.getPos()), (float)w.getValue(), c4.getValue());
				}
			});
			GameSenseTessellator.releaseGL();
		}
	}
}
