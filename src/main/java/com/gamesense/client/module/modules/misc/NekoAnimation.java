package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.Module;
import net.minecraft.item.ItemSword;

public class NekoAnimation extends Module {
    public NekoAnimation() {super("NekoAnimation", Category.Misc);}

    @Override
    public void onUpdate() {
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }
    }
}
