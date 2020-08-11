package com.gamesense.client.module.modules.render;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemSword;

public class RenderTweaks extends Module {
    public RenderTweaks(){
        super("RenderTweaks", Category.Render);
    }

    public Setting.b viewClip;
    Setting.b nekoAnimation;
    Setting.b lowOffhand;
    Setting.b fovChanger;
    Setting.d lowOffhandSlider;
    Setting.i fovChangerSlider;

    ItemRenderer itemRenderer = mc.entityRenderer.itemRenderer;

    private float oldFOV;

    public void setup(){
        viewClip = this.registerB("View Clip", "ViewClip", false);
        nekoAnimation = this.registerB("Neko Animation", "NekoAnimation", false);
        lowOffhand = this.registerB("Low Offhand", "LowOffhand", false);
        lowOffhandSlider = this.registerD("Offhand Height", "OffhandHeight", 1.0, 0.1, 1.0);
        fovChanger = this.registerB("FOV", "FOV", false);
        fovChangerSlider = this.registerI("FOV Slider", "FOVSlider", 90, 70, 200);
    }

    @Override
    public void onUpdate(){
        if (nekoAnimation.getValue()){
            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
                mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
                mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
            }
        }
        if (lowOffhand.getValue()){
            itemRenderer.equippedProgressOffHand = (float)lowOffhandSlider.getValue();
        }
        if (fovChanger.getValue()){
            mc.gameSettings.fovSetting = (float)fovChangerSlider.getValue();
        }
        if (!(fovChanger.getValue())){
            mc.gameSettings.fovSetting = oldFOV;
        }
    }

    public void onEnable(){
        oldFOV = mc.gameSettings.fovSetting;
    }

    public void onDisable(){
        mc.gameSettings.fovSetting = oldFOV;
    }
}