package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

public class AutoTotem extends Module {

	public AutoTotem() {
		super("AutoTotem", Category. Combat);
	}

	int totems;
	boolean moving = false;
	boolean returnI = false;
	Setting.Boolean soft;

	public void setup() {
		soft = registerBoolean("Soft", true);
	}

	@Override
	public void onUpdate() {
		if (mc.currentScreen instanceof GuiContainer) return;
		if (returnI) {
			int t = -1;
			for (int i = 0; i < 45; i++)
				if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
					t = i;
					break;
				}
			if (t == -1) return;
			mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
			returnI = false;
		}
		totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
		if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) totems++;
		else {
			if (soft.getValue() && !AutoTotem.mc.player.getHeldItemOffhand().isEmpty()) return;
			if (moving) {
				mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
				moving = false;
				if (!mc.player.inventory.getItemStack().isEmpty()) returnI = true;
				return;
			}
			if (mc.player.inventory.getItemStack().isEmpty()) {
				if (totems == 0) return;
				int t = -1;
				for (int i = 0; i < 45; i++)
					if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
						t = i;
						break;
					}
				if (t == -1) return; // Should never happen!
				mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
				moving = true;
			}
			else if (!soft.getValue()) {
				int t = -1;
				for (int i = 0; i < 45; i++)
					if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
						t = i;
						break;
					}
				if (t == -1) return;
				mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
			}
		}
	}

	@Override
	public String getHudInfo() {
		String t = "[" + ChatFormatting.WHITE + totems + ChatFormatting.GRAY + "]";
		return t;
	}
}