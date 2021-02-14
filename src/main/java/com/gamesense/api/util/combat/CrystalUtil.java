package com.gamesense.api.util.combat;

import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.block.Block;
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

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean mode) {
        BlockPos up = blockPos.add(0, 1, 0);
        BlockPos up2 = blockPos.add(0, 2, 0);
        if (!mode) {
            // improved it use the same checks as ItemEndCrystal
            Block block = mc.world.getBlockState(blockPos).getBlock();

            if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
                if (mc.world.isAirBlock(up) || block.isReplaceable(mc.world, up)) {
                    double x = up.getX();
                    double y = up.getY();
                    double z = up.getZ();
                    List<Entity> list = mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(x, y, z, x + 1.0D, y + 2.0D, z + 1.0D));

                    return list.isEmpty();
                }
            }
            return false;
        } else
            return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                    || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                    && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(up)).isEmpty()
                    && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(up2)).isEmpty();
    }

    public static List<BlockPos> findCrystalBlocks(float placeRange, boolean mode) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(EntityUtil.getSphere(PlayerUtil.getPlayerPos(), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> CrystalUtil.canPlaceCrystal(pos, mode)).collect(Collectors.toList()));
        return positions;
    }

    public static void breakCrystal(Entity crystal) {
        mc.playerController.attackEntity(mc.player, crystal);
        mc.player.swingArm(EnumHand.MAIN_HAND);
    }

}