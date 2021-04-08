package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.init.Items;

@Module.Declaration(name = "FastPlace", category = Category.Misc)
public class FastPlace extends Module {

    BooleanSetting exp = registerBoolean("Exp", false);
    BooleanSetting crystals = registerBoolean("Crystals", false);
    BooleanSetting offhandCrystal = registerBoolean("Offhand Crystal", false);
    BooleanSetting everything = registerBoolean("Everything", false);

    public void onUpdate() {
        if (exp.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE || mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) {
            mc.rightClickDelayTimer = 0;
        }

        if (crystals.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            mc.rightClickDelayTimer = 0;
        }

        if (offhandCrystal.getValue() && mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            mc.rightClickDelayTimer = 0;
        }

        if (everything.getValue()) {
            mc.rightClickDelayTimer = 0;
        }

        mc.playerController.blockHitDelay = 0;
    }
}
