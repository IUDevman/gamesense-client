package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.Category;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Declaration(name = "SkyColor", category = Category.Render)
public class SkyColor extends Module {

    ColorSetting color;
    BooleanSetting fog;

    public void setup() {
        fog = registerBoolean("Fog", true);
        color = registerColor("Color", new GSColor(0, 255, 0, 255));
    }

    @EventHandler
    private final Listener<EntityViewRenderEvent.FogColors> fogColorsListener = new Listener<>(event -> {
       event.setRed(color.getValue().getRed() / 255.0F);
       event.setGreen(color.getValue().getGreen() / 255.0F);
       event.setBlue(color.getValue().getBlue() / 255.0F);
    });

    @EventHandler
    private final Listener<EntityViewRenderEvent.FogDensity> fogDensityListener = new Listener<>(event -> {
       if (!fog.getValue()) {
           event.setDensity(0);
           event.setCanceled(true);
       }
    });
}