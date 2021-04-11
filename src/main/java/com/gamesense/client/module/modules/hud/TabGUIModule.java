package com.gamesense.client.module.modules.hud;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.lukflug.panelstudio.setting.SettingsAnimation;
import com.lukflug.panelstudio.tabgui.StandardTheme;
import com.lukflug.panelstudio.tabgui.TabGUI;
import com.lukflug.panelstudio.theme.IColorScheme;
import com.lukflug.panelstudio.theme.ITheme;

/**
 * @author lukflug
 */

@Module.Declaration(name = "TabGUI", category = Category.HUD)
@HUDModule.Declaration(posX = GameSenseGUI.DISTANCE, posZ = GameSenseGUI.DISTANCE)
public class TabGUIModule extends HUDModule {

    @Override
    public void populate(ITheme theme) {
        ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
        TabGUI tabgui=new TabGUI(()->"TabGUI",GameSenseGUI.client,new StandardTheme(new IColorScheme() {
			@Override
			public void createSetting (ITheme theme, String name, String description, boolean hasAlpha, boolean allowsRainbow, Color color, boolean rainbow) {
				ColorSetting setting=new ColorSetting(name,name.replace(" ",""),TabGUIModule.this,()->true,rainbow,allowsRainbow,hasAlpha,new GSColor(color));
		    	SettingsManager.addSetting(setting);
			}

			@Override
			public Color getColor (String name) {
				return ((ColorSetting)SettingsManager.getSettingsForModule(TabGUIModule.this).stream().filter(setting->setting.getName()==name).findFirst().orElse(null)).getValue();
			}
        },75,GameSenseGUI.FONT_HEIGHT,2,GameSenseGUI.DISTANCE),GameSenseGUI.gui,()->new SettingsAnimation(()->clickGuiModule.animationSpeed.getValue(),()->GameSenseGUI.guiInterface.getTime()),key->key==Keyboard.KEY_UP,key->key==Keyboard.KEY_DOWN,key->key==Keyboard.KEY_RETURN||key==Keyboard.KEY_RIGHT,key->key==Keyboard.KEY_LEFT);
        component=tabgui.getWrappedComponent(position);
    }
}