package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.friends.Friends;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author TechAle on (remember me to insert the date)
 * Ported and modified from AutoAnvil.java that is modified from Surround.java
 * Break crystal from AutoCrystal
 * TODO: resolve the bug that the crystal does not spawn (hard solved, try to make a soft one) (next update)
 * TODO: Make the placement can be done even 1/2 blocks up the enemy
 * TODO: Resolve the thing if you are under with rotate on
 * TODO: implementing six's idea + redstoneBlock (next update)
 * TODO: Optimize update cicle (i fell like is not efficent / is a mess) (next update)
 */

/*
    FIX: 1) improved rotation off
         2) fixed offhand bug
         3) Fixed bug when, you have NoGlitchBlock, it stay stuck trying to break a crystal that doesnt exist (basically, another crystal)
 */

// Count of bugs solved: A lot

public class PistonCrystal extends Module {
    public PistonCrystal(){
        super("PistonCrystal", Category.Combat);
    }
    Setting.Mode breakType;
    Setting.Double enemyRange;
    Setting.Boolean rotate;
    Setting.Boolean chatMsg;
    Setting.Boolean blockPlayer;
    Setting.Boolean antiWeakness;
    Setting.Boolean confirmBreak;
    Setting.Integer blocksPerTick;
    Setting.Integer startDelay;
    Setting.Integer supBlocksDelay;
    Setting.Integer pistonDelay;
    Setting.Integer crystalDelay;
    Setting.Integer hitDelay;
    Setting.Integer stuckDetector;

    public void setup(){

        ArrayList<String> breakTypes = new ArrayList<>();
        breakTypes.add("Swing");
        breakTypes.add("Packet");
        breakType = registerMode("Type", "Type", breakTypes, "Swing");
        rotate = registerBoolean("Rotate", "Rotate", false);
        blockPlayer = registerBoolean("Trap Player", "TrapPlayer", true);
        confirmBreak = registerBoolean("No Glitch Blocks", "NoGlitchBlocks", true);
        enemyRange = registerDouble("Range", "Range",5.9, 0, 6);
        blocksPerTick = registerInteger("Blocks Per Tick", "BlocksPerTick", 4, 0, 20);
        stuckDetector = registerInteger("Stuck Check", "StuckCheck", 35, 0, 200);
        startDelay = registerInteger("Start Delay", "StartDelay", 4, 0, 20);
        supBlocksDelay = registerInteger("Surround Delay", "SurroundDelay", 4, 0, 20);
        pistonDelay = registerInteger("Piston Delay", "PistonDelay", 2, 0, 20);
        crystalDelay = registerInteger("Crystal Delay", "Crystal Delay", 2, 0, 20);
        hitDelay = registerInteger("Hit Delay", "HitDelay", 2, 0, 20);
        chatMsg = registerBoolean("Chat Msgs", "ChatMsgs", true);
        antiWeakness = registerBoolean("Anti Weakness", "AntiWeakness", false);
    }

    private boolean isSneaking = false;
    private boolean firstRun = false;
    private boolean noMaterials = false;
    private boolean hasMoved = false;
    private boolean isHole = true;
    private boolean enoughSpace = true;
    private int oldSlot = -1;
    private int[] slot_mat;
    private int[] delayTable;
    private int stage;
    private int delayTimeTicks;
    private double[] enemyCoords;
    private structureTemp toPlace;
    int[][] disp_surblock = {
            {1,0,0},
            {-1,0,0},
            {0,0,1},
            {0,0,-1}
    };
    Double[][] sur_block;
    private int stuck = 0;
    boolean broken, brokenCrystalBug, brokenRedstoneTorch;


    private EntityPlayer closestTarget;
    double[] coordsD;
    public void onEnable(){
        coordsD = new double[3];
        // Create new delay table
        delayTable = new int[] {
                startDelay.getValue(),
                supBlocksDelay.getValue(),
                pistonDelay.getValue(),
                crystalDelay.getValue(),
                hitDelay.getValue()
        };
        // Default values
        toPlace = new structureTemp(0,0,null);
        isHole = true;
        hasMoved = broken = brokenCrystalBug = brokenRedstoneTorch = false;
        firstRun = true;
        slot_mat = new int[]{-1, -1, -1, -1, -1};
        stage = delayTimeTicks = stuck = 0;

        if (mc.player == null){
            disable();
            return;
        }

        if (chatMsg.getValue()){
            printChat("PistonCrystal turned ON!", false);
        }

        oldSlot = mc.player.inventory.currentItem;

    }

