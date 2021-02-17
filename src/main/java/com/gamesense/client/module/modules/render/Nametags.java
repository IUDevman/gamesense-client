package com.gamesense.client.module.modules.render;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.player.enemy.Enemies;
import com.gamesense.api.util.player.friend.Friends;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ColorMain;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

/**
 * Author: CyberTF2
 * Edited by Hoosiers :D
 */

public class Nametags extends Module {

	public Nametags() {
		super("Nametags", Category.Render);
	}

	Setting.Integer range;
	Setting.Boolean durability;
	Setting.Boolean armor;
	Setting.Boolean enchantnames;
	Setting.Boolean itemName;
	Setting.Boolean gamemode;
	Setting.Boolean health;
	Setting.Boolean ping;
	Setting.Boolean entityId;
	public static Setting.Boolean customColor;
	public static Setting.ColorSetting borderColor;

	public void setup() {
		range = registerInteger("Range", 100, 10, 260);
		durability = registerBoolean("Durability", true);
		armor = registerBoolean("Armor", true);
		enchantnames = registerBoolean("Enchants", true);
		itemName = registerBoolean("Item Name", false);
		gamemode = registerBoolean("Gamemode", false);
		health = registerBoolean("Health", true);
		ping = registerBoolean("Ping", false);
		entityId = registerBoolean("Entity Id", false);
		customColor = registerBoolean("Custom Color", true);
		borderColor = registerColor("Border Color");
	}

	public void onWorldRender(RenderEvent event) {
		for (Object o : mc.world.playerEntities) {
			final Entity entity = (Entity) o;
			if (entity instanceof EntityPlayer && entity != mc.player && entity.isEntityAlive() && entity.getDistance(mc.player) <= range.getValue()) {
				Vec3d m = renderPosEntity(entity);
				renderNameTagsFor((EntityPlayer) entity, m.x, m.y, m.z);
			}
		}
	}

	public void renderNameTagsFor(final EntityPlayer entityPlayer, final double n, final double n2, final double n3) {
		renderNametags(entityPlayer, n, n2, n3);
	}

	public static double timerPos(final double n, final double n2) {
		return n2 + (n - n2) * mc.timer.renderPartialTicks;
	}

	public static Vec3d renderPosEntity(final Entity entity) {
		return new Vec3d(timerPos(entity.posX, entity.lastTickPosX), timerPos(entity.posY, entity.lastTickPosY), timerPos(entity.posZ, entity.lastTickPosZ));
	}

