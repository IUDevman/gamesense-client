package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.players.enemy.Enemies;
import com.gamesense.api.players.friends.Friends;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.hud.ColorMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

/**
 * Made by Hoosiers on 8/12/20, some GL from Osiris/KAMI was referenced.
 */

public class Tracers extends Module{
	public Tracers(){
		super("Tracers", Category.Render);
	}

	Setting.Integer renderDistance;
	Setting.Mode pointsTo;

	public void setup(){
		renderDistance = registerInteger("Distance", "Distance", 100, 10, 260);

		ArrayList<String> link = new ArrayList<>();
		link.add("Head");
		link.add("Feet");

		pointsTo = registerMode("Draw To", "DrawTo", link, "Feet");
	}

	int tracerColor;

	public void onWorldRender(RenderEvent event){
		mc.world.loadedEntityList.stream()
				.filter(e->e instanceof EntityPlayer)
				.filter(e->e != mc.player)
				.forEach(e->{
					if (mc.player.getDistance(e) > renderDistance.getValue()){
						return;
					} else{
						if (Friends.isFriend(e.getName())){
							tracerColor = ColorMain.getFriendColorInt();
						} else if (Enemies.isEnemy(e.getName())){
							tracerColor = ColorMain.getEnemyColorInt();
						} else{
							if (mc.player.getDistance(e) < 20){
								tracerColor = Color.RED.getRGB();
							}
							if (mc.player.getDistance(e) >= 20 && mc.player.getDistance(e) < 50){
								tracerColor = Color.YELLOW.getRGB();
							}
							if (mc.player.getDistance(e) >= 50){
								tracerColor = Color.GREEN.getRGB();
							}
						}
					}
					if (pointsTo.getValue().equalsIgnoreCase("Head")){
						drawLineToEntityPlayer(e, tracerColor, 255);
					} else if (pointsTo.getValue().equalsIgnoreCase("Feet")){
						drawLineToEntityPlayer(e, tracerColor, 255);
					}
				});
	}

	public void drawLineToEntityPlayer(Entity e, int rgb, int a){
		double[] xyz = interpolate(e);
		final int r = (rgb >>> 16) & 0xFF;
		final int g = (rgb >>> 8) & 0xFF;
		final int b = rgb & 0xFF;
		drawLine1(xyz[0],xyz[1],xyz[2], e.height, r, g, b, a);
	}

	public static double[] interpolate(Entity entity){
		double posX = interpolate(entity.posX, entity.lastTickPosX) - mc.getRenderManager().renderPosX;
		double posY = interpolate(entity.posY, entity.lastTickPosY) - mc.getRenderManager().renderPosY;
		double posZ = interpolate(entity.posZ, entity.lastTickPosZ) - mc.getRenderManager().renderPosZ;
		return new double[]{ posX, posY, posZ };
	}

	public static double interpolate(double now, double then){
		return then + (now - then) * mc.getRenderPartialTicks();
	}

	public void drawLine1(double posx, double posy, double posz, double up, float red, float green, float blue, float opacity){
		Vec3d eyes = new Vec3d(0, 0, 1)
				.rotatePitch(-(float)Math
						.toRadians(Minecraft.getMinecraft().player.rotationPitch))
				.rotateYaw(-(float)Math
						.toRadians(Minecraft.getMinecraft().player.rotationYaw));

		if (pointsTo.getValue().equalsIgnoreCase("Head")){
			renderLine1(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, posx, posy, posz, up, red, green, blue, opacity);
		}
		else{
			renderLine2(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, posx, posy, posz, up, red, green, blue, opacity);
		}
	}

	public static void renderLine1(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, float red, float green, float blue, float opacity){
		GL11.glPushMatrix();
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL_BLEND);
		GL11.glLineWidth(1.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4f(red, green, blue, opacity);
		GlStateManager.disableLighting();
		GL11.glLoadIdentity();
		mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());
		GL11.glBegin(GL11.GL_LINES);{
			GL11.glVertex3d(posx, posy, posz);
			GL11.glVertex3d(posx2, posy2+up, posz2);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor3d(1d,1d,1d);
		GlStateManager.enableLighting();
		GL11.glPopMatrix();
	}

	public static void renderLine2(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, float red, float green, float blue, float opacity){
		GL11.glPushMatrix();
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL_BLEND);
		GL11.glLineWidth(1.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4f(red, green, blue, opacity);
		GlStateManager.disableLighting();
		GL11.glLoadIdentity();
		mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());
		GL11.glBegin(GL11.GL_LINES);{
			GL11.glVertex3d(posx, posy, posz);
			GL11.glVertex3d(posx2, posy2, posz2);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor3d(1d,1d,1d);
		GlStateManager.enableLighting();
		GL11.glPopMatrix();
	}
}