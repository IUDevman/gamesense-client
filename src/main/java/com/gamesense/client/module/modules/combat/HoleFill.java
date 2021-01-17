package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Author Hoosiers on 10/31/2020
 */

public class HoleFill extends Module {

	public HoleFill() {
		super("HoleFill", Category.Combat);
	}

	Setting.Boolean chatMsgs;
	Setting.Boolean autoSwitch;
	Setting.Boolean rotate;
	Setting.Integer placeDelay;
	Setting.Double horizontalRange;
	Setting.Double verticalRange;
	Setting.Mode mode;

	public void setup() {
		ArrayList<String> modes = new ArrayList<>();
		modes.add("Obby");
		modes.add("Echest");
		modes.add("Both");
		modes.add("Web");

		mode = registerMode("Type", modes, "Obby");
		placeDelay = registerInteger("Delay", 3, 0, 10);
		horizontalRange = registerDouble("H Range", 4, 0, 10);
		verticalRange = registerDouble("V Range", 2, 0, 5);
		rotate = registerBoolean("Rotate", true);
		autoSwitch = registerBoolean("Switch", true);
		chatMsgs = registerBoolean("Chat Msgs", true);
	}

	private boolean isSneaking = false;
	private int delayTicks = 0;
	private int oldHandEnable = -1;

	public void onEnable() {
		if (chatMsgs.getValue() && mc.player != null) {
			MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "HoleFill turned ON!");
		}
		if (autoSwitch.getValue() && mc.player != null) {
			oldHandEnable = mc.player.inventory.currentItem;
		}
	}

	public void onDisable() {
		if (chatMsgs.getValue() && mc.player != null) {
			MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "HoleFill turned OFF!");
		}
		if (autoSwitch.getValue() && mc.player != null) {
			mc.player.inventory.currentItem = oldHandEnable;
		}
		if (isSneaking) {
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
			isSneaking = false;
		}
	}

	public void onUpdate() {
		if (mc.player == null || mc.world == null) {
			disable();
			return;
		}

		List<BlockPos> holePos = new ArrayList<>();
		holePos.addAll(findHoles());

		if (holePos != null) {

			if (autoSwitch.getValue()) {
				int oldHand = mc.player.inventory.currentItem;
				int newHand = findRightBlock(oldHand);

				if (newHand != -1) {
					mc.player.inventory.currentItem = findRightBlock(oldHand);
				}
				else {
					return;
				}
			}

			BlockPos placePos = holePos.stream().sorted(Comparator.comparing(blockPos -> blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ))).findFirst().orElse(null);

			if (placePos == null) {
				return;
			}

			for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(placePos))) {
				if (entity instanceof EntityPlayer) {
					return;
				}
			}

			if (delayTicks >= placeDelay.getValue() && isHoldingRightBlock(mc.player.inventory.currentItem, mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem()) && placeBlock(placePos)) {
				delayTicks = 0;
			}
			delayTicks++;
		}
	}

	private List<BlockPos> findHoles() {
		NonNullList<BlockPos> holes = NonNullList.create();

		//from old HoleFill module, really good way to do this
		Iterable<BlockPos> worldPosBlockPos = BlockPos.getAllInBox(mc.player.getPosition().add(-horizontalRange.getValue(), -verticalRange.getValue(), -horizontalRange.getValue()), mc.player.getPosition().add(horizontalRange.getValue(), verticalRange.getValue(), horizontalRange.getValue()));

		for (BlockPos blockPos : worldPosBlockPos) {
			if (isSurrounded(blockPos)) {
				holes.add(blockPos);
			}
		}

		return holes;
	}

	private boolean isSurrounded(BlockPos blockPos) {
		if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR
				&& mc.world.getBlockState(blockPos.east()).getBlock() != Blocks.AIR
				&& mc.world.getBlockState(blockPos.west()).getBlock() != Blocks.AIR
				&& mc.world.getBlockState(blockPos.north()).getBlock() != Blocks.AIR
				&& mc.world.getBlockState(blockPos.south()).getBlock() != Blocks.AIR
				&& mc.world.getBlockState(blockPos.down()).getBlock() != Blocks.AIR
				&& mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR
				&& mc.world.getBlockState(blockPos.up(2)).getBlock() == Blocks.AIR) {
			return true;
		}

		return false;
	}

	private int findRightBlock(int oldHand) {
		int newHand = -1;

		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

			if (itemStack == ItemStack.EMPTY || !(itemStack.getItem() instanceof ItemBlock)) {
				continue;
			}

			Block block = ((ItemBlock) itemStack.getItem()).getBlock();
			if ((mode.getValue().equalsIgnoreCase("Obby") || mode.getValue().equalsIgnoreCase("Both")) && block instanceof BlockObsidian) {
				newHand = i;
				break;
			}
			else if ((mode.getValue().equalsIgnoreCase("Echest") || mode.getValue().equalsIgnoreCase("Both")) && block instanceof BlockEnderChest) {
				newHand = i;
				break;
			}
			else if (mode.getValue().equalsIgnoreCase("Web") && block instanceof BlockWeb) {
				newHand = i;
				break;
			}
		}

		if (newHand == -1) {
			newHand = oldHand;
		}

		return newHand;
	}

	private Boolean isHoldingRightBlock(int hand, Item item) {
		if (hand == -1) {
			return false;
		}

		if (item instanceof ItemBlock) {
			Block block = ((ItemBlock) item).getBlock();

			if (mode.getValue().equalsIgnoreCase("Obby") && block instanceof BlockObsidian) {
				return true;
			}
			else if (mode.getValue().equalsIgnoreCase("Echest") && block instanceof BlockEnderChest) {
				return true;
			}
			else if (mode.getValue().equalsIgnoreCase("Both") && (block instanceof BlockObsidian || block instanceof BlockEnderChest)) {
				return true;
			}
			else if (mode.getValue().equalsIgnoreCase("Web") && block instanceof BlockWeb) {
				return true;
			}
		}

		return false;
	}

	/** Mostly ported from Surround, best way to do it */
	private Boolean placeBlock(BlockPos blockPos) {
		if (blockPos == null) {
			return false;
		}

		Block block = mc.world.getBlockState(blockPos).getBlock();

		if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
			return false;
		}

		EnumFacing side = BlockUtil.getPlaceableSide(blockPos);

		if (side == null) {
			return false;
		}

		BlockPos neighbour = blockPos.offset(side);
		EnumFacing opposite = side.getOpposite();

		if (!BlockUtil.canBeClicked(neighbour)) {
			return false;
		}

		Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
		Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

		if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			isSneaking = true;
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
}