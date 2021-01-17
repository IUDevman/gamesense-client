package com.gamesense.api.util.render;

import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.client.module.modules.render.Nametags;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author 086
 * @author Hoosiers
 * @author lukflug
 * @author TechAle
 */

public class RenderUtil {
	private static final Minecraft mc = Minecraft.getMinecraft();

	public static void drawBox(BlockPos blockPos, double height, GSColor color, int sides) {
		drawBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, height, 1, color, sides);
	}

	public static void drawBox(AxisAlignedBB bb, boolean check, double height, GSColor color, int sides) {
		if (check) {
			drawBox(bb.minX,bb.minY,bb.minZ,bb.maxX-bb.minX, bb.maxY-bb.minY,bb.maxZ-bb.minZ,color,sides);
		}
		else {
			drawBox(bb.minX,bb.minY,bb.minZ,bb.maxX-bb.minX, height,bb.maxZ-bb.minZ,color,sides);
		}
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

	public static void drawBoundingBox (BlockPos bp, double height, float width, GSColor color) {
		drawBoundingBox(getBoundingBox(bp,1, height,1),width,color);
	}

	public static void drawBoundingBox (AxisAlignedBB bb, double width, GSColor color) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.glLineWidth((float) width);
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

	public static void drawBoundingBoxWithSides(BlockPos blockPos, int width, GSColor color, int sides) {
		drawBoundingBoxWithSides(getBoundingBox(blockPos, 1, 1, 1), width, color, sides);
	}



	/* Chams start */
	/*
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

	public static void createChamsPre() {
		mc.getRenderManager().setRenderShadow(false);
		mc.getRenderManager().setRenderOutlines(false);
		GlStateManager.pushMatrix();
		GlStateManager.depthMask(true);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		glEnable(GL11.GL_POLYGON_OFFSET_FILL);
		glPolygonOffset(1.0f, -1100000.0f);
		GlStateManager.popMatrix();
	}

	public static void createChamsPost() {
		boolean shadow = mc.getRenderManager().isRenderShadow();
		mc.getRenderManager().setRenderShadow(shadow);
		GlStateManager.pushMatrix();
		GlStateManager.depthMask(false);
		glDisable(GL11.GL_POLYGON_OFFSET_FILL);
		glPolygonOffset(1.0f, 1100000.0f);
		GlStateManager.popMatrix();
	}

	public static void createColorPre(GSColor color, boolean isPlayer) {
		mc.getRenderManager().setRenderShadow(false);
		mc.getRenderManager().setRenderOutlines(false);
		GlStateManager.pushMatrix();
		GlStateManager.depthMask(true);
		glEnable(GL11.GL_POLYGON_OFFSET_FILL);
		glPolygonOffset(1.0f, -1100000.0f);
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
		glPolygonOffset(1.0f, 1100000.0f);
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
		glPolygonOffset(1.0f, -1100000.0f);
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
		glPolygonOffset(1.0f, 1100000.0f);
		glEnable(GL11.GL_TEXTURE_2D);
		glEnable(GL11.GL_LIGHTING);
		glDisable(GL_LINE_SMOOTH);
		GlStateManager.popMatrix();
	}

	/* Base for direction start */
	public static class Points {
		// Coordinates
		double[][] point = new double[10][2];
		// For counting when adding to point
		private int count = 0;
		// Center of the 2d square
		private final double xCenter;
		private final double zCenter;
		// Feet and Head
		public final double yMin;
		public final double yMax;
		// Rotation
		private final float rotation;
		// Constructor
		public Points(double yMin, double yMax, double xCenter, double zCenter, float rotation) {
			this.yMin = yMin;
			this.yMax = yMax;
			this.xCenter = xCenter;
			this.zCenter = zCenter;
			this.rotation = rotation;
		}
		// This add and elaborate the points
		public void addPoints(double x, double z) {
			/// Creating the rotation
			// Make center = 0
			x -= xCenter;
			z -= zCenter;
			// Matrix Rotation (https://en.wikipedia.org/wiki/Rotation_matrix)
			double rotateX = x * Math.cos(rotation) - z * Math.sin(rotation);
			double rotateZ = x * Math.sin(rotation) + z * Math.cos(rotation);
			// Return to where we were
			rotateX += xCenter;
			rotateZ += zCenter;
			// Add the point
			point[count++] = new double[] {rotateX, rotateZ};
		}
		// Shorter way for getting a point
		public double[] getPoint(int index) {
			return point[index];
		}

	}
	/*
		Mode:
		0 -> boxDirection
		(maybe in the future if i want to add something)
	 */

	public static void drawBoxWithDirection(AxisAlignedBB bb, GSColor color, float rotation, double width, int mode) {
		// Get the center of the 2d square (we are going to rotate based from the cetner)
		double xCenter = bb.minX + (bb.maxX - bb.minX) / 2;
		double zCenter = bb.minZ + (bb.maxZ - bb.minZ) / 2;
		// Collect every points
		Points square = new Points(bb.minY, bb.maxY, xCenter, zCenter, rotation);
		/// Create every points
		// For direction
		if (mode == 0) {
			square.addPoints(bb.minX, bb.minZ);
			square.addPoints(bb.minX, bb.maxZ);
			square.addPoints(bb.maxX, bb.maxZ);
			square.addPoints(bb.maxX, bb.minZ);
		}
		// Direct to the drawning
		switch (mode) {
			case 0:
				drawDirection(square, color, width);
				break;
		}
	}
	/* Base for direction end */

	/* drawBoxWithDirection start */
	public static void drawDirection(Points square, GSColor color, double width) {
		/// Lets dreaw all the lines
		// Down
		for(int i = 0; i < 4; i++) {
			drawLineWidth(square.getPoint(i)[0], square.yMin, square.getPoint(i)[1],
					square.getPoint((i + 1) % 4)[0], square.yMin, square.getPoint((i + 1) % 4)[1],
					color, width
			);
		}
		// Up
		for(int i = 0; i < 4; i++) {
			drawLineWidth(square.getPoint(i)[0], square.yMax, square.getPoint(i)[1],
					square.getPoint((i + 1) % 4)[0], square.yMax, square.getPoint((i + 1) % 4)[1],
					color, width
			);
		}
		// Vertically
		for(int i = 0; i < 4; i++) {
			drawLineWidth(square.getPoint(i)[0], square.yMin, square.getPoint(i)[1],
					square.getPoint(i)[0], square.yMax, square.getPoint(i)[1],
					color, width
			);
		}
	}

	public static void drawLineWidth(double posx, double posy, double posz, double posx2, double posy2, double posz2, GSColor color, double width) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.glLineWidth((float) width);
		color.glColor();
		bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		vertex(posx,posy,posz,bufferbuilder);
		vertex(posx2,posy2,posz2,bufferbuilder);
		tessellator.draw();
	}
	/* drawBoxWithDirection end */

	//hoosiers put this together with blood, sweat, and tears D:
	public static void drawBoundingBoxWithSides(AxisAlignedBB axisAlignedBB, int width, GSColor color, int sides) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.glLineWidth(width);
		color.glColor();
		double w = axisAlignedBB.maxX-axisAlignedBB.minX;
		double h = axisAlignedBB.maxY-axisAlignedBB.minY;
		double d = axisAlignedBB.maxZ-axisAlignedBB.minZ;

		bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
		if ((sides & GeometryMasks.Quad.EAST) != 0) {
			vertex(axisAlignedBB.minX+w,axisAlignedBB.minY,  axisAlignedBB.minZ+d,bufferbuilder);
			vertex(axisAlignedBB.minX+w,axisAlignedBB.minY,  axisAlignedBB.minZ,  bufferbuilder);
			vertex(axisAlignedBB.minX+w,axisAlignedBB.minY+h,axisAlignedBB.minZ,  bufferbuilder);
			vertex(axisAlignedBB.minX+w,axisAlignedBB.minY+h,axisAlignedBB.minZ+d,bufferbuilder);
			vertex(axisAlignedBB.minX +w, axisAlignedBB.minY, axisAlignedBB.minZ +d, bufferbuilder);
		}
		if ((sides & GeometryMasks.Quad.WEST) != 0) {
			vertex(axisAlignedBB.minX,axisAlignedBB.minY, axisAlignedBB.minZ,  bufferbuilder);
			vertex(axisAlignedBB.minX,axisAlignedBB.minY,axisAlignedBB.minZ+d,bufferbuilder);
			vertex(axisAlignedBB.minX,axisAlignedBB.minY+h,axisAlignedBB.minZ+d,bufferbuilder);
			vertex(axisAlignedBB.minX,axisAlignedBB.minY+h,axisAlignedBB.minZ,  bufferbuilder);
			vertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, bufferbuilder);
		}
		if ((sides & GeometryMasks.Quad.NORTH) != 0) {
			vertex(axisAlignedBB.minX + w,axisAlignedBB.minY, axisAlignedBB.minZ, bufferbuilder);
			vertex(axisAlignedBB.minX,  axisAlignedBB.minY, axisAlignedBB.minZ,bufferbuilder);
			vertex(axisAlignedBB.minX,  axisAlignedBB.minY+h,axisAlignedBB.minZ,bufferbuilder);
			vertex(axisAlignedBB.minX+w,axisAlignedBB.minY+h,axisAlignedBB.minZ,bufferbuilder);
			vertex(axisAlignedBB.minX +w, axisAlignedBB.minY, axisAlignedBB.minZ, bufferbuilder);
		}
		if ((sides & GeometryMasks.Quad.SOUTH) != 0) {
			vertex(axisAlignedBB.minX,  axisAlignedBB.minY,axisAlignedBB.minZ+d,bufferbuilder);
			vertex(axisAlignedBB.minX+w,axisAlignedBB.minY,axisAlignedBB.minZ+d,bufferbuilder);
			vertex(axisAlignedBB.minX+w,axisAlignedBB.minY+h,axisAlignedBB.minZ+d,bufferbuilder);
			vertex(axisAlignedBB.minX,  axisAlignedBB.minY+h,axisAlignedBB.minZ+d,bufferbuilder);
			vertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ +d, bufferbuilder);
		}
		if ((sides & GeometryMasks.Quad.UP) != 0) {
			vertex(axisAlignedBB.minX+w,axisAlignedBB.minY+h,axisAlignedBB.minZ,  bufferbuilder);
			vertex(axisAlignedBB.minX,  axisAlignedBB.minY+h,axisAlignedBB.minZ,  bufferbuilder);
			vertex(axisAlignedBB.minX,  axisAlignedBB.minY+h,axisAlignedBB.minZ+d,bufferbuilder);
			vertex(axisAlignedBB.minX+w,axisAlignedBB.minY+h,axisAlignedBB.minZ+d,bufferbuilder);
			vertex(axisAlignedBB.minX +w, axisAlignedBB.minY +h, axisAlignedBB.minZ, bufferbuilder);
		}
		if ((sides & GeometryMasks.Quad.DOWN) != 0) {
			vertex(axisAlignedBB.minX+w,axisAlignedBB.minY,axisAlignedBB.minZ,  bufferbuilder);
			vertex(axisAlignedBB.minX+w,axisAlignedBB.minY,axisAlignedBB.minZ+d,bufferbuilder);
			vertex(axisAlignedBB.minX,  axisAlignedBB.minY,axisAlignedBB.minZ+d,bufferbuilder);
			vertex(axisAlignedBB.minX,  axisAlignedBB.minY,axisAlignedBB.minZ,  bufferbuilder);
			vertex(axisAlignedBB.minX +w, axisAlignedBB.minY, axisAlignedBB.minZ, bufferbuilder);
		}
		tessellator.draw();
	}

	public static void drawLine(double posx, double posy, double posz, double posx2, double posy2, double posz2, GSColor color) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.glLineWidth(1.0f);
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

	public static void drawNametag (Entity entity, String[] text, GSColor color, int type) {
		Vec3d pos = EntityUtil.getInterpolatedPos(entity,mc.getRenderPartialTicks());
		drawNametag(pos.x,pos.y+entity.height,pos.z,text,color,type);
	}

	public static void drawNametag (double x, double y, double z, String[] text, GSColor color, int type) {
		double dist=mc.player.getDistance(x,y,z);
		double scale = 1, offset = 0;
		int start=0;
		switch (type) {
			case 0:
				scale=dist/20*Math.pow(1.2589254,0.1/(dist<25?0.5:2));
				scale=Math.min(Math.max(scale,.5),5);
				offset=scale>2?scale/2:scale;
				scale/=40;
				start=10;
				break;
			case 1:
				scale=-((int)dist)/6.0;
				if (scale<1) scale=1;
				scale*=2.0/75.0;
				break;
			case 2:
				scale=0.0018+0.003*dist;
				if (dist<=8.0) scale=0.0245;
				start=-8;
				break;
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(x-mc.getRenderManager().viewerPosX,y+offset-mc.getRenderManager().viewerPosY,z-mc.getRenderManager().viewerPosZ);
		GlStateManager.rotate(-mc.getRenderManager().playerViewY,0,1,0);
		GlStateManager.rotate(mc.getRenderManager().playerViewX,mc.gameSettings.thirdPersonView==2?-1:1,0,0);
		GlStateManager.scale(-scale,-scale,scale);
		if (type == 2) {
			double width = 0;
			GSColor bcolor = new GSColor(0,0,0,51);
			if (Nametags.customColor.getValue()) {
				bcolor = Nametags.borderColor.getValue();
			}
			for (int i = 0; i < text.length; i++) {
				double w= FontUtil.getStringWidth(ColorMain.customFont.getValue(),text[i])/2;
				if (w > width) {
					width = w;
				}
			}
			drawBorderedRect(-width - 1, -mc.fontRenderer.FONT_HEIGHT, width + 2,1,1.8f, new GSColor(0,4,0,85), bcolor);
		}
		GlStateManager.enableTexture2D();
		for (int i=0;i<text.length;i++) {
			FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(),text[i],-FontUtil.getStringWidth(ColorMain.customFont.getValue(),text[i])/2,i*(mc.fontRenderer.FONT_HEIGHT+1)+start,color);
		}
		GlStateManager.disableTexture2D();
		if (type!=2) {
			GlStateManager.popMatrix();
		}
	}

	private static void drawBorderedRect(double x, double y, double x1, double y1, float lineWidth, GSColor inside, GSColor border) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		inside.glColor();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(x,y1,0).endVertex();
		bufferbuilder.pos(x1,y1,0).endVertex();
		bufferbuilder.pos(x1,y,0).endVertex();
		bufferbuilder.pos(x,y,0).endVertex();
		tessellator.draw();
		border.glColor();
		GlStateManager.glLineWidth(lineWidth);
		bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(x,y,0).endVertex();
		bufferbuilder.pos(x,y1,0).endVertex();
		bufferbuilder.pos(x1,y1,0).endVertex();
		bufferbuilder.pos(x1,y,0).endVertex();
		bufferbuilder.pos(x,y,0).endVertex();
		tessellator.draw();
	}

	private static void vertex (double x, double y, double z, BufferBuilder bufferbuilder) {
		bufferbuilder.pos(x-mc.getRenderManager().viewerPosX,y-mc.getRenderManager().viewerPosY,z-mc.getRenderManager().viewerPosZ).endVertex();
	}

	private static AxisAlignedBB getBoundingBox (BlockPos bp, double width, double height, double depth) {
		double x=bp.getX();
		double y=bp.getY();
		double z=bp.getZ();
		return new AxisAlignedBB(x,y,z,x+width,y+height,z+depth);
	}

	public static void draw2DRect(int posX, int posY, int width, int height, int zHeight, GSColor color) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		color.glColor();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(posX, posY + height, zHeight).endVertex();
		bufferbuilder.pos(posX + width, posY + height, zHeight).endVertex();
		bufferbuilder.pos(posX + width, posY, zHeight).endVertex();
		bufferbuilder.pos(posX, posY, zHeight).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void prepare() {
		glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		glEnable(GL11.GL_LINE_SMOOTH);
		glEnable(GL32.GL_DEPTH_CLAMP);
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
		glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
	}
}