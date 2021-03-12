package com.gamesense.api.util.combat.ac;

import com.gamesense.api.util.misc.Pair;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public enum ACHelper {
    INSTANCE;

    // Threading Stuff
    public static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private static final ExecutorService mainExecutors = Executors.newSingleThreadExecutor();
    private Future<Pair<HashMap<EntityPlayer, Float>, HashMap<EntityPlayer, Float>>> mainThreadOutput;

    public void startCalculations(ACSettings settings, List<PlayerInfo> targetsInfo, List<EntityEnderCrystal> crystals, List<BlockPos> possiblePlacements, long timeout) {
        mainThreadOutput = mainExecutors.submit(new ACCalculate(settings, targetsInfo, crystals, possiblePlacements, timeout));
    }

    public Pair<HashMap<EntityPlayer, Float>, HashMap<EntityPlayer, Float>> getOutput() {
        if (mainThreadOutput == null) {
            return null;
        }

        while (!(mainThreadOutput.isDone() || mainThreadOutput.isCancelled())) {
        }

        Pair<HashMap<EntityPlayer, Float>, HashMap<EntityPlayer, Float>> output = null;
        try {
            output = mainThreadOutput.get();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        return output;
    }
}
