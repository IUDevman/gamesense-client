package com.gamesense.api.util.world.combat.ac;

import com.gamesense.api.util.misc.Pair;
import com.gamesense.api.util.world.combat.ac.threads.ACCalculate;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.concurrent.*;

public enum ACHelper {
    INSTANCE;

    // Threading Stuff
    public static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private static final ExecutorService mainExecutors = Executors.newSingleThreadExecutor();
    private Future<Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>>> mainThreadOutput;

    public void startCalculations(ACSettings settings, List<PlayerInfo> targetsInfo, List<EntityEnderCrystal> crystals, List<BlockPos> possiblePlacements, long timeout) {
        if (mainThreadOutput != null) {
            mainThreadOutput.cancel(true);
        }
        mainThreadOutput = mainExecutors.submit(new ACCalculate(settings, targetsInfo, crystals, possiblePlacements, timeout));
    }

    public Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>> getOutput(boolean wait) {
        if (mainThreadOutput == null) {
            return null;
        }

        if (wait) {
            while (!(mainThreadOutput.isDone() || mainThreadOutput.isCancelled())) {
            }
        } else {
            if (!(mainThreadOutput.isDone() || mainThreadOutput.isCancelled())) {
                return null;
            }
        }

        Pair<List<CrystalInfo.BreakInfo>, List<CrystalInfo.PlaceInfo>> output = null;
        try {
            output = mainThreadOutput.get();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        mainThreadOutput = null;
        return output;
    }
}
