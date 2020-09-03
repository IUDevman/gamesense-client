package com.gamesense.client.module.modules.hud;

import com.gamesense.api.players.enemy.Enemies;
import com.gamesense.api.players.friends.Friends;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

public class TextRadar extends Module {
    public TextRadar(){
        super("TextRadar", Category.HUD);
    }

    Setting.Boolean sortUp;
    Setting.Boolean sortRight;
    Setting.Integer radarX;
    Setting.Integer radarY;
    Setting.Integer range;
    Setting.Mode display;

    public void setup(){
        ArrayList<String> displayModes = new ArrayList<>();
        displayModes.add("All");
        displayModes.add("Friend");
        displayModes.add("Enemy");
        display = registerMode("Display", "Display", displayModes, "All");
        sortUp = registerBoolean("Sort Up", "SortUp", false);
        sortRight = registerBoolean("Sort Right", "SortRight", false);
        radarX = registerInteger("X", "X", 0,0,1000);
        radarY = registerInteger("Y", "Y", 50, 0 , 1000);
        range = registerInteger("Range", "Range", 100, 1, 260);
    }

    int sort;
    int playerCount;
    TextFormatting friendcolor;
    TextFormatting distancecolor;
    TextFormatting healthcolor;

    public void onRender(){
        if (sortUp.getValue()){
            sort = -1; }
        else {
            sort = 1; }
        playerCount = 0;
        mc.world.loadedEntityList.stream()
                .filter(e->e instanceof EntityPlayer)
                .filter(e->e != mc.player)
                .forEach(e->{
                    if (Friends.isFriend(e.getName())){
                        friendcolor = ColorMain.getFriendColor();
                    }
                    else if (Enemies.isEnemy(e.getName())){
                        friendcolor = ColorMain.getEnemyColor();
                    }
                    else {
                        friendcolor = TextFormatting.GRAY;
                    }
                    if ((((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) <= 5){
                        healthcolor = TextFormatting.RED;
                    }
                    if ((((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) > 5 && (((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) < 15){
                        healthcolor = TextFormatting.YELLOW;
                    }
                    if ((((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) >= 15){
                        healthcolor = TextFormatting.GREEN;
                    }
                    if (mc.player.getDistance(e) < 20){
                        distancecolor = TextFormatting.RED;
                    }
                    if (mc.player.getDistance(e) >= 20 && mc.player.getDistance(e) < 50){
                        distancecolor = TextFormatting.YELLOW;
                    }
                    if (mc.player.getDistance(e) >= 50){
                        distancecolor = TextFormatting.GREEN;
                    }
                    if (mc.player.getDistance(e) > range.getValue()){
                        return;
                    }
                    if (display.getValue().equalsIgnoreCase("Friend") && !(Friends.isFriend(e.getName()))){
                        return;
                    }
                    if (display.getValue().equalsIgnoreCase("Enemy") && !(Enemies.isEnemy(e.getName()))){
                        return;
                    }
                    if (sortUp.getValue()){
                        if (sortRight.getValue()){
                                FontUtils.drawStringWithShadow(TextFormatting.GRAY + "[" + healthcolor + (int) (((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int) mc.player.getDistance(e) + TextFormatting.GRAY + "]", radarX.getValue() - FontUtils.getWidth(TextFormatting.GRAY + "[" + healthcolor + (int) (((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int) mc.player.getDistance(e) + TextFormatting.GRAY + "]"), radarY.getValue() + (playerCount * 10), new GSColor(255,255,255));
                        } else {
                                FontUtils.drawStringWithShadow(TextFormatting.GRAY + "[" + healthcolor + (int) (((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int) mc.player.getDistance(e) + TextFormatting.GRAY + "]", radarX.getValue(), radarY.getValue() + (playerCount * 10), new GSColor(255,255,255));
                        }
                        playerCount++;
                    }
                    else {
                        if (sortRight.getValue()){
                            FontUtils.drawStringWithShadow(TextFormatting.GRAY + "[" + healthcolor + (int)(((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int)mc.player.getDistance(e) + TextFormatting.GRAY + "]", radarX.getValue() - FontUtils.getWidth(TextFormatting.GRAY + "[" + healthcolor + (int)(((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int)mc.player.getDistance(e) + TextFormatting.GRAY + "]"), radarY.getValue() + (playerCount * -10), new GSColor(255,255,255));
                        } else {
                            FontUtils.drawStringWithShadow(TextFormatting.GRAY + "[" + healthcolor + (int)(((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int)mc.player.getDistance(e) + TextFormatting.GRAY + "]", radarX.getValue(), radarY.getValue() + (playerCount * -10), new GSColor(255,255,255));
                        }
                        playerCount++;
                    }
                });
    }
}