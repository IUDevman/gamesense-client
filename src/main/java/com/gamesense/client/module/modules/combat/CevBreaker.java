package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.DestroyBlockEvent;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.client.GameSense;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.AutoGG;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.gamesense.api.util.player.SpoofRotationUtil.ROTATION_UTIL;

/**
 * @Author TechAle
 * Ported and modified from PistonCrystal
 */

@Module.Declaration(name = "CevBreaker", category = Category.Combat, priority = 999)
public class CevBreaker extends Module {

    ModeSetting target = registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
    ModeSetting breakCrystal = registerMode("Break Crystal", Arrays.asList("Vanilla", "Packet", "None"), "Packet");
    ModeSetting breakBlock = registerMode("Break Block", Arrays.asList("Normal", "Packet"), "Packet");
    DoubleSetting enemyRange = registerDouble("Range", 4.9, 0, 6);
    IntegerSetting preRotationDelay = registerInteger("Pre Rotation Delay", 0, 0, 20);
    IntegerSetting afterRotationDelay = registerInteger("After Rotation Delay", 0, 0, 20);
    IntegerSetting supDelay = registerInteger("Support Delay", 1, 0, 4);
    IntegerSetting crystalDelay = registerInteger("Crystal Delay", 2, 0, 20);
    IntegerSetting blocksPerTick = registerInteger("Blocks Per Tick", 4, 2, 6);
    IntegerSetting hitDelay = registerInteger("Hit Delay", 2, 0, 20);
    IntegerSetting midHitDelay = registerInteger("Mid Hit Delay", 1, 0, 5);
    IntegerSetting endDelay = registerInteger("End Delay", 1, 0, 4);
    IntegerSetting pickSwitchTick = registerInteger("Pick Switch Tick", 100, 0, 500);
    BooleanSetting rotate = registerBoolean("Rotate", false);
    BooleanSetting confirmBreak = registerBoolean("No Glitch Break", true);
    BooleanSetting confirmPlace = registerBoolean("No Glitch Place", true);
    BooleanSetting antiWeakness = registerBoolean("Anti Weakness", false);
    BooleanSetting switchSword = registerBoolean("Switch Sword", false);
    BooleanSetting predictBreak = registerBoolean("Predict Break", false);
    BooleanSetting fastPlace = registerBoolean("Fast Place", false);
    BooleanSetting fastBreak = registerBoolean("Fast Break", true);
    BooleanSetting trapPlayer = registerBoolean("Trap Player", false);
    BooleanSetting antiStep = registerBoolean("Anti Step", false);
    BooleanSetting placeCrystal = registerBoolean("Place Crystal", true);
    BooleanSetting preRotation = registerBoolean("Pre Rotation", false);
    BooleanSetting forceRotation = registerBoolean("Force Rotation", false);

    public static int cur_item = -1;
    public static boolean isActive = false;

    private boolean noMaterials = false,
            hasMoved = false,
            isSneaking = false,
            isHole = true,
            enoughSpace = true,
            broken,
            stoppedCa,
            deadPl,
            rotationPlayerMoved,
            prevBreak,
            preRotationBol;

    private int oldSlot = -1,
            stage,
            delayTimeTicks,
            hitTryTick,
            tickPick,
            afterRotationTick,
            preRotationTick;
    private final int[][] model = new int[][]{
            {1, 1, 0},
            {-1, 1, 0},
            {0, 1, 1},
            {0, 1, -1}
    };

    public static boolean isPossible = false;

