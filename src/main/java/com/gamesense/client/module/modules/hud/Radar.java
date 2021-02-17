package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.player.enemy.Enemies;
import com.gamesense.api.util.player.friend.Friends;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.hud.HUDComponent;
import com.lukflug.panelstudio.theme.Theme;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

/**
 * @author Hoosiers
 * @since 12/15/2020
 */

public class Radar extends HUDModule {

    private Setting.Boolean renderPlayer;
    private Setting.Boolean renderMobs;
    private Setting.ColorSetting playerColor;
    private Setting.ColorSetting outlineColor;
    private Setting.ColorSetting fillColor;

    public Radar() {
        super("Radar", new Point(0, 300));
    }

    public void setup() {
        renderPlayer = registerBoolean("Player", true);
        renderMobs = registerBoolean("Mobs", true);
        playerColor = registerColor("Player Color", new GSColor(0, 0, 255, 255));
        outlineColor = registerColor("Outline Color", new GSColor(255, 0, 0, 255));
        fillColor = registerColor("Fill Color", new GSColor(0, 0, 0, 255));
    }
    
    @Override
    public void populate (Theme theme) {
    	component = new RadarComponent(theme);
    }

    private Color getPlayerColor(EntityPlayer entityPlayer) {
        if (Friends.isFriend(entityPlayer.getName())) {
            return new GSColor(ColorMain.getFriendGSColor(), 255);
        }
        else if (Enemies.isEnemy(entityPlayer.getName())) {
            return new GSColor(ColorMain.getEnemyGSColor(), 255);
        }
        else {
            return new GSColor(playerColor.getValue(), 255);
        }
    }

    private Color getEntityColor(Entity entity) {
        if (entity instanceof EntityMob || entity instanceof EntitySlime) {
            return new GSColor(255, 0, 0, 255);
        }
        else if (entity instanceof EntityAnimal || entity instanceof EntitySquid) {
            return new GSColor(0, 255, 0, 255);
        }
        else {
            return new GSColor(255, 165, 0, 255);
        }
    }

    private class RadarComponent extends HUDComponent {

        public RadarComponent (Theme theme) {
            super(getName(), theme.getPanelRenderer(), Radar.this.position);
        }

        private int maxRange = 50;

        @Override
        public void render(Context context) {
            super.render(context);

            if (mc.player != null && mc.player.ticksExisted >= 10) {

                //players
                if (renderPlayer.getValue()) {
                    mc.world.playerEntities.stream()
                            .filter(entityPlayer -> entityPlayer != mc.player)
                            .forEach(entityPlayer -> {

                                renderEntityPoint(entityPlayer, getPlayerColor(entityPlayer), context);
                            });
                }

                //mobs
                if (renderMobs.getValue()) {
                    mc.world.loadedEntityList.stream()
                            .filter(entity -> !(entity instanceof EntityPlayer))
                            .forEach(entity -> {

                                if (entity instanceof EntityCreature || entity instanceof EntitySlime || entity instanceof EntitySquid) {
                                    renderEntityPoint(entity, getEntityColor(entity), context);
                                }
                            });
                }

                //background
                Color background = new GSColor(fillColor.getValue(), 100);
                context.getInterface().fillRect(context.getRect(), background, background, background, background);

                //outline, credit to lukflug for this
                Color outline = new GSColor(outlineColor.getValue(), 255);
                context.getInterface().fillRect(new Rectangle(context.getPos(),new Dimension(context.getSize().width,1)),outline,outline,outline,outline);
                context.getInterface().fillRect(new Rectangle(context.getPos(),new Dimension(1,context.getSize().height)),outline,outline,outline,outline);
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x+context.getSize().width-1,context.getPos().y),new Dimension(1,context.getSize().height)),outline,outline,outline,outline);
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x,context.getPos().y+context.getSize().height-1),new Dimension(context.getSize().width,1)),outline,outline,outline,outline);

                //self
                boolean isNorth = isFacing(EnumFacing.NORTH);
                boolean isSouth = isFacing(EnumFacing.SOUTH);
                boolean isEast = isFacing(EnumFacing.EAST);
                boolean isWest = isFacing(EnumFacing.WEST);

                Color selfColor = new Color(255, 255, 255, 255);
                int distanceToCenter = context.getSize().height / 2;
                context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter + 3, context.getPos().y + distanceToCenter), new Point(context.getPos().x + distanceToCenter + (isEast ? 1 : 0), context.getPos().y + distanceToCenter), isEast ? outline : selfColor, isEast ? outline : selfColor);
                context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter, context.getPos().y + distanceToCenter + 3), new Point(context.getPos().x + distanceToCenter, context.getPos().y + distanceToCenter + (isSouth ? 1 : 0)), isSouth ? outline : selfColor, isSouth ? outline : selfColor);
                context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter - (isWest ? 1 : 0), context.getPos().y + distanceToCenter), new Point(context.getPos().x + distanceToCenter - 3, context.getPos().y + distanceToCenter), isWest ? outline : selfColor, isWest ? outline : selfColor);
                context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter, context.getPos().y + distanceToCenter - (isNorth ? 1 : 0)), new Point(context.getPos().x + distanceToCenter, context.getPos().y + distanceToCenter -3), isNorth ? outline : selfColor, isNorth ? outline : selfColor);

            }
        }

        private boolean isFacing(EnumFacing enumFacing) {
            return mc.player.getHorizontalFacing().equals(enumFacing);
        }

        private void renderEntityPoint(Entity entity, Color color, Context context) {
            int distanceX = findDistance1D(mc.player.posX, entity.posX);
            int distanceY = findDistance1D(mc.player.posZ, entity.posZ);

            int distanceToCenter = context.getSize().height / 2;

            if (distanceX > maxRange || distanceY > maxRange || distanceX < -maxRange || distanceY < -maxRange) {
                return;
            }

            context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter + 1 + distanceX, context.getPos().y + distanceToCenter + distanceY), new Point(context.getPos().x + distanceToCenter - 1 + distanceX, context.getPos().y + distanceToCenter + distanceY), color, color);
            context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter + distanceX, context.getPos().y + distanceToCenter + 1 + distanceY), new Point(context.getPos().x + distanceToCenter + distanceX, context.getPos().y + distanceToCenter - 1 + distanceY), color, color);
        }

        private int findDistance1D(double player, double entity) {
            double player1 = player;
            double entity1 = entity;

            if (player1 < 0) {
                player1 = player1 * -1;
            }
            if (entity1 < 0) {
                entity1 = entity1 * -1;
            }

            int value = (int) (entity1 - player1);

            if (player > 0 && entity < 0 || player < 0 && entity > 0) {
                value = (int) ((-1 * player) + entity);
            }

            if ((player > 0 || player < 0) && entity < 0 && entity1 != player1) {
                value = (int) ((-1 * player) + entity);
            }

            if ((player < 0 && entity == 0) || (player == 0 && entity < 0)) {
                value = (int) (-1 * (entity1 - player1));
            }

            return value;
        }

        @Override
        public int getWidth(Interface anInterface) {
            return 103;
        }

        @Override
        public void getHeight(Context context) {
            context.setHeight(103);
        }
    }
}