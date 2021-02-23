package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.DestroyBlockEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.combat.CrystalUtil;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Objects;

import static com.gamesense.api.util.player.RotationUtil.ROTATION_UTIL;

public class CevBreaker extends Module {

    public CevBreaker() {
        super("CevBreaker", Category.Combat);
    }

    Setting.Mode breakCrystal,
                breakBlock,
            target;

    Setting.Double  enemyRange;

    Setting.Integer
            crystalDelay,
            blocksPerTick,
            hitDelay,
            midHitDelay,
            supDelay,
            pickSwitchTick,
            endDelay;
    Setting.Boolean rotate,
            confirmBreak,
            confirmPlace,
            antiWeakness,
            chatMsg,
            switchSword,
            fastPlace,
            predictBreak,
            placeCrystal,
            trapPlayer,
            antiStep;

    private boolean noMaterials = false,
            hasMoved = false,
            isSneaking = false,
            isHole = true,
            enoughSpace = true,
            broken,
            stoppedCa,
            deadPl,
            rotationPlayerMoved,
            prevBreak;

    private int oldSlot = -1,
            stage,
            delayTimeTicks,
            hitTryTick,
            tickPick;
    private final int[][] model = new int[][] {
            {1,1,0},
            {-1,1,0},
            {0,1,1},
            {0,1,-1}
    };

    private int[]   slot_mat,
            delayTable,
            enemyCoordsInt;

    private double[] enemyCoordsDouble;

    private structureTemp toPlace;


    Double[][] sur_block = new Double[4][3];

    private EntityPlayer aimTarget;

    @EventHandler
    private final Listener<DestroyBlockEvent> listener2 = new Listener<>(event -> {

        if (enemyCoordsInt != null && event.getBlockPos().x + (event.getBlockPos().x < 0 ? 1 : 0) == enemyCoordsInt[0] && event.getBlockPos().z + (event.getBlockPos().z < 0 ? 1 : 0) == enemyCoordsInt[2]) {
            destroyCrystalAlgo();
        }
    });

    // Setup the options of the gui
    public void setup(){
        ArrayList<String> breakCrystalList = new ArrayList<>();
        breakCrystalList.add("Vanilla");
        breakCrystalList.add("Packet");
        breakCrystalList.add("None");
        ArrayList<String> breakBlockList = new ArrayList<>();
        breakBlockList.add("Normal");
        breakBlockList.add("Packet");
        ArrayList<String> targetChoose = new ArrayList<>();
        targetChoose.add("Nearest");
        targetChoose.add("Looking");
        target = registerMode("Target", targetChoose, "Nearest");
        breakCrystal = registerMode("Break Crystal", breakCrystalList, "Packet");
        breakBlock = registerMode("Break Block", breakBlockList, "Packet");
        enemyRange = registerDouble("Range",4.9, 0, 6);
        supDelay = registerInteger("Support Delay", 1, 0, 4);
        crystalDelay = registerInteger("Crystal Delay", 2, 0, 20);
        blocksPerTick = registerInteger("Blocks Per Tick", 4, 2, 6);
        hitDelay = registerInteger("Hit Delay", 2, 0, 20);
        midHitDelay = registerInteger("Mid Hit Delay", 1, 0, 5);
        endDelay = registerInteger("End Delay", 1, 0, 4);
        pickSwitchTick = registerInteger("Pick Switch Tick", 100, 0, 500);
        rotate = registerBoolean("Rotate", false);
        confirmBreak = registerBoolean("No Glitch Break", true);
        confirmPlace = registerBoolean("No Glitch Place", true);
        antiWeakness = registerBoolean("Anti Weakness", false);
        switchSword = registerBoolean("Switch Sword", false);
        predictBreak = registerBoolean("Predict Break", false);
        fastPlace = registerBoolean("Fast Place", false);
        trapPlayer = registerBoolean("Trap Player", false);
        antiStep = registerBoolean("Anti Step", false);
        placeCrystal = registerBoolean("Place Crystal", true);
        chatMsg = registerBoolean("Chat Msgs", true);
    }

    // Everytime you enable
    public void onEnable() {
        if (predictBreak.getValue())
            GameSense.EVENT_BUS.subscribe(this);

        ROTATION_UTIL.onEnable();
        // Init values
        initValues();
        // Get Target
        if (getAimTarget())
            return;
        // Make checks
        playerChecks();
    }

