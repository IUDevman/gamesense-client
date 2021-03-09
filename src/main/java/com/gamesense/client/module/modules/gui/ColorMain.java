package com.gamesense.client.module.modules.gui;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.ColorUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;

@Module.Declaration(name = "Colors", category = Category.GUI, drawn = false)
public class ColorMain extends Module {

    public BooleanSetting customFont = registerBoolean("Custom Font", true);
    public BooleanSetting textFont = registerBoolean("Custom Text", false);
    public ModeSetting friendColor = registerMode("Friend Color", ColorUtil.colors, "Blue");
    public ModeSetting enemyColor = registerMode("Enemy Color", ColorUtil.colors, "Red");
    public ModeSetting chatEnableColor = registerMode("Msg Enbl", ColorUtil.colors, "Green");
    public ModeSetting chatDisableColor = registerMode("Msg Dsbl", ColorUtil.colors, "Red");
    public ModeSetting colorModel = registerMode("Color Model", Arrays.asList("RGB", "HSB"), "HSB");

    public void onEnable() {
        this.disable();
    }

    public TextFormatting getFriendColor() {
        return ColorUtil.settingToTextFormatting(friendColor);
    }

    public TextFormatting getEnemyColor() {
        return ColorUtil.settingToTextFormatting(enemyColor);
    }

    public TextFormatting getEnabledColor() {
        return ColorUtil.settingToTextFormatting(chatEnableColor);
    }

    public TextFormatting getDisabledColor() {
        return ColorUtil.settingToTextFormatting(chatDisableColor);
    }

    public GSColor getFriendGSColor() {
        return new GSColor(ColorUtil.settingToColor(friendColor));
    }

    public GSColor getEnemyGSColor() {
        return new GSColor(ColorUtil.settingToColor(enemyColor));
    }
}