package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

/**
 * @author Hoosiers
 * @since 12/29/2020
 */

public class RenderEntityReturnEvent extends GameSenseEvent {

    public final Entity entity;
    public final BlockPos blockPos;

    public RenderEntityReturnEvent(Entity entity, BlockPos blockPos) {
        this.entity = entity;
        this.blockPos = blockPos;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }
}