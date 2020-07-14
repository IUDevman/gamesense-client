package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class DamageBlockEvent extends GameSenseEvent {

    private BlockPos pos;
    private EnumFacing face;

    public DamageBlockEvent(BlockPos pos, EnumFacing face) {
        this.pos = pos;
        this.face = face;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public EnumFacing getFace() {
        return face;
    }

    public void setFace(EnumFacing face) {
        this.face = face;
    }
}
