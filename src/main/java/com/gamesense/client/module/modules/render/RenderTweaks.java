package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.Category;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemSword;

@Module.Declaration(name = "RenderTweaks", category = Category.Render)
public class RenderTweaks extends Module {

    public BooleanSetting viewClip;
    BooleanSetting nekoAnimation;
    BooleanSetting lowOffhand;
    BooleanSetting fovChanger;
    DoubleSetting lowOffhandSlider;
    IntegerSetting fovChangerSlider;

    public void setup() {
        viewClip = registerBoolean("View Clip", false);
        nekoAnimation = registerBoolean("Neko Animation", false);
        lowOffhand = registerBoolean("Low Offhand", false);
        lowOffhandSlider = registerDouble("Offhand Height", 1.0, 0.1, 1.0);
        fovChanger = registerBoolean("FOV", false);
        fovChangerSlider = registerInteger("FOV Slider", 90, 70, 200);
    }

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