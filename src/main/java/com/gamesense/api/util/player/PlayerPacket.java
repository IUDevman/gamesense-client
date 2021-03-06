package com.gamesense.api.util.player;

import com.gamesense.client.module.Module;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class PlayerPacket {

    private final int priority;

    private final Vec3d position;
    private final Vec2f rotation;

    public PlayerPacket(Module module, Vec2f rotation) {
        this(module, null, rotation);
    }

    public PlayerPacket(Module module, Vec3d position) {
        this(module, position, null);
    }

    public PlayerPacket(Module module, Vec3d position, Vec2f rotation) {
        this(module.getPriority(), position, rotation);
    }

    private PlayerPacket(int priority, Vec3d position, Vec2f rotation) {
        this.priority = priority;
        this.position = position;
        this.rotation = rotation;
    }

    public int getPriority() {
        return priority;
    }

    public Vec3d getPosition() {
        return position;
    }

    public Vec2f getRotation() {
        return rotation;
    }
}