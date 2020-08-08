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

    Setting.b sortUp;
    Setting.b sortRight;
    Setting.i radarX;
    Setting.i radarY;
    Setting.mode display;

    public void setup(){
        ArrayList<String> displayModes = new ArrayList<>();
        displayModes.add("All");
        displayModes.add("Friend");
        displayModes.add("Enemy");
        display = registerMode("Display", "Display", displayModes, "All");
        sortUp = registerB("Sort Up", "SortUp", false);
        sortRight = registerB("Sort Right", "SortRight", false);
        radarX = registerI("X", "X", 0,0,1000);
        radarY = registerI("Y", "Y", 50, 0 , 1000);
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
                    if (display.getValue().equalsIgnoreCase("Friend") && !(Friends.isFriend(e.getName()))){
                        return;
                    }
                    if (display.getValue().equalsIgnoreCase("Enemy") && !(Enemies.isEnemy(e.getName()))){
                        return;
                    }
                    if (sortUp.getValue()){
                        if (sortRight.getValue()){
                                drawStringWithShadow(TextFormatting.GRAY + "[" + healthcolor + (int) (((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int) mc.player.getDistance(e) + TextFormatting.GRAY + "]", radarX.getValue() - getWidth(TextFormatting.GRAY + "[" + healthcolor + (int) (((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int) mc.player.getDistance(e) + TextFormatting.GRAY + "]"), (int) radarY.getValue() + (playerCount * 10), 0xffffffff);
                        } else {
                                drawStringWithShadow(TextFormatting.GRAY + "[" + healthcolor + (int) (((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int) mc.player.getDistance(e) + TextFormatting.GRAY + "]", radarX.getValue(), (int) radarY.getValue() + (playerCount * 10), 0xffffffff);
                        }
                        playerCount++;
                    }
                    else {
                        if (sortRight.getValue()){
                            drawStringWithShadow(TextFormatting.GRAY + "[" + healthcolor + (int)(((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int)mc.player.getDistance(e) + TextFormatting.GRAY + "]", radarX.getValue() - getWidth(TextFormatting.GRAY + "[" + healthcolor + (int)(((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int)mc.player.getDistance(e) + TextFormatting.GRAY + "]"), (int) radarY.getValue() + (playerCount * -10), 0xffffffff);
                        } else {
                            drawStringWithShadow(TextFormatting.GRAY + "[" + healthcolor + (int)(((EntityPlayer) e).getHealth() + ((EntityPlayer) e).getAbsorptionAmount()) + TextFormatting.GRAY + "] " + friendcolor + e.getName() + TextFormatting.GRAY + " [" + distancecolor + (int)mc.player.getDistance(e) + TextFormatting.GRAY + "]", radarX.getValue(), (int) radarY.getValue() + (playerCount * -10), 0xffffffff);
                        }
                        playerCount++;
                    }
                });
    }

    //bullshit port from HUD
    private void drawStringWithShadow (String text,int x, int y, int color) {
        if (HUD.customFont.getValue())
            GameSenseMod.fontRenderer.drawStringWithShadow(text, x, y, color);
        else
            mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    //bullshit port from HUD
    private int getWidth(String s){
        if(HUD.customFont.getValue()) return GameSenseMod.fontRenderer.getStringWidth(s);
        else return mc.fontRenderer.getStringWidth(s);
    }
}