package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author TechAle on 12/16/20
 * Ported and modified from Surround.java
 */
/*
    Now AutoAnvil is going to stop himself if the player place a block above him
 */

public class AutoAnvil extends Module {

    public AutoAnvil() {
        super("AutoAnvil", Category.Combat);
    }
    
    Setting.Mode    anvilMode,
                    target,
                    anvilPlace;
    Setting.Double  enemyRange,
                    decrease;
    Setting.Boolean rotate,
                    antiCrystal,
                    fastAnvil,
                    offHandObby,
                    chatMsg;
    Setting.Integer tickDelay,
                    blocksPerTick,
                    hDistance,
                    minH,
                    failStop;


    public void setup() {
        ArrayList<String> anvilTypesList = new ArrayList<>();
        anvilTypesList.add("Pick");
        anvilTypesList.add("Feet");
        anvilTypesList.add("None");
        ArrayList<String> targetChoose = new ArrayList<>();
        targetChoose.add("Nearest");
        targetChoose.add("Looking");
        ArrayList<String> anvilPlaceTypes = new ArrayList<>();
        anvilPlaceTypes.add("single");
        anvilPlaceTypes.add("double");
        anvilPlaceTypes.add("full");

        anvilMode = registerMode("Mode", anvilTypesList, "Pick");
        target = registerMode("Target", targetChoose, "Nearest");
        anvilPlace = registerMode("Anvil Place", anvilPlaceTypes, "single");
        antiCrystal = registerBoolean("Anti Crystal", false);
        fastAnvil = registerBoolean("Fast Anvil", true);
        offHandObby = registerBoolean("Off Hand Obby", false);
        rotate = registerBoolean("Rotate", true);
        enemyRange = registerDouble("Range",5.9, 0, 6);
        decrease = registerDouble("Decrease",2, 0, 6);
        tickDelay = registerInteger("Tick Delay", 5, 0, 10);
        blocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 8);
        hDistance = registerInteger("H Distance", 7, 1, 10);
        minH = registerInteger("Min H", 3, 1, 10);
        failStop = registerInteger("Fail Stop", 2, 1, 10);
        chatMsg = registerBoolean("Chat Msgs", true);
    }

    private boolean isSneaking = false,
                    firstRun = false,
                    noMaterials = false,
                    hasMoved = false,
                    isHole = true,
                    enoughSpace = true,
                    blockUp = false;
    private int oldSlot = -1,
                noKick,
                anvilBlock;
    private ArrayList<Integer> anvilsPositions = new ArrayList<>();
    private int[] slot_mat = {-1, -1, -1, -1};
    private double[] enemyCoords;
    Double[][] sur_block;
    int[][] model = new int[][] {
            {1,1,0},
            {-1,1,0},
            {0,1,1},
            {0,1,-1}
    };

    private int blocksPlaced = 0;
    private int delayTimeTicks = 0;
    private int offsetSteps = 0;
    private boolean pick_d = false;

    private EntityPlayer aimTarget;

    public void onEnable() {
        // Setup
        if (anvilMode.getValue().equalsIgnoreCase("Pick")) {
            pick_d = true;
        }
        blocksPlaced = 0;
        isHole = true;
        hasMoved = blockUp = false;
        firstRun = true;
        slot_mat = new int[]{-1, -1, -1, -1};
        to_place = new ArrayList<>();

        if (mc.player == null) {
            disable();
            return;
        }

        if (chatMsg.getValue()) {
            PistonCrystal.printChat("AutoAnvil turned ON!", false);
        }

        oldSlot = mc.player.inventory.currentItem;
    }

    public void onDisable() {
        if (mc.player == null) {
            return;
        }

        if (chatMsg.getValue()) {
            if (noMaterials) {
                PistonCrystal.printChat("No Materials Detected... AutoAnvil turned OFF!", true);
            }
            else if (!isHole) {
                PistonCrystal.printChat("Enemy is not in a hole... AutoAnvil turned OFF!", true);
            }
            else if(!enoughSpace) {
                PistonCrystal.printChat("Not enough space... AutoAnvil turned OFF!", true);
            }
            else if(hasMoved) {
                PistonCrystal.printChat("Enemy moved away from the hole... AutoAnvil turned OFF!", true);
            }
            else if(blockUp) {
                PistonCrystal.printChat("Enemy head blocked.. AutoAnvil turned OFF!", true);
            }
            else {
                PistonCrystal.printChat("AutoAnvil turned OFF!", true);
            }
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        if (oldSlot != mc.player.inventory.currentItem && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }

        noMaterials = false;
        firstRun = true;
        AutoCrystalGS.stopAC = false;
        // If offHand was enabled
        if (slot_mat[0] == -2) {
            OffHand.removeObsidian();
        }
    }

    public void onUpdate() {
        if (mc.player == null) {
            disable();
            return;
        }

        if (firstRun) {

            // All the setup
            if (target.getValue().equals("Nearest"))
                aimTarget = PlayerUtil.findClosestTarget(enemyRange.getValue(), aimTarget);
            else if(target.getValue().equals("Looking"))
                aimTarget = PlayerUtil.findLookingPlayer(enemyRange.getValue());

            if (aimTarget == null) {
                return;
            }
            firstRun = false;
            if (getMaterialsSlot()) {
                // check if the enemy is in a hole
                if (is_in_hole()) {
                    // Get enemy coordinates
                    enemyCoords = new double[] {aimTarget.posX, aimTarget.posY, aimTarget.posZ};
                    // Start choosing where to place what
                    enoughSpace = createStructure();

                }
                else {
                    isHole = false;
                }
            }
            else noMaterials = true;


        }
        else {
            // Wait
            if (delayTimeTicks < tickDelay.getValue()) {
                delayTimeTicks++;
                return;
            }
            else {
                delayTimeTicks = 0;

                // Check if he has moved away
                if ((int) enemyCoords[0] != (int) aimTarget.posX || (int) enemyCoords[2] != (int) aimTarget.posZ)
                    hasMoved = true;

                // Check a block on the enemy's head
                if (!(BlockUtil.getBlock(enemyCoords[0], enemyCoords[1] + 2, enemyCoords[2]) instanceof BlockAir || BlockUtil.getBlock(enemyCoords[0], enemyCoords[1] + 2, enemyCoords[2]) instanceof BlockAnvil)) {
                    blockUp = true;
                }


            }
        }

        blocksPlaced = 0;

        // If we have to left
        if (noMaterials || !isHole || !enoughSpace || hasMoved || blockUp) {
            disable();
            return;
        }

        anvilsPositions = new ArrayList<>();
        // Lets get every anvils
        for (Entity everyEntity : mc.world.loadedEntityList) {
            if (everyEntity instanceof EntityFallingBlock) {
                anvilsPositions.add((int) everyEntity.posY);
                anvilsPositions.add((int) everyEntity.posY + 1);
            }
        }

        noKick = 0;
        while (blocksPlaced <= blocksPerTick.getValue()) {

            // Max of blocks we have to place
            int maxSteps;
            maxSteps = AutoAnvil.to_place.size();

            // If we are at the end
            if (offsetSteps >= maxSteps) {
                offsetSteps = 0;
                break;
            }

            // Get position
            BlockPos offsetPos = new BlockPos(to_place.get(offsetSteps));
            BlockPos targetPos = new BlockPos(enemyCoords[0], enemyCoords[1], enemyCoords[2]).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

            boolean tryPlacing = true;

            // If there is an entity
            if(offsetSteps > 0 && offsetSteps < AutoAnvil.to_place.size() - 1)
                for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))) {
                    if (entity instanceof EntityPlayer) {
                        tryPlacing = false;
                        break;
                    }
                }

            if (tryPlacing && placeBlock(targetPos, offsetSteps)) {
                blocksPlaced++;
            }

            offsetSteps++;
            // Why?
            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }
            if (noKick == failStop.getValue()) {
                break;
            }
        }


    }

    private boolean placeBlock(BlockPos pos, int step) {
        // Get the block
        Block block = mc.world.getBlockState(pos).getBlock();
        // Get all sides
        EnumFacing side = BlockUtil.getPlaceableSide(pos);
        // If it is a ghostblock
        if (step == to_place.size() - 1 && block instanceof BlockAnvil && side != null) {
            // UnGlitch it with a left click
            //mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
            noKick++;
        }
        // If there is a solid block
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        // If we cannot find any side
        if (side == null) {
            return false;
        }

        // Get position of the side
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        // If that block can be clicked
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }

        // Get the position where we are gonna click
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        /*
			// I use this as a remind to which index refers to what
			-2 => obsidian in offHand
			0 => obsidian
			1 => anvil
			2 => pressure plate / button
			3 => pick
		 */
        int utilSlot;

        EnumHand handSwing = EnumHand.MAIN_HAND;

        // Get what slot we are going to select
        utilSlot =
                (step == 0 && (anvilMode.getValue().equalsIgnoreCase("feet")))
                        ? 2 :
                        (step >= to_place.size() - anvilBlock) ? 1 : 0;

        if (step == 1 && anvilsPositions.contains(pos.y))
            return false;

        // If it's step of the obsidian and we have offHandMode
        if (offHandObby.getValue() && OffHand.isActive() && slot_mat[utilSlot] == -2) {
            // Check if we have the obby in our offhand
            if (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian) {
                // We can continue
                handSwing = EnumHand.OFF_HAND;
            }
            // Else we cannot
            else
                return false;
        }else
        // else check the normal If it's not empty
        if (mc.player.inventory.getStackInSlot(slot_mat[utilSlot]) != ItemStack.EMPTY) {
            // Is it is correct
            if (mc.player.inventory.currentItem != slot_mat[utilSlot]) {
                // Change the hand's item
                mc.player.inventory.currentItem = slot_mat[utilSlot];
            }
        }
        else return false;

        // Why?
        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        // Stop CA
        boolean stoppedAC = false;

        if (ModuleManager.isModuleEnabled(AutoCrystalGS.class)) {
            AutoCrystalGS.stopAC = true;
            stoppedAC = true;
        }

        // For the rotation
        if (rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }

        // FastAnvil
        int bef = mc.rightClickDelayTimer;
        // If we are at our last
        if (step == to_place.size() - 1) {

            // Get the name of the player
            EntityPlayer found = getPlayerFromName(aimTarget.gameProfile.getName());
            // If that player moved
            if (found == null || (int) found.posX != (int) enemyCoords[0] || (int) found.posZ != (int) enemyCoords[2]) {
                hasMoved = true;
                return false;
            }
            // If fastAnvil
            if (fastAnvil.getValue())
                // FastPlace
                mc.rightClickDelayTimer = 0;
        }

        // Place the block
        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, handSwing);
        mc.player.swingArm(handSwing);

        // Disable fastplace
        if (fastAnvil.getValue() && step == to_place.size() - 1) {
            mc.rightClickDelayTimer = bef;
        }

        // Re-Active ca
        if (stoppedAC) {
            AutoCrystalGS.stopAC = false;
            stoppedAC = false;
        }

        // Breaking the anvil
        if (pick_d && step == to_place.size() - 1) {
            EnumFacing prova = BlockUtil.getPlaceableSide(new BlockPos(enemyCoords[0], enemyCoords[1], enemyCoords[2]));
            if (prova != null) {
                mc.player.inventory.currentItem = slot_mat[3];
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(enemyCoords[0], enemyCoords[1], enemyCoords[2]), prova
                ));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(enemyCoords[0], enemyCoords[1], enemyCoords[2]), prova
                ));
            }
        }

        return true;
    }

    private EntityPlayer getPlayerFromName(String name) {
        // Iterate for every player
        List<EntityPlayer> playerList = mc.world.playerEntities;
        for (EntityPlayer entityPlayer : playerList) {
            // same
            if (entityPlayer.gameProfile.getName().equals(name)) {
                return entityPlayer;
            }

        }
        return null;
    }

    private static ArrayList<Vec3d> to_place = new ArrayList<Vec3d>();

    private boolean getMaterialsSlot() {
		/*
			// I use this as a remind to which index refers to what
			-2 => obsidian in offHand
			0 => obsidian
			1 => anvil
			2 => pressure plate
			3 => pick
		 */
        boolean feet = false;
        boolean pick = false;

        // If we have to search also for a button/pressure plate
        if (anvilMode.getValue().equalsIgnoreCase("Feet")) {
            feet = true;
        }
        if (anvilMode.getValue().equalsIgnoreCase("Pick")) {
            pick = true;
        }

        // Iterate for all the inventory
        for(int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            // If there is no block
            if (stack == ItemStack.EMPTY) {
                continue;
            }
            if (pick && stack.getItem() instanceof ItemPickaxe) {
                slot_mat[3] = i;
            }
            if (stack.getItem() instanceof ItemBlock) {

                // If yes, get the block
                Block block = ((ItemBlock) stack.getItem()).getBlock();

                // Obsidian
                if (block instanceof BlockObsidian) {
                    slot_mat[0] = i;
                }
                else
                    // Anvil
                    if (block instanceof BlockAnvil) {
                        slot_mat[1] = i;
                    }
                    else
                        // Button / Pressure Plate
                        if (feet && (block instanceof BlockPressurePlate || block instanceof BlockButton)) {
                            slot_mat[2] = i;
                        }
            }
        }
        // offHand obsidian
        if (offHandObby.getValue() && OffHand.isActive()) {
            slot_mat[0] = -2;
            OffHand.requestObsidian();
        }
        // Count what we found
        int count = 0;
        for(int val : slot_mat) {
            if (val != -1)
                count++;
        }

        // If we have everything we need, return true
        return count - (feet || pick ? 1 : 0) == 2;

    }

    private boolean is_in_hole() {
        sur_block = new Double[][] {
                {aimTarget.posX + 1, aimTarget.posY, aimTarget.posZ},
                {aimTarget.posX - 1, aimTarget.posY, aimTarget.posZ},
                {aimTarget.posX, aimTarget.posY, aimTarget.posZ + 1},
                {aimTarget.posX, aimTarget.posY, aimTarget.posZ - 1}
        };

        enemyCoords = new double[] {
                aimTarget.posX,
                aimTarget.posY,
                aimTarget.posZ
        };
        // Check if the guy is in a hole
        return HoleUtil.isHole(EntityUtil.getPosition(aimTarget), true, true).getType() != HoleUtil.HoleType.NONE;
    }

    private boolean createStructure() {
        // Add the button
        if (anvilMode.getValue().equalsIgnoreCase("feet")){
            to_place.add(new Vec3d(0,0,0));
        }
        /// Add all around the enemy for preventing for him to step outside
        to_place.add(new Vec3d(1, 1, 0));
        to_place.add(new Vec3d(-1, 1,0));
        to_place.add(new Vec3d(0, 1,1));
        to_place.add(new Vec3d(0, 1,-1));
        to_place.add(new Vec3d(1, 2, 0));
        to_place.add(new Vec3d(-1, 2,0));
        to_place.add(new Vec3d(0, 2,1));
        to_place.add(new Vec3d(0, 2,-1));

        /// Decrease hDistance
        // Get the distance from the enemy
        int hDistanceMod = hDistance.getValue();
        double distEnemy = mc.player.getDistance(aimTarget);
        while (distEnemy > decrease.getValue()) {
            hDistanceMod -= 1;
            distEnemy -= decrease.getValue();
        }
        int add = (int) (mc.player.posY - aimTarget.posY);
        if (add > 1)
            add = 2;
        // Different Y
        hDistanceMod += mc.player.posY - aimTarget.posY;

        /// Get in what block the client is going to tower
        // Calculate for each blocks the distance and find the min
        double 	min_found = Double.MAX_VALUE,
                distance_now;
        double[] coords_blocks_min  = new double[] {-1, -1, -1},
                coords_blocks_temp;
        int cor = -1;
        int i = 0;
        // Iterate for every blocks around, find the closest
        for(Double[] cord_b : sur_block) {
            coords_blocks_temp = new double[] {cord_b[0], cord_b[1], cord_b[2]};
            if ((distance_now = mc.player.getDistanceSq(new BlockPos(cord_b[0], cord_b[1], cord_b[2]))) < min_found) {
                min_found = distance_now;
                cor = i;
            }
            i++;
        }
        // We need this for see if we are going to find at list 1 spot for placing
        boolean possible = false;
        int incr = 1;
        // Continue by creating the tower
        do {
            // Search the avaible space
            if ( BlockUtil.getBlock(enemyCoords[0], enemyCoords[1] + incr, enemyCoords[2]) instanceof BlockAir && incr < hDistanceMod) {
                // Lets check for the block near
                if (!antiCrystal.getValue())
                    to_place.add(new Vec3d(model[cor][0], model[cor][1] + incr, model[cor][2]));
                else {
                    for (int ij = 0; ij < 4; ij++) {
                        to_place.add(new Vec3d(model[ij][0], model[ij][1] + incr, model[ij][2]));
                    }
                }
                incr++;
            }else {
                // If there is a block where we want to place the anvil
                if (!(BlockUtil.getBlock(enemyCoords[0], enemyCoords[1] + incr, enemyCoords[2]) instanceof BlockAir)) {
                    // Go down of 1
                    incr--;
                }
                break;
            }

        }while(true);
        if (incr >=  minH.getValue())
            possible = true;


        double yRef = to_place.get(to_place.size() - 1).y;
        anvilBlock = 0;
        switch (anvilPlace.getValue()) {
            case "full":
                to_place.add(new Vec3d(0, 3, 0));
                anvilBlock++;
            case "double":
                to_place.add(new Vec3d(0, 2, 0));
                anvilBlock++;
            case "single":
                to_place.add(new Vec3d(0, yRef, 0));
                anvilBlock++;
                break;
        }

        return possible;
    }
}