package com.gamesense.api.util.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import com.gamesense.api.util.Wrapper;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.client.module.modules.hud.HUD;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


//Credit 086 for Kami base Tessellator

public class GameSenseTessellator {
	private static final Minecraft mc = Wrapper.getMinecraft();
	
	public static void drawBox(BlockPos blockPos, GSColor color, int sides) {
		drawBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, color, sides);
	}
	
	public static void drawBox(AxisAlignedBB bb, GSColor color, int sides) {
		drawBox(bb.minX,bb.minY,bb.minZ,bb.maxX-bb.minX,bb.maxY-bb.minY,bb.maxZ-bb.minZ,color,sides);
	}

	public static void drawDownBox(BlockPos blockPos, GSColor color, int sides) {
		drawDownBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, color, sides);
	}

	public static void drawDownBox (double x, double y, double z, double w, double h, double d, GSColor color, int sides) {
		drawBox(x,y,z,w,-h,d,color,sides);
	}

	public static void drawBox(double x, double y, double z, double w, double h, double d, GSColor color, int sides) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		color.glColor();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		if ((sides & GeometryMasks.Quad.DOWN) != 0) {
			vertex(x+w,y,z,  bufferbuilder);
			vertex(x+w,y,z+d,bufferbuilder);
			vertex(x,  y,z+d,bufferbuilder);
			vertex(x,  y,z,  bufferbuilder);
		}
		if ((sides & GeometryMasks.Quad.UP) != 0) {
			vertex(x+w,y+h,z,  bufferbuilder);
			vertex(x,  y+h,z,  bufferbuilder);
			vertex(x,  y+h,z+d,bufferbuilder);
			vertex(x+w,y+h,z+d,bufferbuilder);
		}
		if ((sides & GeometryMasks.Quad.NORTH) != 0) {
			vertex(x+w,y,  z,bufferbuilder);
			vertex(x,  y,  z,bufferbuilder);
			vertex(x,  y+h,z,bufferbuilder);
			vertex(x+w,y+h,z,bufferbuilder);
		}
		if ((sides & GeometryMasks.Quad.SOUTH) != 0) {
			vertex(x,  y,  z+d,bufferbuilder);
			vertex(x+w,y,  z+d,bufferbuilder);
			vertex(x+w,y+h,z+d,bufferbuilder);
			vertex(x,  y+h,z+d,bufferbuilder);
		}
		if ((sides & GeometryMasks.Quad.WEST) != 0) {
			vertex(x,y,  z,  bufferbuilder);
			vertex(x,y,  z+d,bufferbuilder);
			vertex(x,y+h,z+d,bufferbuilder);
			vertex(x,y+h,z,  bufferbuilder);
		}
		if ((sides & GeometryMasks.Quad.EAST) != 0) {
			vertex(x+w,y,  z+d,bufferbuilder);
			vertex(x+w,y,  z,  bufferbuilder);
			vertex(x+w,y+h,z,  bufferbuilder);
			vertex(x+w,y+h,z+d,bufferbuilder);
		}
		tessellator.draw();
	}
	
	public static void drawBoundingBox (BlockPos bp, float width, GSColor color) {
		drawBoundingBox(getBoundingBox(bp,1,1,1),width,color);
	}
	
	public static void drawBoundingDownBox (BlockPos bp, float width, GSColor color) {
		drawBoundingBox(getBoundingBox(bp,1,-1,1),width,color);
	}
	
	public static void drawBoundingBox (AxisAlignedBB bb, float width, GSColor color) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.glLineWidth(width);
		color.glColor();
		bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
			vertex(bb.minX,bb.minY,bb.minZ,bufferbuilder);
			vertex(bb.minX,bb.minY,bb.maxZ,bufferbuilder);
			vertex(bb.maxX,bb.minY,bb.maxZ,bufferbuilder);
			vertex(bb.maxX,bb.minY,bb.minZ,bufferbuilder);
			vertex(bb.minX,bb.minY,bb.minZ,bufferbuilder);
			vertex(bb.minX,bb.maxY,bb.minZ,bufferbuilder);
			vertex(bb.minX,bb.maxY,bb.maxZ,bufferbuilder);
			vertex(bb.minX,bb.minY,bb.maxZ,bufferbuilder);
			vertex(bb.maxX,bb.minY,bb.maxZ,bufferbuilder);
			vertex(bb.maxX,bb.maxY,bb.maxZ,bufferbuilder);
			vertex(bb.minX,bb.maxY,bb.maxZ,bufferbuilder);
			vertex(bb.maxX,bb.maxY,bb.maxZ,bufferbuilder);
			vertex(bb.maxX,bb.maxY,bb.minZ,bufferbuilder);
			vertex(bb.maxX,bb.minY,bb.minZ,bufferbuilder);
			vertex(bb.maxX,bb.maxY,bb.minZ,bufferbuilder);
			vertex(bb.minX,bb.maxY,bb.minZ,bufferbuilder);
		tessellator.draw();
	}

	public static void drawBoundingBoxBottom (BlockPos bp, float width, GSColor color) {
		GlStateManager.glLineWidth(width);
		AxisAlignedBB bb = getBoundingBox(bp,1,1,1);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		color.glColor();
		bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
			vertex(bb.minX,bb.minY,bb.minZ,bufferbuilder);
			vertex(bb.minX,bb.minY,bb.maxZ,bufferbuilder);
			vertex(bb.maxX,bb.minY,bb.maxZ,bufferbuilder);
			vertex(bb.maxX,bb.minY,bb.minZ,bufferbuilder);
			vertex(bb.minX,bb.minY,bb.minZ,bufferbuilder);
			vertex(bb.minX,bb.minY,bb.maxZ,bufferbuilder);
			vertex(bb.maxX,bb.minY,bb.maxZ,bufferbuilder);
			vertex(bb.maxX,bb.minY,bb.minZ,bufferbuilder);
		tessellator.draw();
	}
	
	public static void drawLine(double posx, double posy, double posz, double posx2, double posy2, double posz2, GSColor color){
		GlStateManager.glLineWidth(1.0f);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		color.glColor();
		bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
			vertex(posx,posy,posz,bufferbuilder);
			vertex(posx2,posy2,posz2,bufferbuilder);
		tessellator.draw();
	}

	public static void drawSphere(double x, double y, double z, float size, int slices, int stacks) {
		final Sphere s = new Sphere();
		GlStateManager.glLineWidth(1.2f);
		s.setDrawStyle(GLU.GLU_SILHOUETTE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x-mc.getRenderManager().viewerPosX,y-mc.getRenderManager().viewerPosY,z-mc.getRenderManager().viewerPosZ);
		s.draw(size, slices, stacks);
		GlStateManager.popMatrix();
	}

	public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
		float s = 2f/75f/*0.016666668f * 1.6f*/;
		GlStateManager.translate(x - Minecraft.getMinecraft().getRenderManager().renderPosX, y - Minecraft.getMinecraft().getRenderManager().renderPosY, z - Minecraft.getMinecraft().getRenderManager().renderPosZ);
		GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(-Minecraft.getMinecraft().player.rotationYaw, 0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(Minecraft.getMinecraft().player.rotationPitch, Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
		GlStateManager.scale(-s, -s, s);
		int distance = (int) player.getDistance(x, y, z);
		float scaleDistance = -distance/(8-2*scale);
		if (scaleDistance < 1f) scaleDistance = 1;
		GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
	}
		
	public static void drawNametag (Entity entity, String[] text, GSColor color, int type) {
		Vec3d pos = EntityUtil.getInterpolatedPos(entity,mc.getRenderPartialTicks());
		drawNametag(pos.x,pos.y+entity.height,pos.z,text,color,type);
	}
	
	public static void drawNametag (double x, double y, double z, String[] text, GSColor color, int type) {
		double dist=mc.player.getDistance(x,y,z);
		double scale=1,offset=0;
		int start=0;
		switch (type) {
		case 0:
			scale=dist/20*Math.pow(1.2589254,0.1/(dist<25?0.5:2));
			scale=Math.min(Math.max(scale,.5),5);
			offset=scale>2?scale/2:scale;
			scale/=40;
			start=1;
			break;
		case 1:
			scale=-((int)dist)/6.0;
			if (scale<1) scale=1;
			scale*=2.0/75.0;
			break;
		case 2:
			scale=0.0018+0.003*dist;
			if (dist<=8.0) scale=0.0245;
			start=-1;
			break;
		}
		GlStateManager.enableTexture2D();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x-mc.getRenderManager().viewerPosX,y+offset-mc.getRenderManager().viewerPosY,z-mc.getRenderManager().viewerPosZ);
		GlStateManager.rotate(-mc.getRenderManager().playerViewY,0,1,0);
		GlStateManager.rotate(mc.getRenderManager().playerViewX,mc.gameSettings.thirdPersonView==2?-1:1,0,0);
		GlStateManager.scale(-scale,-scale,scale);
		for (int i=0;i<text.length;i++) {
			FontUtils.drawStringWithShadow(HUD.customFont.getValue(),text[i],-FontUtils.getStringWidth(HUD.customFont.getValue(),text[i])/2,(i+start)*10,color);
		}
		GlStateManager.popMatrix();
		GlStateManager.disableTexture2D();
	}
	
	public static void prepare() {
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL32.GL_DEPTH_CLAMP);
	}
	
	public static void release() {
		GL11.glDisable(GL32.GL_DEPTH_CLAMP);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableAlpha();
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.glLineWidth(1.0f);
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
	}
	
	private static void vertex (double x, double y, double z, BufferBuilder bufferbuilder) {
		bufferbuilder.pos(x-mc.getRenderManager().viewerPosX,y-mc.getRenderManager().viewerPosY,z-mc.getRenderManager().viewerPosZ).endVertex();
	}
	
	private static AxisAlignedBB getBoundingBox (BlockPos bp, int width, int height, int depth) {
		double x=bp.getX();
		double y=bp.getY();
		double z=bp.getZ();
		return new AxisAlignedBB(x,y,z,x+width,y+height,z+depth);
	}
}
