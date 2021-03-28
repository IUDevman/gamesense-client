package com.gamesense.api.util.world.combat;

import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.stream.Collectors;

public class CrystalUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean newPlacement) {
        if (!isValidBlock(mc.world.getBlockState(blockPos).getBlock())) return false;

        BlockPos posUp = blockPos.up();

        if (newPlacement) {
            if (!mc.world.isAirBlock(posUp)) return false;
        } else {
            if (!isValidMaterial(mc.world.getBlockState(posUp).getMaterial())
                || !isValidMaterial(mc.world.getBlockState(posUp.up()).getMaterial())) return false;
        }

        AxisAlignedBB box = new AxisAlignedBB(
            posUp.getX(), posUp.getY(), posUp.getZ(),
            posUp.getX() + 1.0, posUp.getY() + 2.0, posUp.getZ() + 1.0
        );

        return mc.world.getEntitiesWithinAABB(Entity.class, box, Entity::isEntityAlive).isEmpty();
    }

    public static boolean isValidBlock(Block block) {
        return block == Blocks.BEDROCK || block == Blocks.OBSIDIAN;
    }

    public static boolean isValidMaterial(Material material) {
        return !material.isLiquid() && material.isReplaceable();
    }

    public static List<BlockPos> findCrystalBlocks(float placeRange, boolean newPlacement) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(EntityUtil.getSphere(PlayerUtil.getPlayerPos(), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> CrystalUtil.canPlaceCrystal(pos, newPlacement)).collect(Collectors.toList()));
        return positions;
    }

    public static void breakCrystal(Entity crystal) {
        mc.playerController.attackEntity(mc.player, crystal);
        mc.player.swingArm(EnumHand.MAIN_HAND);
    }
}