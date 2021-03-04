package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.Category;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

@Module.Declaration(name = "Fullbright", category = Category.Render)
public class Fullbright extends Module {

    ModeSetting mode;

    public void setup() {
        ArrayList<String> modes = new ArrayList<>();
        modes.add("Gamma");
        modes.add("Potion");

        mode = registerMode("Mode", modes, "Gamma");
    }

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