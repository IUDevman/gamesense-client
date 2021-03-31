package com.gamesense.api.util.world.combat.ac;

import com.gamesense.api.util.world.combat.DamageUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ACUtil {
    public static CrystalInfo.PlaceInfo calculateBestPlacement(ACSettings settings, PlayerInfo target, List<BlockPos> possibleLocations) {
        double x = settings.playerPos.x;
        double y = settings.playerPos.y;
        double z = settings.playerPos.z;

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

    public static CrystalInfo.BreakInfo calculateBestBreakable(ACSettings settings, PlayerInfo target, List<EntityEnderCrystal> crystals) {
        double x = settings.playerPos.x;
        double y = settings.playerPos.y;
        double z = settings.playerPos.z;

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
