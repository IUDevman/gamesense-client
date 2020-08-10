package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;

public class ColorMain extends Module {
    public ColorMain() {
        super("Colors", Category.HUD);
        setDrawn(false);
    }

    public static Setting.b Rainbow;
    public static Setting.i Red;
    public static Setting.i Blue;
    public static Setting.i Green;
    public static Setting.mode friendcolor;
    public static Setting.mode enemycolor;

    public void setup() {
        Rainbow = this.registerB("Rainbow", "Rainbow", false);
        Red = this.registerI("Red", "Red", 255, 0, 255);
        Green = this.registerI("Green", "Green", 26, 0, 255);
        Blue = this.registerI("Blue", "Blue", 42, 0, 255);

        ArrayList<String> tab = new ArrayList<>();
        tab.add("Black");
        tab.add("Dark Green");
        tab.add("Dark Red");
        tab.add("Gold");
        tab.add("Dark Gray");
        tab.add("Green");
        tab.add("Red");
        tab.add("Yellow");
        tab.add("Dark Blue");
        tab.add("Dark Aqua");
        tab.add("Dark Purple");
        tab.add("Gray");
        tab.add("Blue");
        tab.add("Aqua");
        tab.add("Light Purple");
        tab.add("White");
        friendcolor = this.registerMode("Friend", "FriendColor", tab, "Blue");
        enemycolor = this.registerMode("Enemy", "EnemyColor", tab, "Red");
    }

    public void onEnable(){
        this.disable();
    }

    public static TextFormatting getFriendColor(){
        if (friendcolor.getValue().equalsIgnoreCase("Black")){
            return TextFormatting.BLACK;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Dark Green")){
            return TextFormatting.DARK_GREEN;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Dark Red")){
            return TextFormatting.DARK_RED;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Gold")){
            return TextFormatting.GOLD;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Dark Gray")){
            return TextFormatting.DARK_GRAY;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Green")){
            return TextFormatting.GREEN;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Red")){
            return TextFormatting.RED;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Yellow")){
            return TextFormatting.YELLOW;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Dark Blue")){
            return TextFormatting.DARK_BLUE;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Dark Aqua")){
            return TextFormatting.DARK_AQUA;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Dark Purple")){
            return TextFormatting.DARK_PURPLE;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Gray")){
            return TextFormatting.GRAY;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Blue")){
            return TextFormatting.BLUE;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Light Purple")){
            return TextFormatting.LIGHT_PURPLE;
        }
        if (friendcolor.getValue().equalsIgnoreCase("White")){
            return TextFormatting.WHITE;
        }
        if (friendcolor.getValue().equalsIgnoreCase("Aqua")){
            return TextFormatting.GREEN.AQUA;
        }
        return null;
    }

    public static TextFormatting getEnemyColor() {
        if (enemycolor.getValue().equalsIgnoreCase("Black")) {
            return TextFormatting.BLACK;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Dark Green")) {
            return TextFormatting.DARK_GREEN;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Dark Red")) {
            return TextFormatting.DARK_RED;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Gold")) {
            return TextFormatting.GOLD;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Dark Gray")) {
            return TextFormatting.DARK_GRAY;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Green")) {
            return TextFormatting.GREEN;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Red")) {
            return TextFormatting.RED;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Yellow")) {
            return TextFormatting.YELLOW;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Dark Blue")) {
            return TextFormatting.DARK_BLUE;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Dark Aqua")) {
            return TextFormatting.DARK_AQUA;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Dark Purple")) {
            return TextFormatting.DARK_PURPLE;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Gray")) {
            return TextFormatting.GRAY;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Blue")) {
            return TextFormatting.BLUE;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Light Purple")) {
            return TextFormatting.LIGHT_PURPLE;
        }
        if (enemycolor.getValue().equalsIgnoreCase("White")) {
            return TextFormatting.WHITE;
        }
        if (enemycolor.getValue().equalsIgnoreCase("Aqua")) {
            return TextFormatting.GREEN.AQUA;
        }
        return null;
    }

    public static int getFriendColorInt(){
        if (friendcolor.getValue().equalsIgnoreCase("Black")){
            return Color.BLACK.getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Dark Green")){
            return Color.GREEN.darker().getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Dark Red")){
            return Color.RED.darker().getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Gold")){
            return Color.yellow.darker().getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Dark Gray")){
            return Color.DARK_GRAY.getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Green")){
            return Color.green.getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Red")){
            return Color.red.getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Yellow")){
            return Color.yellow.getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Dark Blue")){
            return Color.blue.darker().getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Dark Aqua")){
            return Color.CYAN.darker().getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Dark Purple")){
            return Color.MAGENTA.darker().getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Gray")){
            return Color.GRAY.getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Blue")){
            return Color.blue.getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Light Purple")){
            return Color.magenta.getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("White")){
            return Color.WHITE.getRGB();
        }
        if (friendcolor.getValue().equalsIgnoreCase("Aqua")){
            return Color.cyan.getRGB();
        }
        return Color.WHITE.getRGB();
    }

    public static int getEnemyColorInt(){
        if (enemycolor.getValue().equalsIgnoreCase("Black")){
            return Color.BLACK.getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Dark Green")){
            return Color.GREEN.darker().getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Dark Red")){
            return Color.RED.darker().getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Gold")){
            return Color.yellow.darker().getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Dark Gray")){
            return Color.DARK_GRAY.getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Green")){
            return Color.green.getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Red")){
            return Color.red.getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Yellow")){
            return Color.yellow.getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Dark Blue")){
            return Color.blue.darker().getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Dark Aqua")){
            return Color.CYAN.darker().getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Dark Purple")){
            return Color.MAGENTA.darker().getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Gray")){
            return Color.GRAY.getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Blue")){
            return Color.blue.getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Light Purple")){
            return Color.magenta.getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("White")){
            return Color.WHITE.getRGB();
        }
        if (enemycolor.getValue().equalsIgnoreCase("Aqua")){
            return Color.cyan.getRGB();
        }
        return Color.WHITE.getRGB();
    }
}