package com.gamesense.client.module.modules.hud;

import java.awt.Point;

import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.FixedComponent;

/**
 * @author lukflug
 */
public class HUDModule extends Module {
	protected final FixedComponent component;
	protected final Point position;
	
	public HUDModule (FixedComponent component) {
		super(component.getTitle(),Category.HUD);
		this.component=component;
		this.position=component.getPosition(null);
	}

	public FixedComponent getComponent() {
		return component;
	}
	
	public void resetPosition() {
		component.setPosition(GameSenseMod.getInstance().clickGUI,position);
	}
}