    // Get target function
    private boolean getAimTarget() {
        /// Get aimTarget
        // If nearest, get it

        if (target.getValue().equals("Nearest"))
            aimTarget = PlayerUtil.findClosestTarget(enemyRange.getValue(), aimTarget);
            // if looking
        else
            aimTarget = PlayerUtil.findLookingPlayer(enemyRange.getValue());

        // If we didnt found a target
        if (aimTarget == null || !target.getValue().equals("Looking")){
            // if it's not looking and we didnt found a target
            if (!target.getValue().equals("Looking") && aimTarget == null)
                disable();
            // If not found a target
            if (aimTarget == null)
                return true;
        }
        return false;
    }

    // Make some checks for startup
    private void playerChecks() {
        // Get all the materials
        if (getMaterialsSlot()) {
            // check if the enemy is in a hole
            if (is_in_hole()) {
                // Get enemy coordinates
                enemyCoordsDouble = new double[] {aimTarget.posX, aimTarget.posY, aimTarget.posZ};
                enemyCoordsInt = new int[] {(int) enemyCoordsDouble[0], (int) enemyCoordsDouble[1], (int) enemyCoordsDouble[2]};
                // Start choosing where to place what
                enoughSpace = createStructure();
                // Is not in a hoke
            } else {
                isHole = false;
            }
            // No materials
        }else noMaterials = true;
    }

    // Init some values
    private void initValues() {
        // Reset aimtarget
        aimTarget = null;
        // Create new delay table
        delayTable = new int[] {
                supDelay.getValue(),
                crystalDelay.getValue(),
                hitDelay.getValue(),
                endDelay.getValue()
        };
        // Default values reset
        toPlace = new structureTemp(0, 0, new ArrayList<>());
        isHole = true;
        hasMoved = rotationPlayerMoved = deadPl = broken = false;
        slot_mat = new int[]{-1, -1, -1, -1};
        stage = delayTimeTicks = 0;

        if (mc.player == null){
            disable();
            return;
        }

        if (chatMsg.getValue()){
            PistonCrystal.printChat("CevBreaker turned ON!", false);
        }

        oldSlot = mc.player.inventory.currentItem;

        stoppedCa = false;

        if (ModuleManager.isModuleEnabled(AutoCrystalGS.class)){
            AutoCrystalGS.stopAC = true;
            stoppedCa = true;
        }

    }

