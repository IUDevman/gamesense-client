package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.misc.ColorUtil;
import com.gamesense.api.util.player.enemy.Enemies;
import com.gamesense.api.util.player.friend.Friends;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

/**
 * @author CyberTF2
 * Rewrote by Hoosiers
 */

@Module.Declaration(name = "Nametags", category = Category.Render)
public class Nametags extends Module {

    BooleanSetting renderSelf;
    BooleanSetting showItems;
    BooleanSetting showDurability;
    BooleanSetting showEnchantName;
    BooleanSetting showItemName;
    BooleanSetting showGameMode;
    BooleanSetting showHealth;
    BooleanSetting showPing;
    BooleanSetting showEntityID;
    IntegerSetting range;
    ModeSetting levelColor;
    public static BooleanSetting customColor;
    public static ColorSetting borderColor;

    public void setup() {
        ArrayList<String> tab = new ArrayList<>();
        tab.add("Black");
        tab.add("Dark Green");
        tab.add("Dark Red");
        tab.add("Gold");
        tab.add("Dark Gray");
        tab.add("Green");
        tab.add("Red");
        tab.add("Yellow");
        tab.add("Dark Blue");
        tab.add("Dark Aqua");
        tab.add("Dark Purple");
        tab.add("Gray");
        tab.add("Blue");
        tab.add("Aqua");
        tab.add("Light Purple");
        tab.add("White");

        range = registerInteger("Range", 100, 10, 260);
        renderSelf = registerBoolean("Render Self", false);
        showDurability = registerBoolean("Durability", true);
        showItems = registerBoolean("Items", true);
        showEnchantName = registerBoolean("Enchants", true);
        showItemName = registerBoolean("Item Name", false);
        showGameMode = registerBoolean("Gamemode", false);
        showHealth = registerBoolean("Health", true);
        showPing = registerBoolean("Ping", false);
        showEntityID = registerBoolean("Entity Id", false);
        levelColor = registerMode("Level Color", tab, "Green");
        customColor = registerBoolean("Custom Color", true);
        borderColor = registerColor("Border Color", new GSColor(255, 0, 0, 255));
    }

    public void onWorldRender(RenderEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        mc.world.playerEntities.stream().filter(this::shouldRender).forEach(entityPlayer -> {
            Vec3d vec3d = findEntityVec3d(entityPlayer);
            renderNameTags(entityPlayer, vec3d.x, vec3d.y, vec3d.z);
        });
    }

    private boolean shouldRender(EntityPlayer entityPlayer) {
        if (entityPlayer == mc.player && !renderSelf.getValue()) return false;

        if (entityPlayer.isDead || entityPlayer.getHealth() <= 0) return false;

        return !(entityPlayer.getDistance(mc.player) > range.getValue());
    }

    private Vec3d findEntityVec3d(EntityPlayer entityPlayer) {
        double posX = balancePosition(entityPlayer.posX, entityPlayer.lastTickPosX);
        double posY = balancePosition(entityPlayer.posY, entityPlayer.lastTickPosY);
        double posZ = balancePosition(entityPlayer.posZ, entityPlayer.lastTickPosZ);

        return new Vec3d(posX, posY, posZ);
    }

    private double balancePosition(double newPosition, double oldPosition) {
        return oldPosition + (newPosition - oldPosition) * mc.timer.renderPartialTicks;
    }

    private void renderNameTags(EntityPlayer entityPlayer, double posX, double posY, double posZ) {
        double adjustedY = posY + (entityPlayer.isSneaking() ? 1.9 : 2.1);

        String[] name = new String[1];
        name[0] = buildEntityNameString(entityPlayer);

        RenderUtil.drawNametag(posX, adjustedY, posZ, name, findTextColor(entityPlayer), 2);
        renderItemsAndArmor(entityPlayer, 0, 0);
        GlStateManager.popMatrix();
    }

