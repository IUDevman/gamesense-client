package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import com.mojang.realmsclient.gui.ChatFormatting;

// PanelStudio rewrite by lukflug
public class ModuleArrayList extends HUDModule {
    private Setting.Boolean sortUp;
    private Setting.Boolean sortRight;
    private Setting.ColorSetting color;
	private ModuleList list=new ModuleList();

    public ModuleArrayList(){
    	super("ArrayList",new Point(0,200));
    }

    public void setup(){
        sortUp = registerBoolean("Sort Up", "SortUp", true);
        sortRight = registerBoolean("Sort Right", "SortRight", false);
        color = registerColor("Color", "Color", new GSColor(255, 0, 0, 255));
    }
    
    @Override
    public void populate (Theme theme) {
    	component=new ListComponent(getName(),theme.getPanelRenderer(),position,list);
    }

    public void onRender(){
    	list.activeModules.clear();
    	for (Module module: ModuleManager.getModules()) {
    		if (module.isEnabled() && module.isDrawn()) list.activeModules.add(module);
    	}
    	list.activeModules.sort(Comparator.comparing(module -> -GameSenseMod.getInstance().clickGUI.guiInterface.getFontWidth(module.getName()+ChatFormatting.GRAY+" "+module.getHudInfo())));
    }
    

    private class ModuleList implements HUDList {
		public List<Module> activeModules=new ArrayList<Module>();
		
		@Override
		public int getSize() {
			return activeModules.size();
		}
	
		@Override
		public String getItem(int index) {
			Module module=activeModules.get(index);
			return module.getName()+ChatFormatting.GRAY+" "+module.getHudInfo();
		}
	
		@Override
		public Color getItemColor(int index) {
			GSColor c=color.getValue();
			return Color.getHSBColor(c.getHue()+(color.getRainbow()?.02f*index:0),c.getSaturation(),c.getBrightness());
		}

		@Override
		public boolean sortUp() {
			return sortUp.isOn();
		}

		@Override
		public boolean sortRight() {
			return sortRight.isOn();
		}
	}
}