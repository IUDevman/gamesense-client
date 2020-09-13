package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.WaterPushEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.client.event.InputUpdateEvent;
import org.lwjgl.input.Keyboard;

public class PlayerTweaks extends Module {
    public Setting.Boolean guiMove;
    public Setting.Boolean noPush;
    @EventHandler
    private final Listener<WaterPushEvent> waterPushEventListener = new Listener<>(event -> {
        if (noPush.getValue()) {
            event.cancel();
        }
    });
    public Setting.Boolean noSlow;
    //No Slow
    @EventHandler
    private final Listener<InputUpdateEvent> eventListener = new Listener<>(event -> {
        if (noSlow.getValue()) {
            if (mc.player.isHandActive() && !mc.player.isRiding()) {
                event.getMovementInput().moveStrafe *= 5;
                event.getMovementInput().moveForward *= 5;
            }
        }
    });
    Setting.Boolean antiKnockBack;
    //Velocity
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (antiKnockBack.getValue()) {
            if (event.getPacket() instanceof SPacketEntityVelocity) {
                if (((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId())
                    event.cancel();
            }
            if (event.getPacket() instanceof SPacketExplosion) {
                event.cancel();
            }
        }
    });

    public PlayerTweaks() {
        super("PlayerTweaks", Category.Movement);
    }

    public void setup() {
        guiMove = registerBoolean("Gui Move", "GuiMove", false);
        noPush = registerBoolean("No Push", "NoPush", false);
        noSlow = registerBoolean("No Slow", "NoSlow", false);
        antiKnockBack = registerBoolean("Velocity", "Velocity", false);
    }

    //Gui Move
    public void onUpdate() {
        if (guiMove.getValue()) {
            if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
                if (Keyboard.isKeyDown(200)) {
                    mc.player.rotationPitch -= 5;
                }
                if (Keyboard.isKeyDown(208)) {
                    mc.player.rotationPitch += 5;
                }
                if (Keyboard.isKeyDown(205)) {
                    mc.player.rotationYaw += 5;
                }
                if (Keyboard.isKeyDown(203)) {
                    mc.player.rotationYaw -= 5;
                }
                if (mc.player.rotationPitch > 90) mc.player.rotationPitch = 90;
                if (mc.player.rotationPitch < -90) mc.player.rotationPitch = -90;
            }
        }
    }

    public void onEnable() {
        GameSenseMod.EVENT_BUS.subscribe(this);
    }

    public void onDisable() {
        GameSenseMod.EVENT_BUS.unsubscribe(this);
    }
}