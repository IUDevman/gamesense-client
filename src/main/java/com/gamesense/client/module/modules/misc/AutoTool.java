package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.DamageBlockEvent;
import com.gamesense.api.event.events.DestroyBlockEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

/**
 * @author Hoosiers
 * @since 04/10/2021
 */

@Module.Declaration(name = "AutoTool", category = Category.Misc)
public class AutoTool extends Module {

    BooleanSetting switchBack = registerBoolean("Switch Back", true);

    private final HashMap<BlockPos, Integer> blockPosIntegerHashMap = new HashMap<>();

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<DamageBlockEvent> damageBlockEventListener = new Listener<>(event -> runAutoTool(event.getBlockPos(), blockPosIntegerHashMap.getOrDefault(event.getBlockPos(), -1)));

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<DestroyBlockEvent> destroyBlockEventListener = new Listener<>(event -> {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (switchBack.getValue() && blockPosIntegerHashMap.containsKey(event.getBlockPos()) && mc.player.inventory.currentItem != blockPosIntegerHashMap.get(event.getBlockPos())) {
            mc.player.inventory.currentItem = blockPosIntegerHashMap.get(event.getBlockPos());
        }

        if (!switchBack.getValue() || blockPosIntegerHashMap.size() >= 10) {
            blockPosIntegerHashMap.clear();
        }
    });

    private void runAutoTool(BlockPos blockPos, int switchSlot) {
        int toolSlot = InventoryUtil.findToolForBlockState(mc.world.getBlockState(blockPos), 0, 9);

        if (toolSlot != -1) {
            blockPosIntegerHashMap.put(blockPos, switchSlot != -1 ? switchSlot : mc.player.inventory.currentItem);
            mc.player.inventory.currentItem = toolSlot;
        }
    }
}