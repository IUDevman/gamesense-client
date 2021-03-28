package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * @author Hoosiers
 * @since 10/20/2020
 * @author 0b00101010
 * @since 01/30/2021
 */

@Module.Declaration(name = "CityESP", category = Category.Render)
public class CityESP extends Module {

    private final HashMap<EntityPlayer, List<BlockPos>> cityable = new HashMap<>();
    IntegerSetting range = registerInteger("Range", 20, 1, 30);
    IntegerSetting down = registerInteger("Down", 1, 0, 3);
    IntegerSetting sides = registerInteger("Sides", 1, 0, 4);
    IntegerSetting depth = registerInteger("Depth", 3, 0, 10);
    DoubleSetting minDamage = registerDouble("Min Damage", 5, 0, 10);
    DoubleSetting maxDamage = registerDouble("Max Self Damage", 7, 0, 20);
    BooleanSetting ignoreCrystals = registerBoolean("Ignore Crystals", true);
    ModeSetting targetMode = registerMode("Target", Arrays.asList("Single", "All"), "Single");
    ModeSetting selectMode = registerMode("Select", Arrays.asList("Closest", "All"), "Closest");
    ModeSetting renderMode = registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
    IntegerSetting width = registerInteger("Width", 1, 1, 10);
    ColorSetting color = registerColor("Color", new GSColor(102, 51, 153));

    public void onUpdate() {
        if (mc.player == null || mc.world == null)
            return;

        cityable.clear();

        List<EntityPlayer> players = mc.world.playerEntities.stream()
                .filter(entityPlayer -> entityPlayer.getDistanceSq(mc.player) <= range.getValue() * range.getValue())
                .filter(entityPlayer -> !EntityUtil.basicChecksEntity(entityPlayer)).collect(Collectors.toList());

        for (EntityPlayer player : players) {
            if (player == mc.player) {
                continue;
            }

            List<BlockPos> blocks = EntityUtil.getBlocksIn(player);
            if (blocks.size() == 0) {
                continue;
            }

            // find lowest point of the player
            int minY = Integer.MAX_VALUE;
            for (BlockPos block : blocks) {
                int y = block.getY();
                if (y < minY) {
                    minY = y;
                }
            }
            if (player.posY % 1 > .2) {
                minY++;
            }

            int finalMinY = minY;
            blocks = blocks.stream().filter(blockPos -> blockPos.getY() == finalMinY).collect(Collectors.toList());

            Optional<BlockPos> any = blocks.stream().findAny();
            if (!any.isPresent()) {
                continue;
            }

            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(any.get(), false, true);
            if (holeInfo.getType() == HoleUtil.HoleType.NONE || holeInfo.getSafety() == HoleUtil.BlockSafety.UNBREAKABLE) {
                continue;
            }

            List<BlockPos> sides = new ArrayList<>();
            for (BlockPos block : blocks) {
                sides.addAll(cityableSides(block, HoleUtil.getUnsafeSides(block).keySet(), player));
            }

            if (sides.size() > 0) {
                cityable.put(player, sides);
            }
        }
    }

    public void onWorldRender(RenderEvent event) {
        AtomicBoolean noRender = new AtomicBoolean(false);

        cityable.entrySet().stream().sorted((entry, entry1) -> (int) entry.getKey().getDistanceSq(entry1.getKey())).forEach((entry) -> {
            if (noRender.get()) {
                return;
            }
            renderBoxes(entry.getValue());
            if (targetMode.getValue().equalsIgnoreCase("All")) {
                noRender.set(true);
            }
        });
    }

    private List<BlockPos> cityableSides(BlockPos centre, Set<HoleUtil.BlockOffset> weakSides, EntityPlayer player) {
        List<BlockPos> cityableSides = new ArrayList<>();
        HashMap<BlockPos, HoleUtil.BlockOffset> directions = new HashMap<>();
        for (HoleUtil.BlockOffset weakSide : weakSides) {
            BlockPos pos = weakSide.offset(centre);
            if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR) {
                directions.put(pos, weakSide);
            }
        }

        directions.forEach(((blockPos, blockOffset) -> {
            if (blockOffset == HoleUtil.BlockOffset.DOWN) {
                return;
            }

            BlockPos pos1 = blockOffset.left(blockPos.down(down.getValue()), sides.getValue());
            BlockPos pos2 = blockOffset.forward(blockOffset.right(blockPos, sides.getValue()), depth.getValue());
            List<BlockPos> square = EntityUtil.getSquare(pos1, pos2);

            IBlockState holder = mc.world.getBlockState(blockPos);
            mc.world.setBlockToAir(blockPos);


            for (BlockPos pos : square) {
                if (this.canPlaceCrystal(pos.down(), ignoreCrystals.getValue())) {

                    if (DamageUtil.calculateDamage((double) pos.getX() + 0.5d, pos.getY(), (double) pos.getZ() + 0.5d, player) >= minDamage.getValue()) {
                        if (DamageUtil.calculateDamage((double) pos.getX() + 0.5d, pos.getY(), (double) pos.getZ() + 0.5d, mc.player) <= maxDamage.getValue()) {
                            cityableSides.add(blockPos);
                        }
                        break;
                    }
                }
            }

            mc.world.setBlockState(blockPos, holder);
        }));

        return cityableSides;
    }

    private boolean canPlaceCrystal(BlockPos blockPos, boolean ignoreCrystal) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(boost, boost2);

        if (!(mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)) {
            return false;
        }

        if (!(mc.world.getBlockState(boost).getBlock() == Blocks.AIR)) {
            return false;
        }

        if (!(mc.world.getBlockState(boost2).getBlock() == Blocks.AIR)) {
            return false;
        }

        if (!ignoreCrystal)
            return mc.world.getEntitiesWithinAABB(Entity.class, axisAlignedBB).isEmpty();
        else {
            List<Entity> entityList = mc.world.getEntitiesWithinAABB(Entity.class, axisAlignedBB);
            entityList.removeIf(entity -> entity instanceof EntityEnderCrystal);
            return entityList.isEmpty();
        }

    }

    private void renderBoxes(List<BlockPos> blockPosList) {
        switch (selectMode.getValue()) {
            case "Closest": {
                blockPosList.stream().min(Comparator.comparing(blockPos -> blockPos.distanceSq((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ))).ifPresent(this::renderBox);
                break;
            }
            case "All": {
                for (BlockPos blockPos : blockPosList) {
                    renderBox(blockPos);
                }
                break;
            }
        }
    }

    private void renderBox(BlockPos blockPos) {
        GSColor gsColor1 = new GSColor(color.getValue(), 255);
        GSColor gsColor2 = new GSColor(color.getValue(), 50);

        switch (renderMode.getValue()) {
            case "Both": {
                RenderUtil.drawBox(blockPos, 1, gsColor2, GeometryMasks.Quad.ALL);
                RenderUtil.drawBoundingBox(blockPos, 1, width.getValue(), gsColor1);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(blockPos, 1, width.getValue(), gsColor1);
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(blockPos, 1, gsColor2, GeometryMasks.Quad.ALL);
                break;
            }
        }
    }
}