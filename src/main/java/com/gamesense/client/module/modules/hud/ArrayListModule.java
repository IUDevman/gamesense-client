package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSense;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import com.mojang.realmsclient.gui.ChatFormatting;

@Module.Declaration(name = "ArrayList", category = Category.HUD)
@HUDModule.Declaration(posX = 0, posZ = 200)
public class ArrayListModule extends HUDModule {

    BooleanSetting sortUp = registerBoolean("Sort Up", true);
    BooleanSetting sortRight = registerBoolean("Sort Right", false);
    ColorSetting color = registerColor("Color", new GSColor(255, 0, 0, 255));

    private final ModuleList list = new ModuleList();

    @Override
    public void populate(ITheme theme) {
    	component = new ListComponent(new Labeled(getName(),null,()->true), position, getName(), list, GameSenseGUI.FONT_HEIGHT, HUDModule.LIST_BORDER);
    }

    public void onRender() {
        list.activeModules.clear();
        for (Module module : ModuleManager.getModules()) {
            if (module.isEnabled() && module.isDrawn()) list.activeModules.add(module);
        }
        list.activeModules.sort(Comparator.comparing(module -> -GameSense.INSTANCE.gameSenseGUI.guiInterface.getFontWidth(GameSenseGUI.FONT_HEIGHT, ((Module)module).getName() + ChatFormatting.GRAY + " " + ((Module)module).getHudInfo())));
    }

    private class ModuleList implements HUDList {

        public List<Module> activeModules = new ArrayList<Module>();

        @Override
        public int getSize() {
            return activeModules.size();
        }

        @Override
        public String getItem(int index) {
            Module module = activeModules.get(index);
            return (!module.getHudInfo().equals("")) ? module.getName() + ChatFormatting.GRAY + " " + module.getHudInfo() : module.getName();
        }

        @Override
        public Color getItemColor(int index) {
            GSColor c = color.getValue();
            return Color.getHSBColor(c.getHue() + (color.getRainbow() ? .02f * index : 0), c.getSaturation(), c.getBrightness());
        }

        @Override
        public boolean sortUp() {
            return sortUp.getValue();
        }

        @Override
        public boolean sortRight() {
            return sortRight.getValue();
        }
    }
}