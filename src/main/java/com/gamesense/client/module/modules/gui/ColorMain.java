package com.gamesense.client.module.modules.gui;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;

public class ColorMain extends Module {

	public ColorMain() {
		super("Colors", Category.GUI);
		setDrawn(false);
	}

	public static Setting.Mode colorModel;
	public static Setting.Mode friendColor;
	public static Setting.Mode enemyColor;
	public static Setting.Mode chatEnableColor;
	public static Setting.Mode chatDisableColor;
	public static Setting.Boolean customFont;
	public static Setting.Boolean textFont;

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
		colorModel=registerMode("Color Model", models,"HSB");
	}

	public void onEnable() {
		this.disable();
	}
	
	private static TextFormatting settingToFormatting (Setting.Mode setting) {
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

	public static TextFormatting getFriendColor() {
		return settingToFormatting(friendColor);
	}

	public static TextFormatting getEnemyColor() {
		return settingToFormatting(enemyColor);
	}

	public static TextFormatting getEnabledColor() {
		return settingToFormatting(chatEnableColor);
	}

	public static TextFormatting getDisabledColor() {
		return settingToFormatting(chatDisableColor);
	}
	
	private static Color settingToColor (Setting.Mode setting) {
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

	public static GSColor getFriendGSColor() {
		return new GSColor(settingToColor(friendColor));
	}

	public static GSColor getEnemyGSColor() {
		return new GSColor(settingToColor(enemyColor));
	}
}
