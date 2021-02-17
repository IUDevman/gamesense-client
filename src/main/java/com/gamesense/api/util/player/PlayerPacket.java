package com.gamesense.api.util.player;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class PlayerPacket {

    private final Vec3d position;
    private final Vec2f rotation;

    public PlayerPacket(Vec2f rotation) {
        this(rotation, null);
    }

    public PlayerPacket(Vec3d position) {
        this(null, position);
    }

    public PlayerPacket(Vec2f rotation, Vec3d position) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vec3d getPosition() {
        return position;
    }

    public Vec2f getRotation() {
        return rotation;
    }

}
