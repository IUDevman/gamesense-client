package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.friends.Friends;
import com.gamesense.api.util.world.BlockUtils;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author TechAle on (remember me to insert the date)
 * Ported and modified from AutoAnvil.java
 */

public class pistonCrystal extends Module {
    public pistonCrystal(){
        super("pistonCrystal", Category.Combat);
    }
    
    Setting.Double enemyRange;
    Setting.Boolean rotate;
    Setting.Boolean chatMsg;
    Setting.Boolean blockPlayer;
    Setting.Integer tickDelay;

    public void setup(){

        rotate = registerBoolean("Rotate", "Rotate", true);
        blockPlayer = registerBoolean("blockPlayer", "blockPlayer", true);
        enemyRange = registerDouble("Range", "Range",5.9, 0, 6);
        tickDelay = registerInteger("Tick Delay", "TickDelay", 5, 0, 10);
        chatMsg = registerBoolean("Chat Msgs", "ChatMsgs", true);
    }

    private boolean isSneaking = false;
    private boolean firstRun = false;
    private boolean noMaterials = false;
    private boolean hasMoved = false;
    private boolean isHole = true;
    private boolean enoughSpace = true;
    private int oldSlot = -1;
    private int[] slot_mat = {-1, -1, -1, -1, -1};
    private double[] enemyCoords;
    private structureTemp toPlace;
    int[][] disp_surblock = {
            {1,0,0},
            {-1,0,0},
            {0,0,1},
            {0,0,-1}
    };
    Double[][] sur_block;


    private EntityPlayer closestTarget;

    public void onEnable(){
        toPlace = new structureTemp(0,0,null);
        isHole = true;
        hasMoved = false;
        firstRun = true;
        slot_mat = new int[]{-1, -1, -1, -1};

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


        }


        // If we have to left
        if (noMaterials || !isHole || !enoughSpace || hasMoved){
            disable();
            return;
        }


