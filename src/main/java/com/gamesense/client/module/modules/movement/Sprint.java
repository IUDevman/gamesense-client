package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.JumpEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.world.MotionUtils;
import com.gamesense.client.module.Module;

import java.util.ArrayList;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", Category.Movement);
    }

    Setting.Mode Mode;

    public void setup() {
        ArrayList<String> sprintModes = new ArrayList<>();
        sprintModes.add("Legit");
        sprintModes.add("Rage");
        Mode = registerMode("Mode", "Mode", sprintModes, "Legit");
    }

    public void onUpdate() {
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.setSprinting(false);
            return;
        }
        if (mc.player.getFoodStats().getFoodLevel() > 6 && Mode.getValue().equalsIgnoreCase("Rage") ? (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) : mc.player.moveForward > 0)
            mc.player.setSprinting(true);
    }

    public void onJump(JumpEvent event) {
        if (Mode.getValue().equalsIgnoreCase("Rage")) {
            double[] dir = MotionUtils.forward(0.017453292F);
            event.getLocation().setX(dir[0] * 0.2f);
            event.getLocation().setZ(dir[1] * 0.2f);
        }
    }
}
