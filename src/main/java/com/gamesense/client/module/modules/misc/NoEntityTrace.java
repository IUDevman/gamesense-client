package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.item.ItemPickaxe;

public class NoEntityTrace extends Module {

	public NoEntityTrace() {
		super("NoEntityTrace", Category.Misc);
	}

	Setting.Boolean pickaxeOnly;

	public void setup() {
		pickaxeOnly = registerBoolean("Pickaxe Only", true);
	}

	boolean isHoldingPickaxe = false;

	public void onUpdate() {
		isHoldingPickaxe = mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe;
	}

	public boolean noTrace() {
		if (pickaxeOnly.getValue()) return isEnabled() && isHoldingPickaxe;
		return isEnabled();
	}
}