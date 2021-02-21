package com.gamesense.api.util.combat.ca;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class CrystalInfo {
    public final float damage;
    public final EntityPlayer target;

    private CrystalInfo(float damage, EntityPlayer target) {
        this.damage = damage;
        this.target = target;

    }

    public static class BreakInfo extends CrystalInfo {
        public final EntityEnderCrystal crystal;

        public BreakInfo(float damage, EntityPlayer target, EntityEnderCrystal crystal) {
            super(damage, target);
            this.crystal = crystal;
        }
    }

    public static class PlaceInfo extends CrystalInfo {
        public final BlockPos crystal;

        public PlaceInfo(float damage, EntityPlayer target, BlockPos crystal) {
            super(damage, target);
            this.crystal = crystal;
        }
    }
}
