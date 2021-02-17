package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	private int delayTimeTicks = 0;
	private int offsetSteps = 0;
	private int oldSlot = -1;

	public void onEnable() {
		PlacementUtil.onEnable();
		if (mc.player == null) {
			disable();
			return;
		}

		if (chatMsg.getValue()) {
			MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "AutoWeb turned ON!");
		}

		oldSlot = mc.player.inventory.currentItem;

		int newSlot = InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8);
		if (newSlot != -1) {
			mc.player.inventory.currentItem = newSlot;
		}
	}

	public void onDisable() {
		PlacementUtil.onDisable();
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

		EntityPlayer closestTarget = PlayerUtil.findClosestTarget();

		if (closestTarget == null) {
			return;
		}

		if (firstRun) {
			firstRun = false;
			if (InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8) == -1) {
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

		int blocksPlaced = 0;

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
			} else {
				if (InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8) == -1) {
					noWeb = true;
					disable();
				}
			}

			offsetSteps++;

			if (isSneaking) {
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
				isSneaking = false;
			}
		}
	}

	private boolean placeBlock(BlockPos pos, int range) {
		if (mc.player.getDistanceSq(pos) > range * range) {
			return false;
		}

		return PlacementUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), BlockWeb.class);
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