package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.api.util.color.Rainbow;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.ColorMain;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.item.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class EntityESP extends Module{
	public EntityESP(){super("EntityESP", Category.Render);}

	public void setup(){
		ArrayList<String> Modes = new ArrayList<>();
		Modes.add("Box");
		Modes.add("Outline");
		Modes.add("Glow");
		exp = registerBoolean("Exp Bottles", "ExpBottles", false);
		epearls = registerBoolean("Ender Pearls", "EnderPearls", false);
		crystals = registerBoolean("Crystals", "Crystals", false);
		items = registerBoolean("Items", "Items", false);
		orbs = registerBoolean("Exp Orbs", "ExpOrbs", false);
		renderMode = registerMode("Mode", "Mode", Modes, "Box");
	}

	Setting.Mode renderMode;
	Setting.Boolean exp;
	Setting.Boolean epearls;
	Setting.Boolean items;
	Setting.Boolean orbs;
	Setting.Boolean crystals;
	int c;
	int c2;

	public void onWorldRender(RenderEvent event){
		ColorMain colorMain = ((ColorMain) ModuleManager.getModuleByName("Colors"));
		if (ColorMain.rainbow.getValue()){
			c = Rainbow.getColorWithOpacity(50).getRGB();
		}
		else{
			c = new Color(ColorMain.Red.getValue(), ColorMain.Green.getValue(), ColorMain.Blue.getValue(), 50).getRGB();
		}
		if (ColorMain.rainbow.getValue()){
			c2 = Rainbow.getColorWithOpacity(255).getRGB();
		}
		else{
			c2 = new Color(ColorMain.Red.getValue(), ColorMain.Green.getValue(), ColorMain.Blue.getValue(), 255).getRGB();
		}
		if (renderMode.getValue().equalsIgnoreCase("Box")){
			mc.world.loadedEntityList.stream()
						.filter(entity -> entity != mc.player)
						.forEach(e ->{
							GameSenseTessellator.prepare(GL11.GL_QUADS);
							if (exp.getValue() && e instanceof EntityExpBottle){
								GameSenseTessellator.drawBox(e.getRenderBoundingBox(), c, GeometryMasks.Quad.ALL);
							}
							if (epearls.getValue() && e instanceof EntityEnderPearl){
								GameSenseTessellator.drawBox(e.getRenderBoundingBox(), c, GeometryMasks.Quad.ALL);
							}
							if (crystals.getValue() && e instanceof EntityEnderCrystal){
								GameSenseTessellator.drawBox(e.getRenderBoundingBox(), c, GeometryMasks.Quad.ALL);
							}
							if (items.getValue() && e instanceof EntityItem){
								GameSenseTessellator.drawBox(e.getRenderBoundingBox(), c, GeometryMasks.Quad.ALL);
							}
							if (orbs.getValue() && e instanceof EntityXPOrb){
								GameSenseTessellator.drawBox(e.getRenderBoundingBox(), c, GeometryMasks.Quad.ALL);
							}
							GameSenseTessellator.release();
							GameSenseTessellator.prepareGL();
							if (exp.getValue() && e instanceof EntityExpBottle){
								GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, c2);
							}
							if (epearls.getValue() && e instanceof EntityEnderPearl){
								GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, c2);
							}
							if (crystals.getValue() && e instanceof EntityEnderCrystal){
								GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, c2);
							}
							if (items.getValue() && e instanceof EntityItem){
								GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, c2);
							}
							if (orbs.getValue() && e instanceof EntityXPOrb){
								GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, c2);
							}
							GameSenseTessellator.releaseGL();
						});
			}
			if (renderMode.getValue().equalsIgnoreCase("Outline")){
				mc.world.loadedEntityList.stream()
						.filter(entity -> entity != mc.player)
						.forEach(e ->{
							GameSenseTessellator.prepareGL();
							if (exp.getValue() && e instanceof EntityExpBottle){
								GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, c2);
							}
							if (epearls.getValue() && e instanceof EntityEnderPearl){
								GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, c2);
							}
							if (crystals.getValue() && e instanceof EntityEnderCrystal){
								GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, c2);
							}
							if (items.getValue() && e instanceof EntityItem){
								GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, c2);
							}
							if (orbs.getValue() && e instanceof EntityXPOrb){
								GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, c2);
							}
							GameSenseTessellator.releaseGL();
						});
			}
			if (renderMode.getValue().equalsIgnoreCase("Glow")){
				mc.world.loadedEntityList.stream()
						.filter(e -> e != mc.player)
						.forEach(e ->{
							if (exp.getValue() && e instanceof EntityExpBottle){
								e.setGlowing(true);
							}
							if (epearls.getValue() && e instanceof EntityEnderPearl){
								e.setGlowing(true);
							}
							if (crystals.getValue() && e instanceof EntityEnderCrystal){
								e.setGlowing(true);
							}
							if (items.getValue() && e instanceof EntityItem){
								e.setGlowing(true);
							}
							if (orbs.getValue() && e instanceof EntityXPOrb){
								e.setGlowing(true);
							}
						});
			}
		}

		public void onUpdate(){
			mc.world.loadedEntityList.stream()
					.filter(e -> e != mc.player)
					.forEach(e ->{
						if (renderMode.getValue().equalsIgnoreCase("Glow") == false){
							if (e instanceof EntityExpBottle){
								e.setGlowing(false);
							}
							if (e instanceof EntityEnderPearl){
								e.setGlowing(false);
							}
							if (e instanceof EntityEnderCrystal){
								e.setGlowing(false);
							}
							if (e instanceof EntityItem){
								e.setGlowing(false);
							}
							if (e instanceof EntityXPOrb){
								e.setGlowing(false);
							}
						}
						if (exp.getValue() == false && e instanceof EntityExpBottle){
							e.setGlowing(false);
						}
						if (epearls.getValue() == false && e instanceof EntityEnderPearl){
							e.setGlowing(false);
						}
						if (crystals.getValue() == false && e instanceof EntityEnderCrystal){
							e.setGlowing(false);
						}
						if (items.getValue() == false && e instanceof EntityItem){
							e.setGlowing(false);
						}
						if (orbs.getValue() == false && e instanceof EntityXPOrb){
							e.setGlowing(false);
						}
					});
		}

	public void onDisable(){
		if (renderMode.getValue().equalsIgnoreCase("Glow")){
			mc.world.loadedEntityList.stream()
					.filter(e -> e != mc.player)
					.forEach(e ->{
						if (e instanceof EntityExpBottle){
							e.setGlowing(false);
						}
						if (e instanceof EntityEnderPearl){
							e.setGlowing(false);
						}
						if (e instanceof EntityEnderCrystal){
							e.setGlowing(false);
						}
						if (e instanceof EntityItem){
							e.setGlowing(false);
						}
						if (e instanceof EntityXPOrb){
							e.setGlowing(false);
						}
					});
		}
	}

	public String getHudInfo(){
		String t = "";
		if(renderMode.getValue().equalsIgnoreCase("Box")){
			t = "[" + ChatFormatting.WHITE + "Box" + ChatFormatting.GRAY + "]";
		}
		if (renderMode.getValue().equalsIgnoreCase("Outline")){
			t = "[" + ChatFormatting.WHITE + "Outline" + ChatFormatting.GRAY + "]";
		}
		if (renderMode.getValue().equalsIgnoreCase("Glow")){
			t = "[" + ChatFormatting.WHITE + "Glow" + ChatFormatting.GRAY + "]";
		}
		return t;
	}
}