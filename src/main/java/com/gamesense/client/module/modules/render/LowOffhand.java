package com.gamesense.client.module.modules.render;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.client.renderer.ItemRenderer;

public class LowOffhand extends Module {
    public LowOffhand() {
        super("LowOffhand", Category.Render);
    }
    Setting.d off;
    ItemRenderer itemRenderer = mc.entityRenderer.itemRenderer;

    public void setup(){
        off = registerD("Height", 1.0, 0.1 ,1.0);

    }

    public void onUpdate(){
        itemRenderer.equippedProgressOffHand = (float)off.getValue();
    }
}