package com.gamesense.client.module.modules.misc;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.init.Items;

public class FastPlace extends Module {
    public FastPlace() {super("FastPlace", Category.Misc);}

    Setting.b exp;
    Setting.b crystals;
    Setting.b everything;

    public void setup() {
        exp = registerB("Exp", "Exp", false);
        crystals = registerB("Crystals", "Crystals", false);
        everything = registerB("Eveything", "Everything",false);
    }

    public void onUpdate() {
        if (exp.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE || mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) {
            mc.rightClickDelayTimer = 0;
        }

        if (crystals.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            mc.rightClickDelayTimer = 0;
        }

        if (everything.getValue()) {
            mc.rightClickDelayTimer = 0;
        }

        mc.playerController.blockHitDelay = 0;
    }
}
