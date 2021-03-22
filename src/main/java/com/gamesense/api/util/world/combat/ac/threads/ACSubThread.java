package com.gamesense.api.util.world.combat.ac.threads;

import com.gamesense.api.util.misc.Pair;
import com.gamesense.api.util.world.combat.ac.*;
import com.gamesense.client.GameSense;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.concurrent.Callable;

public class ACSubThread implements Callable<Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo>> {
    private final ACSettings settings;

    private final List<BlockPos> possibleLocations;
    private final List<EntityEnderCrystal> crystals;
    private final PlayerInfo target;

    public ACSubThread(ACSettings setting, List<BlockPos> possibleLocations, List<EntityEnderCrystal> crystals, PlayerInfo target) {
        this.settings = setting;

        this.possibleLocations = possibleLocations;
        this.crystals = crystals;
        this.target = target;
    }

    @Override
    public Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo> call() {
        Pair<CrystalInfo.BreakInfo, CrystalInfo.PlaceInfo> output = new Pair<>(null, null);
        if (settings.placeCrystals) {
            output.setValue(getPlacement());
        }
        if (settings.breakCrystals) {
            output.setKey(getDestroyable());
        }

        return output;
    }

    private CrystalInfo.PlaceInfo getPlacement() {
        if (possibleLocations == null) {
            return null;
        }

        return ACUtil.calculateBestPlacement(settings, target, possibleLocations);
    }

    private CrystalInfo.BreakInfo getDestroyable() {
        if (crystals == null) {
            return null;
        }

        return ACUtil.calculateBestBreakable(settings, target, crystals);
    }
}
