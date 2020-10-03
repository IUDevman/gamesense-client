package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.players.enemy.Enemies;
import com.gamesense.api.players.friends.Friends;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.hud.ColorMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;


import java.util.ArrayList;

/**
 * Made by Hoosiers on 8/12/20, some GL from Osiris/KAMI was referenced.
 */

public class Tracers extends Module {
	public Tracers(){
		super("Tracers", Category.Render);
	}

	Setting.Integer renderDistance;
	Setting.Mode pointsTo;
	Setting.ColorSetting nearColor;
	Setting.ColorSetting midColor;
	Setting.ColorSetting farColor;

	public void setup(){
		renderDistance = registerInteger("Distance", "Distance", 100, 10, 260);

		ArrayList<String> link = new ArrayList<>();
		link.add("Head");
		link.add("Feet");

		pointsTo = registerMode("Draw To", "DrawTo", link, "Feet");
		nearColor=registerColor("Near Color","NearColor",new GSColor(255,0,0));
		midColor=registerColor("Middle Color","MidColor",new GSColor(255,255,0));
		farColor=registerColor("Far Color","FarColor",new GSColor(0,255,0));
	}

	GSColor tracerColor;

	public void onWorldRender(RenderEvent event){
		mc.world.loadedEntityList.stream()
				.filter(e->e instanceof EntityPlayer)
				.filter(e->e != mc.player)
				.forEach(e->{
					if (mc.player.getDistance(e) > renderDistance.getValue()){
						return;
					} else {
						if (Friends.isFriend(e.getName())) {
							tracerColor = ColorMain.getFriendGSColor();
						} else if (Enemies.isEnemy(e.getName())) {
							tracerColor = ColorMain.getEnemyGSColor();
						} else {
							if (mc.player.getDistance(e) < 20) {
								tracerColor = nearColor.getValue();
							}
							if (mc.player.getDistance(e) >= 20 && mc.player.getDistance(e) < 50) {
								tracerColor = midColor.getValue();
							}
							if (mc.player.getDistance(e) >= 50) {
								tracerColor = farColor.getValue();
							}
						}
					}
					if (pointsTo.getValue().equalsIgnoreCase("Head")) {
						drawLineToEntityPlayer(e, tracerColor);
					} else if (pointsTo.getValue().equalsIgnoreCase("Feet")) {
						drawLineToEntityPlayer(e, tracerColor);
					}
				});
	}

	public void drawLineToEntityPlayer(Entity e, GSColor color){
		double[] xyz = interpolate(e);
		drawLine1(xyz[0],xyz[1],xyz[2], e.height, color);
	}

	public static double[] interpolate(Entity entity) {
		double posX = interpolate(entity.posX, entity.lastTickPosX) - mc.getRenderManager().renderPosX;
		double posY = interpolate(entity.posY, entity.lastTickPosY) - mc.getRenderManager().renderPosY;
		double posZ = interpolate(entity.posZ, entity.lastTickPosZ) - mc.getRenderManager().renderPosZ;
		return new double[] { posX, posY, posZ };
	}

	public static double interpolate(double now, double then) {
		return then + (now - then) * mc.getRenderPartialTicks();
	}

	public void drawLine1(double posx, double posy, double posz, double up, GSColor color){
		Vec3d eyes = new Vec3d(0, 0, 1)
				.rotatePitch(-(float)Math
						.toRadians(Minecraft.getMinecraft().player.rotationPitch))
				.rotateYaw(-(float)Math
						.toRadians(Minecraft.getMinecraft().player.rotationYaw));

		if (pointsTo.getValue().equalsIgnoreCase("Head")) {
			renderLine(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, posx, posy, posz, up, color);
		} else {
			renderLine(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, posx, posy, posz, color);
		}
	}

	public static void renderLine(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, GSColor color){
		GlStateManager.pushMatrix();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.glLineWidth(1.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		color.glColor();
		GlStateManager.disableLighting();
		GlStateManager.loadIdentity();
		mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());
		GlStateManager.glBegin(GL11.GL_LINES);
			GL11.glVertex3d(posx, posy, posz);
			GL11.glVertex3d(posx2, posy2+up, posz2);
		GlStateManager.glEnd();
		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GL11.glColor3d(1d,1d,1d);
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}

	public static void renderLine(double posx, double posy, double posz, double posx2, double posy2, double posz2, GSColor color){
		renderLine(posx,posy,posz,posx2,posy2,posz2,0,color);
	}
}