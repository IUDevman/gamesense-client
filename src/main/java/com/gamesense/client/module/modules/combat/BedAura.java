package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.friends.Friends;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.Timer;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hoosiers
 * @since 1/2/2020
 */

public class BedAura extends Module {

    public BedAura() {
        super("BedAura", Category.Combat);
    }

    Setting.Mode attackMode;
    Setting.Double attackRange;
    Setting.Integer breakDelay;
    Setting.Integer placeDelay;
    Setting.Double targetRange;
    Setting.Boolean antiSuicide;
    Setting.Integer antiSuicideHealth;
    Setting.Integer minDamage;
    Setting.Boolean rotate;
    Setting.Boolean chatMsgs;
    Setting.Boolean disableNone;
    Setting.Boolean autoSwitch;

    public void setup() {
        ArrayList<String> attackModes = new ArrayList<>();
        attackModes.add("Normal");
        attackModes.add("Own");

        attackMode = registerMode("Mode", attackModes, "Own");
        attackRange = registerDouble("Attack Range", 4, 0, 10);
        breakDelay = registerInteger("Break Delay", 1, 0, 20);
        placeDelay = registerInteger("Place Delay", 1, 0, 20);
        targetRange = registerDouble("Target Range", 7, 0, 16);
        rotate = registerBoolean("Rotate", true);
        disableNone = registerBoolean("Disable No Bed", false);
        autoSwitch = registerBoolean("Switch", true);
        antiSuicide = registerBoolean("Anti Suicide", false);
        antiSuicideHealth = registerInteger("Suicide Health", 14, 1, 36);
        minDamage = registerInteger("Min Damage", 5, 1, 36);
        chatMsgs = registerBoolean("Chat Msgs", true);
    }

    private boolean hasNone = false;
    private int oldSlot = -1;
    private ArrayList<BlockPos> placedPos = new ArrayList<>();
    private Timer breakTimer = new Timer();
    private Timer placeTimer = new Timer();

