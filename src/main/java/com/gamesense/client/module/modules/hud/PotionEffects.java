package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;

public class PotionEffects extends Module {
    public PotionEffects(){
        super("PotionEffects", Category.HUD);
    }

    Setting.Integer posX;
    Setting.Integer posY;
    Setting.Boolean sortUp;
    Setting.Boolean sortRight;
    Setting.ColorSetting color;

    public void setup(){
        posX = registerInteger("X", "X", 0, 0, 1000);
        posY = registerInteger("Y", "Y", 300, 0, 1000);
        sortUp = registerBoolean("Sort Up", "SortUp", true);
        sortRight = registerBoolean("Sort Right", "SortRight", false);
        color = registerColor("Color", "Color", new GSColor(0, 255, 0, 255));
    }

    private int count;

    public void onRender(){
        count = 0;
        try {
            mc.player.getActivePotionEffects().forEach(effect -> {

                String name = I18n.format(effect.getPotion().getName());
                int amplifier = effect.getAmplifier() + 1;
                String s = name + " " + amplifier + ChatFormatting.GRAY + " " + Potion.getPotionDurationString(effect, 1.0f);

                if (sortUp.getValue()) {
                    if (sortRight.getValue()) {
                        FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), s, posX.getValue() - FontUtils.getStringWidth(ColorMain.customFont.getValue(),s),posY.getValue() + (count * 10), color.getValue());
                    }
                    else {
                        FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), s, posX.getValue(), posY.getValue() + (count * 10), color.getValue());
                    }
                    count++;
                }
                else {
                    if (sortRight.getValue()) {
                        FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), s, posX.getValue() - FontUtils.getStringWidth(ColorMain.customFont.getValue(),s),  posY.getValue() + (count * -10), color.getValue());
                    }
                    else {
                        FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), s, posX.getValue(), posY.getValue() + (count * -10), color.getValue());
                    }
                    count++;
                }
            });
        }
        catch(NullPointerException e){e.printStackTrace();}
    }
}