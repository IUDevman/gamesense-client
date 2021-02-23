package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.Pair;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.friend.Friends;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author GameSense client for original code (actual author unknown)
 * @source https://github.com/IUDevman/gamesense-client/blob/2.2.6/src/main/java/com/gamesense/client/module/modules/combat/KillAura.java
 * @author 0b00101010
 * @since 07/02/2021
 */
public class KillAura extends Module {

	public KillAura() {
		super("KillAura", Category.Combat);
	}

	Setting.Boolean players;
	Setting.Boolean hostileMobs;
	Setting.Boolean passiveMobs;
	Setting.Mode itemUsed;
	Setting.Boolean autoSwitch;
	Setting.Boolean swordPriority;
	Setting.Boolean caCheck;
	Setting.Boolean criticals;
	Setting.Double range;
	Setting.Double switchHealth;

	public void setup() {
		List<String> weapons = new ArrayList<>();
		weapons.add("Sword");
		weapons.add("Axe");
		weapons.add("Both");
		weapons.add("All");

		players = registerBoolean("Players", true);
		passiveMobs = registerBoolean("Animals", false);
		hostileMobs = registerBoolean("Monsters", false);
		range = registerDouble("Range", 5,0,10);
		itemUsed = registerMode("Item used", weapons, "Sword");
		autoSwitch = registerBoolean("Switch", false);
		switchHealth = registerDouble("Min Switch Health", 0f, 0f, 20f);
		swordPriority = registerBoolean("Prioritise Sword", true);
		criticals = registerBoolean("Criticals",true);
		caCheck = registerBoolean("AC Check",false);
	}

	private boolean isAttacking = false;

	public void onUpdate() {
		if (mc.player == null || mc.player.isDead) return;

		final double rangeSq = range.getValue() * range.getValue();
		List<Entity> targets = mc.world.loadedEntityList.stream()
				.filter(entity -> entity instanceof EntityLivingBase)
				.filter(entity -> !EntityUtil.basicChecksEntity(entity))
				.filter(entity -> mc.player.getDistanceSq(entity) <= rangeSq)
				.filter(this::attackCheck)
				.sorted(Comparator.comparing(e -> mc.player.getDistanceSq(e)))
				.collect(Collectors.toList());

		boolean sword = itemUsed.getValue().equalsIgnoreCase("Sword");
		boolean axe = itemUsed.getValue().equalsIgnoreCase("Axe");
		boolean both = itemUsed.getValue().equalsIgnoreCase("Both");
		boolean all = itemUsed.getValue().equalsIgnoreCase("All");
		// only attack one entity per cycle
		Optional<Entity> first = targets.stream().findFirst();
		if (first.isPresent()) {
			Pair<Float, Integer> newSlot = new Pair<>(0f, -1);
			if (autoSwitch.getValue() && (mc.player.getHealth() + mc.player.getAbsorptionAmount() >= switchHealth.getValue())) {
				// find the best weapon in out hotbar
				if (sword || both || all) {
					newSlot = findSwordSlot();
				}
				if ((axe || both || all) && !(swordPriority.getValue() && newSlot.getValue() != -1)) {
					Pair<Float, Integer> possibleSlot = findAxeSlot();
					if (possibleSlot.getKey() > newSlot.getKey()) {
						newSlot = possibleSlot;
					}
				}
			}

			// we have found a slot
			int temp = mc.player.inventory.currentItem;
			if ((newSlot.getValue() != -1)) {
				mc.player.inventory.currentItem = newSlot.getValue();
			}

			// we have to switch slots for this check to work
			if (shouldAttack(sword, axe, both, all)) {
				attack(first.get());
			} else {
				// if check is false switch back
				mc.player.inventory.currentItem = temp;
			}
		}
	}

	@EventHandler
	private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
		if (event.getPacket() instanceof CPacketUseEntity){
			if (criticals.getValue() && ((CPacketUseEntity) event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && mc.player.onGround && isAttacking) {
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1f, mc.player.posZ, false));
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
			}
		}
	});

	private Pair<Float, Integer> findSwordSlot() {
		List<Integer> items = InventoryUtil.findAllItemSlots(ItemSword.class);
		List<ItemStack> inventory = mc.player.inventory.mainInventory;

		float bestModifier = 0f;
		int correspondingSlot = -1;
		for (Integer integer : items) {
			if (integer > 8) {
				continue;
			}

			ItemStack stack = inventory.get(integer);
			// generic best modifier
			float modifier = (EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED) + 1f) * ((ItemSword)stack.getItem()).getAttackDamage();
			// is it the new best
			if (modifier > bestModifier) {
				bestModifier = modifier;
				correspondingSlot = integer;
			}
		}

		return new Pair<>(bestModifier, correspondingSlot);
	}

	private Pair<Float, Integer> findAxeSlot() {
		List<Integer> items = InventoryUtil.findAllItemSlots(ItemAxe.class);
		List<ItemStack> inventory = mc.player.inventory.mainInventory;

		float bestModifier = 0f;
		int correspondingSlot = -1;
		for (Integer integer : items) {
			if (integer > 8) {
				continue;
			}

			ItemStack stack = inventory.get(integer);
			// generic best modifier
			float modifier = (EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED) + 1f) * ((ItemAxe)stack.getItem()).attackDamage;
			// is it the new best
			if (modifier > bestModifier) {
				bestModifier = modifier;
				correspondingSlot = integer;
			}
		}

		return new Pair<>(bestModifier, correspondingSlot);
	}

	private boolean shouldAttack(boolean sword, boolean axe, boolean both, boolean all) {
		Item item = mc.player.getHeldItemMainhand().getItem();
		if (((sword || both) && item instanceof ItemSword) ||
				((axe || both) && item instanceof ItemAxe)
				|| all) {
			return !caCheck.getValue() || !ModuleManager.getModule(AutoCrystalGS.class).isActive;
		}

		return false;
	}

	private void attack(Entity e) {
		if (mc.player.getCooledAttackStrength(0) >= 1) {
			isAttacking = true;
			mc.playerController.attackEntity(mc.player, e);
			mc.player.swingArm(EnumHand.MAIN_HAND);
			isAttacking = false;
		}
	}

	private boolean attackCheck(Entity entity) {
		if (players.getValue() && entity instanceof EntityPlayer && !Friends.isFriend(entity.getName())) {
			if (((EntityPlayer) entity).getHealth() > 0) {
				return true;
			}
		}

		if (passiveMobs.getValue() && entity instanceof EntityAnimal) {
			return !(entity instanceof EntityTameable);
		}

		return hostileMobs.getValue() && entity instanceof EntityMob;
	}
}