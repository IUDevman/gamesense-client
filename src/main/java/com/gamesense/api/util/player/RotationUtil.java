package com.gamesense.api.util.player;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.client.GameSense;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

public class RotationUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final RotationUtil ROTATION_UTIL = new RotationUtil();

    private int rotationConnections = 0;

    private boolean shouldSpoofAngles;
    private boolean isSpoofingAngles;
    private double yaw;
    private double pitch;

    // Forces only ever one
    private RotationUtil() {
    }

    public void onEnable() {
        rotationConnections++;
        if (rotationConnections == 1)
            GameSense.EVENT_BUS.subscribe(this);
    }

    public void onDisable() {
        rotationConnections--;
        if (rotationConnections == 0)
            GameSense.EVENT_BUS.unsubscribe(this);
    }

    public void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = EntityUtil.calculateLookAt(px, py, pz, me);
        this.setYawAndPitch((float) v[0], (float) v[1]);
    }

    public void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    public void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    public void shouldSpoofAngles(boolean e) {
        shouldSpoofAngles = e;
    }

    public boolean isSpoofingAngles() {
        return isSpoofingAngles;
    }

    @EventHandler
    private final Listener<PacketEvent.Send> packetSendListener = new Listener<>(event -> {
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer && shouldSpoofAngles) {
            if (isSpoofingAngles) {
                ((CPacketPlayer) packet).yaw = (float) yaw;
                ((CPacketPlayer) packet).pitch = (float) pitch;
            }
        }
    });
}