    public void onDisable(){
        if (mc.player == null){
            return;
        }

        if (chatMsg.getValue()){
            if (noMaterials){
                printChat("No Materials Detected... PistonCrystal turned OFF!", true);
            }else if (!isHole) {
                printChat("The enemy is not in a hole... PistonCrystal turned OFF!", true);
            }else if(!enoughSpace) {
                printChat("Not enough space... PistonCrystal turned OFF!", true);
            }else if(hasMoved) {
                printChat("He moved away from the hole... PistonCrystal turned OFF!", true);
            }
            else {
                printChat("PystonCrystal turned OFF!", true);
            }
        }

        if (isSneaking){
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        if (oldSlot != mc.player.inventory.currentItem && oldSlot != -1){
            mc.player.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }

        noMaterials = false;
        firstRun = true;
        AutoCrystal.stopAC = false;
    }

    public void onUpdate(){

        if (mc.player == null){
            disable();
            return;
        }

        if (firstRun){

            // All the setup
            closestTarget = findClosestTarget();

            if (closestTarget == null){
                return;
            }
            firstRun = false;
            if (getMaterialsSlot()) {
                // check if the enemy is in a hole
                if (is_in_hole()) {
                    // Get enemy coordinates
                    enemyCoords = new double[] {closestTarget.posX, closestTarget.posY, closestTarget.posZ};
                    // Start choosing where to place what
                    enoughSpace = createStructure();

                } else {
                    isHole = false;
                }
            }else noMaterials = true;


        }else {
            // Wait
            if (delayTimeTicks < delayTable[stage]){
                delayTimeTicks++;
                return;
            }
            else {
                delayTimeTicks = 0;
            }
        }
        /*
            This is how it works:
            a)
                First of all, it check if  we have all the supportive blocks + if we have arleady something.
                (i want it to prevent some bugs)
            If there is some missing one, it is going to place a block and it will finish.
            then, it place:
                b) Piston
                c) Crystal
                d) redstone torch
            then, it wait and
                e) break the crystal.
         */

        // If we have to left
        if (noMaterials || !isHole || !enoughSpace || hasMoved){
            disable();
            return;
        }

        // A)
        if (supportsBlocks()) {
            // B) Piston
            if (stage == 1) {
                BlockPos offsetPos = new BlockPos(toPlace.to_place.get(toPlace.supportBlock));
                BlockPos targetPos = new BlockPos(closestTarget.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
                placeBlock(targetPos, 1, 1, toPlace.offsetX, toPlace.offsetZ);
                stage++;
                // C) Crystal
            }else if(stage == 2) {
                // Check for the piston
                BlockPos offsetPosPist = new BlockPos(toPlace.to_place.get(toPlace.supportBlock));
                BlockPos targetPosPist = new BlockPos(closestTarget.getPositionVector()).add(offsetPosPist.getX(), offsetPosPist.getY(), offsetPosPist.getZ());
                if (!(get_block(targetPosPist.x, targetPosPist.y, targetPosPist.z) instanceof BlockPistonBase)) {
                    stage--;
                }else {
                    BlockPos offsetPos = new BlockPos(toPlace.to_place.get(toPlace.supportBlock + 1));
                    BlockPos targetPos = new BlockPos(closestTarget.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
                    if (placeBlock(targetPos, 2, 1, toPlace.offsetX, toPlace.offsetZ))
                        stage++;
                }
                // D) Redstone
            }else if(stage == 3) {
                /// TODO check if this code is broken
                // Check if the crystal has been placed
                for(Entity t : mc.world.loadedEntityList) {
                    if (t instanceof EntityEnderCrystal
                            && (int) t.posX == (int) toPlace.to_place.get(toPlace.supportBlock + 1).x &&
                            (int) t.posZ == (int) toPlace.to_place.get(toPlace.supportBlock + 1).z ) {
                        stage--;
                        break;
                    }
                }
                // If the crystal is ok, place
                if (stage == 3) {
                    BlockPos offsetPos = new BlockPos(toPlace.to_place.get(toPlace.supportBlock + 2));
                    BlockPos targetPos = new BlockPos(closestTarget.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
                    placeBlock(targetPos, 3, 1, toPlace.offsetX, toPlace.offsetZ);
                    stage++;
                }
                // E) Destroy
            }else if(stage == 4) {
                // Get the crystal
                Entity crystal = null;
                for(Entity t : mc.world.loadedEntityList) {
                    if (t instanceof EntityEnderCrystal
                        && (
                        (t.posX == (int) t.posX &&
                            ((int) ((int)(t.posX) - 0.1) == (int) closestTarget.posX || (int) ((int) (t.posX) + 0.1) == (int) closestTarget.posX) &&
                            (int) t.posZ == (int) closestTarget.posZ)
                        ||
                        (t.posZ == (int) t.posZ &&
                            ((int) ((int)(t.posZ) - 0.1) == (int) closestTarget.posZ || (int) ((int) (t.posZ) + 0.1) == (int) closestTarget.posZ) &&
                            (int) t.posX == (int) closestTarget.posX)
                    ))
                        crystal = t;
                }// 234.5, 84, 170
                if (confirmBreak.getValue() && broken && crystal == null) {
                    // Reset
                    stage = stuck = 0;
                    broken = false;
                }
                // If found
                if (crystal != null) {
                    breakCrystalPiston(crystal);
                    if (confirmBreak.getValue())
                        broken = true;
                    else
                        stage = stuck = 0;
                }else {
                    // If it stuck
                    if (++stuck >= stuckDetector.getValue()) {
                        /// Try to find the error
                        // First error: crystal not found
                        boolean found = false;
                        for(Entity t : mc.world.loadedEntityList) {
                            if (t instanceof EntityEnderCrystal
                                    && (int) t.posX == (int) toPlace.to_place.get(toPlace.supportBlock + 1).x &&
                                    (int) t.posZ == (int) toPlace.to_place.get(toPlace.supportBlock + 1).z ) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            //// The error is that the crystal has not been placed
                            /// Destroy the redstone torch
                            // If rotation
                            BlockPos offsetPosPist = new BlockPos(toPlace.to_place.get(toPlace.supportBlock + 2));
                            BlockPos pos = new BlockPos(closestTarget.getPositionVector()).add(offsetPosPist.getX(), offsetPosPist.getY(), offsetPosPist.getZ());

                            // Check if there is the redstone torch
                            if (confirmBreak.getValue() && brokenRedstoneTorch && get_block(pos.x, pos.y, pos.z) instanceof BlockAir) {
                                stage = 1;
                                brokenRedstoneTorch = false;
                            } else {

                                EnumFacing side = BlockUtil.getPlaceableSide(pos);
                                if (side != null) {
                                    if (rotate.getValue()) {
                                        BlockPos neighbour = pos.offset(side);
                                        EnumFacing opposite = side.getOpposite();
                                        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 1, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
                                        BlockUtil.faceVectorPacketInstant(hitVec);

                                    }
                                    // -235 84 171
                                    mc.player.swingArm(EnumHand.MAIN_HAND);
                                    mc.player.connection.sendPacket(new CPacketPlayerDigging(
                                            CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side
                                    ));
                                    mc.player.connection.sendPacket(new CPacketPlayerDigging(
                                            CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side
                                    ));
                                    /// Restart from the crystal
                                    if (confirmBreak.getValue())
                                        brokenRedstoneTorch = true;
                                    else
                                        stage = 1;
                                    printChat("Stuck detected: crystal not placed", true);
                                }
                            }

                        }else {
                            // Try to see if the crystal, somehow, is in the piston extended thing
                            boolean ext = false;
                            for(Entity t : mc.world.loadedEntityList) {
                                if (t instanceof EntityEnderCrystal
                                        && (int) t.posX == (int) toPlace.to_place.get(toPlace.supportBlock + 1).x &&
                                        (int) t.posZ == (int) toPlace.to_place.get(toPlace.supportBlock + 1).z ) {
                                    ext = true;
                                    break;
                                }
                            }
                            if (confirmBreak.getValue() && brokenCrystalBug && !ext) {
                                // Reset
                                stage = stuck = 0;
                                brokenCrystalBug = false;
                            }
                            if (ext) {
                                // Break the crystal
                                breakCrystalPiston(crystal);
                                if (confirmBreak.getValue())
                                    brokenCrystalBug = true;
                                else
                                    stage = stuck = 0;
                                printChat("Stuck detected: crystal is stuck in the moving piston", true);
                            }
                        }
                    }
                }
            }
        }

    }
    private void breakCrystalPiston (Entity crystal) {
        // If weaknes
        if (antiWeakness.getValue())
            mc.player.inventory.currentItem = slot_mat[4];
        // If rotate
        if (rotate.getValue()) {
            lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
        }
        /// Break type
        // Swing
        if (breakType.getValue().equals("Swing")) {
            breakCrystal(crystal);
            // Packet
        }else if(breakType.getValue().equals("Packet")) {
            mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        // Rotate
        if (rotate.getValue())
            resetRotation();
    }

    private boolean supportsBlocks() {
        // We create a boolean value because, in case we go over the blocksPerTick limit, we have to stop
        boolean done = true;
        // Index
        int i = 0;
        // N^ blocks placed
        int blockPlaced = 0;
        if (toPlace.to_place.size() > 0 && toPlace.supportBlock > 0)
            do {
                // Get position where we are going to check
                BlockPos offsetPos = new BlockPos(toPlace.to_place.get(i));
                BlockPos targetPos = new BlockPos(closestTarget.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

                // I wont check if there is an entity in the block i'm trying to place. I dont think it's necessary
                if (placeBlock(targetPos, 0, -1, 0, 0)) {
                    blockPlaced++;
                }

                // If we reached the limit
                if (blockPlaced == blocksPerTick.getValue()) {
                    return false;
                }
                // If we have reached the max supportBlock
                else if (++i >= toPlace.supportBlock) {
                    stage = stage == 0 ? 1 : stage;
                    return true;
                }

            }while (true);
        else {
            stage = stage == 0 ? 1 : stage;
            return true;
        }
    }

    private boolean placeBlock(BlockPos pos, int step, int direction, double offsetX, double offsetZ){
        // Get the block
        Block block = mc.world.getBlockState(pos).getBlock();
        // Get all sides
        EnumFacing side = BlockUtil.getPlaceableSide(pos);



        // If there is a solid block
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)){
            return false;
        }
        // If we cannot find any side
        if (side == null){
            return false;
        }

        // Get position of the side
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();


        // If that block can be clicked
        if (!BlockUtil.canBeClicked(neighbour)){
            return false;
        }

        // Get the position where we are gonna click
        Vec3d hitVec = new Vec3d(neighbour).add(0.5 + offsetX, 1, 0.5 + offsetZ).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        /*
			// I use this as a remind to which index refers to what
			0 => obsidian
			1 => piston
			2 => Crystals
			3 => redstone
			4 => sword
		 */
        // Get what slot we are going to select
        // If it's not empty
        if (mc.player.inventory.getStackInSlot(slot_mat[step]) != ItemStack.EMPTY) {
            // Is it is correct
            if (mc.player.inventory.currentItem != slot_mat[step]) {
                // Change the hand's item
                mc.player.inventory.currentItem = slot_mat[step] == 11 ? mc.player.inventory.currentItem : slot_mat[step];
            }
        }else return false;

        // Why?
        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)){
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        // Stop CA
        boolean stoppedAC = false;

        if (ModuleManager.isModuleEnabled("AutoCrystalGS")){
            AutoCrystal.stopAC = true;
            stoppedAC = true;
        }

        // For the rotation
        if (rotate.getValue() || step == 1){
            Vec3d positionHit = hitVec;
            // if rotate
            if (!rotate.getValue() && step == 1) {
                positionHit = new Vec3d(mc.player.posX + offsetX, mc.player.posY, mc.player.posZ + offsetZ);
            }
            BlockUtil.faceVectorPacketInstant(positionHit);
        }

        EnumHand handSwing = EnumHand.MAIN_HAND;
        if (slot_mat[step] == 11)
            handSwing = EnumHand.OFF_HAND;

        // Place the block
        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, handSwing);
        mc.player.swingArm(handSwing);

        // Re-Active ca
        if (stoppedAC){
            AutoCrystal.stopAC = false;
            stoppedAC = false;
        }


        return true;
    }

    private EntityPlayer findClosestTarget(){
        List<EntityPlayer> playerList = mc.world.playerEntities;

        EntityPlayer closestTarget_test = null;

        for (EntityPlayer entityPlayer : playerList){
            if (entityPlayer == mc.player){
                continue;
            }
            if (Friends.isFriend(entityPlayer.getName())){
                continue;
            }
            if (entityPlayer.isDead) {
                continue;
            }

            if (closestTarget == null && mc.player.getDistance(entityPlayer) <= enemyRange.getValue()){
                closestTarget_test = entityPlayer;
                continue;
            }
            if (closestTarget != null && mc.player.getDistance(entityPlayer) <= enemyRange.getValue() && mc.player.getDistance(entityPlayer) < mc.player.getDistance(closestTarget)){
                closestTarget_test = entityPlayer;
            }
        }
        return closestTarget_test;
    }


    private void printChat(String text, Boolean error) {
        MessageBus.sendClientPrefixMessage((error ? ColorMain.getDisabledColor() : ColorMain.getEnabledColor()) + text);
    }

    private boolean getMaterialsSlot() {
		/*
			// I use this as a remind to which index refers to what
			0 => obsidian
			1 => piston
			2 => Crystals
			3 => redstone
			4 => sword
		 */

        if (mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal) {
            slot_mat[2] = 11;
        }

        // Iterate for all the inventory
        for(int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            // If there is no block
            if (stack == ItemStack.EMPTY){
                continue;
            }
            // If endCrystal
            if (stack.getItem() instanceof ItemEndCrystal) {
                slot_mat[2] = i;
                // if sword
            }else if (antiWeakness.getValue() && stack.getItem() instanceof ItemSword) {
                slot_mat[4] = i;
            }else
            if (stack.getItem() instanceof ItemBlock){

                // If yes, get the block
                Block block = ((ItemBlock) stack.getItem()).getBlock();

                // Obsidian
                if (block instanceof BlockObsidian) {
                    slot_mat[0] = i;
                } else
                    // PistonBlock
                    if (block instanceof BlockPistonBase) {
                        slot_mat[1] = i;
                    } else
                        // RedstoneTorch / RedstoneBlock
                        if (block instanceof BlockRedstoneTorch || block.translationKey.equals("blockRedstone")) {
                            slot_mat[3] = i;
                        }
            }
        }
        // Count what we found
        int count = 0;
        for(int val : slot_mat) {
            if (val != -1)
                count++;
        }

        // If we have everything we need, return true
        return count == 4 + (antiWeakness.getValue() ? 1 : 0);

    }

    private boolean is_in_hole() {
        sur_block = new Double[][] {
                {closestTarget.posX + 1, closestTarget.posY, closestTarget.posZ},
                {closestTarget.posX - 1, closestTarget.posY, closestTarget.posZ},
                {closestTarget.posX, closestTarget.posY, closestTarget.posZ + 1},
                {closestTarget.posX, closestTarget.posY, closestTarget.posZ - 1}
        };

        enemyCoords = new double[] {
                closestTarget.posX,
                closestTarget.posY,
                closestTarget.posZ
        };
        // Check if the guy is in a hole
        return !(get_block(sur_block[0][0], sur_block[0][1], sur_block[0][2]) instanceof BlockAir) &&
                !(get_block(sur_block[1][0], sur_block[1][1], sur_block[1][2]) instanceof BlockAir) &&
                !(get_block(sur_block[2][0], sur_block[2][1], sur_block[2][2]) instanceof BlockAir) &&
                !(get_block(sur_block[3][0], sur_block[3][1], sur_block[3][2]) instanceof BlockAir);
    }

    static class structureTemp {
        public double distance;
        public int supportBlock;
        public List<Vec3d> to_place;
        public int direction;
        public float offsetX;
        public float offsetZ;

        public structureTemp(double distance, int supportBlock, List<Vec3d> to_place) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = -1;
        }

        public void replaceValues(double distance, int supportBlock, List<Vec3d> to_place, int direction, float offsetX, float offsetZ) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = direction;
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
        }
    }

