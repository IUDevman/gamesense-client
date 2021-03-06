package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import net.minecraft.util.text.TextComponentString;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Module.Declaration(name = "Notifications", category = Category.HUD)
@HUDModule.Declaration(posX = 0, posZ = 50)
public class Notifications extends HUDModule {

    public BooleanSetting sortUp = registerBoolean("Sort Up", false);
    public BooleanSetting sortRight = registerBoolean("Sort Right", false);
    public BooleanSetting disableChat = registerBoolean("No Chat Msg", true);

    private static NotificationsList list = new NotificationsList();

    @Override
    public void populate(Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, list);
    }

    private static int waitCounter;

    public void onUpdate() {
        if (waitCounter < 500) {
            waitCounter++;
            return;
        } else {
            waitCounter = 0;
        }
        if (list.list.size() > 0)
            list.list.remove(0);
    }

    public void addMessage(TextComponentString m) {
        if (list.list.size() < 3) {
            list.list.remove(m);
            list.list.add(m);
        } else {
            list.list.remove(0);
            list.list.remove(m);
            list.list.add(m);
        }
    }

    private static class NotificationsList implements HUDList {

        public List<TextComponentString> list = new ArrayList<>();

        @Override
        public int getSize() {
            return list.size();
        }

        @Override
        public String getItem(int index) {
            return list.get(index).getText();
        }

        @Override
        public Color getItemColor(int index) {
            return new Color(255, 255, 255);
        }

        @Override
        public boolean sortUp() {
            return ModuleManager.getModule(Notifications.class).sortUp.isOn();
        }

        @Override
        public boolean sortRight() {
            return ModuleManager.getModule(Notifications.class).sortRight.isOn();
        }
    }
}