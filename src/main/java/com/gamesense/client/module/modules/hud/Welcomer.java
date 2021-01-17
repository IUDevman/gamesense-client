package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Point;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.HUDModule;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;

// PanelStudio rewrite by lukflug
public class Welcomer extends HUDModule {

	private Setting.ColorSetting color;
	
	public Welcomer() {
		super("Welcomer",new Point(450,0));
	}
	
	public void setup() {
		color=registerColor("Color", new GSColor(255, 0, 0, 255));
	}

	@Override
	public void populate (Theme theme) {
		component = new ListComponent(getName(),theme.getPanelRenderer(),position,new WelcomerList());
	}
	
	
	private class WelcomerList implements HUDList {

		@Override
		public int getSize() {
			return 1;
		}

		@Override
		public String getItem(int index) {
			return "Hello " + mc.player.getName() + " :^)";
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