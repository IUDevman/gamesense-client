package com.gamesense.api.util.combat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

public class DamageUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float finalDamage = 1.0f;
        try {
            float doubleExplosionSize = 12.0F;
            double distancedSize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
            double blockDensity;
            blockDensity = entity.world.getBlockDensity(new Vec3d(posX, posY, posZ), entity.getEntityBoundingBox());
            double v = (1.0D - distancedSize) * blockDensity;
            float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));

            if (entity instanceof EntityLivingBase) {
                finalDamage = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
            }
        } catch (NullPointerException ignored){
        }

        return finalDamage;
    }

    public static float calculateDamageThreaded(double posX, double posY, double posZ, Entity entity) {
        float finalDamage = 1.0f;
        try {
            float doubleExplosionSize = 12.0F;
            double distancedSize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
            double blockDensity;
            // bit hacky but seems to work
            synchronized (entity.world) {
                blockDensity = entity.world.getBlockDensity(new Vec3d(posX, posY, posZ), entity.getEntityBoundingBox());
            }
            double v = (1.0D - distancedSize) * blockDensity;
            float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));

            if (entity instanceof EntityLivingBase) {
                finalDamage = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
            }
        } catch (NullPointerException ignored){
        }

        return finalDamage;
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

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    // below is an exact copy of getBlockDensity and rayTraceBlocks used for testing
    private static float getBlockDensity(Vec3d start, AxisAlignedBB target, Entity entity) {
        double incrementX = 1.0D / ((target.maxX - target.minX) * 2.0D + 1.0D);
        double incrementY = 1.0D / ((target.maxY - target.minY) * 2.0D + 1.0D);
        double incrementZ = 1.0D / ((target.maxZ - target.minZ) * 2.0D + 1.0D);
        double offsetX = (1.0D - (Math.floor(1.0D / incrementX) * incrementX)) / 2.0D;
        double offsetZ = (1.0D - (Math.floor(1.0D / incrementZ) * incrementZ)) / 2.0D;

        double width = target.maxX - target.minX;
        double height = target.maxY - target.minY;
        double depth = target.maxZ - target.minZ;

        if (incrementX >= 0.0D && incrementY >= 0.0D && incrementZ >= 0.0D) {
            int air = 0;
            int total = 0;

            for (double x = 0.0D; x <= 1.0D; x += incrementX) {
                for (double y = 0.0D; y <= 1.0D; y += incrementY) {
                    for (double z = 0.0D; z <= 1.0D; z += incrementZ) {
                        double raytraceX = target.minX + (width * x);
                        double raytraceY = target.minY + (height * y);
                        double raytraceZ = target.minZ + (depth * z);

                        if (rayTraceBlocks(new Vec3d(raytraceX + offsetX, raytraceY, raytraceZ + offsetZ), start, entity) == null) {
                            ++air;
                        }

                        ++total;
                    }
                }
            }
            return (float) air/(float) total;
        } else {
            return 0.0F;
        }
    }

    private static RayTraceResult rayTraceBlocks(Vec3d end, Vec3d start, Entity entity) {
        if (!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
            if (!Double.isNaN(start.x) && !Double.isNaN(start.y) && !Double.isNaN(start.z)) {
                int startX = MathHelper.floor(start.x);
                int startY = MathHelper.floor(start.y);
                int startZ = MathHelper.floor(start.z);
                int endX = MathHelper.floor(end.x);
                int endY = MathHelper.floor(end.y);
                int endZ = MathHelper.floor(end.z);
                BlockPos endBlockPos = new BlockPos(endX, endY, endZ);
                IBlockState endBlockState = entity.world.getBlockState(endBlockPos);
                Block endBlock = endBlockState.getBlock();
                if (endBlock.canCollideCheck(endBlockState, false)) {
                    return endBlockState.collisionRayTrace(entity.world, endBlockPos, end, start);
                }

                for (int i = 200; i >= 0; i--) {
                    if (Double.isNaN(end.x) || Double.isNaN(end.y) || Double.isNaN(end.z)) {
                        return null;
                    }

                    if (endX == startX && endY == startY && endZ == startZ) {
                        return null;
                    }

                    boolean flagX = true;
                    boolean flagY = true;
                    boolean flagZ = true;
                    double x = 999.0D;
                    double y = 999.0D;
                    double z = 999.0D;

                    if (startX > endX) {
                        x = (double)endX + 1.0D;
                    } else if (startX < endX) {
                        x = endX;
                    } else {
                        flagX = false;
                    }

                    if (startY > endY) {
                        y = (double)endY + 1.0D;
                    } else if (startY < endY) {
                        y = endY;
                    } else {
                        flagY = false;
                    }

                    if (startZ > endZ) {
                        z = (double)endZ + 1.0D;
                    } else if (startZ < endZ) {
                        z = endZ;
                    } else {
                        flagZ = false;
                    }

                    double xChange = 999.0D;
                    double yChange = 999.0D;
                    double zChange = 999.0D;
                    double differenceX = start.x - end.x;
                    double differenceY = start.y - end.y;
                    double differenceZ = start.z - end.z;

                    if (flagX) {
                        xChange = (x - end.x) / differenceX;
                    }

                    if (flagY) {
                        yChange = (y - end.y) / differenceY;
                    }

                    if (flagZ) {
                        zChange = (z - end.z) / differenceZ;
                    }

                    if (xChange == -0.0D) {
                        xChange = -1.0E-4D;
                    }

                    if (yChange == -0.0D) {
                        yChange = -1.0E-4D;
                    }

                    if (zChange == -0.0D) {
                        zChange = -1.0E-4D;
                    }

                    EnumFacing enumfacing;
                    if (xChange < yChange && xChange < zChange) {
                        enumfacing = startX > endX ? EnumFacing.WEST : EnumFacing.EAST;
                        end = new Vec3d(x, end.y + differenceY * xChange, end.z + differenceZ * xChange);
                    } else if (yChange < zChange) {
                        enumfacing = startY > endY ? EnumFacing.DOWN : EnumFacing.UP;
                        end = new Vec3d(end.x + differenceX * yChange, y, end.z + differenceZ * yChange);
                    } else {
                        enumfacing = startZ > endZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        end = new Vec3d(end.x + differenceX * zChange, end.y + differenceY * zChange, z);
                    }

                    endX = MathHelper.floor(end.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    endY = MathHelper.floor(end.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    endZ = MathHelper.floor(end.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    endBlockPos = new BlockPos(endX, endY, endZ);

                    IBlockState iBlockState = entity.world.getBlockState(endBlockPos);
                    Block block = iBlockState.getBlock();
                    if (block.canCollideCheck(iBlockState, false)) {
                        return iBlockState.collisionRayTrace(entity.world, endBlockPos, end, start);
                    }
                }
            }
        }
        return null;
    }
}