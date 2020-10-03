package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.players.friends.Friends;
import com.gamesense.api.util.Wrapper;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.client.module.Module;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class HitSpheres extends Module{

	public HitSpheres(){super("HitSpheres", Category.Render);}

	public void onWorldRender(RenderEvent event){
		for (Entity ep : Wrapper.getWorld().loadedEntityList){
			if (ep instanceof EntityPlayerSP){
				continue;
			}
			if (ep instanceof EntityPlayer){
				double posX = ep.lastTickPosX + (ep.posX - ep.lastTickPosX) * (double)Wrapper.getMinecraft().timer.renderPartialTicks;
				double posY = ep.lastTickPosY + (ep.posY - ep.lastTickPosY) * (double)Wrapper.getMinecraft().timer.renderPartialTicks;
				double posZ = ep.lastTickPosZ + (ep.posZ - ep.lastTickPosZ) * (double)Wrapper.getMinecraft().timer.renderPartialTicks;
				if (Friends.isFriend(ep.getName())){
					GlStateManager.color(0.15F, 0.15F, 1.0F, 1.0F);
				} else{
					if (Wrapper.getPlayer().getDistanceSq(ep) >= 64){
						GlStateManager.color(0.0F, 1.0F, 0.0F, 1.0F);
					} else{
						GlStateManager.color(1.0F, Wrapper.getPlayer().getDistance(ep) / 150, 0.0F, 1.0F);
					}
				}
				GameSenseTessellator.drawSphere(posX, posY, posZ, 6, 20, 15);
			}
		}
	}
}