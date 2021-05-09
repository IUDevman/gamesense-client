package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

import static com.gamesense.api.util.player.SpoofRotationUtil.ROTATION_UTIL;

@Module.Declaration(name = "FastShulker", category = Category.Misc)
public class FastShulker extends Module {

    ModeSetting HowplaceBlock = registerMode("Place Block", Arrays.asList("Near", "Looking"), "Looking");
    IntegerSetting tickDelay = registerInteger("Tick Delay", 5, 0, 10);
    BooleanSetting rotate = registerBoolean("Rotate", false);
    BooleanSetting forceRotation = registerBoolean("ForceRotation", false);

    private int delayTimeTicks;
    BlockPos blockAim;
    private boolean looking,
                    noSpace,
                    materialsNeeded;


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

    private int getShulkerSlot() {
        for(int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock) mc.player.inventory.getStackInSlot(i).getItem()).getBlock() instanceof BlockShulkerBox) {
                return i;
            }

        }
        return -1;
    }
    int slot;
    boolean swapped = false;

    private void initValues() {
        if ((slot = getShulkerSlot()) == -1) {
            materialsNeeded = false;
            return;
        } else materialsNeeded = true;

        if (HowplaceBlock.getValue().equals("Looking")) {
            blockAim = mc.objectMouseOver.getBlockPos();
            blockAim.y += 1;

            if (BlockUtil.getPlaceableSide(blockAim) == null) {
                looking = false;
                return;
            }
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
                    if (BlockUtil.getBlock(mc.player.posX + sur[0], mc.player.posY + h, mc.player.posZ + sur[1]) instanceof BlockAir && BlockUtil.getPlaceableSide(new BlockPos(mc.player.posX + sur[0], mc.player.posY + h, mc.player.posZ + sur[1])) != null && BlockUtil.getBlock(mc.player.posX + sur[0], mc.player.posY + h + 1, mc.player.posZ + sur[1]) instanceof BlockAir) {
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

        EnumFacing side = EnumFacing.getDirectionFromEntityLiving(blockAim, mc.player);
        BlockPos neighbour = blockAim.offset(side);
        EnumFacing opposite = side.getOpposite();


        lastHitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));

    }

    @Override
    public void onDisable() {

        String output = "";

        if (!materialsNeeded) {
            output = "No materials detected... Shulker not found";
        } else if(noSpace) {
            output = "Not enough space";
        } else if (looking) {
            output = "Impossible to place";
        }

        if (!output.equals(""))
            PistonCrystal.printDebug(output, true);
        else
            PistonCrystal.printDebug("Shulker placed and opened", false);

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

        if (blockAim == null || !materialsNeeded|| looking || noSpace) {
            disable();
            return;
        }

        if (slot > 9 && !swapped) {
            mc.playerController.windowClick(0, 9, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 9, 0, ClickType.PICKUP, mc.player);
            swapped = true;
            if (tickDelay.getValue() != 0)
                return;
        }

        if (BlockUtil.getBlock(blockAim) instanceof BlockAir) {
            if (slot > 9) {
                if (mc.player.inventory.currentItem != 9) {
                    mc.player.inventory.currentItem = 9;
                    mc.playerController.updateController();
                }
            } else if (mc.player.inventory.currentItem != slot)
                mc.player.inventory.currentItem = slot;

            PlacementUtil.place(blockAim, EnumHand.MAIN_HAND, rotate.getValue(), true);

            if (tickDelay.getValue() == 0)
                openBlock();

        } else {
            openBlock();
        }

    }

    private void openBlock() {
        EnumFacing side = EnumFacing.getDirectionFromEntityLiving(blockAim, mc.player);
        BlockPos neighbour = blockAim.offset(side);
        EnumFacing opposite = side.getOpposite();


        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        mc.playerController.processRightClickBlock(mc.player, mc.world, blockAim, opposite, hitVec, EnumHand.MAIN_HAND);

        disable();
    }


}
