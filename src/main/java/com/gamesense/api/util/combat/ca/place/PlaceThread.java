package com.gamesense.api.util.combat.ca.place;

import com.gamesense.api.util.combat.DamageUtil;
import com.gamesense.api.util.combat.ca.CASettings;
import com.gamesense.api.util.combat.ca.CrystalInfo;
import com.gamesense.api.util.combat.ca.PlayerInfo;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.concurrent.Callable;

public class PlaceThread implements Callable<CrystalInfo.PlaceInfo> {
    private final CASettings settings;

    private final List<BlockPos> possibleLocations;
    private final PlayerInfo target;

    public PlaceThread(CASettings setting, List<BlockPos> possibleLocations, PlayerInfo target) {
        this.settings = setting;

        this.possibleLocations = possibleLocations;
        this.target = target;
    }

    @Override
    public CrystalInfo.PlaceInfo call() {
        double x = settings.player.x;
        double y = settings.player.y;
        double z = settings.player.z;

        // get the best crystal for the player
        BlockPos best = null;
        float bestDamage = 0f;
        for (BlockPos crystal : possibleLocations) {
            // if player is out of range of this crystal, do nothing
            if (target.entity.getDistanceSq((double) crystal.getX() + 0.5d, (double) crystal.getY() + 1.0d, (double) crystal.getZ() + 0.5d) <= settings.enemyRangeSq) {
                float currentDamage = DamageUtil.calculateDamageThreaded((double) crystal.getX() + 0.5d, (double) crystal.getY() + 1.0d, (double) crystal.getZ() + 0.5d, target);
                if (currentDamage == bestDamage) {
                    // this new crystal is closer
                    // higher chance of being able to break it
                    if (best == null || crystal.distanceSq(x, y, z) < best.distanceSq(x, y, z)) {
                        bestDamage = currentDamage;
                        best = crystal;
                    }
                } else if (currentDamage > bestDamage) {
                    bestDamage = currentDamage;
                    best = crystal;
                }
            }
        }

        if (best != null) {
            if (bestDamage >= settings.minDamage || ((target.entity.getHealth() + target.entity.getAbsorptionAmount()) <= settings.facePlaceHealth && bestDamage >= settings.minFacePlaceDamage)) {
                return new CrystalInfo.PlaceInfo(bestDamage, target.entity, best);
            }
        }

        return null;
    }
}
