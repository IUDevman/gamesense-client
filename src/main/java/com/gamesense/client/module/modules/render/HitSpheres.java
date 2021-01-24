package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.player.friend.Friends;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.module.Module;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class HitSpheres extends Module {

	public HitSpheres() {
		super("HitSpheres", Category.Render);
	}

	public void onWorldRender(RenderEvent event) {
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityPlayerSP) {
				continue;
			}
			if (entity instanceof EntityPlayer) {
				double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) mc.timer.renderPartialTicks;
				double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) mc.timer.renderPartialTicks;
				double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) mc.timer.renderPartialTicks;
				if (Friends.isFriend(entity.getName())) {
					new GSColor(38,38,255).glColor();
				} else {
					if (mc.player.getDistanceSq(entity) >= 64) {
						new GSColor(0,255,0).glColor();
					}
					else {
						new GSColor(255,(int)(mc.player.getDistance(entity)*255/150f),0).glColor();
					}
				}
				RenderUtil.drawSphere(posX, posY, posZ, 6, 20, 15);
			}
		}
	}
}