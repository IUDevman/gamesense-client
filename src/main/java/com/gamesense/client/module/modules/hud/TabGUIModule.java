package com.gamesense.client.module.modules.hud;

import java.awt.Point;

import org.lwjgl.input.Keyboard;

import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.SettingsAnimation;
import com.lukflug.panelstudio.tabgui.DefaultRenderer;
import com.lukflug.panelstudio.tabgui.TabGUI;
import com.lukflug.panelstudio.tabgui.TabGUIContainer;
import com.lukflug.panelstudio.tabgui.TabGUIItem;
import com.lukflug.panelstudio.tabgui.TabGUIRenderer;
import com.lukflug.panelstudio.theme.SettingsColorScheme;

/**
 * @author lukflug
 */
public class TabGUIModule extends HUDModule {
	
	public TabGUIModule() {
		super ("TabGUI",new Point(GameSenseGUI.DISTANCE,GameSenseGUI.DISTANCE));
	}
	
	@Override
	public void populate() {
		TabGUIRenderer renderer=new DefaultRenderer(new SettingsColorScheme(ClickGuiModule.enabledColor,ClickGuiModule.backgroundColor,ClickGuiModule.settingBackgroundColor,ClickGuiModule.backgroundColor,ClickGuiModule.fontColor,ClickGuiModule.opacity),GameSenseGUI.HEIGHT,5,Keyboard.KEY_UP,Keyboard.KEY_DOWN,Keyboard.KEY_LEFT,Keyboard.KEY_RIGHT,Keyboard.KEY_RETURN);
		TabGUI component=new TabGUI("TabGUI",renderer,new Animation() {
			@Override
			protected int getSpeed() {
				return ClickGuiModule.animationSpeed.getValue();
			}
		},position,75);
		for (Module.Category category: Module.Category.values()) {
			TabGUIContainer tab=new TabGUIContainer(category.name(),renderer,new SettingsAnimation(ClickGuiModule.animationSpeed));
			component.addComponent(tab);
			for (Module module: ModuleManager.getModulesInCategory(category)) {
				tab.addComponent(new TabGUIItem(module.getName(),module));
			}
		}
		this.component=component;
	}
}
