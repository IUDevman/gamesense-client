package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Arrays;

@Module.Declaration(name = "Fullbright", category = Category.Render)
public class Fullbright extends Module {

    ModeSetting mode = registerMode("Mode", Arrays.asList("Gamma", "Potion"), "Gamma");

    float oldGamma;

    public void onEnable() {
        oldGamma = mc.gameSettings.gammaSetting;
    }

    public void onUpdate() {
        if (mode.getValue().equalsIgnoreCase("Gamma")) {
            mc.gameSettings.gammaSetting = 666f;
            mc.player.removePotionEffect(Potion.getPotionById(16));
        } else if (mode.getValue().equalsIgnoreCase("Potion")) {
            final PotionEffect potionEffect = new PotionEffect(Potion.getPotionById(16), 123456789, 5);
            potionEffect.setPotionDurationMax(true);
            mc.player.addPotionEffect(potionEffect);
        }
    }

    public void onDisable() {
        mc.gameSettings.gammaSetting = oldGamma;
        mc.player.removePotionEffect(Potion.getPotionById(16));
    }
}