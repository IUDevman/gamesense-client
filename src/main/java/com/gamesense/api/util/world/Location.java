package com.gamesense.api.util.world;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class Location {

    private double x, y, z;
    private boolean ground;

    public Location(double x, double y, double z, boolean ground) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ground = ground;
    }

    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ground = true;
    }

    public Location(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ground = true;
    }

    public Location add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Location add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Location subtract(int x, int y, int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;

        return this;
    }

    public Location subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;

        return this;
    }

    public Block getBlock() {
        return Minecraft.getMinecraft().world.getBlockState(this.toBlockPos()).getBlock();
    }

    public boolean isOnGround() {
        return this.ground;
    }

    public Location setOnGround(boolean ground) {
        this.ground = ground;
        return this;
    }

    public double getX() {
        return this.x;
    }

    public Location setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() {
        return this.y;
    }

    public Location setY(double y) {
        this.y = y;
        return this;
    }

    public double getZ() {
        return this.z;
    }

    public Location setZ(double z) {
        this.z = z;
        return this;
    }

    public static Location fromBlockPos(BlockPos blockPos) {
        return new Location(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public BlockPos toBlockPos() {
        return new BlockPos(getX(), getY(), getZ());
    }
}