        // Get position
        /*
        BlockPos offsetPos = new BlockPos(to_place.get(offsetSteps));
        BlockPos targetPos = new BlockPos(closestTarget.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

        boolean tryPlacing = true;

        // If there is an entity
        if(offsetSteps > 0 && offsetSteps < pistonCrystal.to_place.size() - 1)
            for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))){
                if (entity instanceof EntityPlayer){
                    tryPlacing = false;
                    break;
                }
            }
        */
        // Why?
        if (isSneaking){
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }




    }

    private boolean placeBlock(BlockPos pos, int step){
        // Get the block
        Block block = mc.world.getBlockState(pos).getBlock();
        // Get all sides
        EnumFacing side = BlockUtils.getPlaceableSide(pos);
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
        if (!BlockUtils.canBeClicked(neighbour)){
            return false;
        }

        // Get the position where we are gonna click
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        /*
			// I use this as a remind to which index refers to what
			0 => obsidian
			1 => anvil
			2 => pressure plate / button
			3 => pick
		 */
        // Get what slot we are going to select
        int utilSlot = 0;
        // If it's not empty
        if (mc.player.inventory.getStackInSlot(slot_mat[utilSlot]) != ItemStack.EMPTY) {
            // Is it is correct
            if (mc.player.inventory.currentItem != slot_mat[utilSlot]) {
                // Change the hand's item
                mc.player.inventory.currentItem = slot_mat[utilSlot];
            }
        }else return false;

        // Why?
        if (!isSneaking && BlockUtils.blackList.contains(neighbourBlock) || BlockUtils.shulkerList.contains(neighbourBlock)){
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
        if (rotate.getValue()){
            BlockUtils.faceVectorPacketInstant(hitVec);
        }

        // Place the block
        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);

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
			1 => anvil
			2 => pressure plate
			3 => pick
			4 => sword
		 */

        if (mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal) {
            slot_mat[3] = 11;
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
                slot_mat[3] = i;
            // if sword
            }else if (stack.getItem() instanceof ItemSword) {
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
                    slot_mat[2] = i;
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
        return count == 4;

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

        public structureTemp(double distance, int supportBlock, List<Vec3d> to_place) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
        }

        public void replaceValues(double distance, int supportBlock, List<Vec3d> to_place) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
        }
    }

    private boolean createStructure() {
        /// Get in what block the client is going to tower
        // Calculate for each blocks the distance and find the min
        structureTemp strutturaAggiunta = new structureTemp(Double.MAX_VALUE, 0, null);
        double distance_now;
        int i = 0;
        // Our coordinates
        int[] meCord = new int[] {(int) mc.player.posX,(int) mc.player.posY,(int) mc.player.posZ};
        // Iterate for every blocks around, find the closest
        for(Double[] cord_b : sur_block) {
            /// Check if there is enough space
            // Cord block we are checking
            double[] crystalCords = {cord_b[0], cord_b[1], cord_b[2]};
            BlockPos positionCrystal = new BlockPos(crystalCords[0], crystalCords[1], crystalCords[2]);
            // Check if we are enough near him
            if ((distance_now = mc.player.getDistanceSq(positionCrystal)) < strutturaAggiunta.distance) {
                // if there is enough space (3 in total: 1 for the crystal, 1 for the piston and 1 for the redstoneTorch)
                if (positionCrystal.y != meCord[1] || /* we have to check the y level. if it's the same, we have to check */
                    (meCord[0] != positionCrystal.x || Math.abs(meCord[2] - positionCrystal.z) > 3 && /* if we are at the same x/z level. If yes*/
                     meCord[2] != positionCrystal.z || Math.abs(meCord[0] - positionCrystal.x) > 3) ) { /* check if there is enough space */
                    // Up to 1
                    cord_b[1] += 1;
                    crystalCords[1] += 1;
                    positionCrystal.y += 1;
                    // Check for the position of the crystal (it must be air)
                    if (get_block(crystalCords[0], crystalCords[1], crystalCords[2]) instanceof BlockAir) {

                        /// if yes, lets check for the piston
                        // Get the block
                        double[] pistonCord = {crystalCords[0] + disp_surblock[i][0], crystalCords[1], crystalCords[2] + disp_surblock[i][2]};
                        Block blockPiston = get_block(pistonCord[0], pistonCord[1], pistonCord[2]);
                        // Check if it's possible to place a block and if someone is in that block
                        if (blockPiston instanceof BlockAir && someoneInCoords(pistonCord[0], pistonCord[1], pistonCord[2])) {
                            // Get coordinates of the piston
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
                                    break; /* 218, 86, 18 */
                                }
                            }
                            if (poss != null) {
                                /// Calculate the structure
                                // Variables
                                List<Vec3d> toPlaceTemp = new ArrayList<Vec3d>();
                                int supportBlock = 0;

                                /// First of all, lets check for the support's blocks
                                // Check for the piston If under there is nothing
                                if ( get_block(cord_b[0] + disp_surblock[i][0], cord_b[1] - 1, cord_b[2] + disp_surblock[i][2]) instanceof BlockAir) {
                                    // Add a block
                                    toPlaceTemp.add(new Vec3d(disp_surblock[i][0]*2, disp_surblock[i][1], disp_surblock[i][0]*2));
                                    supportBlock++;
                                }
                                // Check for the redstone torch If under there is nothing
                                if ( get_block(cord_b[0] + disp_surblock[i][0] + poss[0], cord_b[1] - 1, cord_b[2] + disp_surblock[i][2] + poss[2]) instanceof BlockAir) {
                                    // Add a block
                                    toPlaceTemp.add(new Vec3d(disp_surblock[i][0]*2 + poss[0], disp_surblock[i][1], disp_surblock[i][2]*2 + poss[2]));
                                    supportBlock++;
                                }

                                // Add the crystal
                                toPlaceTemp.add(new Vec3d(disp_surblock[i][0], disp_surblock[i][1] + 1, disp_surblock[i][2]));

                                // Add the piston
                                toPlaceTemp.add(new Vec3d(disp_surblock[i][0]*2, disp_surblock[i][1] + 1, disp_surblock[i][2]*2));

                                // Add the redstoneTorch
                                toPlaceTemp.add(new Vec3d(disp_surblock[i][0]*2 + poss[0], disp_surblock[i][1] + 1, disp_surblock[i][2]*2 + poss[2]));


                                // Replace
                                strutturaAggiunta.replaceValues(distance_now, supportBlock, toPlaceTemp);
                            // No place for a torch error (this is for me)
                            }
                        // No place for a piston error (this is for me)
                        }
                    // No place for a crystal error (this is for me)
                    }
                // No place for placing error (this is for me)
                }
            // Better distance error (this is for me)
            }
            i++;
        }
        // We need this for see if we are going to find at list 1 spot for placing
        // If we found at list 1 value
        if (strutturaAggiunta.to_place != null) {
            // Check if we have to block the guy
            if (blockPlayer.getValue()) {
                // Get the values
                Vec3d valuesStart = strutturaAggiunta.to_place.get(0);
                // Get the opposit
                int[] valueBegin = new int[] {(int) -valuesStart.x, (int) valuesStart.y, (int) -valuesStart.z};
                // Add
                strutturaAggiunta.to_place.add(0, new Vec3d(valueBegin[0], valueBegin[1] + 1, valueBegin[2]));
                strutturaAggiunta.to_place.add(0, new Vec3d(valueBegin[0], valueBegin[1] + 2, valueBegin[2]));
                strutturaAggiunta.to_place.add(0, new Vec3d(0, 4, 0));
                strutturaAggiunta.supportBlock += 3;
            }
            // Add to the global value
            toPlace = strutturaAggiunta;
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
}