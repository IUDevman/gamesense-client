package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.awt.*;

@Module.Declaration(name = "PotionEffects", category = Category.HUD)
@HUDModule.Declaration(posX = 0, posZ = 300)
public class PotionEffects extends HUDModule {

    BooleanSetting sortUp = registerBoolean("Sort Up", false);
    BooleanSetting sortRight = registerBoolean("Sort Right", false);
    ColorSetting color = registerColor("Color", new GSColor(0, 255, 0, 255));

    private PotionList list = new PotionList();

    @Override
    public void populate(Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, list);
    }


    private class PotionList implements HUDList {

        @Override
        public int getSize() {
            return mc.player.getActivePotionEffects().size();
        }

        @Override
        public String getItem(int index) {
            PotionEffect effect = (PotionEffect) mc.player.getActivePotionEffects().toArray()[index];
            String name = I18n.format(effect.getPotion().getName());
            int amplifier = effect.getAmplifier() + 1;
            return name + " " + amplifier + ChatFormatting.GRAY + " " + Potion.getPotionDurationString(effect, 1.0f);
        }

        @Override
        public Color getItemColor(int index) {
            return color.getValue();
        }

        @Override
        public boolean sortUp() {
            return sortUp.isOn();
        }

        @Override
        public boolean sortRight() {
            return sortRight.isOn();
        }
    }
}