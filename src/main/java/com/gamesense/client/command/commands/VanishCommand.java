package com.gamesense.client.command.commands;

import com.gamesense.client.command.Command;
import com.gamesense.api.util.misc.MessageBus;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

// Created by d1gress/Qther on 25/11/2017.

public class VanishCommand extends Command{

	@Override
	public String[] getAlias(){
		return new String[]{"vanish", "v"};
	}

	@Override
	public String getSyntax(){
		return "vanish (godmode exploit)";
	}

	private static Entity vehicle;
	Minecraft mc = Minecraft.getMinecraft();

	@Override
	public void onCommand(String command, String[] args) throws Exception{
		if (mc.player.getRidingEntity() != null && vehicle == null){
			vehicle = mc.player.getRidingEntity();
			mc.player.dismountRidingEntity();
			mc.world.removeEntityFromWorld(vehicle.getEntityId());
			MessageBus.sendClientPrefixMessage("Vehicle " + vehicle.getName() + " removed.");
		} else{
			if (vehicle != null){
				vehicle.isDead = false;
				mc.world.addEntityToWorld(vehicle.getEntityId(), vehicle);
				mc.player.startRiding(vehicle, true);
				MessageBus.sendClientPrefixMessage("Vehicle " + vehicle.getName() + " created.");
				vehicle = null;
			} else{
				MessageBus.sendClientPrefixMessage("No Vehicle.");
			}
		}
	}
}