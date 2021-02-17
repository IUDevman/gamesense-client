package com.gamesense.api.util.render;

import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPolygonOffset;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

/**
	Thank to seppuku for the code that i can understand,
	is this a skid? It depends by how you are looking this.
	I didnt only "copy" and "pasted", i also tried to understand what is happening
	(you can see this by reading every lines i commented)
	https://github.com/seppukudevelopment/seppuku/blob/master/src/main/java/me/rigamortis/seppuku/impl/module/render/ChamsModule.java
	Thank to lukflug for helping me to understand
	and for finding a bug

	Hoosiers here- Techale was right above, but I ended up porting in and modifying a few functions from the above link so credit to Seppuku...
	we have the same license, so it's safe to port in
 */
public class ChamsUtil {

	private static final Minecraft mc = Minecraft.getMinecraft();
	private final static float units = 5300000.0f;
	private final static float factor = 1.0f;

	public static void createChamsPre() {
		mc.getRenderManager().setRenderShadow(false);
		mc.getRenderManager().setRenderOutlines(false);
		GlStateManager.pushMatrix();
		GlStateManager.depthMask(true);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		glEnable(GL11.GL_POLYGON_OFFSET_FILL);
		glPolygonOffset(factor, -units);
		GlStateManager.popMatrix();
	}

	public static void createChamsPost() {
		boolean shadow = mc.getRenderManager().isRenderShadow();
		mc.getRenderManager().setRenderShadow(shadow);
		GlStateManager.pushMatrix();
		GlStateManager.depthMask(false);
		glDisable(GL11.GL_POLYGON_OFFSET_FILL);
		glPolygonOffset(factor, units);
		GlStateManager.popMatrix();
	}

	public static void createColorPre(GSColor color, boolean isPlayer) {
		mc.getRenderManager().setRenderShadow(false);
		mc.getRenderManager().setRenderOutlines(false);
		GlStateManager.pushMatrix();
		GlStateManager.depthMask(true);
		glEnable(GL11.GL_POLYGON_OFFSET_FILL);
		glPolygonOffset(factor, -units);
		glDisable(GL11.GL_TEXTURE_2D);
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
		glDisable(GL11.GL_POLYGON_OFFSET_FILL);
		glPolygonOffset(factor, units);
		glEnable(GL11.GL_TEXTURE_2D);
		GlStateManager.popMatrix();
	}

	public static void createWirePre(GSColor color, int lineWidth, boolean isPlayer) {
		mc.getRenderManager().setRenderShadow(false);
		mc.getRenderManager().setRenderOutlines(false);
		GlStateManager.pushMatrix();
		GlStateManager.depthMask(true);
		glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		glEnable(GL11.GL_POLYGON_OFFSET_LINE);
		glPolygonOffset(factor, -units);
		glDisable(GL11.GL_TEXTURE_2D);
		glDisable(GL11.GL_LIGHTING);
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
		glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		glDisable(GL11.GL_POLYGON_OFFSET_LINE);
		glPolygonOffset(factor, units);
		glEnable(GL11.GL_TEXTURE_2D);
		glEnable(GL11.GL_LIGHTING);
		glDisable(GL_LINE_SMOOTH);
		GlStateManager.popMatrix();
	}
}