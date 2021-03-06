package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.client.entity.EntityPlayerSP;

@Module.Declaration(name = "Sprint", category = Category.Movement)
public class Sprint extends Module {

    private BooleanSetting multiDirection = registerBoolean("Multi Direction", true);

    public void onUpdate() {
        EntityPlayerSP player = mc.player;

        if (player != null) {
            player.setSprinting(shouldSprint(player));
        }
    }

    public boolean shouldSprint(EntityPlayerSP player) {
        return !mc.gameSettings.keyBindSneak.isKeyDown()
                && player.getFoodStats().getFoodLevel() > 6
                && !player.isElytraFlying()
                && !mc.player.capabilities.isFlying
                && checkMovementInput(player);
    }

    private boolean checkMovementInput(EntityPlayerSP player) {
        return multiDirection.getValue() ? (player.moveForward != 0.0f || player.moveStrafing != 0.0f) : player.moveForward > 0.0f;
    }
}