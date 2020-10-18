package com.gamesense.api.util.world;

import com.gamesense.api.util.misc.Wrapper;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;

public class EntityUtil{

	private static final Minecraft mc = Minecraft.getMinecraft();

	public static boolean isPassive(Entity e){
		if (e instanceof EntityWolf && ((EntityWolf) e).isAngry()) return false;
		if (e instanceof EntityAnimal || e instanceof EntityAgeable || e instanceof EntityTameable || e instanceof EntityAmbientCreature || e instanceof EntitySquid)
			return true;
		return e instanceof EntityIronGolem && ((EntityIronGolem) e).getRevengeTarget() == null;
	}

	public static boolean isLiving(Entity e){
		return e instanceof EntityLivingBase;
	}

	public static boolean isFakeLocalPlayer(Entity entity){
		return entity != null && entity.getEntityId() == -100 && Wrapper.getPlayer() != entity;
	}

	//Finds the entities interpolated amount
	public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z){
		return new Vec3d(
				(entity.posX - entity.lastTickPosX) * x,
				(entity.posY - entity.lastTickPosY) * y,
				(entity.posZ - entity.lastTickPosZ) * z
		);
	}

	public static String getNameFromUUID(String uuid){
		try{
			String jsonUrl = IOUtils.toString(new URL("https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names"));

			JsonParser parser = new JsonParser();

			return parser.parse(jsonUrl).getAsJsonArray().get(parser.parse(jsonUrl).getAsJsonArray().size() - 1).getAsJsonObject().get("name").toString();
		} catch (IOException ex){
		}
		return null;
	}

	public static Block isColliding(double posX, double posY, double posZ){
		Block block = null;
		if (mc.player != null){
			final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ) : mc.player.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ);
			int y = (int) bb.minY;
			for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++){
				for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++){
					block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
				}
			}
		}
		return block;
	}

	public static double getBaseMoveSpeed(){
		double baseSpeed = 0.2873;
		if (mc.player != null && mc.player.isPotionActive(Potion.getPotionById(1))){
			final int amplifier = mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
			baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
		}
		return baseSpeed;
	}

	public static boolean isInLiquid(){
		if (mc.player != null){
			if (mc.player.fallDistance >= 3.0f){
				return false;
			}
			boolean inLiquid = false;
			final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
			int y = (int) bb.minY;
			for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++){
				for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++){
					final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (!(block instanceof BlockAir)){
						if (!(block instanceof BlockLiquid)){
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

	public static void setTimer(float speed){
		Minecraft.getMinecraft().timer.tickLength = 50.0f / speed;
	}

	public static void resetTimer(){
		Minecraft.getMinecraft().timer.tickLength = 50;
	}

	public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec){
		return getInterpolatedAmount(entity, vec.x, vec.y, vec.z);
	}

	public static Vec3d getInterpolatedAmount(Entity entity, double ticks){
		return getInterpolatedAmount(entity, ticks, ticks, ticks);
	}

	public static boolean isMobAggressive(Entity entity){
		if (entity instanceof EntityPigZombie){
			// arms raised = aggressive, angry = either game or we have set the anger cooldown
			if (((EntityPigZombie) entity).isArmsRaised() || ((EntityPigZombie) entity).isAngry()){
				return true;
			}
		} else if (entity instanceof EntityWolf){
			return ((EntityWolf) entity).isAngry() &&
					!Wrapper.getPlayer().equals(((EntityWolf) entity).getOwner());
		} else if (entity instanceof EntityEnderman){
			return ((EntityEnderman) entity).isScreaming();
		}
		return isHostileMob(entity);
	}

	//If the mob by default wont attack the player, but will if the player attacks it
	public static boolean isNeutralMob(Entity entity){
		return entity instanceof EntityPigZombie ||
				entity instanceof EntityWolf ||
				entity instanceof EntityEnderman;
	}

	//If the mob is friendly (not aggressive)
	public static boolean isFriendlyMob(Entity entity){
		return (entity.isCreatureType(EnumCreatureType.CREATURE, false) && !EntityUtil.isNeutralMob(entity)) ||
				(entity.isCreatureType(EnumCreatureType.AMBIENT, false)) ||
				entity instanceof EntityVillager ||
				entity instanceof EntityIronGolem ||
				(isNeutralMob(entity) && !EntityUtil.isMobAggressive(entity));
	}

	//If the mob is hostile
	public static boolean isHostileMob(Entity entity){
		return (entity.isCreatureType(EnumCreatureType.MONSTER, false) && !EntityUtil.isNeutralMob(entity));
	}

	//Find the entities interpolated position
	public static Vec3d getInterpolatedPos(Entity entity, float ticks){
		return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
	}

	public static Vec3d getInterpolatedRenderPos(Entity entity, float ticks){
		return getInterpolatedPos(entity, ticks).subtract(Wrapper.getMinecraft().getRenderManager().renderPosX, Wrapper.getMinecraft().getRenderManager().renderPosY, Wrapper.getMinecraft().getRenderManager().renderPosZ);
	}

	public static boolean isInWater(Entity entity){
		if (entity == null) return false;

		double y = entity.posY + 0.01;

		for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); x++)
			for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); z++){
				BlockPos pos = new BlockPos(x, (int) y, z);
				if (Wrapper.getWorld().getBlockState(pos).getBlock() instanceof BlockLiquid) return true;
			}
		return false;
	}

	public static boolean isDrivenByPlayer(Entity entityIn){
		return Wrapper.getPlayer() != null && entityIn != null && entityIn.equals(Wrapper.getPlayer().getRidingEntity());
	}

	public static boolean isAboveWater(Entity entity){
		return isAboveWater(entity, false);
	}

	public static boolean isAboveWater(Entity entity, boolean packet){
		if (entity == null) return false;
		double y = entity.posY - (packet ? 0.03 : (EntityUtil.isPlayer(entity) ? 0.2 : 0.5)); // increasing this seems to flag more in NCP but needs to be increased so the player lands on solid water
		for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); x++)
			for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); z++){
				BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);

				if (Wrapper.getWorld().getBlockState(pos).getBlock() instanceof BlockLiquid) return true;
			}
		return false;
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

		// to degree
		pitch = pitch * 180.0d / Math.PI;
		yaw = yaw * 180.0d / Math.PI;
		yaw += 90f;

		return new double[]{yaw, pitch};
	}

	public static boolean isPlayer(Entity entity){
		return entity instanceof EntityPlayer;
	}

	public static double getRelativeX(float yaw){
		return MathHelper.sin(-yaw * 0.017453292F);
	}

	public static double getRelativeZ(float yaw){
		return MathHelper.cos(yaw * 0.017453292F);
	}
}