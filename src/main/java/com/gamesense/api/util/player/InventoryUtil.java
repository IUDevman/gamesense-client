package com.gamesense.api.util.player;

import com.gamesense.client.module.modules.combat.OffHand;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class InventoryUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int findObsidianSlot(boolean offHandActived, boolean activedBefore) {
        int slot = -1;

        if (offHandActived && OffHand.isActive()) {
            if (!activedBefore) {
                OffHand.requestObsidian();
            }
            return 9;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockObsidian) {
                slot = i;
                break;
            }
        }
        return slot;
    }
}