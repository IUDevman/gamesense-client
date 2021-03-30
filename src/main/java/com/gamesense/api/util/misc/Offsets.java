package com.gamesense.api.util.misc;

import net.minecraft.util.math.Vec3d;

/**
 * @author Hoosiers
 * @since 03/29/2021
 */

public class Offsets {

    //The BlockPos of (0, 0, 0) is the "center" / where the player's feet is

    public static final Vec3d[] SURROUND = {
            //layer below feet
            new Vec3d(0, -1, 0),
            new Vec3d(-1, -1, 0),
            new Vec3d(1, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(0, -1, 1),
            //layer at feet level
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1)
    };

    //"anti city" surround places blocks two blocks out in each cardinal direction
    public static final Vec3d[] SURROUND_CITY = {
            //layer below feet
            new Vec3d(0, -1, 0),
            new Vec3d(-1, -1, 0),
            new Vec3d(1, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(0, -1, 1),
            //layer at feet level
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1),
            //anti city layer
            new Vec3d(-2, 0, 0),
            new Vec3d(2, 0, 0),
            new Vec3d(0, 0, -2),
            new Vec3d(0, 0, 2),
    };

    public static final Vec3d[] TRAP_FULL = {
            //layer below feet
            new Vec3d(0, -1, 0),
            new Vec3d(-1, -1, 0),
            new Vec3d(1, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(0, -1, 1),
            //layer at feet level
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1),
            //layer at head level
            new Vec3d(-1, 1, 0),
            new Vec3d(1, 1, 0),
            new Vec3d(0, 1, -1),
            new Vec3d(0, 1, 1),
            //roof layer
            new Vec3d(1, 2, 0),
            new Vec3d(0, 2, 0)
    };

    public static final Vec3d[] TRAP_STEP = {
            //layer below feet
            new Vec3d(0, -1, 0),
            new Vec3d(-1, -1, 0),
            new Vec3d(1, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(0, -1, 1),
            //layer at feet level
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1),
            //layer at head level
            new Vec3d(-1, 1, 0),
            new Vec3d(1, 1, 0),
            new Vec3d(0, 1, -1),
            new Vec3d(0, 1, 1),
            //roof layer
            new Vec3d(1, 2, 0),
            new Vec3d(0, 2, 0),
            new Vec3d(0, 3, 0)
    };

    //"simple" trap method with least number of blocks to trap someone (no head layer)
    public static final Vec3d[] TRAP_SIMPLE = {
            //layer below feet
            new Vec3d(0, -1, 0),
            new Vec3d(-1, -1, 0),
            new Vec3d(1, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(0, -1, 1),
            //layer at feet level
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1),
            //layer at head level (scaffold)
            new Vec3d(1, 1, 0),
            //roof layer
            new Vec3d(1, 2, 0),
            new Vec3d(0, 2, 0)
    };

    public static final Vec3d[] BURROW = {
            new Vec3d(0, 0, 0)
    };

    public static final Vec3d[] BURROW_DOUBLE = {
            new Vec3d(0, 0, 0),
            new Vec3d(0, 1, 0)
    };
}