	private void renderEnchants(ItemStack itemStack, int x, int y) {
		GlStateManager.enableTexture2D();
		final Iterator<Enchantment> iterator2;
		Iterator<Enchantment> iterator = iterator2 = EnchantmentHelper.getEnchantments(itemStack).keySet().iterator();
		while (iterator.hasNext()) {
			final Enchantment enchantment;
			if ((enchantment = iterator2.next()) == null) {
				iterator = iterator2;
			}
			else {
				final Enchantment enchantment3 = enchantment;
				if (enchantnames.getValue()) {
					FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), this.stringForEnchants(enchantment3, EnchantmentHelper.getEnchantmentLevel(enchantment3, itemStack)), (x * 2), y, new GSColor(255,255,255));
				}
				else {
					return;
				}
				y += 8;
				iterator = iterator2;
			}
		}
		if (itemStack.getItem().equals(Items.GOLDEN_APPLE) && itemStack.hasEffect()) {
			FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), "God", (x * 2), y,new GSColor(195,77,65));
		}
		GlStateManager.disableTexture2D();
	}

	private String stringForEnchants(final Enchantment enchantment, final int n) {
		final ResourceLocation resourceLocation;
		String substring = ((resourceLocation = Enchantment.REGISTRY.getNameForObject(enchantment)) == null) ? enchantment.getName() : resourceLocation.toString();
		final int n2 = (n > 1) ? 12 : 13;
		if (substring.length() > n2) {
			substring = substring.substring(10, n2);
		}
		final StringBuilder sb = new StringBuilder();
		final String s = substring;
		final int n3 = 0;
		String s2 = sb.insert(n3, s.substring(n3, 1).toUpperCase()).append(substring.substring(1)).toString();
		if (n > 1) {
			s2 = new StringBuilder().insert(0, s2).append(n).toString();
		}
		return s2;
	}
	
	private void renderItemName(ItemStack itemStack, int x, int y) {
		GlStateManager.enableTexture2D();
		GlStateManager.pushMatrix();
		GlStateManager.scale(.5,.5,.5);
		FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), itemStack.getDisplayName(), -FontUtil.getStringWidth(ColorMain.customFont.getValue(), itemStack.getDisplayName()) /2, y, new GSColor(255,255,255));
		GlStateManager.popMatrix();
		GlStateManager.disableTexture2D();
	}

	private void renderItemDurability(final ItemStack itemStack, final int x, final int y) {
		final int maxDamage = itemStack.getMaxDamage();
		final float n3 = (maxDamage - itemStack.getItemDamage()) / (float)maxDamage;
		float green = ((float) itemStack.getMaxDamage() - (float) itemStack.getItemDamage()) / (float) itemStack.getMaxDamage();
		if (green>1) green=1;					// Ensure that the color value is in range
		else if (green<0) green=0;
		float red = 1 - green;
		GlStateManager.enableTexture2D();
		GlStateManager.pushMatrix();
		GlStateManager.scale(.5,.5,.5);
		FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(),new StringBuilder().insert(0, (int) (n3 * 100.0f)).append('%').toString(), (x * 2), y, new GSColor((int) (red * 255), (int) (green * 255), 0));
		GlStateManager.popMatrix();
		GlStateManager.disableTexture2D();
	}

	private void renderItems(final ItemStack itemStack, final int n, final int n2, final int n3) {
		GlStateManager.enableTexture2D();
		GlStateManager.depthMask(true);
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
		GlStateManager.enableDepth();
		GlStateManager.disableAlpha();
		final int n4 = (n3 > 4) ? ((n3 - 4) * 8 / 2) : 0;
		mc.getRenderItem().zLevel = -150.0f;
		RenderHelper.enableStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, n, n2 + n4);
		mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, n, n2 + n4);
		RenderHelper.disableStandardItemLighting();
		mc.getRenderItem().zLevel = 0.0f;
		RenderUtil.prepare();		// Restore expected state
		GlStateManager.pushMatrix();
		GlStateManager.scale(.5,.5,.5);
		renderEnchants(itemStack, n, n2 - 24);
		GlStateManager.popMatrix();
	}

	//RENDER THE ACTUAL NAMETAGS
	private void renderNametags(final EntityPlayer entityPlayer, final double n, double distance, final double n2) {
		double tempY = distance;
		tempY += (entityPlayer.isSneaking() ? 0.5 : 0.7);
		final Entity entity2;
		final Entity entity = entity2 = (mc.getRenderViewEntity() == null) ? mc.player : mc.getRenderViewEntity();
		final double posX = entity2.posX;
		final double posY = entity2.posY;
		final double posZ = entity2.posZ;
		final Vec3d m;
		entity2.posX = (m = renderPosEntity(entity2)).x;
		entity2.posY = m.y;
		entity2.posZ = m.z;
		distance = entity.getDistance(n, distance, n2);
		String[] text=new String[1];
		text[0]=renderEntityName(entityPlayer);
		RenderUtil.drawNametag(n,tempY+1.4,n2,text,renderPing(entityPlayer),2);
		// Other stuff
		final ItemStack heldItemMainhand = entityPlayer.getHeldItemMainhand();
		final ItemStack heldItemOffhand = entityPlayer.getHeldItemOffhand();
		int n10 = 0;
		int n11 = 0;
		boolean b = false;
		int i = 3;
		int n12 = 3;
		while (i >= 0) {
			final ItemStack itemStack;
			if (!(itemStack = entityPlayer.inventory.armorInventory.get(n12)).isEmpty()) {
				final boolean j = this.durability.getValue();
				n10 -= 8;
				if (j) {
					b = true;
				}
				final int size;
				if (this.armor.getValue() && (size = EnchantmentHelper.getEnchantments(itemStack).size()) > n11) {
					n11 = size;
				}
			}
			i = --n12;
		}
		if (!heldItemOffhand.isEmpty() && (this.armor.getValue() || (this.durability.getValue() && heldItemOffhand.isItemStackDamageable()))) {
			n10 -= 8;
			if (this.durability.getValue() && heldItemOffhand.isItemStackDamageable()) {
				b = true;
			}
			final int size2;
			if (this.armor.getValue() && (size2 = EnchantmentHelper.getEnchantments(heldItemOffhand).size()) > n11) {
				n11 = size2;
			}
		}
		if (!heldItemMainhand.isEmpty()) {
			final int size3;
			if (this.armor.getValue() && (size3 = EnchantmentHelper.getEnchantments(heldItemMainhand).size()) > n11) {
				n11 = size3;
			}
			int k = this.armorValue(n11);
			if (this.armor.getValue() || (this.durability.getValue() && heldItemMainhand.isItemStackDamageable())) {
				n10 -= 8;
			}
			if (this.armor.getValue()) {
				final ItemStack itemStack2 = heldItemMainhand;
				final int n13 = n10;
				final int n14 = k;
				k -= 32;
				this.renderItems(itemStack2, n13, n14, n11);
			}
			Nametags nametags;
			if (this.durability.getValue() && heldItemMainhand.isItemStackDamageable()) {
				final int n15 = k;
				this.renderItemDurability(heldItemMainhand, n10, k);
				k = n15 - (ColorMain.customFont.getValue() ? FontUtil.getFontHeight(ColorMain.customFont.getValue()) : mc.fontRenderer.FONT_HEIGHT);
				nametags = this;
			}
			else {
				if (b) {
					k -= (ColorMain.customFont.getValue() ? FontUtil.getFontHeight(ColorMain.customFont.getValue()) : mc.fontRenderer.FONT_HEIGHT);
				}
				nametags = this;
			}
			if (nametags.itemName.getValue()) {
				this.renderItemName(heldItemMainhand, n10, k);
			}
			if (this.armor.getValue() || (this.durability.getValue() && heldItemMainhand.isItemStackDamageable())) {
				n10 += 16;
			}
		}
		int l = 3;
		int n16 = 3;
		while (l >= 0) {
			final ItemStack itemStack3;
			if (!(itemStack3 = entityPlayer.inventory.armorInventory.get(n16)).isEmpty()) {
				int m2 = this.armorValue(n11);
				if (this.armor.getValue()) {
					final ItemStack itemStack4 = itemStack3;
					final int n17 = n10;
					final int n18 = m2;
					m2 -= 32;
					this.renderItems(itemStack4, n17, n18, n11);
				}
				if (this.durability.getValue() && itemStack3.isItemStackDamageable()) {
					this.renderItemDurability(itemStack3, n10, m2);
				}
				n10 += 16;
			}
			l = --n16;
		}
		if (!heldItemOffhand.isEmpty()) {
			int m3 = this.armorValue(n11);
			if (this.armor.getValue()) {
				final ItemStack itemStack5 = heldItemOffhand;
				final int n19 = n10;
				final int n20 = m3;
				m3 -= 32;
				this.renderItems(itemStack5, n19, n20, n11);
			}
			if (this.durability.getValue() && heldItemOffhand.isItemStackDamageable()) {
				this.renderItemDurability(heldItemOffhand, n10, m3);
			}
			n10 += 16;
		}
		GlStateManager.popMatrix();
		final double posZ2 = posZ;
		final Entity entity3 = entity;
		final double posY2 = posY;
		entity.posX = posX;
		entity3.posY = posY2;
		entity3.posZ = posZ2;
	}
	
	private GSColor renderPing(final EntityPlayer entityPlayer) {
		if (Friends.isFriend(entityPlayer.getName())) {
			return ColorMain.getFriendGSColor();
		}
		if (Enemies.isEnemy(entityPlayer.getName())){
			return ColorMain.getEnemyGSColor();
		}
		if (entityPlayer.isInvisible()) {
			return new GSColor(128,128,128);
		}
		if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()) == null) {
			return new GSColor(239,1,71);
		}
		if (entityPlayer.isSneaking()) {
			return new GSColor(255,153,0);
		}
		return new GSColor(255,255,255);
	}

	private String renderEntityName(final EntityPlayer entityPlayer) {
		String s = entityPlayer.getDisplayName().getFormattedText();
		if (this.entityId.getValue()) {
			s = new StringBuilder().insert(0, s).append(" ID: ").append(entityPlayer.getEntityId()).toString();
		}
		Label_0195: {
			if (this.gamemode.getValue()) {
				if (entityPlayer.isCreative()) {
					s = new StringBuilder().insert(0, s).append(" [C]").toString();
					break Label_0195;
				}
				if (entityPlayer.isSpectator()) {
					s = new StringBuilder().insert(0, s).append(" [I]").toString();
					break Label_0195;
				}
				s = new StringBuilder().insert(0, s).append(" [S]").toString();
			}
		}
		if (this.ping.getValue() && mc.getConnection() != null && mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()) != null) {
			s = new StringBuilder().insert(0, s).append(" ").append(mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()).getResponseTime()).append("ms").toString();
		}
		if (!this.health.getValue()) {
			return s;
		}
		final double ceil;
		String s2 = TextFormatting.GREEN.toString();
		if ((ceil = Math.ceil(entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount())) > 0.0) {

			if ((entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount()) <= 5) {
				s2 = TextFormatting.RED.toString();
			} else if ((entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount()) > 5 && (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount()) <= 10) {
				s2 = TextFormatting.GOLD.toString();
			} else if ((entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount()) > 10 && (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount()) <= 15) {
				s2 = TextFormatting.YELLOW.toString();
			} else if ((entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount()) > 15 && (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount()) <= 20) {
				s2 = TextFormatting.DARK_GREEN.toString();
			} else if ((entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount()) > 20) {
				s2 = TextFormatting.GREEN.toString();
			}
		}
		else {
			s2 = TextFormatting.DARK_RED.toString();
		}
		return new StringBuilder().insert(0, s).append(s2).append(" ").append((ceil > 0.0) ? Integer.valueOf((int)ceil) : "0").toString();
	}

	private int armorValue(final int n) {
		int n2 = this.armor.getValue() ? -26 : -27;
		if (n > 4) {
			n2 -= (n - 4) * 8;
		}
		return n2;
	}
}
