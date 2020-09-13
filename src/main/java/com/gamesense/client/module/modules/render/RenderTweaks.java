package com.gamesense.client.module.modules.render;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemSword;

public class RenderTweaks extends Module {
    public Setting.Boolean viewClip;
    Setting.Boolean nekoAnimation;
    Setting.Boolean lowOffhand;
    Setting.Boolean fovChanger;
    Setting.Double lowOffhandSlider;
    Setting.Integer fovChangerSlider;
    ItemRenderer itemRenderer = mc.entityRenderer.itemRenderer;
    private float oldFOV;

    public RenderTweaks() {
        super("RenderTweaks", Category.Render);
    }

    public void setup() {
        viewClip = registerBoolean("View Clip", "ViewClip", false);
        nekoAnimation = registerBoolean("Neko Animation", "NekoAnimation", false);
        lowOffhand = registerBoolean("Low Offhand", "LowOffhand", false);
        lowOffhandSlider = registerDouble("Offhand Height", "OffhandHeight", 1.0, 0.1, 1.0);
        fovChanger = registerBoolean("FOV", "FOV", false);
        fovChangerSlider = registerInteger("FOV Slider", "FOVSlider", 90, 70, 200);
    }

    @Override
    public void onUpdate() {
        if (nekoAnimation.getValue()) {
            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
                mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
                mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
            }
        }
        if (lowOffhand.getValue()) {
            itemRenderer.equippedProgressOffHand = (float) lowOffhandSlider.getValue();
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