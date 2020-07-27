package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.EntityUtil;
import com.gamesense.api.util.MotionUtils;
import com.gamesense.api.util.Timer;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.*;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;


public class Speed extends Module {
    public Speed() {
        super("Speed", Category.Movement);
    }

    int waitCounter;
    int forward = 1;
    private double moveSpeed;
    public static boolean doSlow;
    public Timer waitTimer = new Timer();

    Setting.b ice;
    Setting.mode Mode;
    Setting.d speed;

    public void setup() {
        ice = registerB("Ice", "SpeedIce", true);
        ArrayList<String> modes = new ArrayList<>();
        modes.add("Strafe");
        modes.add("YPort");
        modes.add("Packet");
        modes.add("Packet2");
        modes.add("FakeStrafe");

        speed =registerD("Speed", "SpeedSpeed", 8.0, 0.0, 10.0);
        Mode = registerMode("Modes", "SpeedModes", modes, "Strafe");
    }

    @Override
    public void onEnable() {
        moveSpeed = MotionUtils.getBaseMoveSpeed();
        GameSenseMod.EVENT_BUS.subscribe(this);
    }

    public void onUpdate() {

        boolean icee = this.ice.getValue() && (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() instanceof BlockIce || mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() instanceof BlockPackedIce);
        if (icee) {
            MotionUtils.setSpeed(mc.player, MotionUtils.getBaseMoveSpeed() + (mc.player.isPotionActive(MobEffects.SPEED) ? (mc.player.ticksExisted % 2 == 0 ? 0.7 : 0.1) : 0.4));
        }
        if (!icee) {
            if (Mode.getValue().equalsIgnoreCase("Packet") || Mode.getValue().equalsIgnoreCase("Packet2")) {
                if (MotionUtils.isMoving(mc.player) && mc.player.onGround) {
                    boolean step = ModuleManager.isModuleEnabled("Step");
                    double posX = mc.player.posX;
                    double posY = mc.player.posY;
                    double posZ = mc.player.posZ;
                    boolean ground = mc.player.onGround;
                    double[] dir1 = MotionUtils.forward(0.5);
                    BlockPos pos = new BlockPos(posX + dir1[0], posY, posZ + dir1[1]);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (step && !(block instanceof BlockAir)) {
                        MotionUtils.setSpeed(mc.player, 0);
                        return;
                    }
                    if (mc.world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock() instanceof BlockAir)
                        return;
                    for (double x = 0.0625; x < speed.getValue(); x += 0.262) {
                        double[] dir = MotionUtils.forward(x);
                        // if (mc.world.getBlockState(new BlockPos(posX + dir1[0], posY - 1, posZ + dir1[1])).getBlock() instanceof BlockAir)
                        //     return;
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(posX + dir[0], posY, posZ + dir[1], ground));
                    }
                    if (Mode.getValue().equalsIgnoreCase("Packet2"))
                        MotionUtils.setSpeed(mc.player, 2);

                    mc.player.connection.sendPacket(new CPacketPlayer.Position(posX + mc.player.motionX, mc.player.posY <= 10 ? 255 : 1, posZ + mc.player.motionZ, ground));
                }
            }
            if (Mode.getValue().equalsIgnoreCase("YPort") ) {
                if (!MotionUtils.isMoving(mc.player) || mc.player.isInWater() && mc.player.isInLava() || mc.player.collidedHorizontally) {
                    return;
                }
                if (mc.player.onGround) {
                    EntityUtil.setTimer(1.15f);
                    mc.player.jump();
                    boolean ice = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() instanceof BlockIce || mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() instanceof BlockPackedIce;
                    MotionUtils.setSpeed(mc.player, MotionUtils.getBaseMoveSpeed() + (ice ? 0.3 : 0.06));
                } else {
                    mc.player.motionY = -1;
                    EntityUtil.resetTimer();
                }
            }
        }
    }

    @EventHandler
    private Listener<PlayerMoveEvent> listener = new Listener<>(event -> {

        boolean icee = this.ice.getValue() && (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() instanceof BlockIce || mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() instanceof BlockPackedIce);
        if (icee)
            return;
        if (mc.player.isInLava() || mc.player.isInWater() || mc.player.isOnLadder())
            return;
        if (Mode.getValue().equalsIgnoreCase("Strafe")) {
            double motionY = 0.42f;
            if (mc.player.onGround && MotionUtils.isMoving(mc.player) && waitTimer.hasReached(300)) {
                if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    motionY += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
                }
                event.setY(mc.player.motionY = motionY);
                moveSpeed = MotionUtils.getBaseMoveSpeed() * (EntityUtil.isColliding(0, -0.5, 0) instanceof BlockLiquid && !EntityUtil.isInLiquid() ? 0.9 : 1.901);
                doSlow = true;
                waitTimer.reset();
            } else {
                if (doSlow || mc.player.collidedHorizontally) {
                    moveSpeed -= (EntityUtil.isColliding(0, -0.8, 0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) ? 0.4 : 0.7 * (moveSpeed = MotionUtils.getBaseMoveSpeed());
                    doSlow = false;
                } else {
                    moveSpeed -= moveSpeed / 159.0;
                }
            }
            moveSpeed = Math.max(moveSpeed, MotionUtils.getBaseMoveSpeed());
            double[] dir = MotionUtils.forward(moveSpeed);
            event.setX(dir[0]);
            event.setZ(dir[1]);
        }

    });

   /* public void onUpdate() {

        boolean icee = this.ice.getValue() && (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() instanceof BlockIce || mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() instanceof BlockPackedIce);
        if (icee)
            return;
        if (Mode.getValue().equalsIgnoreCase("Strafe")) {
            double motionY = 0.42f;
            if (mc.player.onGround && MotionUtils.isMoving(mc.player) && waitTimer.hasReached(300)) {
                if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    motionY += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
                }
                event.setY(mc.player.motionY = motionY);
                moveSpeed = MotionUtils.getBaseMoveSpeed() * (EntityUtil.isColliding(0, -0.5, 0) instanceof BlockLiquid && !EntityUtil.isInLiquid() ? 0.9 : 1.901);
                doSlow = true;
                waitTimer.reset();
            } else {
                if (doSlow || mc.player.collidedHorizontally) {
                    moveSpeed -= (EntityUtil.isColliding(0, -0.8, 0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) ? 0.4 : 0.7 * (moveSpeed = MotionUtils.getBaseMoveSpeed());
                    doSlow = false;
                } else {
                    moveSpeed -= moveSpeed / 159.0;
                }
            }
            moveSpeed = Math.max(moveSpeed, MotionUtils.getBaseMoveSpeed());
            double[] dir = MotionUtils.forward(moveSpeed);
            event.setX(dir[0]);
            event.setZ(dir[1]);
        }
    } */



    @Override
    public void onDisable() {
      //if (!Mode.getValue().equalsIgnoreCase("Strafe") || !Mode.getValue().equalsIgnoreCase("FakeStrafe"))
        // mc.player.setVelocity(0,0,0);
        GameSenseMod.EVENT_BUS.unsubscribe(this);
        EntityUtil.resetTimer();
    }

    @Override
    public String getHudInfo() {
        String t = "";
      if (Mode.getValue().equalsIgnoreCase("Strafe")) {
          t = "[" + ChatFormatting.WHITE + "Strafe" + ChatFormatting.GRAY + "]";
      }
      if (Mode.getValue().equalsIgnoreCase("YPort")) {
          t = "[" + ChatFormatting.WHITE + "YPort" + ChatFormatting.GRAY + "]";
      }
      if (Mode.getValue().equalsIgnoreCase("Packet")) {
          t = "[" + ChatFormatting.WHITE + "Packet" + ChatFormatting.GRAY + "]";
      }
      if (Mode.getValue().equalsIgnoreCase("Packet2")) {
          t = "[" + ChatFormatting.WHITE + "Packet2" + ChatFormatting.GRAY + "]";
      }
      if (Mode.getValue().equalsIgnoreCase("FakeStrafe")) {
          t = "[" + ChatFormatting.WHITE + "Strafe" + ChatFormatting.GRAY + "]";
      }
      return t;
    }
}

