package com.gamesense.client.module.modules.hud;

import java.awt.Point;

import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.theme.Theme;

/**
 * @author lukflug
 */
public abstract class HUDModule extends Module {
	protected FixedComponent component;
	protected Point position;
	
	public HUDModule (String title, Point defaultPos) {
		super(title,Category.HUD);
		position=defaultPos;
	}
	
	public abstract void populate (Theme theme);

	public FixedComponent getComponent() {
		return component;
	}
	
	public void resetPosition() {
		component.setPosition(GameSense.getInstance().gameSenseGUI.guiInterface,position);
	}
}
