package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Point;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GSColor;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

// PanelStudio rewrite by lukflug
public class PotionEffects extends ListModule {
	private static Setting.Boolean sortUp;
	private static Setting.Boolean sortRight;
	private static Setting.ColorSetting color;
	private static PotionList list=new PotionList();
    
    public PotionEffects(){
    	super(new ListModule.ListComponent("PotionEffects",new Point(0,300),list),new Point(0,300));
    }

    public void setup(){
    	sortUp = registerBoolean("Sort Up", "SortUp", false);
		sortRight = registerBoolean("Sort Right", "SortRight", false);
        color = registerColor("Color", "Color", new GSColor(0, 255, 0, 255));
    }
    
    
    private static class PotionList implements ListModule.HUDList {
		@Override
		public int getSize() {
			return mc.player.getActivePotionEffects().size();
		}

		@Override
		public String getItem(int index) {
			PotionEffect effect=(PotionEffect)mc.player.getActivePotionEffects().toArray()[index];
			String name=I18n.format(effect.getPotion().getName());
	        int amplifier=effect.getAmplifier()+1;
	        return name+" "+amplifier+ChatFormatting.GRAY+" "+Potion.getPotionDurationString(effect,1.0f);
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