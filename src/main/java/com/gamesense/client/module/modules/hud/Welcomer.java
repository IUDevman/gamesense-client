package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Point;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GSColor;
import com.lukflug.panelstudio.theme.Theme;

import net.minecraft.client.Minecraft;

// PanelStudio rewrite by lukflug
public class Welcomer extends HUDModule {
	private Setting.ColorSetting color;
	
	public Welcomer() {
		super("Welcomer",new Point(450,0));
	}
	
	public void setup() {
		color=registerColor("Color", "Color", new GSColor(255, 0, 0, 255));
	}

	@Override
	public void populate (Theme theme) {
		component=new ListModule.ListComponent(getName(),theme,position,new WelcomerList());
	}
	
	
	private class WelcomerList implements ListModule.HUDList {
		@Override
		public int getSize() {
			return 1;
		}

		@Override
		public String getItem(int index) {
			return "Hello "+Minecraft.getMinecraft().player.getName()+" :^)";
		}

		@Override
		public Color getItemColor(int index) {
			return color.getValue();
		}

		@Override
		public boolean sortUp() {
			return false;
		}

		@Override
		public boolean sortRight() {
			return false;
		}
	}
}
