package com.gamesense.api.util.world;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class WorldUtils{
	public static void placeBlockMainHand(BlockPos pos){
		placeBlock(EnumHand.MAIN_HAND, pos);
	}

	public static void placeBlock(EnumHand hand, BlockPos pos){
		Vec3d eyesPos = new Vec3d(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY + (double)Minecraft.getMinecraft().player.getEyeHeight(), Minecraft.getMinecraft().player.posZ);
		EnumFacing[] var3 = EnumFacing.values();
		int var4 = var3.length;

		for (int var5 = 0; var5 < var4; var5++){
			EnumFacing side = var3[var5];
			BlockPos neighbor = pos.offset(side);
			EnumFacing side2 = side.getOpposite();
			if (Minecraft.getMinecraft().world.getBlockState(neighbor).getBlock().canCollideCheck(Minecraft.getMinecraft().world.getBlockState(neighbor), false)){
				Vec3d hitVec = (new Vec3d(neighbor)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(side2.getDirectionVec())).scale(0.5D));
				if (eyesPos.squareDistanceTo(hitVec) <= 18.0625D){
					double diffX = hitVec.x - eyesPos.x;
					double diffY = hitVec.y - eyesPos.y;
					double diffZ = hitVec.z - eyesPos.z;
					double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
					float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
					float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
					float[] rotations = new float[]{Minecraft.getMinecraft().player.rotationYaw + MathHelper.wrapDegrees(yaw - Minecraft.getMinecraft().player.rotationYaw), Minecraft.getMinecraft().player.rotationPitch + MathHelper.wrapDegrees(pitch - Minecraft.getMinecraft().player.rotationPitch)};
					Minecraft.getMinecraft().player.connection.sendPacket(new Rotation(rotations[0], rotations[1], Minecraft.getMinecraft().player.onGround));
					Minecraft.getMinecraft().player.connection.sendPacket(new CPacketEntityAction(Minecraft.getMinecraft().player, Action.START_SNEAKING));
					Minecraft.getMinecraft().playerController.processRightClickBlock(Minecraft.getMinecraft().player, Minecraft.getMinecraft().world, neighbor, side2, hitVec, hand);
					Minecraft.getMinecraft().player.swingArm(hand);
					Minecraft.getMinecraft().player.connection.sendPacket(new CPacketEntityAction(Minecraft.getMinecraft().player, Action.STOP_SNEAKING));
					return;
				}
			}
		}
	}

	public static int findBlock(Block block){
		return findItem((new ItemStack(block)).getItem());
	}

	public static int findItem(Item item){
		try{
			for (int i = 0; i < 9; i++){
				ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
				if (item == stack.getItem()){
					return i;
				}
			}
		} catch (Exception var3){
		}
		return -1;
	}

	public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me){
		double dirx = me.posX - px;
		double diry = me.posY - py;
		double dirz = me.posZ - pz;
		double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
		dirx /= len;
		diry /= len;
		dirz /= len;
		double pitch = Math.asin(diry);
		double yaw = Math.atan2(dirz, dirx);
		pitch = pitch * 180.0D / 3.141592653589793D;
		yaw = yaw * 180.0D / 3.141592653589793D;
		yaw += 90.0D;
		return new double[]{yaw, pitch};
	}

	public static void rotate(float yaw, float pitch){
		Minecraft.getMinecraft().player.rotationYaw = yaw;
		Minecraft.getMinecraft().player.rotationPitch = pitch;
	}

	public static void rotate(double[] rotations){
		Minecraft.getMinecraft().player.rotationYaw = (float)rotations[0];
		Minecraft.getMinecraft().player.rotationPitch = (float)rotations[1];
	}

	public static void lookAtBlock(BlockPos blockToLookAt){
		rotate(calculateLookAt(blockToLookAt.getX(), blockToLookAt.getY(), blockToLookAt.getZ(), Minecraft.getMinecraft().player));
	}

	public static BlockPos getRelativeBlockPos(EntityPlayer player, int ChangeX, int ChangeY, int ChangeZ){
		int[] playerCoords = new int[]{(int)player.posX, (int)player.posY, (int)player.posZ};
		BlockPos pos;
		if (player.posX < 0.0D && player.posZ < 0.0D){
			pos = new BlockPos(playerCoords[0] + ChangeX - 1, playerCoords[1] + ChangeY, playerCoords[2] + ChangeZ - 1);
		} else if (player.posX < 0.0D && player.posZ > 0.0D){
			pos = new BlockPos(playerCoords[0] + ChangeX - 1, playerCoords[1] + ChangeY, playerCoords[2] + ChangeZ);
		} else if (player.posX > 0.0D && player.posZ < 0.0D){
			pos = new BlockPos(playerCoords[0] + ChangeX, playerCoords[1] + ChangeY, playerCoords[2] + ChangeZ - 1);
		} else{
			pos = new BlockPos(playerCoords[0] + ChangeX, playerCoords[1] + ChangeY, playerCoords[2] + ChangeZ);
		}
		return pos;
	}

	public static String vectorToString(Vec3d vector, boolean... includeY){
		boolean reallyIncludeY = includeY.length <= 0 || includeY[0];
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		builder.append((int)Math.floor(vector.x));
		builder.append(", ");
		if (reallyIncludeY){
			builder.append((int)Math.floor(vector.y));
			builder.append(", ");
		}
		builder.append((int)Math.floor(vector.z));
		builder.append(")");
		return builder.toString();
	}

	public static String vectorToString(BlockPos pos){
		return vectorToString(new Vec3d(pos));
	}
}
