package com.gamesense.client.module.modules.render;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", Category.Render);
    }

    float old;
    Setting.mode Mode;

    public void setup() {
        ArrayList<String> modes = new ArrayList<>();
        modes.add("Gamma");
        modes.add("Potion");

        Mode = registerMode("Mode", "FBMode", modes, "Gamma");
    }

    public void onEnable(){
        old = mc.gameSettings.gammaSetting;
    }

    public void onUpdate(){
      if (Mode.getValue().equalsIgnoreCase("Gamma")) {
          mc.gameSettings.gammaSetting = 666f;
          mc.player.removePotionEffect(Potion.getPotionById(16));
      } else if (Mode.getValue().equalsIgnoreCase("Potion")) {
          final PotionEffect potionEffect = new PotionEffect(Potion.getPotionById(16), 123456789, 5);
          potionEffect.setPotionDurationMax(true);
          mc.player.addPotionEffect(potionEffect);
      }
    }

    public void onDisable(){
        mc.gameSettings.gammaSetting = old;
        mc.player.removePotionEffect(Potion.getPotionById(16));
    }
}
