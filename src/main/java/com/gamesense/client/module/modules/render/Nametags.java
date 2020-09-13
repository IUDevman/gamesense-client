package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.players.enemy.Enemies;
import com.gamesense.api.players.friends.Friends;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.GSColor;
import com.gamesense.api.util.Wrapper;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.hud.ColorMain;
import com.gamesense.client.module.modules.hud.HUD;
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
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Iterator;

/**
 * Author: CyberTF2
 * Check com.gamesense.api.mixin.mixins.MixinNametags
 * Edited by Hoosiers :D
 */

public class Nametags extends Module {
    Setting.Boolean durability;
    Setting.Boolean armor;
    Setting.Boolean enchantnames;
    Setting.Boolean itemName;
    Setting.Boolean gamemode;
    Setting.Boolean health;
    Setting.Boolean ping;
    Setting.Boolean entityId;
    Setting.Boolean customColor;
    Setting.ColorSetting borderColor;

    public Nametags() {
        super("Nametags", Category.Render);
    }

    public static double timerPos(final double n, final double n2) {
        return n2 + (n - n2) * Wrapper.getMinecraft().timer.renderPartialTicks;
    }

    public static Vec3d renderPosEntity(final Entity entity) {
        return new Vec3d(timerPos(entity.posX, entity.lastTickPosX) - mc.getRenderManager().renderPosX, timerPos(entity.posY, entity.lastTickPosY) - mc.getRenderManager().renderPosY, timerPos(entity.posZ, entity.lastTickPosZ) - mc.getRenderManager().renderPosZ);
    }

    public static Vec3d M2(final Entity entity, final Vec3d vec3d) {
        return location4(entity, vec3d.x, vec3d.y, vec3d.z);
    }

    public static Vec3d location1(final Entity entity, final Vec3d vec3d) {
        return location4(entity, vec3d.x, vec3d.y, vec3d.z);
    }

    public static Vec3d location3(final Entity entity, final double n) {
        return location4(entity, n, n, n);
    }

