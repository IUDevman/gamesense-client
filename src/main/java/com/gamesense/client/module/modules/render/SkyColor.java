package com.gamesense.client.module.modules.render;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkyColor extends Module {
    public SkyColor() {
        super("SkyColor", Category.Render);
    }

    Setting.Integer red;
    Setting.Integer green;
    Setting.Integer blue;

    @Override
    public void setup(){
        red = registerInteger("Red", "Red", 255, 0, 255);
        green = registerInteger("Green", "Green", 255, 0, 255);
        blue = registerInteger("Blue", "Blue", 255, 0, 255);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onFogColorRender(EntityViewRenderEvent.FogColors event) {
        event.setRed(red.getValue() / 255f);
        event.setGreen(green.getValue() / 255f);
        event.setBlue(blue.getValue() / 255f);

    }

    @SubscribeEvent
    public void fog(EntityViewRenderEvent.FogDensity event) {
        event.setDensity(0);
        event.setCanceled(true);
    }
}
