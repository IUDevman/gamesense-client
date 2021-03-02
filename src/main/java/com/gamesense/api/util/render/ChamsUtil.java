package com.gamesense.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

import static org.lwjgl.opengl.GL11.*;

/**
 * Thank to seppuku for the code that i can understand,
 * is this a skid? It depends by how you are looking this.
 * I didnt only "copy" and "pasted", i also tried to understand what is happening
 * (you can see this by reading every lines i commented)
 * https://github.com/seppukudevelopment/seppuku/blob/master/src/main/java/me/rigamortis/seppuku/impl/module/render/ChamsModule.java
 * Thank to lukflug for helping me to understand
 * and for finding a bug
 * <p>
 * Hoosiers here- Techale was right above, but I ended up porting in and modifying a few functions from the above link so credit to Seppuku...
 * we have the same license, so it's safe to port in
 */
public class ChamsUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void createChamsPre() {
        mc.getRenderManager().setRenderShadow(false);
        mc.getRenderManager().setRenderOutlines(false);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        glEnable(GL_POLYGON_OFFSET_FILL);
        glDepthRange(0.0, 0.01);
        GlStateManager.popMatrix();
    }

    public static void createChamsPost() {
        boolean shadow = mc.getRenderManager().isRenderShadow();
        mc.getRenderManager().setRenderShadow(shadow);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        glDisable(GL_POLYGON_OFFSET_FILL);
        glDepthRange(0.0, 1.0);
        GlStateManager.popMatrix();
    }

    public static void createColorPre(GSColor color, boolean isPlayer) {
        mc.getRenderManager().setRenderShadow(false);
        mc.getRenderManager().setRenderOutlines(false);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        glEnable(GL_POLYGON_OFFSET_FILL);
        glDepthRange(0.0, 0.01);
        glDisable(GL_TEXTURE_2D);
        if (!isPlayer) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        color.glColor();
        GlStateManager.popMatrix();
    }

    public static void createColorPost(boolean isPlayer) {
        boolean shadow = mc.getRenderManager().isRenderShadow();
        mc.getRenderManager().setRenderShadow(shadow);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        if (!isPlayer) {
            GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        glDisable(GL_POLYGON_OFFSET_FILL);
        glDepthRange(0.0, 1.0);
        glEnable(GL_TEXTURE_2D);
        GlStateManager.popMatrix();
    }

    public static void createWirePre(GSColor color, int lineWidth, boolean isPlayer) {
        mc.getRenderManager().setRenderShadow(false);
        mc.getRenderManager().setRenderOutlines(false);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glEnable(GL_POLYGON_OFFSET_LINE);
        glDepthRange(0.0, 0.01);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        if (!isPlayer) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        glLineWidth(lineWidth);
        color.glColor();
        GlStateManager.popMatrix();
    }

    public static void createWirePost(boolean isPlayer) {
        boolean shadow = mc.getRenderManager().isRenderShadow();
        mc.getRenderManager().setRenderShadow(shadow);
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        if (!isPlayer) {
            GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glDisable(GL_POLYGON_OFFSET_LINE);
        glDepthRange(0.0, 1.0);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LIGHTING);
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.popMatrix();
    }
}