package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.players.enemy.Enemies;
import com.gamesense.api.util.players.friends.Friends;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.hud.HUDComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * @author Hoosiers
 * @since 12/15/2020
 */

public class Radar extends HUDModule {

    public Radar() {
        super(new RadarComponent(), new Point(0, 300));
    }

    private static Setting.Boolean renderPlayer;
    private static Setting.Boolean renderMobs;
    private static Setting.ColorSetting playerColor;
    private static Setting.ColorSetting outlineColor;
    private static Setting.ColorSetting fillColor;

    public void setup() {
        renderPlayer = registerBoolean("Player", "Player", true);
        renderMobs = registerBoolean("Mobs", "Mobs", true);
        playerColor = registerColor("Player Color", "PlayerColor", new GSColor(0, 0, 255, 255));
        outlineColor = registerColor("Outline Color", "OutlineColor", new GSColor(255, 0, 0, 255));
        fillColor = registerColor("Fill Color", "FillColor", new GSColor(0, 0, 0, 255));
    }

    private static Color getPlayerColor(EntityPlayer entityPlayer) {
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

    private static Color getEntityColor(Entity entity) {
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

    private static class RadarComponent extends HUDComponent {

        public RadarComponent() {
            super("Radar", GameSenseGUI.theme.getPanelRenderer(), new Point(0, 300));
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
            int distanceX = findDistanceByPoint(mc.player.posX, entity.posX);
            int distanceY = findDistanceByPoint(mc.player.posZ, entity.posZ);

            int distanceToCenter = context.getSize().height / 2;

            if (distanceX > maxRange || distanceY > maxRange || distanceX < -maxRange || distanceY < -maxRange) {
                return;
            }

            context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter + 1 + distanceX, context.getPos().y + distanceToCenter + distanceY), new Point(context.getPos().x + distanceToCenter - 1 + distanceX, context.getPos().y + distanceToCenter + distanceY), color, color);
            context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter + distanceX, context.getPos().y + distanceToCenter + 1 + distanceY), new Point(context.getPos().x + distanceToCenter + distanceX, context.getPos().y + distanceToCenter - 1 + distanceY), color, color);
        }

        //this allows for entities to be rendered north/south and east/west of the player (center) no matter the coordinates
        private int findDistanceByPoint(double start, double finish) {
            double start1 = start;
            double finish1 = finish;

            if (start < 0) {
                start1 = start * -1;
            }
            if (finish < 0) {
                finish1 = finish * -1;
            }

            int value = (int) (start1 - finish1);

            if (start - finish != value && start > 0 && finish > 0) {
                value = value * -1;
            }

            if (start < 0 && finish > 0 && value != (start - finish) || start > 0 && finish < 0 && value != (start - finish)) {
                value = (int) ((-1 * finish) + start);
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