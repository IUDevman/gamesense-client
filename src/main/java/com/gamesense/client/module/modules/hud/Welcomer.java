package com.gamesense.client.module.modules.hud;

import java.awt.Color;

import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;

@Module.Declaration(name = "Welcomer", category = Category.HUD)
@HUDModule.Declaration(posX = 450, posZ = 0)
public class Welcomer extends HUDModule {

    ColorSetting color = registerColor("Color", new GSColor(255, 0, 0, 255));

    @Override
    public void populate(ITheme theme) {
    	component = new ListComponent(new Labeled(getName(),null,()->true), position, getName(), new WelcomerList(), GameSenseGUI.FONT_HEIGHT, HUDModule.LIST_BORDER);
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