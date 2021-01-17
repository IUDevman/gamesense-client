package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.stream.Collectors;

public class OffhandCrystal extends Module {

	public int totems;
	int crystals;
	boolean moving;
	boolean returnI;
	Item item;
	Setting.Integer health;
	Setting.Boolean disableGapple;

	public OffhandCrystal() {
		super("OffhandCrystal", Category.Combat);
		this.moving = false;
		this.returnI = false;
	}

	public void setup() {
		disableGapple = registerBoolean("Disable Gap", true);
		health = registerInteger("Health", 15, 0, 36);
	}

	public void onEnable() {
		if (disableGapple.getValue() && ModuleManager.isModuleEnabled("OffhandGap")) {
			ModuleManager.getModuleByName("OffhandGap").disable();
		}
	}

	public void onDisable() {
		if (OffhandCrystal.mc.currentScreen instanceof GuiContainer) {
			return;
		}
		this.crystals = OffhandCrystal.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
		if (OffhandCrystal.mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) {
			if (this.crystals == 0) {
				return;
			}
			int t = -1;
			for (int i = 0; i < 45; i++) {
				if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
					t = i;
					break;
				}
			}
			if (t == -1) {
				return;
			}
			OffhandCrystal.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
			OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
			OffhandCrystal.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
		}
	}

	@Override
	public void onUpdate() {
		this.item = Items.END_CRYSTAL;
		if (OffhandCrystal.mc.currentScreen instanceof GuiContainer) {
			return;
		}
		if (this.returnI) {
			int t = -1;
			for (int i = 0; i < 45; i++) {
				if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).isEmpty()) {
					t = i;
					break;
				}
			}
			if (t == -1) {
				return;
			}
			OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
			this.returnI = false;
		}
		this.totems = OffhandCrystal.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
		this.crystals = OffhandCrystal.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == this.item).mapToInt(ItemStack::getCount).sum();
		if (this.shouldTotem() && OffhandCrystal.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
			this.totems++;
		}
		else if (!this.shouldTotem() && OffhandCrystal.mc.player.getHeldItemOffhand().getItem() == this.item) {
			this.crystals += OffhandCrystal.mc.player.getHeldItemOffhand().getCount();
		}
		else {
			if (this.moving) {
				OffhandCrystal.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
				this.moving = false;
				this.returnI = true;
				return;
			}
			if (OffhandCrystal.mc.player.inventory.getItemStack().isEmpty()) {
				if (!this.shouldTotem() && OffhandCrystal.mc.player.getHeldItemOffhand().getItem() == this.item) {
					return;
				}
				if (this.shouldTotem() && OffhandCrystal.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
					return;
				}
				if (!this.shouldTotem()) {
					if (this.crystals == 0) {
						return;
					}
					int t = -1;
					for (int i = 0; i < 45; i++) {
						if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).getItem() == this.item) {
							t = i;
							break;
						}
					}
					if (t == -1) {
						return;
					}
					OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
					this.moving = true;
				}
				else {
					if (this.totems == 0) {
						return;
					}
					int t = -1;
					for (int i = 0; i < 45; i++) {
						if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
							t = i;
							break;
						}
					}
					if (t == -1) {
						return;
					}
					OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
					this.moving = true;
				}
			}
			else {
				int t = -1;
				for (int i = 0; i < 45; i++) {
					if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).isEmpty()) {
						t = i;
						break;
					}
				}
				if (t == -1) {
					return;
				}
				OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
			}
		}
	}

	private boolean shouldTotem() {
		final boolean hp = OffhandCrystal.mc.player.getHealth() + OffhandCrystal.mc.player.getAbsorptionAmount() <= health.getValue();
		final boolean endcrystal = !this.isCrystalsAABBEmpty();
		return hp;
	}

	private boolean isEmpty(final BlockPos pos) {
		final List<Entity> crystalsInAABB = OffhandCrystal.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).stream().filter(e -> e instanceof EntityEnderCrystal).collect(Collectors.toList());
		return crystalsInAABB.isEmpty();
	}

	private boolean isCrystalsAABBEmpty() {
		return this.isEmpty(OffhandCrystal.mc.player.getPosition().add(1, 0, 0)) && this.isEmpty(OffhandCrystal.mc.player.getPosition().add(-1, 0, 0)) && this.isEmpty(OffhandCrystal.mc.player.getPosition().add(0, 0, 1)) && this.isEmpty(OffhandCrystal.mc.player.getPosition().add(0, 0, -1)) && this.isEmpty(OffhandCrystal.mc.player.getPosition());
	}
}