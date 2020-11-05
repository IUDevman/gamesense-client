package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.api.util.players.enemy.Enemies;
import com.gamesense.api.util.players.friends.Friends;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;

import java.util.Comparator;
import java.util.Objects;

/**
 * @Author Hoosiers on 10/19/2020
 * update by linustouchtips on 10/27/2020
 */

public class TargetHUD extends Module {
    public TargetHUD(){
        super("TargetHUD", Category.HUD);
    }

    Setting.ColorSetting outline;
    Setting.ColorSetting background;
    Setting.Integer posX;
    Setting.Integer posY;
    Setting.Integer range;

    public void setup(){
        range = registerInteger("Range", "Range", 100, 1, 260);
        posX = registerInteger("X", "X", 0, 0, 1000);
        posY = registerInteger("Y", "Y", 70, 0, 1000);
        outline = registerColor("Outline", "Outline", new GSColor(255, 0, 0, 255));
        background = registerColor("Background", "Background", new GSColor(0, 0, 0, 255));
    }

    GSColor outlineColor;
    GSColor backgroundColor;
    GSColor nameColor;
    GSColor healthColor;
    TextFormatting playercolor;
    String playerinfo;
    float ping;

    public void onRender(){
        if (mc.world != null && mc.player.ticksExisted >= 10) {
            backgroundColor = new GSColor(background.getValue(), 100);
            outlineColor = new GSColor(outline.getValue(), 255);

            EntityPlayer entityPlayer = (EntityPlayer) mc.world.loadedEntityList.stream()
                    .filter(entity -> IsValidEntity(entity))
                    .map(entity -> (EntityLivingBase) entity)
                    .min(Comparator.comparing(c -> mc.player.getDistance(c)))
                    .orElse(null);

            if (entityPlayer == null)
                return;

            if (entityPlayer != null) {
                String playerName = entityPlayer.getName();
                int playerHealth = (int) (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount());
                findNameColor(playerName);
                findHealthColor(playerHealth);

                //player model
                drawEntityPlayer(entityPlayer, posX.getValue() + 35, posY.getValue() + 87 - (entityPlayer.isSneaking()?10:0));

                //box
                drawTargetBox();

                //player name
                FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), TextFormatting.BOLD + playerName, posX.getValue() + 71, posY.getValue() + 11, nameColor);

                //health + absorption
                FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), TextFormatting.WHITE + "Health: " + TextFormatting.RESET + playerHealth, posX.getValue() + 71, posY.getValue() + 23, healthColor);

                //distance
                FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "Distance: " + ((int) entityPlayer.getDistance(mc.player)), posX.getValue() + 71, posY.getValue() + 33, new GSColor(255, 255, 255, 255));

                //status effects
                drawStatusEffects(entityPlayer, posX.getValue(), posY.getValue());

                //armor + items
                drawItemTextures(entityPlayer, posX.getValue() + 58, posY.getValue() + 73);

                //player info
                drawPlayerInfo(entityPlayer, posX.getValue() + 71, posY.getValue() + 43);
            }
        }
    }

    public void drawTargetBox(){
        //outline
        Gui.drawRect(posX.getValue() + 1, posY.getValue() + 1, posX.getValue() + 161, posY.getValue() + 93, backgroundColor.getRGB());

        //top
        Gui.drawRect(posX.getValue(), posY.getValue(), posX.getValue() + 162, posY.getValue() + 1, outlineColor.getRGB());
        //bottom
        Gui.drawRect(posX.getValue(), posY.getValue() + 93, posX.getValue() + 162, posY.getValue() + 94, outlineColor.getRGB());
        //left
        Gui.drawRect(posX.getValue(), posY.getValue(), posX.getValue() + 1, posY.getValue() + 94, outlineColor.getRGB());
        //right
        Gui.drawRect(posX.getValue() + 161, posY.getValue(), posX.getValue() + 162, posY.getValue() + 94, outlineColor.getRGB());
    }

    public void drawEntityPlayer(EntityPlayer entityPlayer, int x, int y){
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GuiInventory.drawEntityOnScreen(x, y, 43, 28, 60, entityPlayer);
        GlStateManager.popMatrix();
    }

    public void drawPlayerInfo(EntityPlayer entityPlayer, int x, int y) {

        if (entityPlayer.inventory.armorItemInSlot(2).getItem().equals(Items.ELYTRA)) {
            playerinfo = "Wasp";
            playercolor = TextFormatting.LIGHT_PURPLE;
        }
        else if (entityPlayer.inventory.armorItemInSlot(2).getItem().equals(Items.DIAMOND_CHESTPLATE)) {
            playerinfo = "Threat";
            playercolor = TextFormatting.RED;
        }
        else if (entityPlayer.inventory.armorItemInSlot(3).getItem().equals(Items.AIR)) {
            playerinfo = "NewFag";
            playercolor = TextFormatting.GREEN;
        }
        else {
            playerinfo = "None";
            playercolor = TextFormatting.WHITE;
        }

        ping = getPing(entityPlayer);
        FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), playercolor + playerinfo + TextFormatting.WHITE + " | "  + ping + " ms", x, y, new GSColor(255, 255, 255));
    }

    //having more than one of these displayed at once makes things too crowded
    GSColor statusColor = new GSColor(255, 255, 255, 255);
    public void drawStatusEffects(EntityPlayer entityPlayer, int x, int y){
        int inX = x + 71;
        int inY = y + 55;

        entityPlayer.getActivePotionEffects().forEach(potionEffect -> {
            findPotionColor(potionEffect);

            if (potionEffect.getPotion() == MobEffects.WEAKNESS) {
                FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), TextFormatting.WHITE + "Status: " + TextFormatting.RESET + "Weakness!", inX, inY, statusColor);
            }
            else if (potionEffect.getPotion() == MobEffects.INVISIBILITY){
                FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), TextFormatting.WHITE + "Status: " + TextFormatting.RESET + "Invisible!", inX, inY, statusColor);
            }
            else if (potionEffect.getPotion() == MobEffects.STRENGTH){
                FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), TextFormatting.WHITE + "Status: " + TextFormatting.RESET + "Strength!", inX, inY, statusColor);
            }
        });
    }

    private static final RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
    public void drawItemTextures(EntityPlayer entityPlayer, int x, int y){
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();

        int iteration = 0;
        for (ItemStack itemStack : entityPlayer.getArmorInventoryList()) {
            iteration++;
            if (itemStack.isEmpty()) continue;
            int inX = x - 90 + (9 - iteration) * 20 + 2;

            itemRender.zLevel = 200F;
            itemRender.renderItemAndEffectIntoGUI(itemStack, inX, y);
            itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, inX, y, "");
            itemRender.zLevel = 0F;
        }

        RenderHelper.disableStandardItemLighting();
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.popMatrix();
    }

    public void findPotionColor(PotionEffect potionEffect){
        if (potionEffect.getPotion() == MobEffects.STRENGTH){
            statusColor = new GSColor(135, 0, 25, 255);
        }
        else if (potionEffect.getPotion() == MobEffects.WEAKNESS){
            statusColor = new GSColor(185, 65, 185, 255);
        }
        else if (potionEffect.getPotion() == MobEffects.INVISIBILITY){
            statusColor = new GSColor(90, 90, 90, 255);
        }
    }

    public void findNameColor(String playerName){
        if (Friends.isFriend(playerName)){
            nameColor = new GSColor(ColorMain.getFriendGSColor(), 255);
        }
        else if (Enemies.isEnemy(playerName)){
            nameColor = new GSColor(ColorMain.getEnemyGSColor(), 255);
        }
        else {
            nameColor = new GSColor(255, 255, 255, 255);
        }
    }

    public void findHealthColor(int health){
        if (health >= 15){
            healthColor = new GSColor(0, 255, 0, 255);
        }
        else if (health >= 5 && health < 15){
            healthColor = new GSColor(255, 255, 0, 255);
        }
        else {
            healthColor = new GSColor(255, 0, 0, 255);
        }
    }

    private boolean IsValidEntity (Entity e){
        if (!(e instanceof EntityPlayer)) {
            return false;
        }

        if (e instanceof EntityPlayer) {
            return e != mc.player;
        }

        return true;
    }

    public float getPing (EntityPlayer player){
        float ping = 0;
        try { ping = EntityUtil.clamp(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime(), 1, 300.0f); }
        catch (NullPointerException ignored) {}
        return ping;
    }
}