    private int[] slot_mat,
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

    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener = new Listener<>(event -> {

        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                if ((int) packet.getX() == enemyCoordsInt[0] && (int) packet.getZ() == enemyCoordsInt[2])
                    stage = 1;
            }
        }
    });

    Vec3d lastHitVec;

    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<>(event -> {
        if (event.getPhase() != Phase.PRE || !rotate.getValue() || lastHitVec == null || !forceRotation.getValue()) return;
        Vec2f rotation = RotationUtil.getRotationTo(lastHitVec);
        PlayerPacket packet = new PlayerPacket(this, rotation);
        PlayerPacketManager.INSTANCE.addPacket(packet);
    });

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
        if (aimTarget == null || !target.getValue().equals("Looking")) {
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
                enemyCoordsDouble = new double[]{aimTarget.posX, aimTarget.posY, aimTarget.posZ};
                enemyCoordsInt = new int[]{(int) enemyCoordsDouble[0], (int) enemyCoordsDouble[1], (int) enemyCoordsDouble[2]};
                // Start choosing where to place what
                enoughSpace = createStructure();
                // Is not in a hoke
            } else {
                isHole = false;
            }
            // No materials
        } else noMaterials = true;
    }

    // Init some values
    private void initValues() {
        preRotationBol = false;
        afterRotationTick = preRotationTick = 0;
        isPossible = false;
        // Reset aimtarget
        aimTarget = null;
        lastHitVec = null;
        // Create new delay table
        delayTable = new int[]{
                supDelay.getValue(),
                crystalDelay.getValue(),
                hitDelay.getValue(),
                endDelay.getValue()
        };
        // Default values reset
        toPlace = new structureTemp(0, 0, new ArrayList<>());
        isHole = isActive = true;
        hasMoved = rotationPlayerMoved = deadPl = broken = false;
        slot_mat = new int[]{-1, -1, -1, -1};
        stage = delayTimeTicks = 0;

        if (mc.player == null) {
            disable();
            return;
        }

        oldSlot = mc.player.inventory.currentItem;

        stoppedCa = false;

        cur_item = -1;

        if (ModuleManager.isModuleEnabled(AutoCrystalGS.class)) {
            AutoCrystalGS.stopAC = true;
            stoppedCa = true;
        }

    }

    // On disable of the module
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe(this);
        ROTATION_UTIL.onDisable();
        if (mc.player == null) {
            return;
        }

            String output = "";
            String materialsNeeded = "";
            // No target found
            if (aimTarget == null) {
                output = "No target found...";
            } else
                // H distance not avaible
                if (noMaterials) {
                    output = "No Materials Detected...";
                    materialsNeeded = getMissingMaterials();
                    // No Hole
                } else if (!isHole) {
                    output = "The enemy is not in a hole...";
                    // No Space
                } else if (!enoughSpace) {
                    output = "Not enough space...";
                    // Has Moved
                } else if (hasMoved) {
                    output = "Out of range...";
                } else if (deadPl) {
                    output = "Enemy is dead, gg! ";
                }
            // Output in chat
            setDisabledMessage(output + "CevBreaker turned OFF!");
            if (!materialsNeeded.equals(""))
                setDisabledMessage("Materials missing:" + materialsNeeded);

        if (stoppedCa) {
            AutoCrystalGS.stopAC = false;
            stoppedCa = false;
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        if (oldSlot != mc.player.inventory.currentItem && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }

        noMaterials = isPossible = AutoCrystalGS.stopAC = isActive = false;
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
        if (mc.player == null || mc.player.isDead) {
            disable();
            return;
        }


        // Wait
        if (delayTimeTicks < delayTable[stage]) {
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

                    if (ModuleManager.isModuleEnabled(AutoGG.class)) {
                        AutoGG.INSTANCE.addTargetedPlayer(aimTarget.getName());
                    }
                }
            } else
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
        if (checkVariable()) {
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

                    if (getCrystal() != null) {
                        stage = 3;
                        return;
                    }

                    if (afterRotationTick != afterRotationDelay.getValue()) {
                        afterRotationTick++;
                        return;
                    }

                    if (preRotation.getValue() && !preRotationBol) {
                        placeBlockThings(stage, true, false);
                        if (preRotationTick == preRotationDelay.getValue()) {
                            preRotationBol = true;
                            preRotationTick = 0;
                        } else {
                            preRotationTick++;
                            break;
                        }
                    }

                    placeBlockThings(stage, false, false);
                    if (fastPlace.getValue()) {
                        placeCrystal(false);

                    }
                    prevBreak = false;
                    tickPick = 0;
                    break;

                // Place crystal
                case 2:
                    if (afterRotationTick != afterRotationDelay.getValue()) {
                        afterRotationTick++;
                        return;
                    }

                    if (preRotation.getValue() && !preRotationBol) {
                        placeCrystal(true);
                        if (preRotationTick == preRotationDelay.getValue()) {
                            preRotationBol = true;
                            preRotationTick = 0;
                        } else {
                            preRotationTick++;
                            break;
                        }
                    }
                    // Confirm Place
                    if (confirmPlace.getValue())
                        if (!(BlockUtil.getBlock(compactBlockPos(0)) instanceof BlockObsidian)) {
                            stage--;
                            return;
                        }

                    placeCrystal(false);

                    break;

                // Break
                case 3:

                    // Confirm Place
                    if (confirmPlace.getValue())
                        if (getCrystal() == null) {
                            stage = 0;
                            return;
                        }

                    if (forceRotation.getValue()) {
                        placeBlockThings(1, true, false);
                    }

                    // Switch to pick / sword
                    int switchValue = 3;
                    if (!switchSword.getValue() || (tickPick == pickSwitchTick.getValue() || tickPick++ == 0))
                        switchValue = 2;

                   switchPick(switchValue);

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

    private void switchPick(int switchValue) {
        if (cur_item != slot_mat[switchValue]) {
            if (slot_mat[switchValue] == -1) {
                noMaterials = true;
                return;
            }
            mc.player.connection.sendPacket(new CPacketHeldItemChange((cur_item = slot_mat[switchValue])));
            mc.player.inventory.currentItem = cur_item;
        }
    }

    private void placeCrystal(boolean onlyRotate) {
        // Check pistonPlace if confirmPlace
        placeBlockThings(stage, onlyRotate, true);
        // If fastBreak
        if (fastBreak.getValue() && !onlyRotate) {
            fastBreakFun();
        }
    }

    private void fastBreakFun() {
        switchPick(2);
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                new BlockPos(enemyCoordsInt[0], enemyCoordsInt[1] + 2, enemyCoordsInt[2]), EnumFacing.UP));
        isPossible = true;
    }

    private Entity getCrystal() {
        // Check if the crystal exist
        for (Entity t : mc.world.loadedEntityList) {
            // If it's a crystal
            if (t instanceof EntityEnderCrystal) {
                /// Check if the crystal is in the enemy
                // One coordinate is going to be always the same, the other is going to change (because we are pushing it)
                // We have to check if that coordinate is the same as the enemy. Ww add "crystalDeltaBreak" so we can break the crystal before
                // It go to the hole, for a better speed (we find the frame perfect for every servers)
                if ((int) t.posX == enemyCoordsInt[0] && (int) t.posZ == enemyCoordsInt[2] && t.posY - enemyCoordsInt[1] == 3)
                    // If found, yoink
                    return t;
            }
        }
        return null;
    }

    // Algo for destroying the crystal
    public void destroyCrystalAlgo() {
        isPossible = false;
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
        } else stage = 0;
    }

    // Actual break crystal
    private void breakCrystalPiston(Entity crystal) {
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
                if (BlockUtil.getBlock(targetPos) instanceof BlockAir) {
                    // Rotate
                    if (preRotation.getValue() && !preRotationBol) {
                        if (preRotationTick == 0)
                            placeBlock(targetPos, 0, true);
                        if (preRotationTick == preRotationDelay.getValue()) {
                            preRotationBol = true;
                            preRotationTick = 0;
                        } else {
                            preRotationTick++;
                            return false;
                        }

                    }

                    if (placeBlock(targetPos, 0, false)) {
                        preRotationBol = false;
                        // If we reached the limit
                        if (++blockDone == blocksPerTick.getValue())
                            // Return false
                            return false;
                    }
                }

                // If we reached the limit, exit
            } while (++checksDone != toPlace.supportBlock);
        }
        stage = stage == 0 ? 1 : stage;
        return true;
    }

    private boolean changeItem(int step) {
        /*
			// I use this as a remind to which index refers to what
			0 => obsidian
			1 => Crystal
			2 => Pick
			3 => Sword
		 */
        if (slot_mat[step] == 11 || mc.player.inventory.getStackInSlot(slot_mat[step]) != ItemStack.EMPTY) {
            // Is it is correct
            if (cur_item != slot_mat[step]) {
                if (slot_mat[step] == -1) {
                    noMaterials = true;
                    return true;
                }
                if (slot_mat[step] != 11) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange((cur_item = slot_mat[step])));
                    mc.player.inventory.currentItem = cur_item;
                }
            }
        } else {
            noMaterials = true;
            return true;
        }
        return false;
    }

    // Place a block
    private boolean placeBlock(BlockPos pos, int step, boolean onlyRotate) {

        if (changeItem(step))
            return false;

        if (onlyRotate) {
            EnumFacing side = BlockUtil.getPlaceableSide(pos);

            if (side == null) {
                return false;
            }

            BlockPos neighbour = pos.offset(side);
            EnumFacing opposite = side.getOpposite();

            if (!BlockUtil.canBeClicked(neighbour)) {
                return false;
            }
            double add = step == 1 && (int) mc.player.posY == enemyCoordsInt[1] ? -.5 : 0;
            lastHitVec = new Vec3d(neighbour).add(0.5, 0.5 + add, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
            return false;
        }
        // Get what slot we are going to select
        // If it's not empty

        // If we are placing with the main hand
        EnumHand handSwing = EnumHand.MAIN_HAND;
        // If we are placing with the offhand
        if (slot_mat[step] == 11)
            handSwing = EnumHand.OFF_HAND;

        PlacementUtil.place(pos, handSwing, rotate.getValue() && !forceRotation.getValue(), false);

        return true;
    }

    // Given a step, place the block
    public void placeBlockThings(int step, boolean onlyRotate, boolean isCrystal) {
        if (step != 1 || placeCrystal.getValue()) {
            step--;
            // Get absolute position
            BlockPos targetPos = compactBlockPos(step);
            // Place 93 4 -29
            if (!isCrystal)
                placeBlock(targetPos, step, onlyRotate);
            else {
                if (changeItem(step))
                    return;
                EnumHand handSwing = EnumHand.MAIN_HAND;
                if (slot_mat[step] == 11)
                    handSwing = EnumHand.OFF_HAND;
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(targetPos.add(.5, .5, .5), EnumFacing.getDirectionFromEntityLiving(targetPos, mc.player), handSwing, 0, 0, 0));
                mc.player.swingArm(handSwing);

            }

        }
        if (!onlyRotate) {
            // Next step
            stage++;
            afterRotationTick = 0;
            preRotationBol = false;
        }
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
        if (noMaterials || !isHole || !enoughSpace || hasMoved || deadPl || rotationPlayerMoved) {
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
        for (Double[] cord_b : sur_block) {
            if ((distance_now = mc.player.getDistanceSq(new BlockPos(cord_b[0], cord_b[1], cord_b[2]))) < min_found) {
                min_found = distance_now;
                cor = i;
            }
            i++;
        }

        // Create support blocks
        toPlace.to_place.add(new Vec3d(model[cor][0] * -1, 1, model[cor][2] * -1));
        toPlace.to_place.add(new Vec3d(model[cor][0] * -1, 2, model[cor][2] * -1));
        toPlace.supportBlock = 2;

        // Create antitrap + antiStep
        if (trapPlayer.getValue() || antiStep.getValue()) {
            for (int high = 1; high < 3; high++) {
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
        // Obsidian
        toPlace.to_place.add(new Vec3d(0, 2, 0));
        // Crystal
        toPlace.to_place.add(new Vec3d(0, 2, 0));
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
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            // If there is no block
            if (stack == ItemStack.EMPTY) {
                continue;
            }
            // If endCrystal
            if (slot_mat[1] == -1 && stack.getItem() instanceof ItemEndCrystal) {
                slot_mat[1] = i;
                // If sword
            } else if ((antiWeakness.getValue() || switchSword.getValue()) && stack.getItem() instanceof ItemSword) {
                slot_mat[3] = i;
            } else
                // If Pick
                if (stack.getItem() instanceof ItemPickaxe) {
                    slot_mat[2] = i;
                }
            if (stack.getItem() instanceof ItemBlock) {

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
        for (int val : slot_mat) {
            if (val != -1)
                count++;
        }

        // If we have everything we need, return true
        return count >= 3 + ((antiWeakness.getValue() || switchSword.getValue()) ? 1 : 0);

    }

    private boolean is_in_hole() {
        sur_block = new Double[][]{
                {aimTarget.posX + 1, aimTarget.posY, aimTarget.posZ},
                {aimTarget.posX - 1, aimTarget.posY, aimTarget.posZ},
                {aimTarget.posX, aimTarget.posY, aimTarget.posZ + 1},
                {aimTarget.posX, aimTarget.posY, aimTarget.posZ - 1}
        };

        // Check if the guy is in a hole
        return HoleUtil.isHole(EntityUtil.getPosition(aimTarget), true, true).getType() != HoleUtil.HoleType.NONE;
    }

}