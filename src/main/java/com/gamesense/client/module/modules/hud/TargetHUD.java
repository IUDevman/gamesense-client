package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.api.util.players.enemy.Enemies;
import com.gamesense.api.util.players.friends.Friends;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;

import java.util.Comparator;

/**
 * @Author Hoosiers on 10/19/2020
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
        posX = registerInteger("X", "X", 10, 0, 1000);
        posY = registerInteger("Y", "Y", 10, 0, 1000);
        outline = registerColor("Outline", "Outline");
        background = registerColor("Background", "Background");
    }

    GSColor outlineColor;
    GSColor backgroundColor;
    GSColor nameColor;
    GSColor healthColor;

    public void onRender(){
        if (mc.world != null) {
            backgroundColor = new GSColor(background.getValue(), 100);
            outlineColor = new GSColor(outline.getValue(), 255);

            EntityPlayer player = mc.world.playerEntities.stream()
                    .filter(entityPlayer -> entityPlayer.getName() != mc.player.getName())
                    .filter(entityPlayer -> entityPlayer.getDistance(mc.player) <= range.getValue())
                    .sorted(Comparator.comparing(entityPlayer -> entityPlayer.getDistance(mc.player)))
                    .findFirst().orElse(null);

            if (player != null) {
                String playerName = player.getName();
                int playerHealth = (int) (player.getHealth() + player.getAbsorptionAmount());
                findNameColor(playerName);
                findHealthColor(playerHealth);

                //box
                drawTargetBox();

                //player model
                drawEntityPlayer(player, posX.getValue(), posY.getValue());

                //player name
                FontUtils.drawStringWithShadow(HUD.customFont.getValue(), TextFormatting.BOLD + playerName, posX.getValue() + 101, posY.getValue() + 11, nameColor);

                //health + absorption
                FontUtils.drawStringWithShadow(HUD.customFont.getValue(), TextFormatting.WHITE + "Health: " + TextFormatting.RESET + playerHealth, posX.getValue() + 101, posY.getValue() + 23, healthColor);

                //distance
                FontUtils.drawStringWithShadow(HUD.customFont.getValue(), "Distance: " + ((int) player.getDistance(mc.player)), posX.getValue() + 101, posY.getValue() + 33, new GSColor(255, 255, 255, 255));

                //status effects
                drawStatusEffects(player, posX.getValue(), posY.getValue());

                //armor + items
                drawItemTextures(player, posX.getValue() + 101, posY.getValue() + 83);
            }
        }
    }

    public void drawTargetBox(){
        //outline
        Gui.drawRect(posX.getValue() + 1, posY.getValue() + 1, posX.getValue() + 201, posY.getValue() + 101, backgroundColor.getRGB());

        //top
        Gui.drawRect(posX.getValue(), posY.getValue(), posX.getValue() + 202, posY.getValue() + 1, outlineColor.getRGB());
        //bottom
        Gui.drawRect(posX.getValue(), posY.getValue() + 101, posX.getValue() + 202, posY.getValue() + 102, outlineColor.getRGB());
        //left
        Gui.drawRect(posX.getValue(), posY.getValue(), posX.getValue() + 1, posY.getValue() + 102, outlineColor.getRGB());
        //right
        Gui.drawRect(posX.getValue() + 201, posY.getValue(), posX.getValue() + 202, posY.getValue() + 102, outlineColor.getRGB());
    }

    public void drawEntityPlayer(EntityPlayer entityPlayer, int x, int y){
        //todo- linustouchtips
    }

    //having more than one of these displayed at once makes things too crowded
    GSColor statusColor = new GSColor(255, 255, 255, 255);
    public void drawStatusEffects(EntityPlayer entityPlayer, int x, int y){
        int inX = x + 101;
        int inY = y + 55;

        entityPlayer.getActivePotionEffects().forEach(potionEffect -> {
            findPotionColor(potionEffect);

            if (potionEffect.getPotion() == MobEffects.WEAKNESS) {
                FontUtils.drawStringWithShadow(HUD.customFont.getValue(), TextFormatting.WHITE + "Status: " + TextFormatting.RESET + "Weakness!", inX, inY, statusColor);
            }
            else if (potionEffect.getPotion() == MobEffects.INVISIBILITY){
                FontUtils.drawStringWithShadow(HUD.customFont.getValue(), TextFormatting.WHITE + "Status: " + TextFormatting.RESET + "Invisible!", inX, inY, statusColor);
            }
            else if (potionEffect.getPotion() == MobEffects.STRENGTH){
                FontUtils.drawStringWithShadow(HUD.customFont.getValue(), TextFormatting.WHITE + "Status: " + TextFormatting.RESET + "Strength!", inX, inY, statusColor);
            }
        });
    }

    //ported and modified from HUD/Nametags
    //todo: linus- if you want to make your own, that would be awesome... if not, I still need to modify this to make it look better
    private static final RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
    public void drawItemTextures(EntityPlayer entityPlayer, int px, int py){
        GlStateManager.enableTexture2D();

        int iteration = 0;
        for (ItemStack is : entityPlayer.getArmorInventoryList()) {
            iteration++;
            if (is.isEmpty()) continue;
            int x = px - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();

            itemRender.zLevel = 200F;
            itemRender.renderItemAndEffectIntoGUI(is, x, py);
            itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, py, "");
            itemRender.zLevel = 0F;

            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            mc.fontRenderer.drawStringWithShadow(s, x + 19 - 2 - mc.fontRenderer.getStringWidth(s), py + 9, new GSColor(255,255,255).getRGB());
            float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
            float red = 1 - green;
            int dmg = 100 - (int) (red * 100);
            FontUtils.drawStringWithShadow(HUD.customFont.getValue(), dmg + "", x + 8 - mc.fontRenderer.getStringWidth(dmg + "") / 2, py - 11, new GSColor((int) (red * 255), (int) (green * 255), 0));
        }

        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
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
}