package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.client.module.Module;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

/**
 * @author Techale, ported to its own module by Hoosiers
 */

public class Chams extends Module {

    public Chams() {
        super("Chams", Category.Render);
    }

    Setting.Mode chamsType;
    Setting.ColorSetting chamsColor;

    public void setup() {
        ArrayList<String> chamsTypes = new ArrayList<>();
        chamsTypes.add("Texture");
        chamsTypes.add("Color");

        chamsType = registerMode("Type", "Type", chamsTypes, "Texture");
        chamsColor = registerColor("Color", "Color", new GSColor(0, 255, 255, 255));
    }

    GSColor color = new GSColor(chamsColor.getValue(), 255);

    @SubscribeEvent
    public void onRenderLayers(RenderPlayerEvent.Pre e) {
        switch (chamsType.getValue()) {
            case "Texture":
                GameSenseTessellator.createChamsPre();
                break;
            case "Color":
                GameSenseTessellator.createColorPre(chamsColor.getColor());
                break;
        }
    }

    @SubscribeEvent
    public void onRenderLayers1(RenderPlayerEvent.Post e) {
        switch (chamsType.getValue()) {
            case "Color":
            case "Texture":
                GameSenseTessellator.createChamsPost();
                break;
        }
    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}