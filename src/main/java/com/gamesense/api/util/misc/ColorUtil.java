package com.gamesense.api.util.misc;

import com.gamesense.api.setting.values.ModeSetting;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ColorUtil {

    public static List<String> colors = Arrays.asList("Black", "Dark Green", "Dark Red", "Gold", "Dark Gray", "Green", "Red", "Yellow", "Dark Blue", "Dark Aqua", "Dark Purple", "Gray", "Blue", "Aqua", "Light Purple", "White");

    public static TextFormatting settingToTextFormatting(ModeSetting setting) {
        if (setting.getValue().equalsIgnoreCase("Black")) {
            return TextFormatting.BLACK;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Green")) {
            return TextFormatting.DARK_GREEN;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Red")) {
            return TextFormatting.DARK_RED;
        }
        if (setting.getValue().equalsIgnoreCase("Gold")) {
            return TextFormatting.GOLD;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Gray")) {
            return TextFormatting.DARK_GRAY;
        }
        if (setting.getValue().equalsIgnoreCase("Green")) {
            return TextFormatting.GREEN;
        }
        if (setting.getValue().equalsIgnoreCase("Red")) {
            return TextFormatting.RED;
        }
        if (setting.getValue().equalsIgnoreCase("Yellow")) {
            return TextFormatting.YELLOW;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Blue")) {
            return TextFormatting.DARK_BLUE;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Aqua")) {
            return TextFormatting.DARK_AQUA;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Purple")) {
            return TextFormatting.DARK_PURPLE;
        }
        if (setting.getValue().equalsIgnoreCase("Gray")) {
            return TextFormatting.GRAY;
        }
        if (setting.getValue().equalsIgnoreCase("Blue")) {
            return TextFormatting.BLUE;
        }
        if (setting.getValue().equalsIgnoreCase("Light Purple")) {
            return TextFormatting.LIGHT_PURPLE;
        }
        if (setting.getValue().equalsIgnoreCase("White")) {
            return TextFormatting.WHITE;
        }
        if (setting.getValue().equalsIgnoreCase("Aqua")) {
            return TextFormatting.AQUA;
        }
        return null;
    }

    public static ChatFormatting textToChatFormatting(ModeSetting setting) {
        if (setting.getValue().equalsIgnoreCase("Black")) {
            return ChatFormatting.BLACK;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Green")) {
            return ChatFormatting.DARK_GREEN;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Red")) {
            return ChatFormatting.DARK_RED;
        }
        if (setting.getValue().equalsIgnoreCase("Gold")) {
            return ChatFormatting.GOLD;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Gray")) {
            return ChatFormatting.DARK_GRAY;
        }
        if (setting.getValue().equalsIgnoreCase("Green")) {
            return ChatFormatting.GREEN;
        }
        if (setting.getValue().equalsIgnoreCase("Red")) {
            return ChatFormatting.RED;
        }
        if (setting.getValue().equalsIgnoreCase("Yellow")) {
            return ChatFormatting.YELLOW;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Blue")) {
            return ChatFormatting.DARK_BLUE;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Aqua")) {
            return ChatFormatting.DARK_AQUA;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Purple")) {
            return ChatFormatting.DARK_PURPLE;
        }
        if (setting.getValue().equalsIgnoreCase("Gray")) {
            return ChatFormatting.GRAY;
        }
        if (setting.getValue().equalsIgnoreCase("Blue")) {
            return ChatFormatting.BLUE;
        }
        if (setting.getValue().equalsIgnoreCase("Light Purple")) {
            return ChatFormatting.LIGHT_PURPLE;
        }
        if (setting.getValue().equalsIgnoreCase("White")) {
            return ChatFormatting.WHITE;
        }
        if (setting.getValue().equalsIgnoreCase("Aqua")) {
            return ChatFormatting.AQUA;
        }
        return null;
    }

    public static Color settingToColor(ModeSetting setting) {
        if (setting.getValue().equalsIgnoreCase("Black")) {
            return Color.BLACK;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Green")) {
            return Color.GREEN.darker();
        }
        if (setting.getValue().equalsIgnoreCase("Dark Red")) {
            return Color.RED.darker();
        }
        if (setting.getValue().equalsIgnoreCase("Gold")) {
            return Color.yellow.darker();
        }
        if (setting.getValue().equalsIgnoreCase("Dark Gray")) {
            return Color.DARK_GRAY;
        }
        if (setting.getValue().equalsIgnoreCase("Green")) {
            return Color.green;
        }
        if (setting.getValue().equalsIgnoreCase("Red")) {
            return Color.red;
        }
        if (setting.getValue().equalsIgnoreCase("Yellow")) {
            return Color.yellow;
        }
        if (setting.getValue().equalsIgnoreCase("Dark Blue")) {
            return Color.blue.darker();
        }
        if (setting.getValue().equalsIgnoreCase("Dark Aqua")) {
            return Color.CYAN.darker();
        }
        if (setting.getValue().equalsIgnoreCase("Dark Purple")) {
            return Color.MAGENTA.darker();
        }
        if (setting.getValue().equalsIgnoreCase("Gray")) {
            return Color.GRAY;
        }
        if (setting.getValue().equalsIgnoreCase("Blue")) {
            return Color.blue;
        }
        if (setting.getValue().equalsIgnoreCase("Light Purple")) {
            return Color.magenta;
        }
        if (setting.getValue().equalsIgnoreCase("White")) {
            return Color.WHITE;
        }
        if (setting.getValue().equalsIgnoreCase("Aqua")) {
            return Color.cyan;
        }
        return Color.WHITE;
    }
}