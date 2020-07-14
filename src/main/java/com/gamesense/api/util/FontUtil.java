package com.gamesense.api.util;

import com.gamesense.client.GameSenseMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StringUtils;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit me
 *
 *  @author HeroCode
 */
public class FontUtil {
	private static FontRenderer fontRenderer;

	private static final Minecraft mc = Minecraft.getMinecraft();

	public static void setupFontUtils() {
		fontRenderer = Minecraft.getMinecraft().fontRenderer;
	}

	public static int getStringWidth(String text) {
		return fontRenderer.getStringWidth(StringUtils.stripControlCodes(text));
	}

	public static int getFontHeight() {
		return fontRenderer.FONT_HEIGHT;
	}

	public static void drawString(String text, double x, double y, int color) {
		fontRenderer.drawString(text, (int)x, (int)y, color);
	}

	public static float drawStringWithShadow(boolean customFont, String text, int x, int y, int color){
		if(customFont) return GameSenseMod.fontRenderer.drawStringWithShadow(text, x, y, color);
		else return mc.fontRenderer.drawStringWithShadow(text, x, y, color);
	}

	public static int getStringWidth(boolean customFont, String str){
		if(customFont) return GameSenseMod.fontRenderer.getStringWidth(str);
		else return mc.fontRenderer.getStringWidth(str);
	}

	public static void drawCenteredString(String text, double x, double y, int color) {
		drawString(text, x - fontRenderer.getStringWidth(text) / 2f, y, color);
	}
}
