package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.Category;

@Module.Declaration(name = "ReverseStep", category = Category.Movement)
public class ReverseStep extends Module {

    DoubleSetting height;

    public void setup() {
        height = registerDouble("Height", 2.5, 0.5, 2.5);
    }

    public void onUpdate() {
        if (mc.world == null || mc.player == null || mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder()
                || mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }

        if (ModuleManager.isModuleEnabled(Speed.class))
            return;

        if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder()) {
            for (double y = 0.0; y < this.height.getValue() + 0.5; y += 0.01) {
                if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                    mc.player.motionY = -10.0;
                    break;
                }
            }
        }
    }
}