package com.gamesense.client.module.modules.hud;

import java.awt.Point;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.hud.HUDComponent;

// PanelStudio rewrite by lukflug
public class Watermark extends HUDModule {
	private static Setting.ColorSetting color;
	
	public Watermark() {
		super(new WatermarkComponent());
	}
	
	public void setup() {
		color=registerColor("Color", "Color", new GSColor(255, 0, 0, 255));
	}

	private static class WatermarkComponent extends HUDComponent {
		public WatermarkComponent() {
			super("Watermark",GameSenseGUI.theme.getPanelRenderer(),new Point(0,0));
		}
		
		private String getString() {
			return "GameSense "+GameSenseMod.MODVER;
		}
		
		@Override
		public void render (Context context) {
			super.render(context);
			context.getInterface().drawString(context.getPos(),getString(),color.getValue());
		}

		@Override
		public int getWidth (Interface inter) {
			return inter.getFontWidth(getString());
		}

		@Override
		public void getHeight (Context context) {
			context.setHeight(renderer.getHeight());
		}
	}
}
