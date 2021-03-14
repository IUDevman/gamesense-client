package com.gamesense.api.util.world.combat.ac.threads;

import com.gamesense.api.util.misc.Pair;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.api.util.world.combat.ac.ACSettings;
import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
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
            if (bestDamage >= settings.minDamage || ((target.health <= settings.facePlaceHealth || target.lowArmour) && bestDamage >= settings.minFacePlaceDamage)) {
                return new CrystalInfo.PlaceInfo(bestDamage, target, best);
            }
        }

        return null;
    }

    private CrystalInfo.BreakInfo getDestroyable() {
        if (crystals == null) {
            return null;
        }
        double x = settings.player.x;
        double y = settings.player.y;
        double z = settings.player.z;

        final boolean smart = settings.breakMode.equalsIgnoreCase("Smart");
        EntityEnderCrystal best = null;
        float bestDamage = 0f;
        for (EntityEnderCrystal crystal : crystals) {
            float currentDamage = DamageUtil.calculateDamageThreaded(crystal.posX, crystal.posY, crystal.posZ, target);
            if (currentDamage == bestDamage) {
                // this new crystal is closer
                // higher chance of being able to break it
                if (best == null || crystal.getDistanceSq(x, y, z) < best.getDistanceSq(x, y, z)) {
                    bestDamage = currentDamage;
                    best = crystal;
                }
            } else if (currentDamage > bestDamage) {
                bestDamage = currentDamage;
                best = crystal;
            }
        }

        if (best != null) {
            boolean shouldAdd = false;
            if (smart) {
                if ((double) bestDamage >= settings.minBreakDamage || ((target.health <= settings.facePlaceHealth || target.lowArmour) && bestDamage > settings.minFacePlaceDamage)) {
                    shouldAdd = true;
                }
            } else {
                shouldAdd = true;
            }

            if (shouldAdd) {
                return new CrystalInfo.BreakInfo(bestDamage, target, best);
            }
        }

        return null;
    }
}