    public void onEnable() {
        hasNone = false;
        placedPos.clear();

        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        int bedSlot = findBedSlot();

        if (mc.player.inventory.currentItem != bedSlot && bedSlot != -1 && autoSwitch.getValue()) {
            oldSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = bedSlot;
        }
        else if (bedSlot == -1) {
            hasNone = true;
        }

        if (chatMsgs.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "BedAura turned ON!");
        }
    }

    public void onDisable() {
        placedPos.clear();

        if (mc.player == null || mc.world == null) {
            return;
        }

        if (autoSwitch.getValue() && mc.player.inventory.currentItem != oldSlot && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
        }

        if (chatMsgs.getValue()) {
            if (hasNone && disableNone.getValue()) {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "No beds detected... BedAura turned OFF!");
            }
            else {
                MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "BedAura turned OFF!");
            }
        }

        hasNone = false;
        oldSlot = -1;
    }

    public void onUpdate() {
        if (mc.player == null || mc.world == null || mc.player.dimension == 0) {
            disable();
            return;
        }

        int bedSlot = findBedSlot();

        if (mc.player.inventory.currentItem != bedSlot && bedSlot != -1 && autoSwitch.getValue()) {
            oldSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = bedSlot;
        }
        else if (bedSlot == -1) {
            hasNone = true;
        }

        if (antiSuicide.getValue() && (mc.player.getHealth() + mc.player.getAbsorptionAmount()) < antiSuicideHealth.getValue()) {
            return;
        }

        if (breakTimer.getTimePassed() / 50L >= breakDelay.getValue()) {
            breakTimer.reset();
            breakBed();
        }

        if (hasNone) {

            if (disableNone.getValue()) {
                disable();
                return;
            }

            return;
        }

        if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() != Items.BED) {
            return;
        }

        if (placeTimer.getTimePassed() / 50L >= placeDelay.getValue()) {
            placeTimer.reset();
            placeBed();
        }
    }

    private void breakBed() {
        for (TileEntity tileEntity : findBedEntities(mc.player)) {
            if (!(tileEntity instanceof TileEntityBed)) {
                continue;
            }

            if (rotate.getValue()) {
                BlockUtil.faceVectorPacketInstant(new Vec3d(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()));
            }

            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(tileEntity.getPos(), EnumFacing.UP, EnumHand.OFF_HAND, 0, 0, 0));
            return;
        }
    }

    private void placeBed() {
        for (EntityPlayer entityPlayer : findTargetEntities(mc.player)) {

            if (entityPlayer.isDead) {
                continue;
            }

            NonNullList<BlockPos> targetPos = findTargetPlacePos(entityPlayer);

            if (targetPos.size() < 1) {
                continue;
            }

            for (BlockPos blockPos : targetPos) {

                BlockPos targetPos1 = blockPos.up();

                if (targetPos1.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) > attackRange.getValue()) {
                    continue;
                }

                if (mc.world.getBlockState(targetPos1).getBlock() != Blocks.AIR) {
                    continue;
                }

                if (entityPlayer.getPosition() == targetPos1) {
                    continue;
                }

                if (calculateDamage(targetPos1.getX(), targetPos1.getY(), targetPos1.getZ(), entityPlayer) < minDamage.getValue()) {
                    continue;
                }

                if (mc.world.getBlockState(targetPos1.east()).getBlock() == Blocks.AIR) {
                    placeBedFinal(targetPos1, 90, EnumFacing.DOWN);
                    return;
                }
                else if (mc.world.getBlockState(targetPos1.west()).getBlock() == Blocks.AIR) {
                    placeBedFinal(targetPos1, -90, EnumFacing.DOWN);
                    return;
                }
                else if (mc.world.getBlockState(targetPos1.north()).getBlock() == Blocks.AIR) {
                    placeBedFinal(targetPos1, 0, EnumFacing.DOWN);
                    return;
                }
                else if (mc.world.getBlockState(targetPos1.south()).getBlock() == Blocks.AIR) {
                    placeBedFinal(targetPos1, 180, EnumFacing.SOUTH);
                    return;
                }
            }
        }
    }

    private int findBedSlot() {
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == ItemStack.EMPTY) {
                continue;
            }

            if (stack.getItem() instanceof ItemBed) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    private NonNullList<TileEntity> findBedEntities(EntityPlayer entityPlayer) {
        NonNullList<TileEntity> bedEntities = NonNullList.create();

        mc.world.loadedTileEntityList.stream()
                .filter(tileEntity -> tileEntity instanceof TileEntityBed)
                .filter(tileEntity -> tileEntity.getDistanceSq(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ) <= (attackRange.getValue() * attackRange.getValue()))
                .filter(this::isOwn)
                .forEach(bedEntities::add);

        bedEntities.stream().min(Comparator.comparing(tileEntity -> tileEntity.getDistanceSq(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ)));

        return bedEntities;
    }

    private boolean isOwn(TileEntity tileEntity) {
        if (attackMode.getValue().equalsIgnoreCase("Normal")) {
            return true;
        }
        else if (attackMode.getValue().equalsIgnoreCase("Own")) {
            for (BlockPos blockPos : placedPos) {
                if (blockPos.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) <= 3) {
                    return true;
                }
            }
        }

        return false;
    }

    private NonNullList<EntityPlayer> findTargetEntities(EntityPlayer entityPlayer) {
        NonNullList<EntityPlayer> targetEntities = NonNullList.create();

        mc.world.playerEntities.stream()
                .filter(entityPlayer1 -> entityPlayer1 != mc.player)
                .filter(entityPlayer1 -> !entityPlayer1.isDead)
                .filter(entityPlayer1 -> !Friends.isFriend(entityPlayer1.getName()))
                .filter(entityPlayer1 -> entityPlayer1.getDistance(entityPlayer) <= targetRange.getValue())
                .sorted(Comparator.comparing(entityPlayer1 -> entityPlayer1.getDistance(entityPlayer)))
                .forEach(targetEntities::add);

        return targetEntities;
    }

    private NonNullList<BlockPos> findTargetPlacePos(EntityPlayer entityPlayer) {
        NonNullList<BlockPos> targetPlacePos = NonNullList.create();

        targetPlacePos.addAll(getSphere(mc.player.getPosition(), (float) attackRange.getValue(), (int) attackRange.getValue(), false, true, 0)
                .stream()
                .filter(this::canPlaceBed)
                .sorted(Comparator.comparing(blockPos -> 1 - (calculateDamage(blockPos.up().getX(), blockPos.up().getY(), blockPos.up().getZ(), entityPlayer))))
                .collect(Collectors.toList()));

        return targetPlacePos;
    }

    /** start port from AutoCrystal */

    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plusY) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plusY, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1.0D;

        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finald;
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;

            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage = damage - (damage / 4);
            }
            damage = Math.max(damage, 0.0F);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    /** end port from AutoCrystal */

    private boolean canPlaceBed(BlockPos blockPos) {
        if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR) {
            return false;
        }

        if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR) {
            return false;
        }

        if (!mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos)).isEmpty()) {
            return false;
        }

        return true;
    }

    //bon55's bedAura really helped me understand how this all works
    private void placeBedFinal(BlockPos blockPos, int direction, EnumFacing enumFacing) {
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(direction, 0, mc.player.onGround));

        if (mc.world.getBlockState(blockPos).getBlock() != Blocks.AIR) {
            return;
        }

        BlockPos neighbourPos = blockPos.offset(enumFacing);
        EnumFacing oppositeFacing = enumFacing.getOpposite();

        Vec3d vec3d = new Vec3d(neighbourPos).add(0.5, 0.5, 0.5).add(new Vec3d(oppositeFacing.getDirectionVec()).scale(0.5));

        if (rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(vec3d);
        }

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbourPos, oppositeFacing, vec3d, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        placedPos.add(blockPos);
    }
}