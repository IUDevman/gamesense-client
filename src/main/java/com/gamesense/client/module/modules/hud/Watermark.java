package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Point;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSenseMod;

// PanelStudio rewrite by lukflug
public class Watermark extends HUDModule {
	private Setting.ColorSetting color;
	
	public Watermark() {
		super("Watermark",new Point(0,0));
	}
	
	public void setup() {
		color=registerColor("Color", "Color", new GSColor(255, 0, 0, 255));
	}
	
	@Override
	public void populate() {
		component=new ListModule.ListComponent(getName(),position,new WatermarkList());
	}
	
	
	private class WatermarkList implements ListModule.HUDList {
		@Override
		public int getSize() {
			return 1;
		}

		@Override
		public String getItem(int index) {
			return "GameSense "+GameSenseMod.MODVER;
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
