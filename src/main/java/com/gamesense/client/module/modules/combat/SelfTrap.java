package com.gamesense.client.module.modules.combat;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.world.BlockUtils;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static com.gamesense.api.util.world.BlockUtils.faceVectorPacketInstant;

/**
 * @Author Hoosiers on 09/19/20
 * Ported and modified from Surround.java
 */

public class SelfTrap extends Module {
    public SelfTrap(){
        super("SelfTrap", Category.Combat);
    }

    Setting.Mode trapType;
    Setting.Boolean chatMsg;
    Setting.Boolean rotate;
    Setting.Boolean disableNone;
    Setting.Boolean centerPlayer;
    Setting.Integer tickDelay;
    Setting.Integer blocksPerTick;

    public void setup(){
        ArrayList<String> trapTypes = new ArrayList<>();
        trapTypes.add("Normal");
        trapTypes.add("No Step");
        trapTypes.add("Simple");

        trapType = registerMode("Mode", "Mode", trapTypes, "Normal");
        disableNone = registerBoolean("Disable No Obby", "DisableNoObby", true);
        rotate = registerBoolean("Rotate", "Rotate", true);
        centerPlayer = registerBoolean("Center Player", "CenterPlayer", false);
        tickDelay = registerInteger("Tick Delay", "TickDelay", 5, 0, 10);
        blocksPerTick = registerInteger("Blocks Per Tick", "BlocksPerTick", 4, 0, 8);
        chatMsg = registerBoolean("Chat Msgs", "ChatMsgs", true);
    }

    private int cachedHotbarSlot = -1;
    private int obbyHotbarSlot;

    private boolean noObby = false;
    private boolean isSneaking = false;
    private boolean firstRun = false;

    private int blocksPlaced;
    private int delayTimeTicks = 0;
    private int playerYLevel = 0;
    private int offsetSteps = 0;

    private Vec3d centeredBlock = Vec3d.ZERO;

    public void onEnable(){
        if (mc.player == null){
            disable();
            return;
        }

        if (chatMsg.getValue()){
            Command.sendRawMessage("\u00A7aSelfTrap turned ON!");
        }

        if (centerPlayer.getValue() && mc.player.onGround){
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        centeredBlock = getCenterOfBlock(mc.player.posX, mc.player.posY, mc.player.posY);

        cachedHotbarSlot = mc.player.inventory.currentItem;
        obbyHotbarSlot = -1;
    }

    public void onDisable(){
        if (mc.player == null){
            return;
        }

        if (chatMsg.getValue()){
            if (noObby){
                return;
            }
            else {
                Command.sendRawMessage("\u00A7cSelfTrap turned OFF!");
            }
        }

        if (obbyHotbarSlot != cachedHotbarSlot && cachedHotbarSlot != -1){
            mc.player.inventory.currentItem = cachedHotbarSlot;
        }

        if (isSneaking){
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        cachedHotbarSlot = -1;
        obbyHotbarSlot = -1;
        centeredBlock = Vec3d.ZERO;

        noObby = false;
        firstRun = false;
        AutoCrystal.stopAC = false;
    }

    public void onUpdate(){
        if (mc.player == null){
            disable();
            return;
        }

        if (disableNone.getValue() && noObby){
            if (chatMsg.getValue()){
                Command.sendRawMessage("\u00A7cNo obsidian detected... SelfTrap turned OFF!");
            }
            mc.player.inventory.currentItem = cachedHotbarSlot;
            disable();
            return;
        }

        if (mc.player.posY <= 0){
            return;
        }

        if (firstRun){
            firstRun = false;
            if (findObsidianSlot() == -1){
                noObby = true;
            }
        }
        else {
            if (delayTimeTicks < tickDelay.getValue()){
                delayTimeTicks++;
                return;
            }
            else {
                delayTimeTicks = 0;
            }
        }

        if (centerPlayer.getValue() && centeredBlock != Vec3d.ZERO && mc.player.onGround){

            double xDeviation = Math.abs(centeredBlock.x - mc.player.posX);
            double zDeviation = Math.abs(centeredBlock.z - mc.player.posZ);

            if (xDeviation <= 0.1 && zDeviation <= 0.1){
                centeredBlock = Vec3d.ZERO;
            }
            else {
                double newX;
                double newZ;
                if (mc.player.posX > Math.round(mc.player.posX)){
                    newX = Math.round(mc.player.posX) + 0.5;
                }
                else if (mc.player.posX < Math.round(mc.player.posX)){
                    newX = Math.round(mc.player.posX) - 0.5;
                }
                else {
                    newX = mc.player.posX;
                }

                if (mc.player.posZ > Math.round(mc.player.posZ)){
                    newZ = Math.round(mc.player.posZ) + 0.5;
                }
                else if (mc.player.posZ < Math.round(mc.player.posZ)){
                    newZ = Math.round(mc.player.posZ) - 0.5;
                }
                else {
                    newZ = mc.player.posZ;
                }

                mc.player.connection.sendPacket(new CPacketPlayer.Position(newX, mc.player.posY, newZ, true));
                mc.player.setPosition(newX, mc.player.posY, newZ);
            }
        }

        blocksPlaced = 0;

        while (blocksPlaced <= blocksPerTick.getValue()){

            Vec3d[] offsetPattern;
            int maxSteps;

            if (trapType.getValue().equalsIgnoreCase("Normal")){
                offsetPattern = Offsets.TRAP;
                maxSteps = Offsets.TRAP.length;
            }
            else if (trapType.getValue().equalsIgnoreCase("No Step")){
                offsetPattern = Offsets.TRAPFULLROOF;
                maxSteps = Offsets.TRAPFULLROOF.length;
            }
            else {
                offsetPattern = Offsets.TRAPSIMPLE;
                maxSteps = Offsets.TRAPSIMPLE.length;
            }

            if (offsetSteps >= maxSteps){
                offsetSteps = 0;
                break;
            }

            BlockPos offsetPos = new BlockPos(offsetPattern[offsetSteps]);
            BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

            boolean tryPlacing = true;

            if (!mc.world.getBlockState(targetPos).getMaterial().isReplaceable()){
                tryPlacing = false;
            }

            for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))){
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb){
                    tryPlacing = false;
                    break;
                }
            }

