package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEntityHeadEvent;
import com.gamesense.api.event.events.RenderEntityReturnEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

/**
 * @author Techale
 * @author Hoosiers
 */

public class Chams extends Module {

    public Chams() {
        super("Chams", Category.Render);
    }

    Setting.Mode chamsType;
    Setting.ColorSetting chamsColor;
    Setting.Integer colorOpacity;
    Setting.Integer range;
    Setting.Boolean player;

    public void setup() {
        ArrayList<String> chamsTypes = new ArrayList<>();
        chamsTypes.add("Texture");
        chamsTypes.add("Color");

        chamsType = registerMode("Type", "Type", chamsTypes, "Texture");
        range = registerInteger("Range", "Range", 100, 10, 260);
        player = registerBoolean("Player", "Player", true);
        colorOpacity = registerInteger("Opacity", "Opacity", 155, 10, 255);
        chamsColor = registerColor("Color", "Color", new GSColor(0, 255, 255, 255));
    }

    @EventHandler
    private final Listener<RenderEntityHeadEvent> renderEntityHeadEventListener = new Listener<>(event -> {
        if (mc.player == null || mc.world == null) {
            return;
        }

        Entity entity1 = event.getEntity();

        if (entity1.getDistance(mc.player) > range.getValue()) {
            return;
        }

        if (player.getValue() && entity1 instanceof EntityPlayer && entity1 != mc.player) {
            switch (chamsType.getValue()) {
                case "Texture":
                    GameSenseTessellator.createChamsPre();
                    break;
                case "Color":
                    GameSenseTessellator.createColorPre(new GSColor(chamsColor.getValue(), colorOpacity.getValue()));
                    break;
            }
        }
    });

    @EventHandler
    private final Listener<RenderEntityReturnEvent> renderEntityReturnEventListener = new Listener<>(event -> {
        if (mc.player == null || mc.world == null) {
            return;
        }

        Entity entity1 = event.getEntity();

        if (entity1.getDistance(mc.player) > range.getValue()) {
            return;
        }

        if (player.getValue() && entity1 instanceof EntityPlayer && entity1 != mc.player) {
            switch (chamsType.getValue()) {
                case "Color":
                case "Texture":
                    GameSenseTessellator.createChamsPost();
                    break;
            }
        }
    });

    public void onEnable() {
        GameSense.EVENT_BUS.subscribe(this);
    }

    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe(this);
    }
}