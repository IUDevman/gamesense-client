package com.gamesense.client.waypoint;

import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class Waypoint {
    BlockPos blockPos;
    double x;
    double y;
    double z;
    String name;
    int color;

    public Waypoint(String name, double x, double y, double z, int color){
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.blockPos = new BlockPos(x, y, z);
    }

    public Waypoint(String name, double x, double y, double z, Color color){
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color.getRGB();
        this.blockPos = new BlockPos(x, y, z);
    }

    public Waypoint(String name, double x, double y, double z){
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = 0xffffffff;
        this.blockPos = new BlockPos(x, y, z);
    }

    public Waypoint(String name, BlockPos blockPos, int color){
        this.name = name;
        this.x = blockPos.getX();
        this.y = blockPos.getY();
        this.z = blockPos.getZ();
        this.color = color;
        this.blockPos = blockPos;
    }

    public Waypoint(String name, BlockPos blockPos){
        this.name = name;
        this.x = blockPos.getX();
        this.y = blockPos.getY();
        this.z = blockPos.getZ();
        this.color = 0xffffffff;
        this.blockPos = blockPos;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
