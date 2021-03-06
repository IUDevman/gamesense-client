package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module.Declaration(name = "TextRadar", category = Category.HUD)
@HUDModule.Declaration(posX = 0, posZ = 50)
public class TextRadar extends HUDModule {

    ModeSetting display = registerMode("Display", Arrays.asList("All", "Friend", "Enemy"), "All");
    BooleanSetting sortUp = registerBoolean("Sort Up", false);
    BooleanSetting sortRight = registerBoolean("Sort Right", false);
    IntegerSetting range = registerInteger("Range", 100, 1, 260);

    private PlayerList list = new PlayerList();

    @Override
    public void populate(Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, list);
    }

    public void onRender() {
        list.players.clear();
        mc.world.loadedEntityList.stream()
                .filter(e -> e instanceof EntityPlayer)
                .filter(e -> e != mc.player)
                .forEach(e -> {
                    if (mc.player.getDistance(e) > range.getValue()) {
                        return;
                    }
                    if (display.getValue().equalsIgnoreCase("Friend") && !(SocialManager.isFriend(e.getName()))) {
                        return;
                    }
                    if (display.getValue().equalsIgnoreCase("Enemy") && !(SocialManager.isEnemy(e.getName()))) {
                        return;
                    }
                    list.players.add((EntityPlayer) e);
                });
    }


    private class PlayerList implements HUDList {

        public List<EntityPlayer> players = new ArrayList<EntityPlayer>();

        @Override
        public int getSize() {
            return players.size();
        }

        @Override
        public String getItem(int index) {
            EntityPlayer e = players.get(index);
            TextFormatting friendcolor;
            if (SocialManager.isFriend(e.getName())) {
                friendcolor = ModuleManager.getModule(ColorMain.class).getFriendColor();
            } else if (SocialManager.isEnemy(e.getName())) {
                friendcolor = ModuleManager.getModule(ColorMain.class).getEnemyColor();
            } else {
                friendcolor = TextFormatting.GRAY;
            }
            TextFormatting healthcolor;
            float health = e.getHealth() + e.getAbsorptionAmount();
            if (health <= 5) {
                healthcolor = TextFormatting.RED;
            } else if (health > 5 && health < 15) {
                healthcolor = TextFormatting.YELLOW;
            } else {
                healthcolor = TextFormatting.GREEN;
            }
            TextFormatting distancecolor;
            float distance = mc.player.getDistance(e);
            if (distance < 20) {
                distancecolor = TextFormatting.RED;
            } else if (distance >= 20 && distance < 50) {
                distancecolor = TextFormatting.YELLOW;
            } else {
                distancecolor = TextFormatting.GREEN;
            }
            return TextFormatting.GRAY + "[" + healthcolor + ((int) health) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + ((int) distance) + TextFormatting.GRAY + "]";
        }

        @Override
        public Color getItemColor(int index) {
            return new Color(255, 255, 255);
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