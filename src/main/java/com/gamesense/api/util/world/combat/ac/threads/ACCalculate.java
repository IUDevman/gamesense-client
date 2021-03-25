package com.gamesense.api.util.world.combat.ac.threads;

import com.gamesense.api.util.world.combat.ac.ACHelper;
import com.gamesense.api.util.world.combat.ac.ACSettings;
import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ACCalculate implements Callable<List<CrystalInfo.PlaceInfo>> {

    private final ACSettings settings;

    private final List<PlayerInfo> targets;
    private final List<BlockPos> blocks;

    private final long globalTimeoutTime;

    public ACCalculate(ACSettings settings, List<PlayerInfo> targets, List<BlockPos> blocks, long globalTimeoutTime) {
        this.settings = settings;

        this.targets = targets;
        this.blocks = blocks;

        this.globalTimeoutTime = globalTimeoutTime;
    }

    @Override
    public List<CrystalInfo.PlaceInfo> call() {
        return getPlayers(startThreads());
    }

    @Nonnull
    private List<Future<CrystalInfo.PlaceInfo>> startThreads() {
        List<Future<CrystalInfo.PlaceInfo>> output = new ArrayList<>();

        for (PlayerInfo target : targets) {
            output.add(ACHelper.executor.submit(new ACSubThread(settings, blocks, target)));
        }

        return output;
    }

    private List<CrystalInfo.PlaceInfo> getPlayers(List<Future<CrystalInfo.PlaceInfo>> input) {
        List<CrystalInfo.PlaceInfo> place = new ArrayList<>();
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
                    place.add(crystal);
                }
            } else {
                future.cancel(true);
            }
        }

        if (settings.crystalPriority.equalsIgnoreCase("Health")) {
            place.sort(Comparator.comparingDouble((i) -> -i.target.health));
        } else if (settings.crystalPriority.equalsIgnoreCase("Closest")) {
            place.sort(Comparator.comparingDouble((i) -> -settings.player.entity.getDistanceSq(i.target.entity)));
        } else {
            place.sort(Comparator.comparingDouble((i) -> i.damage));
        }

        return place;
    }
}
