package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hoosiers
 * @since 10/31/2020
 * @author 0b00101010
 * @since 26/01/2021
 */
public class HoleFill extends Module {

	public HoleFill() {
		super("HoleFill", Category.Combat);
	}

	Setting.Boolean chatMsgs;
	Setting.Boolean autoSwitch;
	Setting.Boolean rotate;
	Setting.Boolean disable;
	Setting.Integer placeDelay;
	Setting.Integer retryDelay;
	Setting.Integer bpc;
	Setting.Double range;
	Setting.Mode mode;

	public void setup() {
		ArrayList<String> modes = new ArrayList<>();
		modes.add("Obby");
		modes.add("Echest");
		modes.add("Both");
		modes.add("Web");

		mode = registerMode("Type", modes, "Obby");
		disable = registerBoolean("Disable on Finish", false);
		placeDelay = registerInteger("Delay", 3, 0, 10);
		retryDelay = registerInteger("Retry Delay", 10, 0, 50);
		bpc = registerInteger("Block pre Cycle", 1, 1, 5);
		range = registerDouble("Range", 4, 0, 10);
		rotate = registerBoolean("Rotate", true);
		autoSwitch = registerBoolean("Switch", true);
		chatMsgs = registerBoolean("Chat Msgs", true);
	}

	private boolean isSneaking = false;
	private int delayTicks = 0;
	private int oldHandEnable = -1;

	private final HashMap<BlockPos, Integer> recentPlacements = new HashMap<>();

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
		recentPlacements.clear();
	}

	public void onUpdate() {
		if (mc.player == null || mc.world == null) {
			disable();
			return;
		}

		recentPlacements.replaceAll(((blockPos, integer) -> integer+1));
		recentPlacements.values().removeIf(integer -> integer > retryDelay.getValue() * 2);

		if (delayTicks <= placeDelay.getValue() * 2) {
			delayTicks++;
			return;
		}

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

		List<BlockPos> holePos = new ArrayList<>(findHoles());

		int placements = 0;
		holePos = holePos.stream().sorted(Comparator.comparing(blockPos -> blockPos.distanceSq((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ))).collect(Collectors.toList());
		for (BlockPos placePos: holePos) {
			if (placements >= bpc.getValue()) {
				return;
			}

			if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(placePos)).stream().anyMatch(entity -> entity instanceof EntityPlayer)) {
				continue;
			}
			if (recentPlacements.containsKey(placePos)) {
				continue;
			}

			if (isHoldingRightBlock(mc.player.inventory.currentItem, mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem())) {
				if (placeBlock(placePos)) {
					placements++;
					delayTicks = 0;
				}
				recentPlacements.put(placePos, 0);
			}
		}
	}

	private List<BlockPos> findHoles() {
		NonNullList<BlockPos> holes = NonNullList.create();

		//from old HoleFill module, really good way to do this
		List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), 5, 5, false, true, 0);

		for (BlockPos blockPos : blockPosList) {
			if (HoleUtil.isHole(blockPos, true).getType() == HoleUtil.HoleType.SINGLE) {
				holes.add(blockPos);
			}
		}

		return holes;
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
			else return mode.getValue().equalsIgnoreCase("Web") && block instanceof BlockWeb;
		}

		return false;
	}

	/** Mostly ported from Surround, best way to do it */
	private Boolean placeBlock(BlockPos blockPos) {
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

		EnumActionResult action = mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
		if (action == EnumActionResult.SUCCESS) {
			mc.player.swingArm(EnumHand.MAIN_HAND);
			mc.rightClickDelayTimer = 4;
		}

		if (stoppedAC) {
			AutoCrystalGS.stopAC = false;
			stoppedAC = false;
		}

		return action == EnumActionResult.SUCCESS;
	}
}