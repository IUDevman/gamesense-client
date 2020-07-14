package com.gamesense.client.module.modules.misc;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.item.ItemPickaxe;

public class NoEntityTrace extends Module {
    public NoEntityTrace() {
        super("NoEntityTrace", Category.Misc);
    }

    Setting.b pickaxeOnly;

    public void setup() {
        pickaxeOnly = registerB("Pickaxe Only", false);
    }

    boolean isHoldingPickaxe = false;

    public void onUpdate(){
        isHoldingPickaxe = mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe;
    }

    public boolean noTrace(){
        if(pickaxeOnly.getValBoolean()) return isEnabled() && isHoldingPickaxe;
        return isEnabled();
    }
}

