package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.ColorUtil;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.manager.managers.TotemPopManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
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
    BooleanSetting weaknessDetect = registerBoolean("Weakness Detect", false);
    BooleanSetting popCounter = registerBoolean("Pop Counter", false);
    ModeSetting chatColor = registerMode("Color", ColorUtil.colors, "Light Purple");

    List<Entity> knownPlayers = new ArrayList<>();
    List<Entity> antiPearlList = new ArrayList<>();
    List<Entity> players;
    List<Entity> pearls;
    List<Entity> burrowedPlayers = new ArrayList<>();
    List<Entity> strengthPlayers = new ArrayList<>();
    List<Entity> weaknessPlayers = new ArrayList<>();

    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        TotemPopManager.INSTANCE.sendMsgs = popCounter.getValue();
        if (popCounter.getValue()) TotemPopManager.INSTANCE.chatFormatting = ColorUtil.textToChatFormatting(chatColor);

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
        if (strengthDetect.getValue() || weaknessDetect.getValue()) {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (player.isPotionActive(MobEffects.STRENGTH) && !(strengthPlayers.contains(player))) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + player.getName() + " has (drank) strength!");
                    strengthPlayers.add(player);
                }
                if (player.isPotionActive(MobEffects.WEAKNESS) && !(weaknessPlayers.contains(player))) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + player.getName() + " has (drank) wealness!");
                    weaknessPlayers.add(player);
                }
                if (!(player.isPotionActive(MobEffects.STRENGTH)) && strengthPlayers.contains(player)) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + player.getName() + " no longer has strength!");
                    strengthPlayers.remove(player);
                }
                if (!(player.isPotionActive(MobEffects.WEAKNESS)) && weaknessPlayers.contains(player)) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(chatColor) + player.getName() + " no longer has weakness!");
                    weaknessPlayers.remove(player);
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

    public void onDisable() {
        knownPlayers.clear();
        TotemPopManager.INSTANCE.sendMsgs = false;
    }
}