    // On disable of the module
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe(this);
        ROTATION_UTIL.onDisable();
        if (mc.player == null){
            return;
        }
        // If output
        if (chatMsg.getValue()){
            String output = "";
            String materialsNeeded = "";
            // No target found
            if (aimTarget == null) {
                output = "No target found...";
            }else
                // H distance not avaible
                if (noMaterials){
                    output = "No Materials Detected...";
                    materialsNeeded = getMissingMaterials();
                    // No Hole
                }else if (!isHole) {
                    output = "The enemy is not in a hole...";
                    // No Space
                }else if(!enoughSpace) {
                    output = "Not enough space...";
                    // Has Moved
                }else if(hasMoved) {
                    output = "Out of range...";
                }else if(deadPl) {
                    output = "Enemy is dead, gg! ";
                }
            // Output in chat
            PistonCrystal.printChat(output + "CevBreaker turned OFF!", true);
            if (!materialsNeeded.equals(""))
                PistonCrystal.printChat("Materials missing:" + materialsNeeded, true);

            // Re-Active ca
            if (stoppedCa){
                AutoCrystalGS.stopAC = false;
                stoppedCa = false;
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
        AutoCrystalGS.stopAC = false;
    }

    private String getMissingMaterials() {
        /*
			// I use this as a remind to which index refers to what
			0 => obsidian
			1 => Crystal
			2 => Pick
			3 => Sword
		 */
        StringBuilder output = new StringBuilder();

        if (slot_mat[0] == -1)
            output.append(" Obsidian");
        if (slot_mat[1] == -1)
            output.append(" Crystal");
        if ((antiWeakness.getValue() || switchSword.getValue()) && slot_mat[3] == -1)
            output.append(" Sword");
        if (slot_mat[2] == -1)
            output.append(" Pick");

        return output.toString();
    }

    // Every updates
    public void onUpdate() {
        // If no mc.player
        if (mc.player == null || mc.player.isDead){
            disable();
            return;
        }



        // Wait
        if (delayTimeTicks < delayTable[stage]){
            delayTimeTicks++;
            return;
        }
        // If the delay is finished
        else {
            delayTimeTicks = 0;
        }

        ROTATION_UTIL.shouldSpoofAngles(true);

        // Check if something is not ok
        if (enemyCoordsDouble == null || aimTarget == null) {
            if (aimTarget == null) {
                aimTarget = PlayerUtil.findLookingPlayer(enemyRange.getValue());
                if (aimTarget != null) {
                    playerChecks();
                }
            }else
                checkVariable();
            return;
        }

        // Check if he is dead
        if (aimTarget.isDead) {
            deadPl = true;
        }

        // Check if he is not in the hole
        if ((int) aimTarget.posX != (int) enemyCoordsDouble[0] || (int) aimTarget.posZ != (int) enemyCoordsDouble[2])
            hasMoved = true;

        // If we have to left
        if (checkVariable()){
            return;
        }

        /*
            This is how it works:
            a) Place obsidian
            b) Place Crystal
            c) Break
            and then, in C, check for destroying the crystal
         */

        /// Start Placing ///
        // A) Lets place all the supports blocks
        if (placeSupport()) {
            switch (stage) {
                // Place obsidian
                case 1:
                    placeBlockThings(stage);
                    if (fastPlace.getValue()) {
                        placeCrystal();
                    }
                    prevBreak = false;
                    tickPick = 0;
                    break;

                // Place crystal
                case 2:
                    // Confirm Place
                    if (confirmPlace.getValue())
                        if (!(BlockUtil.getBlock(compactBlockPos(0)) instanceof BlockObsidian)) {
                            stage--;
                            return;
                        }

                    placeCrystal();
                    break;

                // Break
                case 3:
                    // Confirm Place
                    if (confirmPlace.getValue())
                        if (getCrystal() == null) {
                            stage--;
                            return;
                        }

                    // Switch to pick / sword
                    int switchValue = 3;
                    if (!switchSword.getValue() || (tickPick == pickSwitchTick.getValue() || tickPick++ == 0))
                        switchValue = 2;

                if (mc.player.inventory.currentItem != slot_mat[switchValue]) {
                    mc.player.inventory.currentItem = slot_mat[switchValue];
                }

                    // Get block
                    BlockPos obbyBreak = new BlockPos(enemyCoordsDouble[0], enemyCoordsInt[1] + 2, enemyCoordsDouble[2]);
                    // If we have not break it yet
                    if (BlockUtil.getBlock(obbyBreak) instanceof BlockObsidian) {
                        // Get side
                        EnumFacing sideBreak = BlockUtil.getPlaceableSide(obbyBreak);
                        // If it's != null
                        if (sideBreak != null) {
                            // Switch break values
                            switch (breakBlock.getValue()) {
                                // Normal Packet
                                case "Packet":
                                    if (!prevBreak) {

                                        mc.player.swingArm(EnumHand.MAIN_HAND);
                                        mc.player.connection.sendPacket(new CPacketPlayerDigging(
                                                CPacketPlayerDigging.Action.START_DESTROY_BLOCK, obbyBreak, sideBreak
                                        ));
                                        mc.player.connection.sendPacket(new CPacketPlayerDigging(
                                                CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, obbyBreak, sideBreak
                                        ));

                                        prevBreak = true;
                                    }
                                    break;
                                // Vanilla
                                case "Normal":
                                    mc.player.swingArm(EnumHand.MAIN_HAND);
                                    mc.playerController.onPlayerDamageBlock(obbyBreak, sideBreak);
                                    break;
                            }
                        }
                    } else {
                        // Destroy crystal
                        destroyCrystalAlgo();
                    }

                    break;
            }
        }

    }

    private void placeCrystal() {
        // Check pistonPlace if confirmPlace
        placeBlockThings(stage);
    }

    private Entity getCrystal() {
        // Check if the crystal exist
        for(Entity t : mc.world.loadedEntityList) {
            // If it's a crystal
            if (t instanceof EntityEnderCrystal) {
                /// Check if the crystal is in the enemy
                // One coordinate is going to be always the same, the other is going to change (because we are pushing it)
                // We have to check if that coordinate is the same as the enemy. Ww add "crystalDeltaBreak" so we can break the crystal before
                // It go to the hole, for a better speed (we find the frame perfect for every servers)
                if (  (int) t.posX == enemyCoordsInt[0] && (int) t.posZ == enemyCoordsInt[2] && t.posY - enemyCoordsInt[1] == 3  )
                    // If found, yoink
                    return t;
            }
        }
        return null;
    }

    // Algo for destroying the crystal
    public void destroyCrystalAlgo() {
        // Get the crystal
        Entity crystal = getCrystal();
        // If we have confirmBreak, we have found 0 crystal and we broke a crystal before
        if (confirmBreak.getValue() && broken && crystal == null) {
            /// That means the crystal was broken 100%
            // Reset
            stage = 0;
            broken = false;

        }
        // If found the crystal
        if (crystal != null) {
            // Break it
            breakCrystalPiston(crystal);
            // If we have to check
            if (confirmBreak.getValue())
                broken = true;
                // If not, left
            else {
                stage = 0;
            }
        }else stage = 0;
    }

    // Actual break crystal
    private void breakCrystalPiston (Entity crystal) {
        // HitDelay
        if (hitTryTick++ < midHitDelay.getValue())
            return;
        else
            hitTryTick = 0;
        // If weaknes
        if (antiWeakness.getValue())
            mc.player.inventory.currentItem = slot_mat[3];
        // If rotate
        if (rotate.getValue()) {
            ROTATION_UTIL.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
        }
        /// Break type
        // Swing
        switch (breakCrystal.getValue()) {
            case "Vanilla":
                CrystalUtil.breakCrystal(crystal);
                // Packet
                break;
            case "Packet":
                try {
                    mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                } catch (NullPointerException e) {

                }
                break;
            case "None":

                break;
        }
        // Rotate
        if (rotate.getValue())
            ROTATION_UTIL.resetRotation();
    }

    // Place the supports blocks
    private boolean placeSupport() {
        // N^ blocks checked
        int checksDone = 0;
        // N^ blocks placed
        int blockDone = 0;
        // If we have to place
        if (toPlace.supportBlock > 0) {
            // Lets iterate and check
            // Lets finish
            do {
                BlockPos targetPos = getTargetPos(checksDone);

                // Try to place the block
                if (placeBlock(targetPos, 0)) {
                    // If we reached the limit
                    if (++blockDone == blocksPerTick.getValue())
                        // Return false
                        return false;
                }

                // If we reached the limit, exit
            } while (++checksDone != toPlace.supportBlock);
        }
        stage = stage == 0 ? 1 : stage;
        return true;
    }

    // Place a block
    private boolean placeBlock(BlockPos pos, int step){
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
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        /*
			// I use this as a remind to which index refers to what
			0 => obsidian
			1 => Crystal
			2 => Pick
			3 => Sword
		 */
        // Get what slot we are going to select
        // If it's not empty
        if (slot_mat[step] == 11 || mc.player.inventory.getStackInSlot(slot_mat[step]) != ItemStack.EMPTY) {
            // Is it is correct
            if (mc.player.inventory.currentItem != slot_mat[step]) {
                // Change the hand's item (è qui l'errore)
                mc.player.inventory.currentItem = slot_mat[step] == 11 ? mc.player.inventory.currentItem : slot_mat[step];
            }
        } else {
            noMaterials = true;
            return false;
        }


        // Why?
        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)){
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        // For the rotation
        if (rotate.getValue()){
            // Look
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }
        // If we are placing with the main hand
        EnumHand handSwing = EnumHand.MAIN_HAND;
        // If we are placing with the offhand
        if (slot_mat[step] == 11)
            handSwing = EnumHand.OFF_HAND;

        // Place the block
        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, handSwing);
        mc.player.swingArm(handSwing);

