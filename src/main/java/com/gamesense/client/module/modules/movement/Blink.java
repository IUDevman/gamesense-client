package com.gamesense.client.module.modules.movement;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Blink extends Module {
    public Blink() {
        super("Blink", Category.Movement);
    }

    Setting.Boolean ghostPlayer;

    public void setup(){
        ghostPlayer = registerBoolean("Ghost Player", "GhostPlayer", true);
    }

    EntityOtherPlayerMP entity;
    private final Queue<Packet> packets = new ConcurrentLinkedQueue();

    @EventHandler
    private Listener<PacketEvent.Send> packetSendListener = new Listener<>(event -> {
        Packet packet = event.getPacket();

        if (packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus) {
            return;
        }
        packets.add(packet);
        event.cancel();
    });

    public void onEnable() {
        GameSenseMod.EVENT_BUS.subscribe(this);

        if (ghostPlayer.getValue()) {
            entity = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            entity.copyLocationAndAnglesFrom(mc.player);
            entity.rotationYaw = mc.player.rotationYaw;
            entity.rotationYawHead = mc.player.rotationYawHead;
            mc.world.addEntityToWorld(667, entity);
        }
    }

    public void onUpdate(){
        if (!ghostPlayer.getValue() && entity != null){
            mc.world.removeEntity(entity);
        }
    }

    public void onDisable() {
        GameSenseMod.EVENT_BUS.unsubscribe(this);

        if (entity != null) {
            mc.world.removeEntity(entity);
        }

        if (packets.size() > 0) {
            for (Packet packet : packets) {
                mc.player.connection.sendPacket(packet);
            }
            packets.clear();
        }
    }

    public String getHudInfo(){
        String t = "[" + ChatFormatting.WHITE + packets.size() + ChatFormatting.GRAY + "]";

        return t;
    }
}
