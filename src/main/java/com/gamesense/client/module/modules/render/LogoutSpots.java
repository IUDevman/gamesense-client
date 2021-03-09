package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.PlayerJoinEvent;
import com.gamesense.api.event.events.PlayerLeaveEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.WorldEvent;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rewrote by Hoosiers on 10/30/20
 */

@Module.Declaration(name = "LogoutSpots", category = Category.Render)
public class LogoutSpots extends Module {

    IntegerSetting range = registerInteger("Range", 100, 10, 260);
    BooleanSetting chatMsg = registerBoolean("Chat Msgs", true);
    BooleanSetting nameTag = registerBoolean("Nametag", true);
    IntegerSetting lineWidth = registerInteger("Width", 1, 1, 10);
    ModeSetting renderMode = registerMode("Render", Arrays.asList("Both", "Outline", "Fill"), "Both");
    ColorSetting color = registerColor("Color", new GSColor(255, 0, 0, 255));

    Map<net.minecraft.entity.Entity, String> loggedPlayers = new ConcurrentHashMap<>();
    Set<EntityPlayer> worldPlayers = ConcurrentHashMap.newKeySet();
    Timer timer = new Timer();

    public void onUpdate() {
        mc.world.playerEntities.stream()
                .filter(entityPlayer -> entityPlayer != mc.player)
                .filter(entityPlayer -> entityPlayer.getDistance(mc.player) <= range.getValue())
                .forEach(entityPlayer -> worldPlayers.add(entityPlayer));
    }

    public void onWorldRender(RenderEvent event) {
        if (mc.player != null && mc.world != null) {
            loggedPlayers.forEach(this::startFunction);
        }
    }

    public void onEnable() {
        loggedPlayers.clear();
        worldPlayers = ConcurrentHashMap.newKeySet();
    }

    public void onDisable() {
        worldPlayers.clear();
    }

    private void startFunction(Entity entity, String string) {
        if (entity.getDistance(mc.player) > range.getValue()) {
            return;
        }

        int posX = (int) entity.posX;
        int posY = (int) entity.posY;
        int posZ = (int) entity.posZ;

        String[] nameTagMessage = new String[2];
        nameTagMessage[0] = entity.getName() + " (" + string + ")";
        nameTagMessage[1] = "(" + posX + "," + posY + "," + posZ + ")";

        GlStateManager.pushMatrix();

        if (nameTag.getValue()) {
            RenderUtil.drawNametag(entity, nameTagMessage, color.getValue(), 0);
        }

        switch (renderMode.getValue()) {
            case "Both": {
                RenderUtil.drawBoundingBox(entity.getRenderBoundingBox(), lineWidth.getValue(), color.getValue());
                RenderUtil.drawBox(entity.getRenderBoundingBox(), true, -0.4, new GSColor(color.getValue(), 50), GeometryMasks.Quad.ALL);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(entity.getRenderBoundingBox(), lineWidth.getValue(), color.getValue());
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(entity.getRenderBoundingBox(), true, -0.4, new GSColor(color.getValue(), 50), GeometryMasks.Quad.ALL);
                break;
            }
        }
        GlStateManager.popMatrix();
    }

    /**
     * event handlers below:
     **/

    @EventHandler
    private final Listener<PlayerJoinEvent> playerJoinEventListener = new Listener<>(event -> {
        if (mc.world != null) {
            loggedPlayers.keySet().removeIf((entity) -> {
                if (entity.getName().equalsIgnoreCase(event.getName())) {
                    if (chatMsg.getValue()) {
                        MessageBus.sendClientPrefixMessage(event.getName() + " reconnected!");
                    }
                    return true;
                }
                return false;
            });
        }
    });

    @EventHandler
    private final Listener<PlayerLeaveEvent> playerLeaveEventListener = new Listener<>(event -> {
        if (mc.world != null) {
            worldPlayers.removeIf(entity -> {
                if (entity.getName().equalsIgnoreCase(event.getName())) {
                    String date = new SimpleDateFormat("k:mm").format(new Date());
                    loggedPlayers.put(entity, date);

                    if (chatMsg.getValue() && timer.getTimePassed() / 50L >= 5) {
                        String location = "(" + (int) entity.posX + "," + (int) entity.posY + "," + (int) entity.posZ + ")";
                        MessageBus.sendClientPrefixMessage(event.getName() + " disconnected at " + location + "!");
                        timer.reset();
                    }
                    return true;
                }
                return false;
            });
        }
    });

    @EventHandler
    private final Listener<WorldEvent.Unload> unloadListener = new Listener<>(event -> {
        worldPlayers.clear();
        if (mc.player == null || mc.world == null) {
            loggedPlayers.clear();
        }
    });

    @EventHandler
    private final Listener<WorldEvent.Load> loadListener = new Listener<>(event -> {
        worldPlayers.clear();
        if (mc.player == null || mc.world == null) {
            loggedPlayers.clear();
        }
    });
}