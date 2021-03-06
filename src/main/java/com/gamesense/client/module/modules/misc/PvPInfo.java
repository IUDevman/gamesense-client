package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.TotemPopEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.ColorUtil;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Darki for popcounter
 * @src https://github.com/DarkiBoi/CliNet/blob/master/src/main/java/me/zeroeightsix/kami/module/modules/combat/TotemPopCounter.java
 **/

@Module.Declaration(name = "PvPInfo", category = Category.Misc)
public class PvPInfo extends Module {

    BooleanSetting visualRange = registerBoolean("Visual Range", false);
    BooleanSetting pearlAlert = registerBoolean("Pearl Alert", false);
    BooleanSetting burrowAlert = registerBoolean("Burrow Alert", false);
    BooleanSetting strengthDetect = registerBoolean("Strength Detect", false);
    BooleanSetting popCounter = registerBoolean("Pop Counter", false);
    ModeSetting chatColor = registerMode("Color", ColorUtil.colors, "Light Purple");

    List<Entity> knownPlayers = new ArrayList<>();
    List<Entity> antiPearlList = new ArrayList<>();
    List<Entity> players;
    List<Entity> pearls;
    List<Entity> burrowedPlayers = new ArrayList<>();
    List<Entity> strengthPlayers = new ArrayList<>();
    private HashMap<String, Integer> popCounterHashMap = new HashMap<>();

    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (visualRange.getValue()) {
            players = mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList());
            try {
                for (Entity e : players) {
                    if (e instanceof EntityPlayer && !e.getName().equalsIgnoreCase(mc.player.getName())) {
                        if (!knownPlayers.contains(e)) {
                            knownPlayers.add(e);
                            MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + e.getName() + " has been spotted thanks to GameSense!");
                        }
                    }
                }
            } catch (Exception e) {
            }
            try {
                for (Entity e : knownPlayers) {
                    if (e instanceof EntityPlayer && !e.getName().equalsIgnoreCase(mc.player.getName())) {
                        if (!players.contains(e)) {
                            knownPlayers.remove(e);
                        }
                    }
                }
            } catch (Exception e) {
            }
        }

        if (burrowAlert.getValue()) {
            for (Entity entity : mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList())) {
                if (!(entity instanceof EntityPlayer)) {
                    continue;
                }

                if (!burrowedPlayers.contains(entity) && isBurrowed(entity)) {
                    burrowedPlayers.add(entity);
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + entity.getName() + " has just burrowed!");
                } else if (burrowedPlayers.contains(entity) && !isBurrowed(entity)) {
                    burrowedPlayers.remove(entity);
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + entity.getName() + " is no longer burrowed!");
                }
            }
        }

        if (pearlAlert.getValue()) {
            pearls = mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderPearl).collect(Collectors.toList());
            try {
                for (Entity e : pearls) {
                    if (e instanceof EntityEnderPearl) {
                        if (!antiPearlList.contains(e)) {
                            antiPearlList.add(e);
                            MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + e.getEntityWorld().getClosestPlayerToEntity(e, 3).getName() + " has just thrown a pearl!");
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        if (strengthDetect.getValue()) {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (player.isPotionActive(MobEffects.STRENGTH) && !(strengthPlayers.contains(player))) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + player.getName() + " has (drank) strength!");
                    strengthPlayers.add(player);
                }
                if (!(player.isPotionActive(MobEffects.STRENGTH)) && strengthPlayers.contains(player)) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + player.getName() + " no longer has strength!");
                    strengthPlayers.remove(player);
                }
            }
        }
        if (popCounter.getValue()) {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (player.getHealth() <= 0) {
                    if (popCounterHashMap.containsKey(player.getDisplayNameString())) {
                        MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + player.getName() + " died after popping " + ChatFormatting.GREEN + popCounterHashMap.get(player.getName()) + ColorUtil.textToChatFormatting(chatColor) + " totems!");
                        popCounterHashMap.remove(player.getName(), popCounterHashMap.get(player.getName()));
                    }
                }
            }
        }
    }

    private boolean isBurrowed(Entity entity) {
        BlockPos entityPos = new BlockPos(roundValueToCenter(entity.posX), entity.posY + .2, roundValueToCenter(entity.posZ));

        if (mc.world.getBlockState(entityPos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(entityPos).getBlock() == Blocks.ENDER_CHEST) {
            return true;
        }

        return false;
    }

    private double roundValueToCenter(double inputVal) {
        double roundVal = Math.round(inputVal);

        if (roundVal > inputVal) {
            roundVal -= 0.5;
        } else if (roundVal <= inputVal) {
            roundVal += 0.5;
        }

        return roundVal;
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> packetEventListener = new Listener<>(event -> {
        if (mc.world == null || mc.player == null) {
            return;
        }

        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35) {
                Entity entity = packet.getEntity(mc.world);
                GameSense.EVENT_BUS.post(new TotemPopEvent(entity));
            }
        }
    });

    @EventHandler
    private final Listener<TotemPopEvent> totemPopEventListener = new Listener<>(event -> {
        if (mc.world == null || mc.player == null) {
            return;
        }

        if (popCounter.getValue()) {
            if (popCounterHashMap == null) {
                popCounterHashMap = new HashMap<>();
            }

            if (popCounterHashMap.get(event.getEntity().getName()) == null) {
                popCounterHashMap.put(event.getEntity().getName(), 1);
                MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + event.getEntity().getName() + " popped " + ChatFormatting.RED + 1 + ColorUtil.textToChatFormatting(chatColor) + " totem!");
            } else if (popCounterHashMap.get(event.getEntity().getName()) != null) {
                int popCounter = popCounterHashMap.get(event.getEntity().getName());
                int newPopCounter = popCounter += 1;
                popCounterHashMap.put(event.getEntity().getName(), newPopCounter);
                MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + event.getEntity().getName() + " popped " + ChatFormatting.RED + newPopCounter + ColorUtil.textToChatFormatting(chatColor) + " totems!");
            }
        }
    });

    public void onEnable() {
        popCounterHashMap = new HashMap<>();
    }

    public void onDisable() {
        knownPlayers.clear();
    }
}