package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEntityEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntitySquid;
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
    Setting.ColorSetting playerColor;
    Setting.ColorSetting mobColor;
    Setting.ColorSetting crystalColor;
    Setting.Integer colorOpacity;
    Setting.Integer wireOpacity;
    Setting.Integer lineWidth;
    Setting.Integer range;
    Setting.Boolean player;
    Setting.Boolean mob;
    Setting.Boolean crystal;

    public void setup() {
        ArrayList<String> chamsTypes = new ArrayList<>();
        chamsTypes.add("Texture");
        chamsTypes.add("Color");
        chamsTypes.add("WireFrame");

        chamsType = registerMode("Type", chamsTypes, "Texture");
        range = registerInteger("Range", 100, 10, 260);
        player = registerBoolean("Player", true);
        mob = registerBoolean("Mob", false);
        crystal = registerBoolean("Crystal", false);
        lineWidth = registerInteger("Line Width", 1, 1, 5);
        colorOpacity = registerInteger("Color Opacity", 100, 0, 255);
        wireOpacity = registerInteger("Wire Opacity", 200, 0, 255);
        playerColor = registerColor("Player Color", new GSColor(0, 255, 255, 255));
        mobColor = registerColor("Mob Color", new GSColor(255, 255, 0, 255));
        crystalColor = registerColor("Crystal Color", new GSColor(0, 255, 0, 255));
    }

    @EventHandler
    private final Listener<RenderEntityEvent.Head> renderEntityHeadEventListener = new Listener<>(event -> {
        if (event.getType() == RenderEntityEvent.Type.COLOR && chamsType.getValue().equalsIgnoreCase("Texture")) {
            return;
        }
        else if (event.getType() == RenderEntityEvent.Type.TEXTURE && (chamsType.getValue().equalsIgnoreCase("Color") || chamsType.getValue().equalsIgnoreCase("WireFrame"))) {
            return;
        }

        if (mc.player == null || mc.world == null) {
            return;
        }

        Entity entity1 = event.getEntity();

        if (entity1.getDistance(mc.player) > range.getValue()) {
            return;
        }

        if (player.getValue() && entity1 instanceof EntityPlayer && entity1 != mc.player) {
            renderChamsPre(new GSColor(playerColor.getValue(), 255), true);
        }

        if (mob.getValue() && (entity1 instanceof EntityCreature || entity1 instanceof EntitySlime || entity1 instanceof EntitySquid)) {
            renderChamsPre(new GSColor(mobColor.getValue(), 255), false);
        }

        if (crystal.getValue() && entity1 instanceof EntityEnderCrystal) {
            renderChamsPre(new GSColor(crystalColor.getValue(), 255), false);
        }
    });

    @EventHandler
    private final Listener<RenderEntityEvent.Return> renderEntityReturnEventListener = new Listener<>(event -> {
        if (event.getType() == RenderEntityEvent.Type.COLOR && chamsType.getValue().equalsIgnoreCase("Texture")) {
            return;
        }
        else if (event.getType() == RenderEntityEvent.Type.TEXTURE && (chamsType.getValue().equalsIgnoreCase("Color") || chamsType.getValue().equalsIgnoreCase("WireFrame"))) {
            return;
        }

        if (mc.player == null || mc.world == null) {
            return;
        }

        Entity entity1 = event.getEntity();

        if (entity1.getDistance(mc.player) > range.getValue()) {
            return;
        }

        if (player.getValue() && entity1 instanceof EntityPlayer && entity1 != mc.player) {
            renderChamsPost(true);
        }

        if (mob.getValue() && (entity1 instanceof EntityCreature || entity1 instanceof EntitySlime || entity1 instanceof EntitySquid)) {
            renderChamsPost(false);
        }

        if (crystal.getValue() && entity1 instanceof EntityEnderCrystal) {
            renderChamsPost(false);
        }
    });

    private void renderChamsPre(GSColor color, boolean isPlayer) {
        switch (chamsType.getValue()) {
            case "Texture": {
                RenderUtil.createChamsPre();
                break;
            }
            case "Color": {
                RenderUtil.createColorPre(new GSColor(color, colorOpacity.getValue()), isPlayer);
                break;
            }
            case "WireFrame": {
                RenderUtil.createWirePre(new GSColor(color, wireOpacity.getValue()), lineWidth.getValue(), isPlayer);
                break;
            }
        }
    }

    private void renderChamsPost(boolean isPlayer) {
        switch (chamsType.getValue()) {
            case "Texture": {
                RenderUtil.createChamsPost();
                break;
            }
            case "Color": {
                RenderUtil.createColorPost(isPlayer);
                break;
            }
            case "WireFrame": {
                RenderUtil.createWirePost(isPlayer);
                break;
            }
        }
    }

    public void onEnable() {
        GameSense.EVENT_BUS.subscribe(this);
    }

    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe(this);
    }
}