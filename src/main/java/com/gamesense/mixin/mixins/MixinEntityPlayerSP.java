package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.Sprint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    @Shadow
    @Final
    public NetHandlerPlayClient connection;
    @Shadow
    protected Minecraft mc;
    @Shadow
    private boolean prevOnGround;
    @Shadow
    private float lastReportedYaw;
    @Shadow
    private float lastReportedPitch;
    @Shadow
    private int positionUpdateTicks;
    @Shadow
    private double lastReportedPosX;
    @Shadow
    private double lastReportedPosY;
    @Shadow
    private double lastReportedPosZ;
    @Shadow
    private boolean autoJumpEnabled;
    @Shadow
    private boolean serverSprintState;
    @Shadow
    private boolean serverSneakState;

    public MixinEntityPlayerSP() {
        super(Minecraft.getMinecraft().world, Minecraft.getMinecraft().session.getProfile());
    }

    @Shadow
    protected abstract boolean isCurrentViewEntity();

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(AbstractClientPlayer player, MoverType type, double x, double y, double z) {
        PlayerMoveEvent moveEvent = new PlayerMoveEvent(type, x, y, z);
        GameSense.EVENT_BUS.post(moveEvent);
        super.move(type, moveEvent.x, moveEvent.y, moveEvent.z);
    }

    @ModifyArg(method = "setSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;setSprinting(Z)V"), index = 0)
    public boolean modifySprinting(boolean sprinting) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        Sprint sprint = ModuleManager.getModule(Sprint.class);

        if (player != null && sprint.isEnabled() && sprint.shouldSprint(player)) {
            return true;
        } else {
            return sprinting;
        }
    }

    // Sponsored by KAMI Blue
    // https://github.com/kami-blue/client/blob/master/src/main/java/org/kamiblue/client/mixin/client/player/MixinEntityPlayerSP.java
    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void onUpdateWalkingPlayerPre(CallbackInfo callbackInfo) {
        // Setup flags
        Vec3d position = new Vec3d(this.posX, this.getEntityBoundingBox().minY, this.posZ);
        Vec2f rotation = new Vec2f(this.rotationYaw, this.rotationPitch);

        OnUpdateWalkingPlayerEvent event = new OnUpdateWalkingPlayerEvent(position, rotation);
        GameSense.EVENT_BUS.post(event);

        event = event.nextPhase();
        GameSense.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            callbackInfo.cancel();

            boolean moving = event.isMoving() || isMoving(position);
            boolean rotating = event.isRotating() || isRotating(rotation);

            // Copy flags from event
            position = event.getPosition();
            rotation = event.getRotation();

            ++this.positionUpdateTicks;
            sendSprintPacket();
            sendSneakPacket();
            sendPlayerPacket(moving, rotating, position, rotation);
        }

        event = event.nextPhase();
        GameSense.EVENT_BUS.post(event);
    }

    private void sendSprintPacket() {
        boolean sprinting = this.isSprinting();

        if (sprinting != this.serverSprintState) {
            if (sprinting) {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SPRINTING));
            } else {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SPRINTING));
            }
            this.serverSprintState = sprinting;
        }
    }

    private void sendSneakPacket() {
        boolean sneaking = this.isSneaking();

        if (sneaking != this.serverSneakState) {
            if (sneaking) {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            this.serverSneakState = sneaking;
        }
    }

    private void sendPlayerPacket(boolean moving, boolean rotating, Vec3d position, Vec2f rotation) {
        if (!this.isCurrentViewEntity()) return;

        if (this.isRiding()) {
            this.connection.sendPacket(new CPacketPlayer.PositionRotation(this.motionX, -999.0D, this.motionZ, rotation.x, rotation.y, onGround));
            moving = false;
        } else if (moving && rotating) {
            this.connection.sendPacket(new CPacketPlayer.PositionRotation(position.x, position.y, position.z, rotation.x, rotation.y, onGround));
        } else if (moving) {
            this.connection.sendPacket(new CPacketPlayer.Position(position.x, position.y, position.z, onGround));
        } else if (rotating) {
            this.connection.sendPacket(new CPacketPlayer.Rotation(rotation.x, rotation.y, onGround));
        } else if (this.prevOnGround != onGround) {
            this.connection.sendPacket(new CPacketPlayer(onGround));
        }

        if (moving) {
            this.lastReportedPosX = position.x;
            this.lastReportedPosY = position.y;
            this.lastReportedPosZ = position.z;
            this.positionUpdateTicks = 0;
        }

        if (rotating) {
            this.lastReportedYaw = rotation.x;
            this.lastReportedPitch = rotation.y;
        }

        this.prevOnGround = onGround;
        this.autoJumpEnabled = this.mc.gameSettings.autoJump;
    }

    private boolean isMoving(Vec3d position) {
        double xDiff = position.x - this.lastReportedPosX;
        double yDiff = position.y - this.lastReportedPosY;
        double zDiff = position.z - this.lastReportedPosZ;

        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff > 9.0E-4D || this.positionUpdateTicks >= 20;
    }

    private boolean isRotating(Vec2f rotation) {
        double yawDiff = rotation.x - this.lastReportedYaw;
        double pitchDiff = rotation.y - this.lastReportedPitch;

        return yawDiff != 0.0D || pitchDiff != 0.0D;
    }
}