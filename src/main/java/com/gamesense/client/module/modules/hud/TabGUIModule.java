package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Point;

import org.lwjgl.input.Keyboard;

import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.clickgui.GameSenseGUI.GameSenseAnimation;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.lukflug.panelstudio.tabgui.DefaultRenderer;
import com.lukflug.panelstudio.tabgui.TabGUI;
import com.lukflug.panelstudio.tabgui.TabGUIContainer;
import com.lukflug.panelstudio.tabgui.TabGUIItem;
import com.lukflug.panelstudio.tabgui.TabGUIRenderer;
import com.lukflug.panelstudio.theme.ColorScheme;

/**
 * @author lukflug
 */
public class TabGUIModule extends TabGUI {
	private static TabGUI instance;
	private static final TabGUIRenderer renderer=new GameSenseRenderer();
	
	public TabGUIModule() {
		super("TabGUI",renderer,new GameSenseGUI.GameSenseAnimation(),new Point(GameSenseGUI.DISTANCE,GameSenseGUI.DISTANCE),75);
		instance=this;
	}
	
	public static void populate() {
		for (Module.Category category: Module.Category.values()) {
			TabGUIContainer tab=new TabGUIContainer(category.name(),renderer,new GameSenseAnimation());
			instance.addComponent(tab);
			for (Module module: ModuleManager.getModulesInCategory(category)) {
				tab.addComponent(new TabGUIItem(module.getName(),module));
			}
		}
	}
	
	private static class GameSenseRenderer extends DefaultRenderer {
		public GameSenseRenderer() {
			super(new GameSenseScheme(),GameSenseGUI.HEIGHT,5,Keyboard.KEY_UP,Keyboard.KEY_DOWN,Keyboard.KEY_LEFT,Keyboard.KEY_RIGHT,Keyboard.KEY_RETURN);
		}
	}
	
	private static class GameSenseScheme implements ColorScheme {
		@Override
		public Color getActiveColor() {
			return ClickGuiModule.enabledColor.getValue();
		}

		@Override
		public Color getInactiveColor() {
			return ClickGuiModule.backgroundColor.getValue();
		}

		@Override
		public Color getBackgroundColor() {
			return ClickGuiModule.settingBackgroundColor.getValue();
		}

		@Override
		public Color getOutlineColor() {
			return ClickGuiModule.backgroundColor.getValue();
		}

		@Override
		public Color getFontColor() {
			return ClickGuiModule.fontColor.getValue();
		}

		@Override
		public int getOpacity() {
			return ClickGuiModule.opacity.getValue();
		}
	}
}
