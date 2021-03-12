package com.gamesense.api.util.combat.ac;

import com.gamesense.api.util.combat.DamageUtil;
import com.gamesense.api.util.combat.ac.breaks.BreakThread;
import com.gamesense.api.util.combat.ac.place.PlaceThread;
import com.gamesense.api.util.misc.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ACCalculate implements Callable<Pair<HashMap<EntityPlayer, Float>, HashMap<EntityPlayer, Float>>> {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private final ACSettings settings;

    private final List<PlayerInfo> targets;
    private final List<EntityEnderCrystal> crystals;
    private final List<BlockPos> blocks;

    private final long globalTimeoutTime;

    public ACCalculate(ACSettings settings, List<PlayerInfo> targets, List<EntityEnderCrystal> crystals, List<BlockPos> blocks, long globalTimeoutTime) {
        this.settings = settings;

        this.targets = targets;
        this.crystals = crystals;
        this.blocks = blocks;

        this.globalTimeoutTime = globalTimeoutTime;
    }

    @Override
    public Pair<HashMap<EntityPlayer, Float>, HashMap<EntityPlayer, Float>> call() {
        List<Future<CrystalInfo.PlaceInfo>> placeFutures = null;
        List<Future<List<CrystalInfo.BreakInfo>>> breakFutures = null;
        if (settings.breakCrystals) {
            breakFutures = startBreakThreads(targets, crystals, settings);
        }
        if (settings.placeCrystals) {
            placeFutures = startPlaceThreads(targets, settings);
        }

        Pair<HashMap<EntityPlayer, Float>, HashMap<EntityPlayer, Float>> output = new Pair<>(null, null);

        if (breakFutures != null) {
            output.setKey(getBreakPlayers(breakFutures));
        }
        if (placeFutures != null) {
            output.setValue(getPlacePlayers(placeFutures));
        }
        return output;
    }

    private List<Future<List<CrystalInfo.BreakInfo>>> startBreakThreads(List<PlayerInfo> targets, List<EntityEnderCrystal> crystalList, ACSettings settings) {
        if (crystalList.size() == 0) {
            return null;
        }

        List<Future<List<CrystalInfo.BreakInfo>>> output = new ArrayList<>();
        // split targets equally between threads
        int targetsPerThread = (int) Math.ceil((double) targets.size()/ (double) settings.breakThreads);
        int threadsPerTarget = (int) Math.floor((double) settings.breakThreads/ (double) targets.size());

        List<List<EntityEnderCrystal>> splits = new ArrayList<>();
        int smallListSize = (int) Math.ceil((double) crystalList.size()/ (double) threadsPerTarget);

        int j = 0;
        for (int i = smallListSize; i < crystalList.size(); i += smallListSize) {
            splits.add(crystalList.subList(j, i + 1));
            j += smallListSize;
        }
        splits.add(crystalList.subList(j, crystalList.size()));

        j = 0;
        for (int i = targetsPerThread; i < targets.size(); i += targetsPerThread) {
            List<PlayerInfo> sublist = targets.subList(j, i + 1);
            for (List<EntityEnderCrystal> split : splits) {
                output.add(ACHelper.executor.submit(new BreakThread(settings, split, sublist)));
            }
            j += targetsPerThread;
        }
        List<PlayerInfo> sublist = targets.subList(j, targets.size());
        for (List<EntityEnderCrystal> split : splits) {
            output.add(ACHelper.executor.submit(new BreakThread(settings, split, sublist)));
        }

        return output;
    }

    private HashMap<EntityPlayer, Float> getBreakPlayers(List<Future<List<CrystalInfo.BreakInfo>>> input) {
        List<CrystalInfo.BreakInfo> crystals = new ArrayList<>();
        for (Future<List<CrystalInfo.BreakInfo>> future : input) {
            while (!future.isDone() && !future.isCancelled()) {
                if (System.currentTimeMillis() > globalTimeoutTime) {
                    break;
                }
            }
            if (future.isDone()) {
                try {
                    crystals.addAll(future.get());
                } catch (InterruptedException | ExecutionException ignored) {
                }
            } else {
                future.cancel(true);
            }
        }

        HashMap<EntityPlayer, Float> output = new HashMap<>();
        for (CrystalInfo.BreakInfo crystal : crystals) {
            output.computeIfPresent(crystal.target.entity, ((player, newDamage) -> newDamage < crystal.damage ? crystal.damage : newDamage));
            output.computeIfAbsent(crystal.target.entity, (player) -> crystal.damage);
        }
        return output;
    }

    private List<Future<CrystalInfo.PlaceInfo>> startPlaceThreads(List<PlayerInfo> targets, ACSettings settings) {
        // remove all placements that deal more than max self damage
        // no point in checking these
        final float playerHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        blocks.removeIf(crystal -> {
            float damage = DamageUtil.calculateDamage((double) crystal.getX() + 0.5d, (double) crystal.getY() + 1.0d, (double) crystal.getZ() + 0.5d, mc.player);
            if (damage > settings.maxSelfDamage) {
                return true;
            } else return settings.antiSuicide && damage > playerHealth;
        });
        if (blocks.size() == 0) {
            return null;
        }

        List<Future<CrystalInfo.PlaceInfo>> output = new ArrayList<>();

        for (PlayerInfo target : targets) {
            output.add(ACHelper.executor.submit(new PlaceThread(settings, blocks, target)));
        }

        return output;
    }

    private HashMap<EntityPlayer, Float> getPlacePlayers(List<Future<CrystalInfo.PlaceInfo>> input) {
        List<CrystalInfo.PlaceInfo> crystals = new ArrayList<>();
        for (Future<CrystalInfo.PlaceInfo> future : input) {
            while (!future.isDone() && !future.isCancelled()) {
                if (System.currentTimeMillis() > globalTimeoutTime) {
                    break;
                }
            }
            if (future.isDone()) {
                CrystalInfo.PlaceInfo crystal = null;
                try {
                    crystal = future.get();
                } catch (InterruptedException | ExecutionException ignored) {
                }
                if (crystal != null) {
                    crystals.add(crystal);
                }
            } else {
                future.cancel(true);
            }
        }

        HashMap<EntityPlayer, Float> output = new HashMap<>();
        for (CrystalInfo.PlaceInfo crystal : crystals) {
            output.computeIfPresent(crystal.target.entity, ((player, newDamage) -> newDamage < crystal.damage ? crystal.damage : newDamage));
            output.computeIfAbsent(crystal.target.entity, (player) -> crystal.damage);
        }
        return output;
    }
}
