package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.TotemPopEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.*;

/**
* @author Darki
* @src https://github.com/DarkiBoi/CliNet/blob/master/src/main/java/me/zeroeightsix/kami/module/modules/combat/TotemPopCounter.java
**/

public class TotemPopCounter extends Module {
    public TotemPopCounter(){
        super("PopCounter", Category.Misc);
    }

    Setting.Mode chatColor;

    public void setup(){
        ArrayList<String> chatColors = new ArrayList<>();
        chatColors.add("Black");
        chatColors.add("Dark Green");
        chatColors.add("Dark Red");
        chatColors.add("Gold");
        chatColors.add("Dark Gray");
        chatColors.add("Green");
        chatColors.add("Red");
        chatColors.add("Yellow");
        chatColors.add("Dark Blue");
        chatColors.add("Dark Aqua");
        chatColors.add("Dark Purple");
        chatColors.add("Gray");
        chatColors.add("Blue");
        chatColors.add("Aqua");
        chatColors.add("Light Purple");
        chatColors.add("White");

        chatColor = registerMode("Color", "Color", chatColors, "Yellow");
    }

    private HashMap<String, Integer> popCounterHashMap = new HashMap<>();

    public void onEnable(){
        GameSenseMod.EVENT_BUS.subscribe(this);
        popCounterHashMap = new HashMap<>();
    }

    public void onDisable(){
        GameSenseMod.EVENT_BUS.unsubscribe(this);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> packetEventListener = new Listener<>(event -> {

        if (mc.world == null || mc.player == null){
            return;
        }
        if (event.getPacket() instanceof SPacketEntityStatus){
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35){
                Entity entity = packet.getEntity(mc.world);
                GameSenseMod.EVENT_BUS.post(new TotemPopEvent(entity));
            }
        }
    });

    @EventHandler
    private final Listener<TotemPopEvent> totemPopEventListener = new Listener<>(event -> {
        if (popCounterHashMap == null){
            popCounterHashMap = new HashMap<>();
        }

        if (popCounterHashMap.get(event.getEntity().getName()) == null){
            popCounterHashMap.put(event.getEntity().getName(), 1);
            Command.sendClientMessage(getTextColor() + event.getEntity().getName() + " popped " + ChatFormatting.RED + 1 + getTextColor() + " totem!");
        }
        else if (popCounterHashMap.get(event.getEntity().getName()) != null){
            int popCounter = popCounterHashMap.get(event.getEntity().getName());
            int newPopCounter = popCounter += 1;
            popCounterHashMap.put(event.getEntity().getName(), newPopCounter);
            Command.sendClientMessage(getTextColor() + event.getEntity().getName() + " popped " + ChatFormatting.RED + newPopCounter + getTextColor() + " totems!");
        }
    });

    public void onUpdate(){
        if (mc.world != null && mc.player != null){
            for (EntityPlayer player : mc.world.playerEntities) {
                if (player.getHealth() <= 0) {
                    if (popCounterHashMap.containsKey(player.getDisplayNameString())) {
                        Command.sendClientMessage(getTextColor() + player.getName() + " died after popping " + ChatFormatting.GREEN + popCounterHashMap.get(player.getName()) + getTextColor() + " totems!");
                        popCounterHashMap.remove(player.getName(), popCounterHashMap.get(player.getName()));
                    }
                }
            }
        }
    }

    public ChatFormatting getTextColor(){
        if (chatColor.getValue().equalsIgnoreCase("Black")){
            return ChatFormatting.BLACK;
        }
        if (chatColor.getValue().equalsIgnoreCase("Dark Green")){
            return ChatFormatting.DARK_GREEN;
        }
        if (chatColor.getValue().equalsIgnoreCase("Dark Red")){
            return ChatFormatting.DARK_RED;
        }
        if (chatColor.getValue().equalsIgnoreCase("Gold")){
            return ChatFormatting.GOLD;
        }
        if (chatColor.getValue().equalsIgnoreCase("Dark Gray")){
            return ChatFormatting.DARK_GRAY;
        }
        if (chatColor.getValue().equalsIgnoreCase("Green")){
            return ChatFormatting.GREEN;
        }
        if (chatColor.getValue().equalsIgnoreCase("Red")){
            return ChatFormatting.RED;
        }
        if (chatColor.getValue().equalsIgnoreCase("Yellow")){
            return ChatFormatting.YELLOW;
        }
        if (chatColor.getValue().equalsIgnoreCase("Dark Blue")){
            return ChatFormatting.DARK_BLUE;
        }
        if (chatColor.getValue().equalsIgnoreCase("Dark Aqua")){
            return ChatFormatting.DARK_AQUA;
        }
        if (chatColor.getValue().equalsIgnoreCase("Dark Purple")){
            return ChatFormatting.DARK_PURPLE;
        }
        if (chatColor.getValue().equalsIgnoreCase("Gray")){
            return ChatFormatting.GRAY;
        }
        if (chatColor.getValue().equalsIgnoreCase("Blue")){
            return ChatFormatting.BLUE;
        }
        if (chatColor.getValue().equalsIgnoreCase("Light Purple")){
            return ChatFormatting.LIGHT_PURPLE;
        }
        if (chatColor.getValue().equalsIgnoreCase("White")){
            return ChatFormatting.WHITE;
        }
        if (chatColor.getValue().equalsIgnoreCase("Aqua")){
            return ChatFormatting.AQUA;
        }
        return null;
    }
}
