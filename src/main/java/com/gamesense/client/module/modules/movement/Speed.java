package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.*;
import net.minecraft.init.MobEffects;

import java.util.ArrayList;

/**
 * @author Crystallinqq/Auto for original code
 * @source https://github.com/Crystallinqq/Mercury-Client/blob/master/src/main/java/fail/mercury/client/client/modules/movement/Speed.java
 * @reworked by Hoosiers on 11/1/2020
 */

public class Speed extends Module {

	public Speed() {
		super("Speed", Category.Movement);
	}

	Setting.Boolean timerBool;
	Setting.Double timerVal;
	Setting.Double jumpHeight;
	Setting.Double yPortSpeed;
	Setting.Mode mode;

	public void setup() {
		ArrayList<String> modes = new ArrayList<>();
		modes.add("Strafe");
		modes.add("Fake");
		modes.add("YPort");

		mode = registerMode("Mode", modes, "Strafe");
		yPortSpeed = registerDouble("Y Port Speed", 0.06, 0.01, 0.15);
		jumpHeight = registerDouble("Jump Speed", 0.41, 0, 1);
		timerBool = registerBoolean("Timer", true);
		timerVal = registerDouble("Timer Speed", 1.15, 1, 1.5);
	}

	private boolean slowDown;
	private double playerSpeed;
	private Timer timer = new Timer();

	public void onEnable() {
		GameSense.EVENT_BUS.subscribe(this);
		playerSpeed = MotionUtil.getBaseMoveSpeed();
	}

	public void onDisable() {
		GameSense.EVENT_BUS.unsubscribe(this);
		timer.reset();
		EntityUtil.resetTimer();
	}

	public void onUpdate() {
		if (mc.player == null || mc.world == null) {
			disable();
			return;
		}

		if (mode.getValue().equalsIgnoreCase("YPort")) {
			handleYPortSpeed();
		}
	}

	private void handleYPortSpeed() {
		if (!MotionUtil.isMoving(mc.player) || mc.player.isInWater() && mc.player.isInLava() || mc.player.collidedHorizontally) {
			return;
		}

		if (mc.player.onGround) {
			EntityUtil.setTimer(1.15f);
			mc.player.jump();
			MotionUtil.setSpeed(mc.player, MotionUtil.getBaseMoveSpeed() + yPortSpeed.getValue());
		}
		else {
			mc.player.motionY = -1;
			EntityUtil.resetTimer();
		}
	}

	@EventHandler
	private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<>(event -> {
		if (mc.player.isInLava() || mc.player.isInWater() || mc.player.isOnLadder() || mc.player.isInWeb) {
			return;
		}

		if (mode.getValue().equalsIgnoreCase("Strafe")) {
			double speedY = jumpHeight.getValue();

			if (mc.player.onGround && MotionUtil.isMoving(mc.player) && timer.hasReached(300)) {
				EntityUtil.setTimer((float)timerVal.getValue());
				if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
					speedY += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
				}

				event.setY(mc.player.motionY = speedY);
				playerSpeed = MotionUtil.getBaseMoveSpeed() * (EntityUtil.isColliding(0, -0.5, 0) instanceof BlockLiquid && !EntityUtil.isInLiquid() ? 0.9 : 1.901);
				slowDown = true;
				timer.reset();
			}
			else {
				EntityUtil.resetTimer();
				if (slowDown || mc.player.collidedHorizontally) {
					playerSpeed -= (EntityUtil.isColliding(0, -0.8, 0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) ? 0.4 : 0.7 * (playerSpeed = MotionUtil.getBaseMoveSpeed());
					slowDown = false;
				}
				else {
					playerSpeed -= playerSpeed / 159.0;
				}
			}
			playerSpeed = Math.max(playerSpeed, MotionUtil.getBaseMoveSpeed());
			double[] dir = MotionUtil.forward(playerSpeed);
			event.setX(dir[0]);
			event.setZ(dir[1]);
		}
	});

	public String getHudInfo() {
		String t = "";
		if (mode.getValue().equalsIgnoreCase("Strafe")) {
			t = "[" + ChatFormatting.WHITE + "Strafe" + ChatFormatting.GRAY + "]";
		}
		else if (mode.getValue().equalsIgnoreCase("YPort")) {
			t = "[" + ChatFormatting.WHITE + "YPort" + ChatFormatting.GRAY + "]";
		}
		else if (mode.getValue().equalsIgnoreCase("Fake")) {
			t = "[" + ChatFormatting.WHITE + "Fake" + ChatFormatting.GRAY + "]";
		}
		return t;
	}
}