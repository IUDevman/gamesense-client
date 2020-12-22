package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

public class Fullbright extends Module{
	public Fullbright(){
		super("Fullbright", Category.Render);
	}

	float oldGamma;
	Setting.Mode Mode;

	public void setup(){
		ArrayList<String> modes = new ArrayList<>();
		modes.add("Gamma");
		modes.add("Potion");

		Mode = registerMode("Mode", "Mode", modes, "Gamma");
	}

	public void onEnable(){
		oldGamma = mc.gameSettings.gammaSetting;
	}

	public void onUpdate(){
		if (Mode.getValue().equalsIgnoreCase("Gamma")){
			mc.gameSettings.gammaSetting = 666f;
			mc.player.removePotionEffect(Potion.getPotionById(16));
		}
		else if (Mode.getValue().equalsIgnoreCase("Potion")){
			final PotionEffect potionEffect = new PotionEffect(Potion.getPotionById(16), 123456789, 5);
			potionEffect.setPotionDurationMax(true);
			mc.player.addPotionEffect(potionEffect);
		}
	}

	public void onDisable(){
		mc.gameSettings.gammaSetting = oldGamma;
		mc.player.removePotionEffect(Potion.getPotionById(16));
	}
}