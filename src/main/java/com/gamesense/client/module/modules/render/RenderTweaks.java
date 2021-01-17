package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemSword;

public class RenderTweaks extends Module {

	public RenderTweaks() {
		super("RenderTweaks", Category.Render);
	}

	public Setting.Boolean viewClip;
	Setting.Boolean nekoAnimation;
	Setting.Boolean lowOffhand;
	Setting.Boolean fovChanger;
	Setting.Double lowOffhandSlider;
	Setting.Integer fovChangerSlider;

	ItemRenderer itemRenderer = mc.entityRenderer.itemRenderer;

	private float oldFOV;

	public void setup() {
		viewClip = registerBoolean("View Clip", false);
		nekoAnimation = registerBoolean("Neko Animation", false);
		lowOffhand = registerBoolean("Low Offhand", false);
		lowOffhandSlider = registerDouble("Offhand Height", 1.0, 0.1, 1.0);
		fovChanger = registerBoolean("FOV", false);
		fovChangerSlider = registerInteger("FOV Slider", 90, 70, 200);
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
			itemRenderer.equippedProgressOffHand = (float)lowOffhandSlider.getValue();
		}
		if (fovChanger.getValue()) {
			mc.gameSettings.fovSetting = (float)fovChangerSlider.getValue();
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