package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.player.enemy.Enemies;
import com.gamesense.api.util.player.friends.Friends;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.hud.HUDComponent;
import com.lukflug.panelstudio.theme.Theme;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Hoosiers
 * @since 12/13/2020
 */

public class TargetInfo extends HUDModule {

    private Setting.Integer range;
    private Setting.ColorSetting backgroundColor;
    private Setting.ColorSetting outlineColor;

    public TargetInfo() {
        super("TargetInfo", new Point(0, 150));
    }

    public void setup() {
        range = registerInteger("Range", 100, 10, 260);
        backgroundColor = registerColor("Background", new GSColor(0, 0, 0, 255));
        outlineColor = registerColor("Outline", new GSColor(255, 0, 0, 255));
    }
    
    public void populate (Theme theme) {
    	component = new TargetInfoComponent(theme);
    }

    private Color getNameColor(EntityPlayer entityPlayer) {
        if (Friends.isFriend(entityPlayer.getName())) {
            return new GSColor(ColorMain.getFriendGSColor(), 255);
        }
        else if (Enemies.isEnemy(entityPlayer.getName())) {
            return new GSColor(ColorMain.getEnemyGSColor(), 255);
        }
        else {
            return new GSColor(255, 255, 255, 255);
        }
    }

    private Color getHealthColor(EntityPlayer entityPlayer) {
        int health = (int) (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount());

        if (health > 36) {
            health = 36;
        }
        if (health < 0) {
            health = 0;
        }

        int red = (int) (255 - (health * 7.0833));
        int green = 255 - red;

        return new Color(red, green, 0, 100);
    }

    private static Color getDistanceColor(EntityPlayer entityPlayer) {
        int distance = (int) entityPlayer.getDistance(mc.player);

        if (distance > 50) {
            distance = 50;
        }

        int red = (int) (255 - (distance * 5.1));
        int green = 255 - red;

        return new Color(red, green, 0, 100);
    }

    public static EntityPlayer targetPlayer;

    public static boolean isRenderingEntity(EntityPlayer entityPlayer) {
        return targetPlayer == entityPlayer;
    }

    private class TargetInfoComponent extends HUDComponent {

        public TargetInfoComponent (Theme theme) {
            super(getName(), theme.getPanelRenderer(), TargetInfo.this.position);
        }

        @Override
        public void render(Context context) {
            super.render(context);

            if (mc.player != null && mc.player.ticksExisted >= 10) {

                EntityPlayer entityPlayer = (EntityPlayer) mc.world.loadedEntityList.stream()
                        .filter(entity -> entity instanceof EntityPlayer)
                        .filter(entity -> entity != mc.player)
                        .map(entity -> (EntityLivingBase) entity)
                        .min(Comparator.comparing(c -> mc.player.getDistance(c)))
                        .orElse(null);

                if (entityPlayer != null && entityPlayer.getDistance(mc.player) <= range.getValue()) {

                    //background
                    Color background = new GSColor(backgroundColor.getValue(), 100);
                    context.getInterface().fillRect(context.getRect(), background, background, background, background);

                    //outline, credit to lukflug for this
                    Color outline = new GSColor(outlineColor.getValue(), 255);
                    context.getInterface().fillRect(new Rectangle(context.getPos(),new Dimension(context.getSize().width,1)),outline,outline,outline,outline);
                    context.getInterface().fillRect(new Rectangle(context.getPos(),new Dimension(1,context.getSize().height)),outline,outline,outline,outline);
                    context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x+context.getSize().width-1,context.getPos().y),new Dimension(1,context.getSize().height)),outline,outline,outline,outline);
                    context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x,context.getPos().y+context.getSize().height-1),new Dimension(context.getSize().width,1)),outline,outline,outline,outline);

                    //name
                    String name = entityPlayer.getName();
                    Color nameColor = getNameColor(entityPlayer);
                    context.getInterface().drawString(new Point(context.getPos().x + 2, context.getPos().y + 2), name, nameColor);

                    //health box
                    int healthVal = (int) (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount());
                    Color healthBox = getHealthColor(entityPlayer);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + 32, context.getPos().y + 12, (int) (healthVal * 1.9444), 15), healthBox, healthBox, healthBox, healthBox);

                    //distance box
                    int distanceVal = (int) (entityPlayer.getDistance(mc.player));
                    int width = (int) (distanceVal * 1.38);
                    if (width > 69) {
                        width = 69;
                    }
                    Color distanceBox = getDistanceColor(entityPlayer);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + 32, context.getPos().y + 27, width, 15), distanceBox, distanceBox, distanceBox, distanceBox);

                    //player model
                    targetPlayer = entityPlayer;
                    GameSenseGUI.renderEntity(entityPlayer, new Point(context.getPos().x + 17, context.getPos().y + 40), 15);

                    //health string
                    String health = "Health: " + healthVal;
                    Color healthColor = new Color(255, 255, 255, 255);
                    context.getInterface().drawString(new Point(context.getPos().x + 33, context.getPos().y + 14), health, healthColor);

                    //distance string
                    String distance = "Distance: " + distanceVal;
                    Color distanceColor = new Color(255, 255, 255, 255);
                    context.getInterface().drawString(new Point(context.getPos().x + 33, context.getPos().y + 29), distance, distanceColor);
                }
            }
        }

        @Override
        public int getWidth (Interface inter) {
            return 102;
        }

        @Override
        public void getHeight (Context context) {
            context.setHeight(43);
        }
    }
}