    private boolean createStructure() {
        /// Get in what block the client is going to tower
        // Calculate for each blocks the distance and find the min
        structureTemp addedStructure = new structureTemp(Double.MAX_VALUE, 0, null);
        double distance_now;
        int i = 0;
        // Our coordinates
        int[] meCord = new int[] {(int) mc.player.posX,(int) mc.player.posY,(int) mc.player.posZ};

        ArrayList<Double> ignoreList = new ArrayList<>();

        // Iterate for every blocks around, find the closest
        for(Double[] cord_b : sur_block) {
            /// Check if there is enough space
            // Cord block we are checking
            double[] crystalCords = {cord_b[0], cord_b[1] + 1, cord_b[2]};
            BlockPos positionCrystal = new BlockPos(crystalCords[0], crystalCords[1], crystalCords[2]);
            // Check if we are enough near him
            if ((distance_now = mc.player.getDistance(crystalCords[0], crystalCords[1], crystalCords[2])) < addedStructure.distance) {
                // if there is enough space (3 in total: 1 for the crystal, 1 for the piston and 1 for the redstoneTorch)
                if (positionCrystal.y != meCord[1] || /* we have to check the y level. if it's the same, we have to check */
                        (meCord[0] != positionCrystal.x || Math.abs(meCord[2] - positionCrystal.z) > 3 && /* if we are at the same x/z level. If yes*/
                                meCord[2] != positionCrystal.z || Math.abs(meCord[0] - positionCrystal.x) > 3) ) { /* check if there is enough space */
                    // Up to 1
                    cord_b[1] += 1;
                    // Check for the position of the crystal (it must be air)
                    if (get_block(crystalCords[0], crystalCords[1], crystalCords[2]) instanceof BlockAir) {
                        /// if yes, lets check for the piston
                        // Get the block
                        double[] pistonCord = {crystalCords[0] + disp_surblock[i][0], crystalCords[1], crystalCords[2] + disp_surblock[i][2]};
                        // Get coordinates of the piston
                        Block blockPiston = get_block(pistonCord[0], pistonCord[1], pistonCord[2]);
                        // Check if it's possible to place a block and if someone is in that block
                        if ((blockPiston instanceof BlockAir || blockPiston instanceof BlockPistonBase) && someoneInCoords(pistonCord[0], pistonCord[1], pistonCord[2])){

                            // |I decided to separate join and enter because, else, it would be hard for fixing in case of errors|

                            // if the position is right
                            boolean join =
                                    !rotate.getValue() || ((int) pistonCord[0] == meCord[0] ?
                                            ((closestTarget.posZ > mc.player.posZ) != (closestTarget.posZ > pistonCord[2])
                                                    || (Math.abs((int) closestTarget.posZ - (int) mc.player.posZ)) == 1) :
                                            (int) pistonCord[2] != meCord[2] || (((closestTarget.posX > mc.player.posX) != (closestTarget.posX > pistonCord[0])
                                                    || (Math.abs((int) closestTarget.posX - (int) mc.player.posX)) == 1) && (!((Math.abs((int) closestTarget.posX - (int) mc.player.posX)) > 1))));
                            // Extended version :
                            /*
                            boolean join = false;
                            // If rotate
                            if (rotate.getValue()) {
                                // If same X
                                if ((int) pistonCord[0] == meCord[0]) {
                                    // If we are not in the same quarter ot the distance is 1
                                    if ((closestTarget.posZ > mc.player.posZ) != (closestTarget.posZ > pistonCord[2]) || (Math.abs((int) closestTarget.posZ - (int) mc.player.posZ)) == 1)
                                        join = true;
                                }else
                                // If same z
                                if ((int) pistonCord[2] == meCord[2]) {
                                    // If we are not in the same quarter or the distance is 1
                                    if ((closestTarget.posX > mc.player.posX) != (closestTarget.posX > pistonCord[0]) || (Math.abs((int) closestTarget.posX - (int) mc.player.posX)) == 1)
                                        // I dunno why but i need this, else in some points it wont work
                                        if (!((Math.abs((int) closestTarget.posX - (int) mc.player.posX)) > 1))
                                            join = true;
                                }else join = true;
                            }else join = true;
                            */


                            if (join) {
                                // Check if the distance + position

                                boolean enter = (!rotate.getValue() || (
                                        (meCord[0] == (int) closestTarget.posX || meCord[2] == (int) closestTarget.posZ) ?
                                                (mc.player.getDistance(crystalCords[0], crystalCords[1], crystalCords[2]) <= 3.5 || (meCord[0] == (int) crystalCords[0] || meCord[2] == (int) crystalCords[2])) :
                                                (!((meCord[0] == (int) pistonCord[0] && (Math.abs((int) closestTarget.posZ - (int) mc.player.posZ)) != 1)) || meCord[2] == (int) pistonCord[2] && (Math.abs((int) closestTarget.posZ - (int) mc.player.posZ)) != 1)));

                                // Extended version


                                // If rotate, remove the first two near
                                /*
                                boolean enter = false;
                                if (!rotate.getValue())
                                    enter = true;
                                else if ((meCord[0] == (int) closestTarget.posX || meCord[2] == (int) closestTarget.posZ)) {
                                    if (mc.player.getDistance(crystalCords[0], crystalCords[1], crystalCords[2]) <= 2.8)
                                        enter = true;
                                    else if (meCord[0] == (int) crystalCords[0])
                                        if (closestTarget.posX > pistonCord[0] == closestTarget.posX > meCord[0] && (int) pistonCord[0] >= meCord[0])
                                            enter = true;
                                    else if (meCord[2] == (int) crystalCords[2])
                                            if (closestTarget.posZ > pistonCord[2] == closestTarget.posZ > meCord[2] && (int) pistonCord[2] >= meCord[2])
                                                enter = true;
                                } else if (!((meCord[0] == (int) pistonCord[0] && (Math.abs((int) closestTarget.posZ - (int) mc.player.posZ)) != 1)) || meCord[2] == (int) pistonCord[2] && (Math.abs((int) closestTarget.posZ - (int) mc.player.posZ)) != 1)
                                    enter = true;
                                */
                                if (enter) {

                                    // Check if there is enough space for the redstone torch
                                    int[] poss = null;
                                    for (int[] possibilites : disp_surblock) {
                                        // Check if there is a block and if we are not checking the crystal
                                        double[] coordinatesTemp = {cord_b[0] + disp_surblock[i][0] + possibilites[0], cord_b[1], cord_b[2] + disp_surblock[i][2] + possibilites[2]};
                                        /* Get all values for the torch */
                                        // Torch
                                        int[] torchCoords = {(int) coordinatesTemp[0], (int) coordinatesTemp[1], (int) coordinatesTemp[2]};
                                        // Crystal
                                        int[] crystalCoords = {(int) crystalCords[0], (int) crystalCords[1], (int) crystalCords[2]};

                                        if (get_block(coordinatesTemp[0], coordinatesTemp[1], coordinatesTemp[2]) instanceof BlockAir
                                                /* Check if the space is avaible */
                                                && !(torchCoords[0] == crystalCoords[0] && torchCoords[1] == crystalCoords[1] && crystalCoords[2] == torchCoords[2])
                                                /* Check if there is someone */
                                                && someoneInCoords(coordinatesTemp[0], coordinatesTemp[1], coordinatesTemp[2])) {
                                            // We can exit
                                            poss = possibilites;
                                            break;
                                        }
                                    }
                                    if (poss != null) {
                                        /// Calculate the structure
                                        // Variables
                                        List<Vec3d> toPlaceTemp = new ArrayList<Vec3d>();
                                        int supportBlock = 0;

                                        /// First of all, lets check for the support's blocks
                                        // Check for the piston If under there is nothing
                                        if (get_block(cord_b[0] + disp_surblock[i][0], cord_b[1] - 1, cord_b[2] + disp_surblock[i][2]) instanceof BlockAir) {
                                            // Add a block
                                            toPlaceTemp.add(new Vec3d(disp_surblock[i][0] * 2, disp_surblock[i][1], disp_surblock[i][2] * 2));
                                            supportBlock++;
                                        }
                                        // Check for the redstone torch If under there is nothing
                                        if (get_block(cord_b[0] + disp_surblock[i][0] + poss[0], cord_b[1] - 1, cord_b[2] + disp_surblock[i][2] + poss[2]) instanceof BlockAir) {
                                            // Add a block
                                            toPlaceTemp.add(new Vec3d(disp_surblock[i][0] * 2 + poss[0], disp_surblock[i][1], disp_surblock[i][2] * 2 + poss[2]));
                                            supportBlock++;
                                        }

                                        // Add the piston
                                        toPlaceTemp.add(new Vec3d(disp_surblock[i][0] * 2, disp_surblock[i][1] + 1, disp_surblock[i][2] * 2));

                                        // Add the crystal
                                        toPlaceTemp.add(new Vec3d(disp_surblock[i][0], disp_surblock[i][1] + 1, disp_surblock[i][2]));

                                        // Add the redstoneTorch
                                        toPlaceTemp.add(new Vec3d(disp_surblock[i][0] * 2 + poss[0], disp_surblock[i][1] + 1, disp_surblock[i][2] * 2 + poss[2]));
                                        float offsetX, offsetZ ;
                                        /// Calculate the offset
                                        // If horrizontaly
                                        if (disp_surblock[i][0] != 0) {
                                            offsetX = rotate.getValue() ? disp_surblock[i][0] / 2f : disp_surblock[i][0];
                                            // Check which is better for distance
                                            if (rotate.getValue()) {
                                                if (mc.player.getDistanceSq(pistonCord[0], pistonCord[1], pistonCord[2] + 0.5) > mc.player.getDistanceSq(pistonCord[0], pistonCord[1], pistonCord[2] - 0.5))
                                                    offsetZ = -0.5f;
                                                else
                                                    offsetZ = 0.5f;
                                            }else offsetZ = disp_surblock[i][2];
                                            // If vertically
                                        }else {
                                            offsetZ = rotate.getValue() ? disp_surblock[i][2] / 2f : disp_surblock[i][2];
                                            // Check which is better for distance
                                            if (rotate.getValue()) {
                                                if (mc.player.getDistanceSq(pistonCord[0] + 0.5, pistonCord[1], pistonCord[2]) > mc.player.getDistanceSq(pistonCord[0] - 0.5, pistonCord[1], pistonCord[2]))
                                                    offsetX = -0.5f;
                                                else
                                                    offsetX = 0.5f;
                                            }else offsetX = disp_surblock[i][0];
                                        }

                                        // Replace
                                        addedStructure.replaceValues(distance_now, supportBlock, toPlaceTemp, -1, offsetX, offsetZ);
                                    }
                                }
                            }

                        }

                    }

                }

            }
            // Incr i
            i++;
        }
        // We need this for see if we are going to find at list 1 spot for placing
        // If we found at list 1 value
        if (addedStructure.to_place != null) {
            // Check if we have to block the guy
            if (blockPlayer.getValue()) {
                // Get the values
                Vec3d valuesStart = addedStructure.to_place.get(addedStructure.supportBlock + 1);
                // Get the opposit
                int[] valueBegin = new int[] {(int) -valuesStart.x, (int) valuesStart.y, (int) -valuesStart.z};
                // Add
                addedStructure.to_place.add(0, new Vec3d(0, 2, 0));
                addedStructure.to_place.add(0, new Vec3d(valueBegin[0], valueBegin[1] + 1, valueBegin[2]));
                addedStructure.to_place.add(0, new Vec3d(valueBegin[0], valueBegin[1], valueBegin[2]));
                addedStructure.supportBlock += 3;
            }
            // Add to the global value
            toPlace = addedStructure;
            return true;
        }

        return false;
    }

