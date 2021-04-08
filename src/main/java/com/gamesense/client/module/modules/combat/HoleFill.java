package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Hoosiers
 * @author 0b00101010
 * @author TechAle
 * @since 10/31/2020
 * @since 26/01/2021
 * @author Madmegsox1
 * @since 18/03/2021
 */

@Module.Declaration(name = "HoleFill", category = Category.Combat)
public class HoleFill extends Module {

    ModeSetting mode = registerMode("Type", Arrays.asList("Obby", "Echest", "Both", "Web", "Plate"), "Obby");
    IntegerSetting placeDelay = registerInteger("Delay", 2, 0, 10);
    IntegerSetting retryDelay = registerInteger("Retry Delay", 10, 0, 50);
    IntegerSetting bpc = registerInteger("Block pre Cycle", 2, 1, 5);
    DoubleSetting range = registerDouble("Range", 4, 0, 10);
    DoubleSetting playerRange = registerDouble("Player Range", 3, 1, 6);
    BooleanSetting onlyPlayer = registerBoolean("Only Player", false);
    BooleanSetting rotate = registerBoolean("Rotate", true);
    BooleanSetting autoSwitch = registerBoolean("Switch", true);
    BooleanSetting offHandObby = registerBoolean("Off Hand Obby", false);
    BooleanSetting disableOnFinish = registerBoolean("Disable on Finish", true);

    private int delayTicks = 0;
    private int oldHandEnable = -1;
    private boolean activedOff;
    private int obbySlot;

    /*
     * Stops us from spam placing same closest position while
     * we wait for the block to be placed by the game
     */
    private final HashMap<BlockPos, Integer> recentPlacements = new HashMap<>();

    public void onEnable() {
        activedOff = false;
        PlacementUtil.onEnable();

        if (autoSwitch.getValue() && mc.player != null) {
            oldHandEnable = mc.player.inventory.currentItem;
        }
        obbySlot = InventoryUtil.findObsidianSlot(offHandObby.getValue(), activedOff);
        if (obbySlot == 9) {
            activedOff = true;
        }
    }

    public void onDisable() {
        PlacementUtil.onDisable();

        if (autoSwitch.getValue() && mc.player != null) {
            mc.player.inventory.currentItem = oldHandEnable;
        }
        recentPlacements.clear();

        if (offHandObby.getValue() && OffHand.isActive()) {
            OffHand.removeObsidian();
            activedOff = false;
        }
    }

    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        recentPlacements.replaceAll(((blockPos, integer) -> integer + 1));
        // https://github.com/IUDevman/gamesense-client/issues/127
        recentPlacements.values().removeIf(integer -> integer > retryDelay.getValue() * 2);

        // https://github.com/IUDevman/gamesense-client/issues/127
        if (delayTicks <= placeDelay.getValue() * 2) {
            delayTicks++;
            return;
        }

        if (obbySlot == 9) {
            if (!(mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian)) {
                return;
            }
        }

        if (autoSwitch.getValue()) {
            //int oldHand = mc.player.inventory.currentItem;
            int newHand = findRightBlock();

            if (newHand != -1) {
                mc.player.inventory.currentItem = newHand;
                mc.playerController.syncCurrentPlayItem();
            } else {
                return;
            }
        }

        List<BlockPos> holePos = new ArrayList<>(findHoles());
        holePos.removeAll(recentPlacements.keySet());

        AtomicInteger placements = new AtomicInteger();
        holePos = holePos.stream().sorted(Comparator.comparing(blockPos -> blockPos.distanceSq((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ))).collect(Collectors.toList());
        // Get every players
        List<EntityPlayer> listPlayer = new ArrayList<>(mc.world.playerEntities);
        listPlayer.removeIf(player -> EntityUtil.basicChecksEntity(player) || (!onlyPlayer.getValue() || mc.player.getDistance(player) > 6 + playerRange.getValue()));
        holePos.removeIf(placePos -> {
            if (placements.get() >= bpc.getValue()) {
                return false;
            }

            if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(placePos)).stream().anyMatch(entity -> entity instanceof EntityPlayer)) {
                return true;
            }

            boolean output = false;

            if (isHoldingRightBlock(mc.player.inventory.currentItem, mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem()) || offHandObby.getValue()) {
                // Player range
                boolean found = false;
                if (onlyPlayer.getValue()) {
                    for (EntityPlayer player : listPlayer) {
                        if (player.getDistanceSqToCenter(placePos) < playerRange.getValue() * 2) {
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                        return false;
                }
                if (placeBlock(placePos)) {
                    placements.getAndIncrement();
                    output = true;
                    delayTicks = 0;

                }
                recentPlacements.put(placePos, 0);
            }

            return output;
        });

        if (disableOnFinish.getValue() && holePos.size() == 0) {
            disable();
        }
    }

    private boolean placeBlock(BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        if(offHandObby.getValue()) {
            int obsidianSlot = InventoryUtil.findObsidianSlot(offHandObby.getValue(), activedOff);

            if (obsidianSlot == -1) {
                return false;
            }

            if (obsidianSlot == 9) {
                activedOff = true;
                if (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian) {
                    // We can continue
                    handSwing = EnumHand.OFF_HAND;
                } else return false;
            }
        }

        return PlacementUtil.place(pos, handSwing, rotate.getValue(), true);
    }

    private List<BlockPos> findHoles() {
        NonNullList<BlockPos> holes = NonNullList.create();

        //from old HoleFill module, really good way to do this
        List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), range.getValue().floatValue(), range.getValue().intValue(), false, true, 0);

        for (BlockPos blockPos : blockPosList) {
            if (HoleUtil.isHole(blockPos, true, true).getType() == HoleUtil.HoleType.SINGLE) {
                holes.add(blockPos);
            }
        }
        return holes;
    }

    private int findRightBlock() {
        switch (mode.getValue()) {
            case "Both" : {
                int newHand = InventoryUtil.findFirstBlockSlot(BlockObsidian.class, 0, 8);
                if (newHand == -1) return InventoryUtil.findFirstBlockSlot(BlockEnderChest.class, 0, 8);
                else return newHand;
            }
            case "Obby" : {
                return InventoryUtil.findFirstBlockSlot(BlockObsidian.class, 0, 8);
            }
            case "Echest" : {
                return InventoryUtil.findFirstBlockSlot(BlockEnderChest.class, 0, 8);
            }
            case "Web" : {
                return InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8);
            }
            case "Plate" : {
                return InventoryUtil.findFirstBlockSlot(BlockPressurePlate.class, 0 ,8);
            }
            default: {
                return -1;
            }
        }
    }

    private Boolean isHoldingRightBlock(int hand, Item item) {
        if (hand == -1) return false;

        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();

            switch (mode.getValue()) {
                case "Both" : {
                    return block instanceof BlockObsidian || block instanceof BlockEnderChest;
                }
                case "Obby" : {
                    return block instanceof BlockObsidian;
                }
                case "Echest" : {
                    return block instanceof BlockEnderChest;
                }
                case "Web" : {
                    return block instanceof BlockWeb;
                }
                case "Plate" : {
                    return block instanceof BlockPressurePlate;
                }
                default: {
                    return false;
                }
            }
        }

        return false;
    }
}