package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.WaterPushEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.client.event.InputUpdateEvent;
import org.lwjgl.input.Keyboard;

public class PlayerTweaks extends Module {

	public PlayerTweaks() {
		super("PlayerTweaks", Category.Movement);
	}

	public Setting.Boolean guiMove;
	public static Setting.Boolean noPush;
	public Setting.Boolean noSlow;
	Setting.Boolean antiKnockBack;

	public void setup() {
		guiMove = registerBoolean("Gui Move", false);
		noPush = registerBoolean("No Push", false);
		noSlow = registerBoolean("No Slow", false);
		antiKnockBack = registerBoolean("Velocity", false);
	}

	//No Slow
	@EventHandler
	private final Listener<InputUpdateEvent> eventListener = new Listener<>(event -> {
		if (noSlow.getValue()) {
			if (mc.player.isHandActive() && !mc.player.isRiding()) {
				event.getMovementInput().moveStrafe *= 5;
				event.getMovementInput().moveForward *= 5;
			}
		}
	});

	//Gui Move, this breaks with future for some reason... IDK if there is another way to do this
	public void onUpdate() {
		if (guiMove.getValue() && mc.currentScreen != null) {
			if (!(mc.currentScreen instanceof GuiChat)){
				if (Keyboard.isKeyDown(200)) {
					mc.player.rotationPitch -= 5;
				}
				if (Keyboard.isKeyDown(208)) {
					mc.player.rotationPitch += 5;
				}
				if (Keyboard.isKeyDown(205)) {
					mc.player.rotationYaw += 5;
				}
				if (Keyboard.isKeyDown(203)) {
					mc.player.rotationYaw -= 5;
				}
				if (mc.player.rotationPitch > 90) {
					mc.player.rotationPitch = 90;
				}
				if (mc.player.rotationPitch < -90) {
					mc.player.rotationPitch = -90;
				}
			}
		}
	}

	//Velocity
	@EventHandler
	private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
		if (antiKnockBack.getValue()) {
			if (event.getPacket() instanceof SPacketEntityVelocity) {
				if (((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) {
					event.cancel();
				}
			}
			if (event.getPacket() instanceof SPacketExplosion) {
				event.cancel();
			}
		}
	});

	@EventHandler
	private final Listener<WaterPushEvent> waterPushEventListener = new Listener<>(event -> {
		if (noPush.getValue()) {
			event.cancel();
		}
	});
}