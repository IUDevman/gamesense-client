package com.gamesense.client.module.modules.hud;

import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.SettingsAnimation;
import com.lukflug.panelstudio.tabgui.*;
import com.lukflug.panelstudio.theme.SettingsColorScheme;
import com.lukflug.panelstudio.theme.Theme;
import org.lwjgl.input.Keyboard;

/**
 * @author lukflug
 */

@Module.Declaration(name = "TabGUI", category = Category.HUD)
@HUDModule.Declaration(posX = GameSenseGUI.DISTANCE, posZ = GameSenseGUI.DISTANCE)
public class TabGUIModule extends HUDModule {

    @Override
    public void populate(Theme theme) {
        ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
        TabGUIRenderer renderer = new DefaultRenderer(new SettingsColorScheme(clickGuiModule.enabledColor, clickGuiModule.backgroundColor, clickGuiModule.settingBackgroundColor, clickGuiModule.backgroundColor, clickGuiModule.fontColor, clickGuiModule.opacity), GameSenseGUI.HEIGHT, 5, Keyboard.KEY_UP, Keyboard.KEY_DOWN, Keyboard.KEY_LEFT, Keyboard.KEY_RIGHT, Keyboard.KEY_RETURN);
        TabGUI component = new TabGUI("TabGUI", renderer, new Animation() {
            @Override
            protected int getSpeed() {
                return clickGuiModule.animationSpeed.getValue();
            }
        }, position, 75);
        for (Category category : Category.values()) {
            TabGUIContainer tab = new TabGUIContainer(category.name(), renderer, new SettingsAnimation(clickGuiModule.animationSpeed));
            component.addComponent(tab);
            for (Module module : ModuleManager.getModulesInCategory(category)) {
                tab.addComponent(new TabGUIItem(module.getName(), module));
            }
        }
        this.component = component;
    }
}