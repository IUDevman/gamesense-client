package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.FixedComponent;

/**
 * @author lukflug
 */
public class HUDModule extends Module {
	protected final FixedComponent component;
	
	public HUDModule (FixedComponent component) {
		super(component.getTitle(),Category.HUD);
		this.component=component;
	}

	public FixedComponent getComponent() {
		return component;
	}
}
