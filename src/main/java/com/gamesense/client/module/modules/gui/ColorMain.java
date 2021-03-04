package com.gamesense.client.module.modules.gui;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.ColorUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.Category;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

@Module.Declaration(name = "Colors", category = Category.GUI, drawn = false)
public class ColorMain extends Module {

    public static ModeSetting colorModel;
    public static ModeSetting friendColor;
    public static ModeSetting enemyColor;
    public static ModeSetting chatEnableColor;
    public static ModeSetting chatDisableColor;
    public static BooleanSetting customFont;
    public static BooleanSetting textFont;

    public void setup() {
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

        ArrayList<String> models = new ArrayList<>();
        models.add("RGB");
        models.add("HSB");

        customFont = registerBoolean("Custom Font", true);
        textFont = registerBoolean("Custom Text", false);
        friendColor = registerMode("Friend Color", tab, "Blue");
        enemyColor = registerMode("Enemy Color", tab, "Red");
        chatEnableColor = registerMode("Msg Enbl", tab, "Green");
        chatDisableColor = registerMode("Msg Dsbl", tab, "Red");
        colorModel = registerMode("Color Model", models, "HSB");
    }

    public void onEnable() {
        this.disable();
    }

    public static TextFormatting getFriendColor() {
        return ColorUtil.settingToTextFormatting(friendColor);
    }

    public static TextFormatting getEnemyColor() {
        return ColorUtil.settingToTextFormatting(enemyColor);
    }

    public static TextFormatting getEnabledColor() {
        return ColorUtil.settingToTextFormatting(chatEnableColor);
    }

    public static TextFormatting getDisabledColor() {
        return ColorUtil.settingToTextFormatting(chatDisableColor);
    }

    public static GSColor getFriendGSColor() {
        return new GSColor(ColorUtil.settingToColor(friendColor));
    }

    public static GSColor getEnemyGSColor() {
        return new GSColor(ColorUtil.settingToColor(enemyColor));
    }
}
