package com.gamesense.api.util.world;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;

public class WorldUtils{

	public static void rotate(float yaw, float pitch){
		Minecraft.getMinecraft().player.rotationYaw = yaw;
		Minecraft.getMinecraft().player.rotationPitch = pitch;
	}

	public static void rotate(double[] rotations){
		Minecraft.getMinecraft().player.rotationYaw = (float)rotations[0];
		Minecraft.getMinecraft().player.rotationPitch = (float)rotations[1];
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
}