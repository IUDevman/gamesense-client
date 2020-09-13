package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.DamageBlockEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

public class AutoTool extends Module {
    @EventHandler
    private final Listener<DamageBlockEvent> leftClickListener = new Listener<>(event -> {
        equipBestTool(mc.world.getBlockState(event.getPos()));
    });
    Setting.Boolean switchBack;


    boolean shouldMoveBack = false;
    int lastSlot = 0;
    long lastChange = 0L;

    public AutoTool() {
        super("AutoTool", Category.Misc);
    }

    private static void equip(int slot) {
        mc.player.inventory.currentItem = slot;
        mc.playerController.syncCurrentPlayItem();
    }

    public void setup() {
        switchBack = registerBoolean("Switch Back", "SwitchBack", false);
    }

    public void onUpdate() {

        if (!switchBack.getValue())
            shouldMoveBack = false;

        if (mc.currentScreen != null || !switchBack.getValue()) return;

        boolean mouse = Mouse.isButtonDown(0);
        if (mouse && !shouldMoveBack) {
            lastChange = System.currentTimeMillis();
            shouldMoveBack = true;
            lastSlot = mc.player.inventory.currentItem;
            mc.playerController.syncCurrentPlayItem();
        } else if (!mouse && shouldMoveBack) {
            shouldMoveBack = false;
            mc.player.inventory.currentItem = lastSlot;
            mc.playerController.syncCurrentPlayItem();
        }

    }

    private void equipBestTool(IBlockState blockState) {
        int bestSlot = -1;
        double max = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            float speed = stack.getDestroySpeed(blockState);
            int eff;
            if (speed > 1) {
                speed += ((eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)) > 0 ? (Math.pow(eff, 2) + 1) : 0);
                if (speed > max) {
                    max = speed;
                    bestSlot = i;
                }
            }
        }
        if (bestSlot != -1) equip(bestSlot);
    }

    public void onEnable() {
        GameSenseMod.EVENT_BUS.subscribe(this);
    }

    public void onDisable() {
        GameSenseMod.EVENT_BUS.unsubscribe(this);
    }
}
