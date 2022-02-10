package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.*;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.OffHand;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;

import static com.gamesense.api.util.player.SpoofRotationUtil.ROTATION_UTIL;

@Module.Declaration(name = "EchestFarmer", category = Category.Misc)
public class EchestFarmer extends Module {

    ModeSetting breakBlock = registerMode("Break Block", Arrays.asList("Normal", "Packet"), "Packet");
    ModeSetting HowplaceBlock = registerMode("Place Block", Arrays.asList("Near", "Looking"), "Looking");
    IntegerSetting stackCount = registerInteger("N^Stack", 0, 0, 64);
    IntegerSetting tickDelay = registerInteger("Tick Delay", 5, 0, 10);
    BooleanSetting offHandEchest = registerBoolean("OffHand echest", false);
    BooleanSetting rotate = registerBoolean("Rotate", false);
    BooleanSetting forceRotation = registerBoolean("ForceRotation", false);

    private int delayTimeTicks,
                echestToMine,
                slotObby,
                slotPick;
    BlockPos blockAim;
    private boolean looking,
                    noSpace,
                    materialsNeeded,
                    prevBreak;
    private ArrayList<EnumFacing> sides = new ArrayList<>();


    Vec3d lastHitVec;

    // This is for the force rotation, strict servers
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<>(event -> {
        if (event.getPhase() != Phase.PRE || !rotate.getValue() || lastHitVec == null || !forceRotation.getValue())
            return;
        Vec2f rotation = RotationUtil.getRotationTo(lastHitVec);
        PlayerPacket packet = new PlayerPacket(this, rotation);
        PlayerPacketManager.INSTANCE.addPacket(packet);
    });

    @Override
    public void onEnable() {
        ROTATION_UTIL.onEnable();
        // Init of values
        initValues();
    }

    private void initValues() {
        prevBreak = noSpace = looking = false;
        delayTimeTicks = 0;
        materialsNeeded = true;
        int obbyCount = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() instanceof ItemBlock && ((ItemBlock) itemStack.getItem()).getBlock() == Blocks.OBSIDIAN).mapToInt(ItemStack::getCount).sum();
        int stackWanted = ( stackCount.getValue() == 0 ?  -1 : stackCount.getValue() * 64 );
        echestToMine = (stackWanted - obbyCount) / 8;

        if (HowplaceBlock.getValue().equals("Looking")) {
            blockAim = mc.objectMouseOver.getBlockPos();
            blockAim.y += 1;

            if (BlockUtil.getPlaceableSide(blockAim) == null) {
                looking = false;
                return;
            }
            sides.clear();
            sides.add(EnumFacing.getDirectionFromEntityLiving(blockAim, mc.player));
        } else {

            for(int[] sur : new int[][] {
                    {1, 0},
                    {-1, 0},
                    {0, 1},
                    {0, -1},
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1}
            }) {
                for(int h : new int[] {
                        1,
                        0
                }) {
                    if (BlockUtil.getBlock(mc.player.posX + sur[0], mc.player.posY + h, mc.player.posZ + sur[1]) instanceof BlockAir && BlockUtil.getPlaceableSide(new BlockPos(mc.player.posX + sur[0], mc.player.posY + h, mc.player.posZ + sur[1])) != null) {
                        if (!PistonCrystal.someoneInCoords(mc.player.posX + sur[0], mc.player.posZ + sur[1])) {
                            blockAim = new BlockPos(mc.player.posX + sur[0], mc.player.posY + h, mc.player.posZ + sur[1]);
                            break;
                        }
                    }
                }
                if (blockAim != null)
                    break;
            }
            if (blockAim == null) {
                noSpace = false;
                return;
            }
        }

        if (isToggleMsg()) {
            if (stackCount.getValue() == 0)
                PistonCrystal.printDebug("Starting farming obby", false);
            else
                PistonCrystal.printDebug(String.format("N^obby: %d, N^stack: %d, echest needed: %d", obbyCount, stackWanted, echestToMine), false);
        }

        slotPick = InventoryUtil.findFirstItemSlot(Items.DIAMOND_PICKAXE.getClass(), 0, 9);

        if (offHandEchest.getValue()) {
            slotObby = 11;
            OffHand.requestItems(2);
            mc.player.inventory.currentItem = slotPick;
            mc.playerController.updateController();
        } else slotObby = InventoryUtil.findFirstBlockSlot(Blocks.ENDER_CHEST.getClass(), 0, 9);

        if (slotObby == -1 || slotPick == -1)
            materialsNeeded = false;
    }

    @Override
    public void onDisable() {

        String output = "";

        if (!materialsNeeded) {
            output = "No materials detected... " + (slotObby == -1 ? "No Echest detected " : "") + (slotPick == -1 ? "No Pick detected" : "");
        } else if(noSpace) {
            output = "Not enough space";
        } else if (looking) {
            output = "Impossible to place";
        }

        if (!output.equals(""))
            PistonCrystal.printDebug(output, true);
        else if (echestToMine == 0)
            PistonCrystal.printDebug("Mined every echest", false);



        if (offHandEchest.getValue())
            OffHand.removeItem(2);
    }

    @Override
    public void onUpdate() {

        if (mc.player == null) {
            disable();
            return;
        }

        if (delayTimeTicks < tickDelay.getValue()) {
            delayTimeTicks++;
            return;
        } else {
            delayTimeTicks = 0;
        }

        if (blockAim == null || !materialsNeeded || slotPick == -1 || looking || noSpace) {
            disable();
            return;
        }

        if (BlockUtil.getBlock(blockAim) instanceof BlockAir) {
            if (prevBreak) {
                if (--echestToMine == 0) {
                    disable();
                    return;
                }
            }
            placeBlock(blockAim);
            prevBreak = false;
        } else {
            if (mc.player.inventory.currentItem != slotPick) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slotPick));
                mc.player.inventory.currentItem = slotPick;
                mc.playerController.updateController();
            }

            // Get side
            EnumFacing sideBreak = BlockUtil.getPlaceableSide(blockAim);
            // If it's != null
            if (sideBreak != null) {
                // Switch break values
                switch (breakBlock.getValue()) {
                    // Normal Packet
                    case "Packet":
                        if (!prevBreak) {

                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(
                                    CPacketPlayerDigging.Action.START_DESTROY_BLOCK, blockAim, sideBreak
                            ));
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(
                                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockAim, sideBreak
                            ));

                            prevBreak = true;
                        }
                        break;
                    // Vanilla
                    case "Normal":
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.playerController.onPlayerDamageBlock(blockAim, sideBreak);
                        prevBreak = true;
                        break;
                }
            }

        }

    }

    private void placeBlock(BlockPos pos) {
        EnumHand handSwing;

        if (slotObby == 11) {
            handSwing = EnumHand.OFF_HAND;
        } else {
            handSwing = EnumHand.MAIN_HAND;
            if (mc.player.inventory.currentItem != slotObby) {
                mc.player.inventory.currentItem = slotObby;
                mc.playerController.updateController();
            }
        }

        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemMainhand().getItem()).getBlock() != Blocks.ENDER_CHEST || mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() != Blocks.ENDER_CHEST) {
            return;
        }

        if (forceRotation.getValue()) {
            EnumFacing side = BlockUtil.getPlaceableSide(blockAim);

            if (side == null) {
                return;
            }

            BlockPos neighbour = blockAim.offset(side);
            EnumFacing opposite = side.getOpposite();

            if (!BlockUtil.canBeClicked(neighbour)) {
                return;
            }

            lastHitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        }

        PlacementUtil.place(pos, handSwing, rotate.getValue(), true);
    }

}
