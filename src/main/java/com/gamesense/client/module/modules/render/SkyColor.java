package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.Category;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Declaration(name = "SkyColor", category = Category.Render)
public class SkyColor extends Module {

    public static ColorSetting color;
    public static BooleanSetting fog;

    public void setup() {
        fog = registerBoolean("Fog", true);
        color = registerColor("Color", new GSColor(0, 255, 0, 255));
    }

    @SubscribeEvent
    public void onFogColorRender(EntityViewRenderEvent.FogColors event) {
        GSColor color = SkyColor.color.getValue();
        event.setRed(color.getRed() / 255f);
        event.setGreen(color.getGreen() / 255f);
        event.setBlue(color.getBlue() / 255f);
    }

    @SubscribeEvent
    public void fog(EntityViewRenderEvent.FogDensity event) {
        if (!fog.getValue()) {
            event.setDensity(0);
            event.setCanceled(true);
        }
    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}