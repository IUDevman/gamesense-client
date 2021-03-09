package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;

import java.awt.*;

@Module.Declaration(name = "Welcomer", category = Category.HUD)
@HUDModule.Declaration(posX = 450, posZ = 0)
public class Welcomer extends HUDModule {

    private ColorSetting color = registerColor("Color", new GSColor(255, 0, 0, 255));

    @Override
    public void populate(Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new WelcomerList());
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