    public static Vec3d location4(final Entity entity, final double n, final double n2, final double n3) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * n, (entity.posY - entity.lastTickPosY) * n2, (entity.posZ - entity.lastTickPosZ) * n3);
    }

    public static Vec3d location5(final Entity entity, final float n) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(location3(entity, n));
    }

    public static void M(final float n) {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_CULL_FACE);
        mc.entityRenderer.enableLightmap();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glLineWidth(n);
    }

    public static void drawBorderedRectReliant(final float x, final float y, final float x1, final float y1, final float lineWidth, final GSColor inside, final GSColor border) {
        enableGL2D();
        drawRect(x, y, x1, y1, inside);
        border.glColor();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        disableGL2D();
    }

    public static void enableGL2D() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
    }

    public static void disableGL2D() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
    }

    public static void drawRect(final Rectangle rectangle, final GSColor color) {
        drawRect((float) rectangle.x, (float) rectangle.y, (float) (rectangle.x + rectangle.width), (float) (rectangle.y + rectangle.height), color);
    }

    public static void drawRect(final float x, final float y, final float x1, final float y1, final GSColor color) {
        enableGL2D();
        color.glColor();
        drawRect(x, y, x1, y1);
        disableGL2D();
    }

    public static void drawRect(final float x, final float y, final float x1, final float y1) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }

    public void setup() {
        durability = registerBoolean("Durability", "Durability", true);
        armor = registerBoolean("Armor", "Armor", true);
        enchantnames = registerBoolean("Enchants", "Enchants", true);
        itemName = registerBoolean("Item Name", "ItemName", false);
        gamemode = registerBoolean("Gamemode", "Gamemode", false);
        health = registerBoolean("Health", "Health", true);
        ping = registerBoolean("Ping", "Ping", false);
        entityId = registerBoolean("Entity Id", "EntityId", false);
        customColor = registerBoolean("Custom Color", "CustomColor", true);
        borderColor = registerColor("Border Color", "BorderColor");
    }

    public void onWorldRender(RenderEvent event) {
        for (Object o : mc.world.playerEntities) {
            final Entity entity = (Entity) o;
            if (entity instanceof EntityPlayer && entity != mc.player && entity.isEntityAlive()) {
                double x = interpolate(entity.lastTickPosX, entity.posX, event.getPartialTicks()) - mc.getRenderManager().renderPosX;
                double y = interpolate(entity.lastTickPosY, entity.posY, event.getPartialTicks()) - mc.getRenderManager().renderPosY;
                double z = interpolate(entity.lastTickPosZ, entity.posZ, event.getPartialTicks()) - mc.getRenderManager().renderPosZ;
                Vec3d m = renderPosEntity(entity);
                renderNameTagsFor((EntityPlayer) entity, m.x, m.y, m.z);
            }
        }
    }

    public void renderNameTagsFor(final EntityPlayer entityPlayer, final double n, final double n2, final double n3) {
        renderNametags(entityPlayer, n, n2, n3);
    }

    private double interpolate(final double previous, final double current, final float delta) {
        return previous + (current - previous) * delta;
    }

    private void renderItemName(ItemStack itemStack, int x, int y) {
        float n3 = 0.5f;
        float n4 = 0.5f;
        GlStateManager.scale(n4, n3, n4);
        GlStateManager.disableDepth();
        String displayName = itemStack.getDisplayName();
        final String s2 = displayName;
        FontUtils.drawStringWithShadow(HUD.customFont.getValue(), s2, -FontUtils.getStringWidth(HUD.customFont.getValue(), s2) / 2, y, new GSColor(255, 255, 255));
        GlStateManager.enableDepth();
        final float n5 = 2.0f;
        final int n6 = 2;
        GlStateManager.scale((float) n6, n5, (float) n6);
    }

    private void renderEnchants(ItemStack itemStack, int x, int y) {
        y = y;
        final Iterator<Enchantment> iterator2;
        Iterator<Enchantment> iterator = iterator2 = EnchantmentHelper.getEnchantments(itemStack).keySet().iterator();
        while (iterator.hasNext()) {
            final Enchantment enchantment;
            if ((enchantment = iterator2.next()) == null) {
                iterator = iterator2;
            } else {
                final Enchantment enchantment3 = enchantment;
                if (enchantnames.getValue()) {
                    FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.stringForEnchants(enchantment3, EnchantmentHelper.getEnchantmentLevel(enchantment3, itemStack)), (x * 2), y, new GSColor(255, 255, 255));
                } else {
                    return;
                }
                y += 8;
                iterator = iterator2;
            }
        }
        if (itemStack.getItem().equals(Items.GOLDEN_APPLE) && itemStack.hasEffect()) {
            FontUtils.drawStringWithShadow(HUD.customFont.getValue(), "God", (x * 2), y, new GSColor(195, 77, 65));
        }
    }

    //I dont know
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

    private void renderItemDurability(final ItemStack itemStack, final int x, final int y) {
        final int maxDamage = itemStack.getMaxDamage();
        final float n3 = (maxDamage - itemStack.getItemDamage()) / (float) maxDamage;
        float green = ((float) itemStack.getMaxDamage() - (float) itemStack.getItemDamage()) / (float) itemStack.getMaxDamage();
        float red = 1 - green;
        int dmg = 100 - (int) (red * 100);
        final float n4 = 0.5f;
        final float n5 = 0.5f;
        GlStateManager.scale(n5, n4, n5);
        GlStateManager.disableDepth();
        FontUtils.drawStringWithShadow(HUD.customFont.getValue(), new StringBuilder().insert(0, (int) (n3 * 100.0f)).append('%').toString(), (x * 2), y, new GSColor((int) (red * 255), (int) (green * 255), 0));
        GlStateManager.enableDepth();
        final float n6 = 2.0f;
        final int n7 = 2;
        GlStateManager.scale((float) n7, n6, (float) n7);
    }

    private void renderItems(final ItemStack itemStack, final int n, final int n2, final int n3) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        final int n4 = (n3 > 4) ? ((n3 - 4) * 8 / 2) : 0;
        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, n, n2 + n4);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, n, n2 + n4);
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        final float n5 = 0.5f;
        final float n6 = 0.5f;
        GlStateManager.scale(n6, n5, n6);
        GlStateManager.disableDepth();
        this.renderEnchants(itemStack, n, n2 - 24);
        GlStateManager.enableDepth();
        final float n7 = 2.0f;
        final int n8 = 2;
        GlStateManager.scale((float) n8, n7, (float) n8);
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
        final int n4 = FontUtils.getStringWidth(HUD.customFont.getValue(), this.renderEntityName(entityPlayer)) / 2;
        final int n5 = FontUtils.getStringWidth(HUD.customFont.getValue(), this.renderEntityName(entityPlayer)) / 2;
        double n6 = 0.0018 + 0.003F * distance;
        if (distance <= 8.0) {
            n6 = 0.0245;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) n, (float) tempY + 1.4f, (float) n2);
        final float n7 = -mc.getRenderManager().playerViewY;
        final float n8 = 1.0f;
        final float n9 = 0.0f;
        GlStateManager.rotate(n7, n9, n8, n9);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, (float) 0);
        GlStateManager.scale(-n6, -n6, n6);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        EntityPlayer entityPlayer2;
        GlStateManager.enableBlend();
        GSColor color;
        if (customColor.getValue()) color = borderColor.getValue();
        else color = new GSColor(0, 0, 0, 51);
        drawBorderedRectReliant((float) (-n4 - 1), (float) (-mc.fontRenderer.FONT_HEIGHT), (float) (n4 + 2), 1.0f, 1.8f, new GSColor(0, 4, 0, 85), color);
        GlStateManager.disableBlend();
        FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.renderEntityName(entityPlayer), (-n4), (-(mc.fontRenderer.FONT_HEIGHT - 1)), this.renderPing(entityPlayer));
        entityPlayer2 = entityPlayer;
        final ItemStack heldItemMainhand = entityPlayer2.getHeldItemMainhand();
        final ItemStack heldItemOffhand = entityPlayer.getHeldItemOffhand();
        int n10 = 0;
        int n11 = 0;
        boolean b = false;
        GlStateManager.pushMatrix();
        int i = 3;
        int n12 = 3;
        while (i >= 0) {
            final ItemStack itemStack;
            if (!(itemStack = entityPlayer.inventory.armorInventory.get(n12)).isEmpty()) {
                final Boolean j = this.durability.getValue();
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
                k = n15 - (HUD.customFont.getValue() ? FontUtils.getFontHeight(HUD.customFont.getValue()) : mc.fontRenderer.FONT_HEIGHT);
                nametags = this;
            } else {
                if (b) {
                    k -= (HUD.customFont.getValue() ? FontUtils.getFontHeight(HUD.customFont.getValue()) : mc.fontRenderer.FONT_HEIGHT);
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
        final float n21 = 1.0f;
        final double posZ2 = posZ;
        final Entity entity3 = entity;
        final double posY2 = posY;
        entity.posX = posX;
        entity3.posY = posY2;
        entity3.posZ = posZ2;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(n21, 1500000.0f);
        GlStateManager.popMatrix();
    }

    private GSColor renderPing(final EntityPlayer entityPlayer) {
        if (Friends.isFriend(entityPlayer.getName())) {
            return ColorMain.getFriendGSColor();
        }
        if (Enemies.isEnemy(entityPlayer.getName())) {
            return ColorMain.getEnemyGSColor();
        }
        if (entityPlayer.isInvisible()) {
            return new GSColor(128, 128, 128);
        }
        if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()) == null) {
            return new GSColor(239, 1, 71);
        }
        if (entityPlayer.isSneaking()) {
            return new GSColor(255, 153, 0);
        }
        return new GSColor(255, 255, 255);
    }

    private String renderEntityName(final EntityPlayer entityPlayer) {
        String s = entityPlayer.getDisplayName().getFormattedText();
        if (this.entityId.getValue()) {
            s = new StringBuilder().insert(0, s).append(" ID: ").append(entityPlayer.getEntityId()).toString();
        }
        Nametags nametags = null;
        Label_0195:
        {
            if (this.gamemode.getValue()) {
                if (entityPlayer.isCreative()) {
                    s = new StringBuilder().insert(0, s).append(" [C]").toString();
                    nametags = this;
                    break Label_0195;
                }
                if (entityPlayer.isSpectator()) {
                    s = new StringBuilder().insert(0, s).append(" [I]").toString();
                    nametags = this;
                    break Label_0195;
                }
                s = new StringBuilder().insert(0, s).append(" [S]").toString();
            }
            nametags = this;
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
        } else {
            s2 = TextFormatting.DARK_RED.toString();
        }
        return new StringBuilder().insert(0, s).append(s2).append(" ").append((ceil > 0.0) ? Integer.valueOf((int) ceil) : "0").toString();
    }

    private int armorValue(final int n) {
        int n2 = this.armor.getValue() ? -26 : -27;
        if (n > 4) {
            n2 -= (n - 4) * 8;
        }
        return n2;
    }
}