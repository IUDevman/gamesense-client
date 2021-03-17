package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.misc.Pair;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Module.Declaration(name = "HotbarRefill", category = Category.Misc)
public class HotbarRefill extends Module {

    IntegerSetting threshold = registerInteger("Threshold", 32, 1, 63);
    IntegerSetting tickDelay = registerInteger("Tick Delay", 2, 1, 10);

    private int delayStep = 0;

    public void onUpdate() {

        if (mc.player == null) {
            return;
        }

        if (mc.currentScreen instanceof GuiContainer) {
            return;
        }

        if (delayStep < tickDelay.getValue()) {
            delayStep++;
            return;
        } else {
            delayStep = 0;
        }

        Pair<Integer, Integer> slots = findReplenishableHotbarSlot();

        if (slots == null) {
            return;
        }

        int inventorySlot = slots.getKey();
        int hotbarSlot = slots.getValue();

        // pick up inventory slot
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);

        // click on hotbar slot
        // 36 is the offset for start of hotbar in inventoryContainer
        mc.playerController.windowClick(0, hotbarSlot + 36, 0, ClickType.PICKUP, mc.player);

        // put back inventory slot
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
    }

    private Pair<Integer, Integer> findReplenishableHotbarSlot() {
        List<ItemStack> inventory = mc.player.inventory.mainInventory;

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            ItemStack stack = inventory.get(hotbarSlot);

            if (!stack.isStackable()) {
                continue;
            }

            if (stack.isEmpty || stack.getItem() == Items.AIR) {
                continue;
            }

            if (stack.stackSize >= stack.getMaxStackSize() || stack.stackSize > threshold.getValue()) {
                continue;
            }

            int inventorySlot = findCompatibleInventorySlot(stack);

            if (inventorySlot == -1) {
                continue;
            }
            return new Pair<>(inventorySlot, hotbarSlot);
        }
        return null;
    }

    private int findCompatibleInventorySlot(ItemStack hotbarStack) {
        List<Integer> potentialSlots;

        Item item = hotbarStack.getItem();
        if (item instanceof ItemBlock) {
            potentialSlots = InventoryUtil.findAllBlockSlots(((ItemBlock) item).getBlock().getClass());
        } else {
            potentialSlots = InventoryUtil.findAllItemSlots(item.getClass());
        }

        potentialSlots = potentialSlots.stream()
                .filter(integer -> integer > 8 && integer < 36)
                .sorted(Comparator.comparingInt(interger -> -interger))
                .collect(Collectors.toList());

        for (int slot : potentialSlots) {
            if (isCompatibleStacks(hotbarStack, mc.player.inventory.getStackInSlot(slot))) {
                return slot;
            }
        }
        return -1;
    }

    private boolean isCompatibleStacks(ItemStack stack1, ItemStack stack2) {
        // check if not same item
        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        }

        // check if not same block
        if ((stack1.getItem() instanceof ItemBlock) && (stack2.getItem() instanceof ItemBlock)) {
            Block block1 = ((ItemBlock) stack1.getItem()).getBlock();
            Block block2 = ((ItemBlock) stack2.getItem()).getBlock();
            if (!block1.material.equals(block2.material)) {
                return false;
            }
        }

        // check if not same names
        if (!stack1.getDisplayName().equals(stack2.getDisplayName())) {
            return false;
        }

        // check if not same damage (e.g. skulls)
        //noinspection RedundantIfStatement
        if (stack1.getItemDamage() != stack2.getItemDamage()) {
            return false;
        }
        return true;
    }
}