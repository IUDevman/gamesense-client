package com.gamesense.api.util.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import com.gamesense.api.util.Wrapper;
import com.gamesense.api.util.world.GeometryMasks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;


//Credit 086 for Kami base Tessellator

public class GameSenseTessellator {
	private static final Minecraft mc = Wrapper.getMinecraft();
	
	public static void drawBox(BlockPos blockPos, GSColor color, int sides) {
		drawBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, color, sides);
	}
	
	public static void drawBox(AxisAlignedBB bb, GSColor color, int sides) {
		drawBox((float)bb.minX,(float)bb.minY,(float)bb.minZ,(float)(bb.maxX-bb.minX),(float)(bb.maxY-bb.minY),(float)(bb.maxZ-bb.minZ),color,sides);
	}

	public static void drawDownBox(BlockPos blockPos, GSColor color, int sides) {
		drawDownBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, color, sides);
	}

	public static void drawDownBox (float x, float y, float z, float w, float h, float d, GSColor color, int sides) {
		drawBox(x,y,z,w,-h,d,color,sides);
	}

	public static void drawBox(float x, float y, float z, float w, float h, float d, GSColor color, int sides) {
		int r=color.getRed(), g=color.getGreen(), b=color.getBlue(), a=color.getAlpha();
		prepareGL();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		if ((sides & GeometryMasks.Quad.DOWN) != 0) {
			bufferbuilder.pos(x + w, y, z).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x, y, z + d).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x, y, z).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.UP) != 0) {
			bufferbuilder.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x, y + h, z).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.NORTH) != 0) {
			bufferbuilder.pos(x + w, y, z).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x, y, z).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x, y + h, z).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.SOUTH) != 0) {
			bufferbuilder.pos(x, y, z + d).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.WEST) != 0) {
			bufferbuilder.pos(x, y, z).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x, y, z + d).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x, y + h, z).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.EAST) != 0) {
			bufferbuilder.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + w, y, z).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
			bufferbuilder.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
		}
		tessellator.draw();
		releaseGL();
	}

	public static void drawSphere(double x, double y, double z, float size, int slices, int stacks) {
		final Sphere s = new Sphere();
		begin3D(1.2f);
		s.setDrawStyle(GLU.GLU_SILHOUETTE);
		GlStateManager.translate(x - mc.getRenderManager().renderPosX, y - mc.getRenderManager().renderPosY, z - mc.getRenderManager().renderPosZ);
		s.draw(size, slices, stacks);
		end3D();
	}
	
	public static void drawBoundingBox(final AxisAlignedBB bb, final float width, GSColor color) {
		int red=color.getRed(), green=color.getGreen(), blue=color.getBlue(), alpha=color.getAlpha();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		begin3D(width);
		bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
		tessellator.draw();
		end3D();
	}

	public static void drawBoundingBoxBlockPos (BlockPos bp, float width, GSColor color) {
		drawBoundingBox(getBoundingBox(bp,1,1,1),width,color);
	}
	
	public static void drawBoundingBoxBlockPos2 (BlockPos bp, float width, GSColor color) {
		drawBoundingBox(getBoundingBox(bp,1,-1,1),width,color);
	}

	public static void drawBoundingBoxBottom2(BlockPos bp, float width, GSColor color) {
		int red=color.getRed(), green=color.getGreen(), blue=color.getBlue(), alpha=color.getAlpha();
		begin3D(width);
		AxisAlignedBB bb = getBoundingBox(bp,1,1,1);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
			bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
		tessellator.draw();
		end3D();
	}

	public static void glBillboard(float x, float y, float z){
		float scale = 0.016666668f * 1.6f;
		GlStateManager.translate(x - Minecraft.getMinecraft().getRenderManager().renderPosX, y - Minecraft.getMinecraft().getRenderManager().renderPosY, z - Minecraft.getMinecraft().getRenderManager().renderPosZ);
		GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(-Minecraft.getMinecraft().player.rotationYaw, 0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(Minecraft.getMinecraft().player.rotationPitch, Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
		GlStateManager.scale(-scale, -scale, scale);
	}

	public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
		glBillboard(x, y, z);
		int distance = (int) player.getDistance(x, y, z);
		float scaleDistance = (distance / 2.0f) / (2.0f + (2.0f - scale));
		if (scaleDistance < 1f) scaleDistance = 1;
		GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
	}
	
	private static void begin3D (float width) {
		Tessellator.getInstance().getBuffer().setTranslation(-mc.getRenderManager().viewerPosX,-mc.getRenderManager().viewerPosY,-mc.getRenderManager().viewerPosZ);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
		GlStateManager.glLineWidth(width);
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GlStateManager.pushMatrix();
	}
	
	private static void end3D() {
		GlStateManager.popMatrix();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.glLineWidth(1.0f);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
		Tessellator.getInstance().getBuffer().setTranslation(0,0,0);
	}

	private static void prepareGL() {
		Tessellator.getInstance().getBuffer().setTranslation(-mc.getRenderManager().viewerPosX,-mc.getRenderManager().viewerPosY,-mc.getRenderManager().viewerPosZ);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(1.5F);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.color(1, 1, 1, 1);
	}

	private static void releaseGL(){
		GlStateManager.enableCull();
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.enableDepth();
		GlStateManager.color(1, 1, 1, 1);
		Tessellator.getInstance().getBuffer().setTranslation(0,0,0);
	}
	
	private static AxisAlignedBB getBoundingBox (BlockPos bp, int width, int height, int depth) {
		double x=bp.getX();
		double y=bp.getY();
		double z=bp.getZ();
		return new AxisAlignedBB(x,y,z,x+width,y+height,z+depth);
	}
}
