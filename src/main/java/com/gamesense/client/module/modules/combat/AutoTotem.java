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
		// Esci se hai aperto un contenitore
		if (mc.currentScreen instanceof GuiContainer) return;
		/// DOPO
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
		// Contatore di totem, questo server per l'hud
		totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
		// Se ha un totem in mano
		if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) totems++;
		else {
			// Se non hai un totem in mano
			// Se soft, fà iterare una seconda volta
			if (soft.getValue() && !AutoTotem.mc.player.getHeldItemOffhand().isEmpty()) return;
			if (moving) {
				// Perchè
				mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
				moving = false;
				if (!mc.player.inventory.getItemStack().isEmpty()) returnI = true;
				return;
			}
			// Se non ce l'ha
			if (mc.player.inventory.getItemStack().isEmpty()) {
				// Se non hai totem
				if (totems == 0) return;
				int t = -1;
				// Cerca un totem
				for (int i = 0; i < 45; i++)
					if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
						t = i;
						break;
					}
				// Se non trova un totem
				if (t == -1) return; // Should never happen!
				// Cambia
				mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
				moving = true;
			}
			// Se soft
			else if (!soft.getValue()) {
				// Rifai
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

}