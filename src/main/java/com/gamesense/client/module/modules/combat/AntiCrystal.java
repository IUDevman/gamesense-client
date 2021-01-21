package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * @Author TechAle
 */

public class AntiCrystal extends Module {

    Setting.Double rangePlace;
    Setting.Double damageMin;
    Setting.Double enemyRange;
    Setting.Integer tickDelay;
    Setting.Integer blocksPerTick;
    Setting.Boolean rotate;
    Setting.Boolean offHandMode;
    Setting.Boolean onlyIfEnemy;
    Setting.Boolean checkDamage;
    Setting.Boolean chatMsg;

    private int delayTimeTicks,
                blocksPlaced;
    private boolean isSneaking = false;

    public AntiCrystal() {
        super("AntiCrystal", Category.Combat);
    }

    @Override
    public void setup() {
        // Range of place
        rangePlace = registerDouble("RangePlace",5.9, 0, 6);
        // Range of place
        enemyRange = registerDouble("EnemyRange",12, 0, 20);
        // Damage
        damageMin = registerDouble("DamageMin", 4, 0, 15);
        // Tick delay every wait
        tickDelay = registerInteger("Tick Delay", 5, 0, 10);
        // Max blocks per tick
        blocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 8);
        // OffHandMode
        offHandMode = registerBoolean("offHandMode", true);
        // Rotate
        rotate = registerBoolean("rotate", false);
        // Enemy
        onlyIfEnemy = registerBoolean("onlyIfEnemy", true);
        // Damage
        checkDamage = registerBoolean("damageCheck", true);
        // ChatMsg
        chatMsg = registerBoolean("Chat Msgs", true);
    }

    @Override
    public void onEnable() {

        delayTimeTicks = 0;

        if (chatMsg.getValue()) {
            PistonCrystal.printChat("AntiCrystal turned ON!", false);
        }

    }

    @Override
    public void onDisable() {

        if (chatMsg.getValue()) {
            PistonCrystal.printChat("AntiCrystal turned Off!", true);
        }

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
        }else delayTimeTicks = 0;

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
            }
            else return;
        }

        blocksPlaced = 0;
        // If we arleady change our item
        boolean pressureSwitch = true;
        int slotPressure = -1;
        // Iterate for every entity
        for(Entity t : mc.world.loadedEntityList) {
            // If it's a crystal
            if (t instanceof EntityEnderCrystal && mc.player.getDistance(t) <= rangePlace.getValue()) {
                /// I decided to put this here so it's going to be checked only if there is at least 1 crystal
                // Check if we arleady switched to the pressure plate
                if (pressureSwitch) {
                    // If offhand is not on
                    if (offHandMode.getValue() && isOffHandPressure()) {
                        slotPressure = 9;
                    }else {
                        // get number and check if it's -1
                        if ((slotPressure = getHotBarPressure()) == -1)
                            return;
                    }
                    pressureSwitch = false;

                }
                // Check the damage
                if (checkDamage.getValue() && OffHand.calculateDamage(t.posX, t.posY, t.posZ, mc.player) < damageMin.getValue())
                    return;
                // Check if it's air
                if (get_block(t.posX, t.posY, t.posZ) instanceof BlockAir) {
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

    public static boolean isOffHandPressure() {
        return OffHand.nonDefaultItem.getValue().equals("Plates") || OffHand.defaultItem.getValue().equals("Plates");
    }

    // Get block from coordinates
    private Block get_block(double x, double y, double z) {
        return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    private void placeBlock(BlockPos pos, int slotPressure) {

        EnumFacing side = EnumFacing.DOWN;

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        if (slotPressure != 9 && mc.player.inventory.currentItem != slotPressure) {
            mc.player.inventory.currentItem = slotPressure;
        }

        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }


        boolean stoppedAC = false;

        if (ModuleManager.isModuleEnabled("AutoCrystalGS")) {
            AutoCrystalGS.stopAC = true;
            stoppedAC = true;
        }

        if (rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec);
        }
        EnumHand swingHand = EnumHand.MAIN_HAND;
        // Check if we are offhand + if we have the item
        if (slotPressure == 9) {
            swingHand = EnumHand.OFF_HAND;
            if (!isPressure(mc.player.getHeldItemOffhand()))
                return;
        }else {
            if (!isPressure(mc.player.getHeldItemMainhand()))
                return;
        }


        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, swingHand);
        mc.player.swingArm(swingHand);

        if (stoppedAC) {
            AutoCrystalGS.stopAC = false;
            stoppedAC = false;
        }

    }

    private boolean isPressure(ItemStack stack) {
        // If it's not what we want
        if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
            return false;
        }
        // If it's a block and it's a pressure plate
        return ((ItemBlock) stack.getItem()).getBlock() instanceof BlockPressurePlate;
    }

    private int getHotBarPressure() {
        // Iterate for the entire inventory
        for(int i = 0; i < 9; i++) {
            // Check if it's a piece of pressure plate
            if (isPressure(mc.player.inventory.getStackInSlot(i)))
                return i;
        }

        return -1;
    }

}
