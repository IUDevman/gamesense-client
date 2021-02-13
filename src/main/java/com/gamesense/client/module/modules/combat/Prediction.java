package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;

public class Prediction extends Module {
    public Prediction() {
        super("Prediction", Category.Combat);
    }

    Setting.Boolean assumeSprint;
    Setting.Boolean assumeJumping;
    Setting.Boolean dynamicTicks;
    Setting.Integer ticksAhead;

    @Override
    public void setup() {
        assumeSprint = registerBoolean("Sprinting", true);
        assumeJumping = registerBoolean("Jumping", true);
        ticksAhead = registerInteger("Predict Ticks", 10, 0, 80);
        dynamicTicks = registerBoolean("Dynamic", false);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onWorldRender(RenderEvent event) {
    }
}
