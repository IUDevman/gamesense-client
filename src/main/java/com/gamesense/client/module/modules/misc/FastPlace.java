package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.Category;
import net.minecraft.init.Items;

@Module.Declaration(name = "FastPlace", category = Category.Misc)
public class FastPlace extends Module {

	Setting.Boolean exp;
	Setting.Boolean crystals;
	Setting.Boolean offhandCrystal;
	Setting.Boolean everything;

	public void setup() {
		exp = registerBoolean("Exp", false);
		crystals = registerBoolean("Crystals", false);
		offhandCrystal = registerBoolean("Offhand Crystal", false);
		everything = registerBoolean("Everything",false);
	}

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