    private boolean someoneInCoords(double x, double y, double z) {
        int xCheck = (int) x;
        int yCheck = (int) y;
        int zCheck = (int) z;
        // Get player's list
        List<EntityPlayer> playerList = mc.world.playerEntities;
        // Iterate
        for(EntityPlayer player : playerList) {
            // If the coordinates are the same (the y can be yCheck -+ 1 )
            if ((int) player.posX == xCheck && (int) player.posZ == zCheck &&
                    ((int) player.posY >= yCheck - 1 && (int) player.posY <= yCheck + 1))
                return false;
        }

        return true;
    }

    private Block get_block(double x, double y, double z) {
        return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }


    /// AutoCrystal break things ///
    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }
    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;

        double len = Math.sqrt(dirx*dirx + diry*diry + dirz*dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;

        yaw += 90f;

        return new double[]{yaw,pitch};
    }
    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;
    private static void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }
    private void breakCrystal(Entity crystal) {
        mc.playerController.attackEntity(mc.player, crystal);
        mc.player.swingArm(EnumHand.MAIN_HAND);
    }
    @EventHandler
    private final Listener<PacketEvent.Send> packetSendListener = new Listener<>(event -> {
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer) {
            if (isSpoofingAngles) {
                ((CPacketPlayer) packet).yaw = (float) yaw;
                ((CPacketPlayer) packet).pitch = (float) pitch;
            }
        }
    });
    private static void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

}
