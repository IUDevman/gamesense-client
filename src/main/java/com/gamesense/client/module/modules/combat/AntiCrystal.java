package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

/**
 * @Author TechAle
 * Ported and modified by holefill
 */

@Module.Declaration(name = "AntiCrystal", category = Category.Combat)
public class AntiCrystal extends Module {

    DoubleSetting rangePlace = registerDouble("Range Place", 5.9, 0, 6);
    DoubleSetting enemyRange = registerDouble("Enemy Range", 12, 0, 20);
    DoubleSetting damageMin = registerDouble("Damage Min", 4, 0, 15);
    DoubleSetting biasDamage = registerDouble("Bias Damage", 1, 0, 3);
    ModeSetting blockPlaced = registerMode("Block Place", Arrays.asList("Pressure", "String"), "String");
    IntegerSetting tickDelay = registerInteger("Tick Delay", 5, 0, 10);
    IntegerSetting blocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 8);
    BooleanSetting offHandMode = registerBoolean("OffHand Mode", true);
    BooleanSetting rotate = registerBoolean("Rotate", false);
    BooleanSetting onlyIfEnemy = registerBoolean("Only If Enemy", true);
    BooleanSetting nonAbusive = registerBoolean("Non Abusive", true);
    BooleanSetting checkDamage = registerBoolean("Damage Check", true);
    BooleanSetting switchBack = registerBoolean("Switch Back", true);
    BooleanSetting notOurCrystals = registerBoolean("Ignore AutoCrystal", true);

    private int delayTimeTicks;
    private boolean isSneaking = false;

    @Override
    public void onEnable() {
        delayTimeTicks = 0;
    }

    @Override
    public void onDisable() {
        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }
    }

    @Override
    public void onUpdate() {
        if (delayTimeTicks < tickDelay.getValue()) {
            delayTimeTicks++;
            return;
        } else delayTimeTicks = 0;

        if (onlyIfEnemy.getValue()) {
            if (mc.world.playerEntities.size() > 1) {
                // Check for the distance
                boolean found = false;
                for (EntityPlayer check : mc.world.playerEntities) {
                    // Check the distance
                    if (check != mc.player && mc.player.getDistance(check) <= enemyRange.getValue()) {
                        found = true;
                        break;
                    }
                }
                // If there is only 1 enemy
                if (!found)
                    return;
            } else return;
        }

        int blocksPlaced = 0;
        // If we arleady change our item
        boolean pressureSwitch = true;
        int slotPressure = -1;
        // Iterate for every entity
        for (Entity t : mc.world.loadedEntityList) {
            // If it's a crystal
            if (t instanceof EntityEnderCrystal && mc.player.getDistance(t) <= rangePlace.getValue()) {
                /// I decided to put this here so it's going to be checked only if there is at least 1 crystal
                // Check if we arleady switched to the pressure plate
                if (pressureSwitch) {
                    // If offhand is not on
                    if (offHandMode.getValue() && isOffHandPressure(blockPlaced.getValue())) {
                        slotPressure = 9;
                    } else {
                        // get number and check if it's -1
                        if ((slotPressure = getHotBarPressure(blockPlaced.getValue())) == -1)
                            return;
                    }
                    pressureSwitch = false;

                }
                // Check if it's a crystal placed by us
                if (!notOurCrystals.getValue() && usCrystal(t))
                    return;

                float damage;
                // Check for the damage
                if (checkDamage.getValue()) {
                    // Get it
                    damage = (float) (DamageUtil.calculateDamage(t.posX, t.posY, t.posZ, mc.player) * biasDamage.getValue());
                    // If it's lower then damageMin and is lower the our health, exit
                    if (damage < damageMin.getValue() && damage < mc.player.getHealth())
                        return;
                }

                // Check if it's air
                if (BlockUtil.getBlock(t.posX, t.posY, t.posZ) instanceof BlockAir) {
                    // Place the pressure plate
                    placeBlock(new BlockPos(t.posX, t.posY, t.posZ), slotPressure);
                    if (++blocksPlaced == blocksPerTick.getValue())
                        return;
                }

                if (isSneaking) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    isSneaking = false;
                }
            }
        }

    }

    public boolean usCrystal(Entity crystal) {
        AutoCrystal autoCrystal = ModuleManager.getModule(AutoCrystal.class);

        if (ModuleManager.isModuleEnabled(AutoCrystal.class)) {
            return autoCrystal.targets.stream().filter(placeInfo -> placeInfo.crystal.equals(new BlockPos((int) crystal.posX, crystal.posY - 1, (int) crystal.posZ))).findFirst().orElse(null) != null;
        }
        else return false;
    }

    // This function check if the offHand has "Plates" as value
    public static boolean isOffHandPressure(String itemMode) {
        OffHand offHand = ModuleManager.getModule(OffHand.class);
        return offHand.nonDefaultItem.getValue().equals(itemMode) || offHand.defaultItem.getValue().equals(itemMode);
    }

    // Place block
    private void placeBlock(BlockPos pos, int slotPressure) {

        int oldSlot = -1;

        EnumFacing side = EnumFacing.DOWN;

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        if (slotPressure != 9 && mc.player.inventory.currentItem != slotPressure) {
            if (!nonAbusive.getValue()) {
                if (switchBack.getValue())
                    oldSlot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = slotPressure;
            } else
                return;
        }

        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }


        boolean stoppedAC = false;

        if (ModuleManager.isModuleEnabled(AutoCrystal.class)) {
            AutoCrystal.stopAC = true;
            stoppedAC = true;
        }

        if (rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }
        EnumHand swingHand = EnumHand.MAIN_HAND;
        // Check if we are offhand + if we have the item
        if (slotPressure == 9) {
            swingHand = EnumHand.OFF_HAND;
            if (!isPressure(mc.player.getHeldItemOffhand()))
                return;
        } else {
            if (blockPlaced.getValue().equals("Pressure")) {
                if (!isPressure(mc.player.getHeldItemMainhand()))
                    return;
            } else if (!isString(mc.player.getHeldItemMainhand()))
                return;
        }


        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, swingHand);
        mc.player.swingArm(swingHand);

        if (switchBack.getValue() && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
        }

        if (stoppedAC) {
            AutoCrystal.stopAC = false;
            stoppedAC = false;
        }

    }

    // Check if a ItemStack is a Pressure Plate
    public static boolean isPressure(ItemStack stack) {
        // If it's not what we want
        if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
            return false;
        }
        // If it's a block and it's a pressure plate
        return ((ItemBlock) stack.getItem()).getBlock() instanceof BlockPressurePlate;
    }

    // Check if a ItemStack is a Pressure Plate
    public static boolean isString(ItemStack stack) {
        // If it's not what we want
        if (stack == ItemStack.EMPTY || (stack.getItem() instanceof ItemBlock)) {
            return false;
        }
        // If it's a block and it's a pressure plate
        return stack.getItem() == Items.STRING;
    }

    // Get the index of the Pressure Plate on the hotBar
    public static int getHotBarPressure(String mode) {
        // Iterate for the entire inventory
        for (int i = 0; i < 9; i++) {
            // Check if it's a piece of pressure plate
            if (mode.equals("Pressure")) {
                if (isPressure(mc.player.inventory.getStackInSlot(i)))
                    return i;
            } else if (isString(mc.player.inventory.getStackInSlot(i)))
                return i;
        }

        return -1;
    }

}
