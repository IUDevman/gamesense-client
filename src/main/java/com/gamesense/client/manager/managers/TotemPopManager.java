package com.gamesense.client.manager.managers;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.TotemPopEvent;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.GameSense;
import com.gamesense.client.manager.Manager;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author Darki for popcounter
 * Originally made for PvPInfo
 * @src https://github.com/DarkiBoi/CliNet/blob/master/src/main/java/me/zeroeightsix/kami/module/modules/combat/TotemPopCounter.java
 * @author Hoosiers
 **/

public enum TotemPopManager implements Manager {

    INSTANCE;

    private final Minecraft mc = Minecraft.getMinecraft();
    private final HashMap<String, Integer> playerPopCount = new HashMap<>();
    @EventHandler
    private final Listener<PacketEvent.Receive> packetEventListener = new Listener<>(event -> {
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            Entity entity = packet.getEntity(mc.world);

            if (packet.getOpCode() == 35) {
                GameSense.EVENT_BUS.post(new TotemPopEvent(entity));
            }
        }
    });
    public boolean sendMsgs = false;
    public ChatFormatting chatFormatting = ChatFormatting.WHITE;
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> clientTickEventListener = new Listener<>(event -> {
        if (mc.player == null || mc.world == null) {
            playerPopCount.clear();
            return;
        }

        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.getHealth() <= 0 && playerPopCount.containsKey(entityPlayer.getName())) {
                if (sendMsgs) MessageBus.sendClientPrefixMessage(chatFormatting + entityPlayer.getName() + " died after popping " + ChatFormatting.GREEN + getPlayerPopCount(entityPlayer.getName()) + chatFormatting + " totems!");
                playerPopCount.remove(entityPlayer.getName());
            }
        }
    });
    @EventHandler
    private final Listener<TotemPopEvent> totemPopEventListener = new Listener<>(event -> {
        if (mc.player == null || mc.world == null) return;

        String entityName = event.getEntity().getName();

        if (playerPopCount.get(entityName) == null) {
            playerPopCount.put(entityName, 1);
            if(sendMsgs) MessageBus.sendClientPrefixMessage(chatFormatting + entityName + " popped " + ChatFormatting.RED + 1 + chatFormatting + " totem!");
        } else {
            int popCounter = playerPopCount.get(entityName) + 1;

            playerPopCount.put(entityName, popCounter);
            if(sendMsgs) MessageBus.sendClientPrefixMessage(chatFormatting + entityName + " popped " + ChatFormatting.RED + popCounter + chatFormatting + " totems!");
        }
    });

    public int getPlayerPopCount(String name) {
        if (playerPopCount.containsKey(name)) {
            return playerPopCount.get(name);
        }

        return 0;
    }
}