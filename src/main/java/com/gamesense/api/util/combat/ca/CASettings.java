package com.gamesense.api.util.combat.ca;

import net.minecraft.util.math.Vec3d;

import java.security.PublicKey;

public class CASettings {
    public final double enemyRangeSq;

    public final float minDamage;
    public final float minBreakDamage;
    public final float minFacePlaceDamage;

    public final float facePlaceHealth;

    public final String breakMode;

    public final Vec3d player;

    public CASettings(double enemyRange, double minDamage, double minBreakDamage, double minFacePlaceDamage, double facePlaceHealth, String breakMode, Vec3d player) {
        this.enemyRangeSq = enemyRange * enemyRange;

        this.minDamage = (float) minDamage;
        this.minBreakDamage = (float) minBreakDamage;
        this.minFacePlaceDamage = (float) minFacePlaceDamage;

        this.facePlaceHealth = (float) facePlaceHealth;

        this.breakMode = breakMode;

        this.player = player;
    }
}