        return true;
    }

    // Given a step, place the block
    public void placeBlockThings(int step) {
        if (step != 1 || placeCrystal.getValue() ) {
            step--;
            // Get absolute position
            BlockPos targetPos = compactBlockPos(step);
            // Place 93 4 -29
            placeBlock(targetPos, step);
        }
        // Next step
        stage++;
    }

    // Given a step, return the absolute block position
    public BlockPos compactBlockPos(int step) {
        // Get enemy's relative position of the block
        BlockPos offsetPos = new BlockPos(toPlace.to_place.get(toPlace.supportBlock + step));
        // Get absolute position and return
        return new BlockPos(enemyCoordsDouble[0] + offsetPos.getX(), enemyCoordsDouble[1] + offsetPos.getY(), enemyCoordsDouble[2] + offsetPos.getZ());

    }

    // Given a index of a block, get the target position (this is used for support blocks)
    private BlockPos getTargetPos(int idx) {
        BlockPos offsetPos = new BlockPos(toPlace.to_place.get(idx));
        return new BlockPos(enemyCoordsDouble[0] + offsetPos.getX(), enemyCoordsDouble[1] + offsetPos.getY(), enemyCoordsDouble[2] + offsetPos.getZ());
    }

    // Check if we have to disable
    private boolean checkVariable() {
        // If something went wrong
        if (noMaterials || !isHole || !enoughSpace || hasMoved || deadPl || rotationPlayerMoved){
            disable();
            return true;
        }
        return false;
    }

    // Class for the structure
    private static class structureTemp {
        public double distance;
        public int supportBlock;
        public ArrayList<Vec3d> to_place;
        public int direction;

        public structureTemp(double distance, int supportBlock, ArrayList<Vec3d> to_place) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = -1;
        }

    }

    // Create the skeleton of the structure
    private boolean createStructure() {

        if ((Objects.requireNonNull(BlockUtil.getBlock(enemyCoordsDouble[0], enemyCoordsDouble[1] + 2, enemyCoordsDouble[2]).getRegistryName()).toString().toLowerCase().contains("bedrock"))
            || !(BlockUtil.getBlock(enemyCoordsDouble[0], enemyCoordsDouble[1] + 3, enemyCoordsDouble[2]) instanceof BlockAir)
            || !(BlockUtil.getBlock(enemyCoordsDouble[0], enemyCoordsDouble[1] + 4, enemyCoordsDouble[2]) instanceof BlockAir))
            return false;

        // Iterate for every blocks around, find the closest
        double distance_now;
        double min_found = Double.MAX_VALUE;
        int cor = 0;
        int i = 0;
        for(Double[] cord_b : sur_block) {
            if ((distance_now = mc.player.getDistanceSq(new BlockPos(cord_b[0], cord_b[1], cord_b[2]))) < min_found) {
                min_found = distance_now;
                cor = i;
            }
            i++;
        }

        int bias = enemyCoordsInt[0] == (int) mc.player.posX || enemyCoordsInt[2] == (int) mc.player.posZ ? -1 : 1;

        // Create support blocks
        toPlace.to_place.add(new Vec3d(model[cor][0] * bias, 1, model[cor][2] * bias));
        toPlace.to_place.add(new Vec3d(model[cor][0] * bias, 2, model[cor][2] * bias));
        toPlace.supportBlock = 2;

        // Create antitrap + antiStep
        if (trapPlayer.getValue() || antiStep.getValue()) {
            for(int high = 1; high < 3; high++ ) {
                if (high != 2 || antiStep.getValue())
                    for (int[] modelBas : model) {
                        Vec3d toAdd = new Vec3d(modelBas[0], high, modelBas[2]);
                        if (!toPlace.to_place.contains(toAdd)) {
                            toPlace.to_place.add(toAdd);
                            toPlace.supportBlock++;
                        }
                    }
            }
        }


        // Create structure
        toPlace.to_place.add(new Vec3d(0, 2, 0));
        toPlace.to_place.add(new Vec3d(0, 3, 0));
        return true;
    }

    // Get all the materials
    private boolean getMaterialsSlot() {
		/*
			// I use this as a remind to which index refers to what
			0 => obsidian
			1 => Crystal
			2 => Pick
			3 => Sword
		 */

        if (mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal) {
            slot_mat[1] = 11;
        }
        // Iterate for all the inventory
        for(int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            // If there is no block
            if (stack == ItemStack.EMPTY){
                continue;
            }
            // If endCrystal
            if (slot_mat[1] == -1 && stack.getItem() instanceof ItemEndCrystal) {
                slot_mat[1] = i;
                // If sword
            }else if ((antiWeakness.getValue() || switchSword.getValue()) && stack.getItem() instanceof ItemSword) {
                slot_mat[3] = i;
            }else
                // If Pick
                if (stack.getItem() instanceof ItemPickaxe) {
                    slot_mat[2] = i;
                }
            if (stack.getItem() instanceof ItemBlock){

                // If yes, get the block
                Block block = ((ItemBlock) stack.getItem()).getBlock();

                // Obsidian
                if (block instanceof BlockObsidian) {
                    slot_mat[0] = i;
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
        return count >= 3 + ((antiWeakness.getValue() || switchSword.getValue()) ? 1 : 0) ;

    }

    private boolean is_in_hole() {
        sur_block = new Double[][] {
                {aimTarget.posX + 1, aimTarget.posY, aimTarget.posZ},
                {aimTarget.posX - 1, aimTarget.posY, aimTarget.posZ},
                {aimTarget.posX, aimTarget.posY, aimTarget.posZ + 1},
                {aimTarget.posX, aimTarget.posY, aimTarget.posZ - 1}
        };

        // Check if the guy is in a hole
        return HoleUtil.isHole(EntityUtil.getPosition(aimTarget), true, true).getType() != HoleUtil.HoleType.NONE;
    }

}