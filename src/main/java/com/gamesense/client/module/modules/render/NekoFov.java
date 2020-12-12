package com.gamesense.client.module.modules.render;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class NekoFov extends Module {
    public NekoFov() {
        super("Neko Fov", Category.Render);
    }

    Setting.Double itemfov;

    public void setup() {
        itemfov = registerDouble("ItemFov", "ItemFOV", 130, 110, 170);


    }

    @SubscribeEvent
    public void onFov(EntityViewRenderEvent.FOVModifier event) {
        event.setFOV((float) itemfov.getValue());
    }

    public void onEnable(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable(){
        MinecraftForge.EVENT_BUS.unregister(this);
    }

}