package com.gamesense.client.module.modules.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gamesense.api.util.player.friends.Friends;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoWeb extends Module {

	public AutoWeb() {
		super("AutoWeb", Category.Combat);
	}

	Setting.Mode trapType;
	Setting.Boolean chatMsg;
	Setting.Boolean rotate;
	Setting.Boolean disableNone;
	Setting.Integer enemyRange;
	Setting.Integer tickDelay;
	Setting.Integer blocksPerTick;

	public void setup() {
		ArrayList<String> trapTypes = new ArrayList<>();
		trapTypes.add("Single");
		trapTypes.add("Double");

		trapType = registerMode("Mode", trapTypes, "Double");
		disableNone = registerBoolean("Disable No Web", true);
		rotate = registerBoolean("Rotate", true);
		tickDelay = registerInteger("Tick Delay", 5, 0, 10);
		blocksPerTick = registerInteger("Blocks Per Tick", 4, 0, 8);
		enemyRange = registerInteger("Range",4, 0, 6);
		chatMsg = registerBoolean("Chat Msgs", true);
	}

	private boolean noWeb = false;
	private boolean isSneaking = false;
	private boolean firstRun = false;

	private int blocksPlaced;
	private int delayTimeTicks = 0;
	private int offsetSteps = 0;
	private int oldSlot = -1;

	private EntityPlayer closestTarget;

	public void onEnable() {
		if (mc.player == null) {
			disable();
			return;
		}

		if (chatMsg.getValue()) {
			MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "AutoWeb turned ON!");
		}

		oldSlot = mc.player.inventory.currentItem;

		if (findWebSlot() != -1) {
			mc.player.inventory.currentItem = findWebSlot();
		}
	}

	public void onDisable() {
		if (mc.player == null) {
			return;
		}

		if (chatMsg.getValue()) {
			if (noWeb) {
				MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "No web detected... AutoWeb turned OFF!");
			}
			else {
				MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "AutoWeb turned OFF!");
			}
		}

		if (isSneaking) {
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
			isSneaking = false;
		}

		if (oldSlot != mc.player.inventory.currentItem && oldSlot != -1) {
			mc.player.inventory.currentItem = oldSlot;
			oldSlot = -1;
		}

		noWeb = false;
		firstRun = true;
		AutoCrystalGS.stopAC = false;
	}

	public void onUpdate() {
		if (mc.player == null) {
			disable();
			return;
		}

		if (disableNone.getValue() && noWeb) {
			disable();
			return;
		}

		findClosestTarget();

		if (closestTarget == null) {
			return;
		}

		if (firstRun) {
			firstRun = false;
			if (findWebSlot() == -1) {
				noWeb = true;
			}
		}
		else {
			if (delayTimeTicks < tickDelay.getValue()) {
				delayTimeTicks++;
				return;
			}
			else {
				delayTimeTicks = 0;
			}
		}

		blocksPlaced = 0;

		while (blocksPlaced <= blocksPerTick.getValue()) {

			List<Vec3d> placeTargets = new ArrayList<>();
			int maxSteps;

			if (trapType.getValue().equalsIgnoreCase("Single")) {
				Collections.addAll(placeTargets, AutoWeb.Offsets.SINGLE);
				maxSteps = AutoWeb.Offsets.SINGLE.length;
			}
			else {
				Collections.addAll(placeTargets, Offsets.DOUBLE);
				maxSteps = Offsets.DOUBLE.length;
			}

			if (offsetSteps >= maxSteps) {
				offsetSteps = 0;
				break;
			}

			BlockPos offsetPos = new BlockPos(placeTargets.get(offsetSteps));
			BlockPos targetPos = new BlockPos(closestTarget.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

			boolean tryPlacing = true;

			if (!mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
				tryPlacing = false;
			}

			if (tryPlacing && placeBlock(targetPos, enemyRange.getValue())) {
				blocksPlaced++;
			}

			offsetSteps++;

			if (isSneaking) {
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
				isSneaking = false;
			}
		}
	}

	private int findWebSlot() {
		int slot = -1;

		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.inventory.getStackInSlot(i);

			if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
				continue;
			}

			Block block = ((ItemBlock) stack.getItem()).getBlock();
			if (block instanceof BlockWeb) {
				slot = i;
				break;
			}
		}
		return slot;
	}

	private boolean placeBlock(BlockPos pos, int range) {
		Block block = mc.world.getBlockState(pos).getBlock();

		if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
			return false;
		}

		EnumFacing side = BlockUtil.getPlaceableSide(pos);

		if (side == null) {
			return false;
		}

		BlockPos neighbour = pos.offset(side);
		EnumFacing opposite = side.getOpposite();

		if (!BlockUtil.canBeClicked(neighbour)) {
			return false;
		}

		Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
		Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

		if (mc.player.getPositionVector().distanceTo(hitVec) > range) {
			return false;
		}

		int webbSlot = findWebSlot();

		if (mc.player.inventory.currentItem != webbSlot && webbSlot != -1) {
			mc.player.inventory.currentItem = webbSlot;
		}

		if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			isSneaking = true;
		}

		if (webbSlot == -1) {
			noWeb = true;
			return false;
		}

		boolean stoppedAC = false;

		if (ModuleManager.isModuleEnabled("AutoCrystalGS")) {
			AutoCrystalGS.stopAC = true;
			stoppedAC = true;
		}

		if (rotate.getValue()) {
			BlockUtil.faceVectorPacketInstant(hitVec);
		}

		mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
		mc.player.swingArm(EnumHand.MAIN_HAND);
		mc.rightClickDelayTimer = 4;

		if (stoppedAC) {
			AutoCrystalGS.stopAC = false;
			stoppedAC = false;
		}

		return true;
	}

	private void findClosestTarget() {
		List<EntityPlayer> playerList = mc.world.playerEntities;

		closestTarget = null;

		for (EntityPlayer entityPlayer : playerList) {
			if (entityPlayer == mc.player) {
				continue;
			}
			if (Friends.isFriend(entityPlayer.getName())) {
				continue;
			}
			if (entityPlayer.isDead) {
				continue;
			}
			if (closestTarget == null) {
				closestTarget = entityPlayer;
				continue;
			}
			if (mc.player.getDistance(entityPlayer) < mc.player.getDistance(closestTarget)) {
				closestTarget = entityPlayer;
			}
		}
	}

	private static class Offsets {
		private static final Vec3d[] SINGLE = {
				new Vec3d(0, 0, 0)
		};

		private static final Vec3d[] DOUBLE = {
				new Vec3d(0, 0, 0),
				new Vec3d(0, 1, 0)
		};
	}
}