    private String buildEntityNameString(EntityPlayer entityPlayer) {
        String name = entityPlayer.getName();

        if (showEntityID.getValue()) {
            name = name + " ID: " + entityPlayer.getEntityId();
        }

        if (showGameMode.getValue()) {
            if (entityPlayer.isCreative()) {
                name = name + " [C]";
            }
            else if (entityPlayer.isSpectator()) {
                name = name + " [I]";
            }
            else {
                name = name + " [S]";
            }
        }

        if (showPing.getValue()) {
            int value = 0;

            if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()) != null) {
                value = mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()).getResponseTime();
            }

            name = name + " " + value + "ms";
        }

        if (showHealth.getValue()) {
            int health = (int) (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount());
            TextFormatting textFormatting = findHealthColor(health);

            name = name + " " + textFormatting + health;
        }

        return name;
    }

    private TextFormatting findHealthColor(int health) {
        if (health <= 0) {
            return TextFormatting.DARK_RED;
        } else if (health <= 5) {
            return TextFormatting.RED;
        } else if (health <= 10) {
            return TextFormatting.GOLD;
        } else if (health <= 15) {
            return TextFormatting.YELLOW;
        } else if (health <= 20) {
            return TextFormatting.DARK_GREEN;
        }

        return TextFormatting.GREEN;
    }

    private GSColor findTextColor(EntityPlayer entityPlayer) {
        if (Friends.isFriend(entityPlayer.getName())) {
            return ColorMain.getFriendGSColor();
        } else if (Enemies.isEnemy(entityPlayer.getName())) {
            return ColorMain.getEnemyGSColor();
        } else if (entityPlayer.isInvisible()) {
            return new GSColor(128, 128, 128);
        } else if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()) == null) {
            return new GSColor(239, 1, 71);
        } else if (entityPlayer.isSneaking()) {
            return new GSColor(255, 153, 0);
        }

        return new GSColor(255, 255, 255);
    }

    private void renderItemsAndArmor(EntityPlayer entityPlayer, int posX, int posY) {
        ItemStack mainHandItem = entityPlayer.getHeldItemMainhand();
        ItemStack offHandItem = entityPlayer.getHeldItemOffhand();

        int armorCount = 3;
        for (int i = 0; i <= 3; i++) {
            ItemStack itemStack = entityPlayer.inventory.armorInventory.get(armorCount);

            if (!itemStack.isEmpty()) {
                posX -= 8;

                int size = EnchantmentHelper.getEnchantments(itemStack).size();

                if (showItems.getValue() && size > posY) {
                    posY = size;
                }
            }
            armorCount --;
        }

        if (!mainHandItem.isEmpty() && (showItems.getValue() || showDurability.getValue() && offHandItem.isItemStackDamageable())) {
            posX -= 8;

            int enchantSize = EnchantmentHelper.getEnchantments(offHandItem).size();
            if (showItems.getValue() && enchantSize > posY) {
                posY = enchantSize;
            }
        }

        if (!mainHandItem.isEmpty()) {

            int enchantSize = EnchantmentHelper.getEnchantments(mainHandItem).size();

            if (showItems.getValue() && enchantSize > posY) {
                posY = enchantSize;
            }

            int armorY = findArmorY(posY);

            if (showItems.getValue() || (showDurability.getValue() && mainHandItem.isItemStackDamageable())) {
                posX -= 8;
            }

            if (showItems.getValue()) {
                renderItem(mainHandItem, posX, armorY, posY);
                armorY -= 32;
            }

            if (showDurability.getValue() && mainHandItem.isItemStackDamageable()) {
                renderItemDurability(mainHandItem, posX, armorY);
            }

            armorY -= (ColorMain.customFont.getValue() ? FontUtil.getFontHeight(ColorMain.customFont.getValue()) : mc.fontRenderer.FONT_HEIGHT);

            if (showItemName.getValue()) {
                renderItemName(mainHandItem, armorY);
            }

            if (showItems.getValue() || (showDurability.getValue() && mainHandItem.isItemStackDamageable())) {
                posX += 16;
            }
        }

        int armorCount2 = 3;
        for (int i = 0; i <= 3; i++) {
            ItemStack itemStack = entityPlayer.inventory.armorInventory.get(armorCount2);

            if (!itemStack.isEmpty()) {
                int armorY = findArmorY(posY);

                if (showItems.getValue()) {
                    renderItem(itemStack, posX, armorY, posY);
                    armorY -= 32;
                }

                if (showDurability.getValue() && itemStack.isItemStackDamageable()) {
                    renderItemDurability(itemStack, posX, armorY);
                }
                posX += 16;
            }
            armorCount2--;
        }

        if (!offHandItem.isEmpty()) {
            int armorY = findArmorY(posY);

            if (showItems.getValue()) {
                renderItem(offHandItem, posX, armorY, posY);
                armorY -= 32;
            }

            if (showDurability.getValue() && offHandItem.isItemStackDamageable()) {
                renderItemDurability(offHandItem, posX, armorY);
            }
        }
    }

    private int findArmorY(int posY) {
        int posY2 = showItems.getValue() ? -26 : -27;
        if (posY > 4) {
            posY2 -= (posY - 4) * 8;
        }

        return posY2;
    }

    private void renderItemName(ItemStack itemStack, int posY) {
        GlStateManager.enableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), itemStack.getDisplayName(), -FontUtil.getStringWidth(ColorMain.customFont.getValue(), itemStack.getDisplayName()) / 2, posY, new GSColor(255, 255, 255));
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
    }

    private void renderItemDurability(ItemStack itemStack, int posX, int posY) {
        float damagePercent = (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (float) itemStack.getMaxDamage();

        float green = damagePercent;
        if (green > 1) green = 1;
        else if (green < 0) green = 0;

        float red = 1 - green;

        GlStateManager.enableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), (int) (damagePercent * 100) + "%", posX * 2, posY, new GSColor((int) (red * 255), (int) (green * 255), 0));
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
    }

    private void renderItem(ItemStack itemStack, int posX, int posY, int posY2) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();

        final int posY3 = (posY2 > 4) ? ((posY2 - 4) * 8 / 2) : 0;

        mc.getRenderItem().zLevel = -150.0f;
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, posX, posY + posY3);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, posX, posY + posY3);
        RenderHelper.disableStandardItemLighting();
        mc.getRenderItem().zLevel = 0.0f;
        RenderUtil.prepare();
        GlStateManager.pushMatrix();
        GlStateManager.scale(.5, .5, .5);
        renderEnchants(itemStack, posX, posY - 24);
        GlStateManager.popMatrix();
    }

    private void renderEnchants(ItemStack itemStack, int posX, int posY) {
        GlStateManager.enableTexture2D();

        for (Enchantment enchantment : EnchantmentHelper.getEnchantments(itemStack).keySet()) {
            if (enchantment == null) {
                continue;
            }

            if (showEnchantName.getValue()) {
                int level = EnchantmentHelper.getEnchantmentLevel(enchantment, itemStack);
                FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), findStringForEnchants(enchantment, level), posX * 2, posY, new GSColor(255, 255, 255));
            }
            posY += 8;
        }

        if (itemStack.getItem().equals(Items.GOLDEN_APPLE) && itemStack.hasEffect()) {
            FontUtil.drawStringWithShadow(ColorMain.customFont.getValue(), "God", posX * 2, posY, new GSColor(195, 77, 65));
        }

        GlStateManager.disableTexture2D();
    }

    private String findStringForEnchants(Enchantment enchantment, int level) {
        ResourceLocation resourceLocation = Enchantment.REGISTRY.getNameForObject(enchantment);

        String string = resourceLocation == null ? enchantment.getName() : resourceLocation.toString();

        int charCount = (level > 1) ? 12 : 13;

        if (string.length() > charCount) {
            string = string.substring(10, charCount);
        }

        return string.substring(0, 1).toUpperCase() + string.substring(1) + ColorUtil.settingToTextFormatting(levelColor) + ((level > 1) ? level : "");
    }
}