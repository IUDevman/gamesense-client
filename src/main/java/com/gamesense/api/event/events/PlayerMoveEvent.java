package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import net.minecraft.entity.MoverType;

public class PlayerMoveEvent extends GameSenseEvent {
    MoverType type;
    public double x;
    public double y;
    public double z;
    public PlayerMoveEvent(MoverType moverType, double xx, double yy, double zz){
        super();
        type = moverType;
        x = xx;
        y = yy;
        z =zz;
    }

    public MoverType getType(){
        return type;
    }

    public void setType(MoverType type) {
        this.type = type;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getZ(){
        return z;
    }

    public void setX(double xx){
        x = xx;
    }

    public void setY(double yy){
        y = yy;
    }

    public void setZ(double zz){
        z = zz;
    }
}
