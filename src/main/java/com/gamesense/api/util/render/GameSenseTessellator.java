package com.gamesense.api.util.render;

import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.api.util.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import static org.lwjgl.opengl.GL11.*;

//Credit 086 for Kami base Tessellator

public class GameSenseTessellator extends Tessellator{

	public static GameSenseTessellator INSTANCE = new GameSenseTessellator();

	public GameSenseTessellator(){
		super(0x200000);
	}

	public static void prepare(int mode){
		prepareGL();
		begin(mode);
	}

	public static void prepareGL(){
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(1.5F);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.color(1, 1, 1);
	}

	public static void begin(int mode){
		INSTANCE.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
	}

	public static void release(){
		render();
		releaseGL();
	}

	public static void render(){
		INSTANCE.draw();
	}

	public static void releaseGL(){
		GlStateManager.enableCull();
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.enableDepth();
		GlStateManager.color(1, 1, 1);
		GL11.glColor4f(1, 1, 1, 1);
	}

	public static void drawBox(AxisAlignedBB bb, int argb, int sides){
		final int a = (argb >>> 24) & 0xFF;
		final int r = (argb >>> 16) & 0xFF;
		final int g = (argb >>> 8) & 0xFF;
		final int b = argb & 0xFF;
		drawBox(INSTANCE.getBuffer(), bb, r, g, b, a, sides);
	}

	public static void drawBox(BlockPos blockPos, int argb, int sides){
		final int a = (argb >>> 24) & 0xFF;
		final int r = (argb >>> 16) & 0xFF;
		final int g = (argb >>> 8) & 0xFF;
		final int b = argb & 0xFF;
		drawBox(blockPos, r, g, b, a, sides);
	}

	public static void drawBox2(BlockPos blockPos, int argb, int sides){
		final int a = (argb >>> 24) & 0xFF;
		final int r = (argb >>> 16) & 0xFF;
		final int g = (argb >>> 8) & 0xFF;
		final int b = argb & 0xFF;
		drawDownBox(blockPos, r, g, b, a, sides);
	}

	public static void drawDownBox(BlockPos blockPos, int r, int g, int b, int a, int sides){
		drawDownBox2(INSTANCE.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, r, g, b, a, sides);
	}

