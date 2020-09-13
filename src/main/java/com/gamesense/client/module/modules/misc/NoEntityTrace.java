package com.gamesense.client.module.modules.misc;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.item.ItemPickaxe;

public class NoEntityTrace extends Module {
    Setting.Boolean pickaxeOnly;
    boolean isHoldingPickaxe = false;

    public NoEntityTrace() {
        super("NoEntityTrace", Category.Misc);
    }

    public void setup() {
        pickaxeOnly = registerBoolean("Pickaxe Only", "PickaxeOnly", true);
    }

    public void onUpdate() {
        isHoldingPickaxe = mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe;
    }

    public boolean noTrace() {
        if (pickaxeOnly.getValue()) return isEnabled() && isHoldingPickaxe;
        return isEnabled();
    }
}

