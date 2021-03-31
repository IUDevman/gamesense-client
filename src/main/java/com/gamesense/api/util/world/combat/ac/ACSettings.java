package com.gamesense.api.util.world.combat.ac;

import net.minecraft.util.math.Vec3d;

public class ACSettings {
    public final boolean breakCrystals;
    public final boolean placeCrystals;

    public final double enemyRangeSq;
    public final double breakRangeSq;
    public final double wallsRangeSq;
    public final float placeRange;

    public final float minDamage;
    public final float minBreakDamage;
    public final float minFacePlaceDamage;
    public final float maxSelfDamage;

    public final float facePlaceHealth;

    public final boolean antiSuicide;
    public final boolean endCrystalMode;

    public final String breakMode;
    public final String crystalPriority;

    public final PlayerInfo player;
    public final Vec3d playerPos;

    public ACSettings(boolean breakCrystals, boolean placeCrystals, double enemyRange, double breakRange, double wallsRange, double placeRange, double minDamage, double minBreakDamage, double minFacePlaceDamage, double maxSelfDamage, double facePlaceHealth, boolean antiSuicide, boolean endCrystalMode, String breakMode, String crystalPriority, PlayerInfo player, Vec3d playerPos) {
        this.breakCrystals = breakCrystals;
        this.placeCrystals = placeCrystals;

        this.enemyRangeSq = enemyRange * enemyRange;
        this.breakRangeSq = breakRange * breakRange;
        this.wallsRangeSq = wallsRange * wallsRange;
        this.placeRange = (float) placeRange;

        this.minDamage = (float) minDamage;
        this.minBreakDamage = (float) minBreakDamage;
        this.minFacePlaceDamage = (float) minFacePlaceDamage;
        this.maxSelfDamage = (float) maxSelfDamage;

        this.facePlaceHealth = (float) facePlaceHealth;

        this.antiSuicide = antiSuicide;
        this.endCrystalMode = endCrystalMode;

        this.breakMode = breakMode;
        this.crystalPriority = crystalPriority;

        this.player = player;
        this.playerPos = playerPos;
    }
}
