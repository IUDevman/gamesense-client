package com.gamesense.api.util.world.combat.ac.threads;

import com.gamesense.api.util.misc.Pair;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.api.util.world.combat.ac.ACHelper;
import com.gamesense.api.util.world.combat.ac.ACSettings;
import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import com.gamesense.client.GameSense;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ACCalculate implements Callable<Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>>> {

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
    public Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>> call() {
        return getPlayers(startThreads());
    }

    @Nonnull
    private List<Future<Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo>>> startThreads() {
        List<Future<Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo>>> output = new ArrayList<>();

        for (PlayerInfo target : targets) {
            output.add(ACHelper.executor.submit(new ACSubThread(settings, blocks, crystals, target)));
        }

        return output;
    }

    private Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>> getPlayers(List<Future<Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo>>> input) {
        List<CrystalInfo.PlaceInfo> place = new ArrayList<>();
        List<CrystalInfo.BreakInfo> breaks = new ArrayList<>();
        for (Future<Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo>> future : input) {
            while (!future.isDone() && !future.isCancelled()) {
                if (System.currentTimeMillis() > globalTimeoutTime) {
                    break;
                }
            }
            if (future.isDone()) {
                Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo> crystal = null;
                try {
                    crystal = future.get();
                } catch (InterruptedException | ExecutionException ignored) {
                }
                if (crystal != null) {
                    CrystalInfo.BreakInfo breakInfo = crystal.getKey();
                    CrystalInfo.PlaceInfo placeInfo = crystal.getValue();
                    if (breakInfo != null) {
                        breaks.add(breakInfo);
                    }
                    if (placeInfo != null) {
                        place.add(placeInfo);
                    }
                }
            } else {
                future.cancel(true);
            }
        }

        if (settings.crystalPriority.equalsIgnoreCase("Health")) {
            breaks.sort(Comparator.comparingDouble((i) -> i.target.health));
            place.sort(Comparator.comparingDouble((i) -> i.target.health));
        } else if (settings.crystalPriority.equalsIgnoreCase("Closest")) {
            breaks.sort(Comparator.comparingDouble((i) -> settings.player.entity.getDistanceSq(i.target.entity)));
            place.sort(Comparator.comparingDouble((i) -> settings.player.entity.getDistanceSq(i.target.entity)));
        } else {
            breaks.sort(Comparator.comparingDouble((i) -> i.damage));
            place.sort(Comparator.comparingDouble((i) -> i.damage));
        }

        return new Pair<>(breaks, place);
    }
}
