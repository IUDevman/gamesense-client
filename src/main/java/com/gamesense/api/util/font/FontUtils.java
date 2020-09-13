package com.gamesense.api.util.font;

import com.gamesense.api.util.GSColor;
import com.gamesense.client.GameSenseMod;
import net.minecraft.client.Minecraft;

public class FontUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float drawStringWithShadow(boolean customFont, String text, int x, int y, GSColor color) {
        if (customFont) return GameSenseMod.fontRenderer.drawStringWithShadow(text, x, y, color);
        else return mc.fontRenderer.drawStringWithShadow(text, x, y, color.getRGB());
    }

    public static int getStringWidth(boolean customFont, String str) {
        if (customFont) return GameSenseMod.fontRenderer.getStringWidth(str);
        else return mc.fontRenderer.getStringWidth(str);
    }

    public static int getFontHeight(boolean customFont) {
        if (customFont) return GameSenseMod.fontRenderer.getHeight();
        else return mc.fontRenderer.FONT_HEIGHT;
    }
}