            if (tryPlacing && placeBlock(targetPos)){
                blocksPlaced++;
            }

            offsetSteps++;
        }
    }

    private int findObsidianSlot(){
        int slot = -1;

        for (int i = 0; i < 9; i++){
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)){
                continue;
            }

            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockObsidian){
                slot = i;
                break;
            }
        }
        return slot;
    }

    private boolean placeBlock(BlockPos pos){
        Block block = mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)){
            return false;
        }

        EnumFacing side = BlockUtils.getPlaceableSide(pos);

        if (side == null){
            return false;
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        if (!BlockUtils.canBeClicked(neighbour)){
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        int obsidianSlot = findObsidianSlot();

        if (mc.player.inventory.currentItem != obsidianSlot){
            obbyHotbarSlot = obsidianSlot;

            mc.player.inventory.currentItem = obsidianSlot;
        }

        if (!isSneaking && BlockUtils.blackList.contains(neighbourBlock) || BlockUtils.shulkerList.contains(neighbourBlock)){
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        if (obsidianSlot == -1){
            noObby = true;
            return false;
        }

        boolean stoppedAC = false;

        if (ModuleManager.isModuleEnabled("AutoCrystalGS")){
            AutoCrystal.stopAC = true;
            stoppedAC = true;
        }

        if (rotate.getValue()){
            faceVectorPacketInstant(hitVec);
        }

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;

        if (stoppedAC){
            AutoCrystal.stopAC = false;
            stoppedAC = false;
        }

        return true;
    }

    private Vec3d getCenterOfBlock(double playerX, double playerY, double playerZ){

        double newX = Math.floor(playerX) + 0.5;
        double newY = Math.floor(playerY);
        double newZ = Math.floor(playerZ) + 0.5;

        return new Vec3d(newX, newY, newZ);
    }

    private static class Offsets {
        private static final Vec3d[] TRAP ={
                new Vec3d(0, -1, -1),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, 0,-1),
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 1, -1),
                new Vec3d(1, 1, 0),
                new Vec3d(0, 1, 1),
                new Vec3d(-1, 1, 0),
                new Vec3d(0, 2, -1),
                new Vec3d(0, 2, 0)
        };

        private static final Vec3d[] TRAPFULLROOF ={
                new Vec3d(0, -1, -1),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, 0, -1),
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 1, -1),
                new Vec3d(1, 1, 0),
                new Vec3d(0, 1, 1),
                new Vec3d(-1, 1, 0),
                new Vec3d(0, 2, -1),
                new Vec3d(0, 2, 0),
                new Vec3d(0, 3, 0)
        };

        private static final Vec3d[] TRAPSIMPLE ={
                new Vec3d(-1, -1, 0),
                new Vec3d(1, -1,0),
                new Vec3d(0,-1,-1),
                new Vec3d(0,-1,1),
                new Vec3d(1, 0,0),
                new Vec3d(0,0,-1),
                new Vec3d(0,0,1),
                new Vec3d(-1, 0, 0),
                new Vec3d(-1, 1, 0),
                new Vec3d(-1, 2, 0),
                new Vec3d(0, 2, 0)
        };
    }
}