	public static void drawDownBox2(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides){
		if ((sides & GeometryMasks.Quad.DOWN) != 0){
			buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x, y, z).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.UP) != 0){
			buffer.pos(x + w, y - h, z).color(r, g, b, a).endVertex();
			buffer.pos(x, y - h, z).color(r, g, b, a).endVertex();
			buffer.pos(x, y - h, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y - h, z + d).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.NORTH) != 0){
			buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
			buffer.pos(x, y, z).color(r, g, b, a).endVertex();
			buffer.pos(x, y - h, z).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y - h, z).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.SOUTH) != 0){
			buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y - h, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x, y - h, z + d).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.WEST) != 0){
			buffer.pos(x, y, z).color(r, g, b, a).endVertex();
			buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x, y - h, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x, y - h, z).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.EAST) != 0){
			buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y - h, z).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y - h, z + d).color(r, g, b, a).endVertex();
		}
	}

	public static void drawBox(BlockPos blockPos, int r, int g, int b, int a, int sides){
		drawBox(INSTANCE.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, r, g, b, a, sides);
	}

	public static void drawBox(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides){
		if ((sides & GeometryMasks.Quad.DOWN) != 0){
			buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x, y, z).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.UP) != 0){
			buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
			buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
			buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.NORTH) != 0){
			buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
			buffer.pos(x, y, z).color(r, g, b, a).endVertex();
			buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.SOUTH) != 0){
			buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.WEST) != 0){
			buffer.pos(x, y, z).color(r, g, b, a).endVertex();
			buffer.pos(x, y, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x, y + h, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x, y + h, z).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.EAST) != 0){
			buffer.pos(x + w, y, z + d).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y, z).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
			buffer.pos(x + w, y + h, z + d).color(r, g, b, a).endVertex();
		}
	}

	public static void drawBox(BufferBuilder buffer, AxisAlignedBB bb, int r, int g, int b, int a, int sides){
		if ((sides & GeometryMasks.Quad.DOWN) != 0){
			buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.UP) != 0){
			buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.NORTH) != 0){
			buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.SOUTH) != 0){
			buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.WEST) != 0){
			buffer.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
		}
		if ((sides & GeometryMasks.Quad.EAST) != 0){
			buffer.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
			buffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
		}
	}

	private static final Minecraft mc = Wrapper.getMinecraft();

	public static void drawSphere(double x, double y, double z, float size, int slices, int stacks){
		final Sphere s = new Sphere();
		GL11.glPushMatrix();
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL_BLEND);
		GL11.glLineWidth(1.2F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		s.setDrawStyle(GLU.GLU_SILHOUETTE);
		GL11.glTranslated(x - mc.getRenderManager().renderPosX, y - mc.getRenderManager().renderPosY, z - mc.getRenderManager().renderPosZ);
		s.draw(size, slices, stacks);
		GL11.glLineWidth(2.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL_BLEND);
		GL11.glPopMatrix();
	}

	public static void drawBoundingBox(final AxisAlignedBB bb, final float width, final int argb){
		final int a = argb >>> 24 & 0xFF;
		final int r = argb >>> 16 & 0xFF;
		final int g = argb >>> 8 & 0xFF;
		final int b = argb & 0xFF;
		drawBoundingBox(bb, width, r, g, b, a);
	}

	public static void drawBoundingBoxBlockPos(BlockPos bp, float width, int r, int g, int b, int alpha){
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glLineWidth(width);
		Minecraft mc = Minecraft.getMinecraft();
		double x = (double) bp.getX() - mc.getRenderManager().viewerPosX;
		double y = (double) bp.getY() - mc.getRenderManager().viewerPosY;
		double z = (double) bp.getZ() - mc.getRenderManager().viewerPosZ;
		AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
		tessellator.draw();
		bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
		tessellator.draw();
		bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
		tessellator.draw();
		GL11.glDisable(2848);
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	public static void drawBoundingBoxBlockPos2(BlockPos bp, float width, int r, int g, int b, int alpha){
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glLineWidth(width);
		Minecraft mc = Minecraft.getMinecraft();
		double x = (double) bp.getX() - mc.getRenderManager().viewerPosX;
		double y = (double) bp.getY() - mc.getRenderManager().viewerPosY;
		double z = (double) bp.getZ() - mc.getRenderManager().viewerPosZ;
		AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y - 1.0, z + 1.0);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
		tessellator.draw();
		bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
		tessellator.draw();
		bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
		tessellator.draw();
		GL11.glDisable(2848);
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	public static void drawBoundingBoxBottom2(BlockPos bp, float width, final int red, final int green, final int blue, final int alpha){
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glLineWidth(width);
		Minecraft mc = Minecraft.getMinecraft();
		double x = (double) bp.getX() - mc.getRenderManager().viewerPosX;
		double y = (double) bp.getY() - mc.getRenderManager().viewerPosY;
		double z = (double) bp.getZ() - mc.getRenderManager().viewerPosZ;
		AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
		tessellator.draw();
		GL11.glDisable(2848);
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}


	public static void glBillboard(float x, float y, float z){
		float scale = 0.016666668f * 1.6f;
		GlStateManager.translate(x - Minecraft.getMinecraft().getRenderManager().renderPosX, y - Minecraft.getMinecraft().getRenderManager().renderPosY, z - Minecraft.getMinecraft().getRenderManager().renderPosZ);
		GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(-Minecraft.getMinecraft().player.rotationYaw, 0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(Minecraft.getMinecraft().player.rotationPitch, Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
		GlStateManager.scale(-scale, -scale, scale);
	}

	public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale){
		glBillboard(x, y, z);
		int distance = (int) player.getDistance(x, y, z);
		float scaleDistance = (distance / 2.0f) / (2.0f + (2.0f - scale));
		if (scaleDistance < 1f)
			scaleDistance = 1;
		GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
	}

	public static void drawBoundingBox(final AxisAlignedBB bb, final float width, final int red, final int green, final int blue, final int alpha){
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GlStateManager.glLineWidth(width);
		final BufferBuilder bufferbuilder = INSTANCE.getBuffer();
		bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
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
		render();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}
