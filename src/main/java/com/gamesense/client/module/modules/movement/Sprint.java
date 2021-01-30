package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.JumpEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.world.MotionUtil;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class Sprint extends Module {

	public Sprint() {
		super("Sprint", Category.Movement);
	}

	Setting.Boolean reverseSprint;

	public void setup() {
		reverseSprint = registerBoolean("Reverse", false);
	}

	public void onUpdate() {
		if (mc.player == null) {
			return;
		}

		if (mc.gameSettings.keyBindSneak.isKeyDown()) {
			mc.player.setSprinting(false);
		}
		else if (mc.player.getFoodStats().getFoodLevel() > 6 && reverseSprint.getValue()? (mc.player.moveForward != 0 || mc.player.moveStrafing != 0):mc.player.moveForward > 0) {
			mc.player.setSprinting(true);
		}
	}

	@EventHandler
	private final Listener<JumpEvent> jumpEventListener = new Listener<>(event -> {
		if (reverseSprint.getValue()) {
			double[] direction = MotionUtil.forward(0.017453292F);
			event.getLocation().setX(direction[0] * 0.2F);
			event.getLocation().setZ(direction[1] * 0.2F);
		}
	});
}