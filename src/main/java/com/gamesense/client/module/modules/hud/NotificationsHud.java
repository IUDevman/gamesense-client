package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.List;

public class NotificationsHud extends Module {
    public NotificationsHud() {
        super("HudNotifications", Category.HUD);
    }

    int count;
    int waitCounter;
    int color;
    Setting.i x;
    Setting.i y;
    Setting.b sortUp;
    Setting.b right;

    static List<TextComponentString> list = new ArrayList<>();

    public void setup(){
        x = this.registerI("X", "NHX", 2, 0,1000);
        y = this.registerI("Y", "NHY",  2, 0,1000);
        sortUp = this.registerB("Sort Up", "NHSortUp", true);
        right = this.registerB("Align Right", "NHAlignRight", false);

    }

    public void onUpdate(){
            if (waitCounter < 500) {
                waitCounter++;
                return;
            } else {
                waitCounter = 0;
            }
            if(list.size() > 0)
                list.remove(0);
    }

    public void onRender(){
        count = 0;
        for(TextComponentString s : list) {
            count = list.indexOf(s) + 1;
            try {
                color = s.getStyle().getColor().getColorIndex();
            } catch(NullPointerException e){
                color = 0xffffffff;
            }
            if (sortUp.getValue()) {
                if (right.getValue()) {
                    mc.fontRenderer.drawStringWithShadow(s.getText(), (int) x.getValue() - mc.fontRenderer.getStringWidth(s.getText()), (int) y.getValue() + (count * 10), color);
                } else {
                    mc.fontRenderer.drawStringWithShadow(s.getText(), (int) x.getValue(), (int) y.getValue() + (count * 10), color);
                }
                count++;
            } else {
                if (right.getValue()) {
                    mc.fontRenderer.drawStringWithShadow(s.getText(), (int) x.getValue() - mc.fontRenderer.getStringWidth(s.getText()), (int) y.getValue() + (count * -10), color);
                } else {
                    mc.fontRenderer.drawStringWithShadow(s.getText(), (int) x.getValue(), (int) y.getValue() + (count * -10), color);
                }
                count++;
            }
        }
    }

    public static void addMessage(TextComponentString m){
        if(list.size() < 3) {
            list.remove(m);
            list.add(m);
        }else {
            list.remove(0);
            list.remove(m);
            list.add(m);
        }
    }
}
