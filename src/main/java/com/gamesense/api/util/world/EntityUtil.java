package com.gamesense.api.util.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * @Author 086
 * @Author Crystallinqq/Auto
 */

public class EntityUtil {

	private static final Minecraft mc = Minecraft.getMinecraft();

	public static Block isColliding(double posX, double posY, double posZ) {
		Block block = null;
		if (mc.player != null) {
			final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ) : mc.player.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ);
			int y = (int) bb.minY;
			for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
				for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
					block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
				}
			}
		}
		return block;
	}

	public static boolean isInLiquid() {
		if (mc.player != null) {
			if (mc.player.fallDistance >= 3.0f) {
				return false;
			}
			boolean inLiquid = false;
			final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
			int y = (int) bb.minY;
			for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
				for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
					final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (!(block instanceof BlockAir)) {
						if (!(block instanceof BlockLiquid)) {
							return false;
						}
						inLiquid = true;
					}
				}
			}
			return inLiquid;
		}
		return false;
	}

	public static void setTimer(float speed) {
		Minecraft.getMinecraft().timer.tickLength = 50.0f / speed;
	}

	public static void resetTimer() {
		Minecraft.getMinecraft().timer.tickLength = 50;
	}

	public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
		return getInterpolatedAmount(entity, ticks, ticks, ticks);
	}

	public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
		return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
	}

	public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
		return new Vec3d(
				(entity.posX - entity.lastTickPosX) * x,
				(entity.posY - entity.lastTickPosY) * y,
				(entity.posZ - entity.lastTickPosZ) * z
		);
	}

	public static float clamp(float val, float min, float max) {
		if (val <= min) {
			val = min;
		}
		if (val >= max) {
			val = max;
		}
		return val;
	}
}