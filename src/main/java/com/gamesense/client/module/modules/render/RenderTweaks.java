package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemSword;

@Module.Declaration(name = "RenderTweaks", category = Category.Render)
public class RenderTweaks extends Module {

    public BooleanSetting viewClip = registerBoolean("View Clip", false);
    BooleanSetting nekoAnimation = registerBoolean("Neko Animation", false);
    BooleanSetting lowOffhand = registerBoolean("Low Offhand", false);
    DoubleSetting lowOffhandSlider = registerDouble("Offhand Height", 1.0, 0.1, 1.0);
    BooleanSetting fovChanger = registerBoolean("FOV", false);
    IntegerSetting fovChangerSlider = registerInteger("FOV Slider", 90, 70, 200);

    ItemRenderer itemRenderer = mc.entityRenderer.itemRenderer;

    private float oldFOV;

    public void onUpdate() {
        if (nekoAnimation.getValue()) {
            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
                mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
                mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
            }
        }
        if (lowOffhand.getValue()) {
            itemRenderer.equippedProgressOffHand = lowOffhandSlider.getValue().floatValue();
        }
        if (fovChanger.getValue()) {
            mc.gameSettings.fovSetting = (float) fovChangerSlider.getValue();
        }
        if (!(fovChanger.getValue())) {
            mc.gameSettings.fovSetting = oldFOV;
        }
    }

    public void onEnable() {
        oldFOV = mc.gameSettings.fovSetting;
    }

    public void onDisable() {
        mc.gameSettings.fovSetting = oldFOV;
    }
}