package com.gamesense.api.util.combat.ca.breaks;

import com.gamesense.api.util.combat.DamageUtil;
import com.gamesense.api.util.combat.ca.CASettings;
import com.gamesense.api.util.combat.ca.CrystalInfo;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class BreakThread implements Callable<List<CrystalInfo.BreakInfo>> {
    private final CASettings settings;

    private final List<EntityEnderCrystal> crystals;
    private final List<EntityPlayer> targets;

    public BreakThread(CASettings setting, List<EntityEnderCrystal> crystals, List<EntityPlayer> targets) {
        this.settings = setting;

        this.crystals = crystals;
        this.targets = targets;
    }

    @Override
    public List<CrystalInfo.BreakInfo> call() {
        double x = settings.player.x;
        double y = settings.player.y;
        double z = settings.player.z;

        final boolean smart = settings.breakMode.equalsIgnoreCase("Smart");
        List<CrystalInfo.BreakInfo> worthyCrystals = new ArrayList<>();
        // get the best crystal for each player
        // store in worthyCrystals
        for (EntityPlayer target : targets) {
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
                    if ((double) bestDamage >= settings.minBreakDamage || ((target.getHealth() + target.getAbsorptionAmount()) <= settings.facePlaceHealth && bestDamage > settings.minFacePlaceDamage)) {
                        shouldAdd = true;
                    }
                } else {
                    shouldAdd = true;
                }

                if (shouldAdd) {
                    worthyCrystals.add(new CrystalInfo.BreakInfo(bestDamage, target, best));
                }
            }
        }

        return worthyCrystals;
    }
}
