package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.GameSense;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Blink extends Module {

    public Blink() {
        super("Blink", Category.Movement);
    }

    private Setting.Boolean ghostPlayer;

    public void setup() {
        ghostPlayer = registerBoolean("Ghost Player", true);
    }

    private EntityOtherPlayerMP entity;
    private final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();

    public void onEnable() {
        EntityPlayerSP player = mc.player;
        WorldClient world = mc.world;

        if (player == null || world == null) {
            disable();
        } else if (ghostPlayer.getValue()) {
            entity = new EntityOtherPlayerMP(world, mc.getSession().getProfile());
            entity.copyLocationAndAnglesFrom(player);
            entity.inventory.copyInventory(player.inventory);
            entity.rotationYaw = player.rotationYaw;
            entity.rotationYawHead = player.rotationYawHead;
            world.addEntityToWorld(667, entity);
        }
    }

    public void onUpdate() {
        Entity entity = this.entity;
        WorldClient world = mc.world;

        if (!ghostPlayer.getValue() && entity != null && world != null) {
            world.removeEntity(entity);
        }
    }

    public void onDisable() {
        Entity entity = this.entity;
        WorldClient world = mc.world;

        if (entity != null && world != null) {
            world.removeEntity(entity);
        }

        EntityPlayerSP player = mc.player;

        if (packets.size() > 0 && player != null) {
            for (Packet<?> packet : packets) {
                player.connection.sendPacket(packet);
            }
            packets.clear();
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<PacketEvent.Send> packetSendListener = new Listener<>(event -> {
        Packet<?> packet = event.getPacket();
        EntityPlayerSP player = mc.player;

        if (player != null && player.isEntityAlive() && packet instanceof CPacketPlayer) {
            packets.add(packet);
            event.cancel();
        }
    });

    public String getHudInfo() {
        return "[" + ChatFormatting.WHITE + packets.size() + ChatFormatting.GRAY + "]";
    }
}