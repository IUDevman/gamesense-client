package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.Objects;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.players.enemy.Enemies;
import com.gamesense.api.util.players.friends.Friends;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.hud.HUDComponent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;

/**
 * @Author Hoosiers on 10/19/2020,
 * update by linustouchtips on 10/27/2020,
 * rewritten by lukflug on 21.11.2020
 */

public class TargetHUD extends HUDModule {
    private static Setting.ColorSetting outline;
    private static Setting.ColorSetting background;
    private static EntityPlayer targetPlayer;
    
    public TargetHUD(){
        super(new TargetHUDComponent());
    }

    public void setup() {
        outline = registerColor("Outline", "Outline", new GSColor(255, 0, 0, 255));
        background = registerColor("Background", "Background", new GSColor(0, 0, 0, 255));
    }

    private static Color getNameColor(String playerName) {
        if (Friends.isFriend(playerName)){
            return new GSColor(ColorMain.getFriendGSColor(), 255);
        }
        else if (Enemies.isEnemy(playerName)){
            return new GSColor(ColorMain.getEnemyGSColor(), 255);
        }
        else {
        	return new GSColor(255, 255, 255, 255);
        }
    }

    private static Color getHealthColor(int health) {
        if (health >= 15){
            return new GSColor(0, 255, 0, 255);
        }
        else if (health >= 5 && health < 15){
            return new GSColor(255, 255, 0, 255);
        }
        else {
            return new GSColor(255, 0, 0, 255);
        }
    }

    private static boolean isValidEntity (Entity e){
    	if (!(e instanceof EntityPlayer)) return false;
        else return e!=mc.player;
    }

    private static float getPing (EntityPlayer player){
        float ping = 0;
        try { ping = EntityUtil.clamp(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime(), 1, 300.0f); }
        catch (NullPointerException ignored) {}
        return ping;
    }
    
    public static boolean isRenderingEntity(EntityPlayer entityPlayer){
        return targetPlayer==entityPlayer;
    }
    
    
    private static class TargetHUDComponent extends HUDComponent {
		public TargetHUDComponent() {
			super("TargetHUD",GameSenseGUI.theme.getPanelRenderer(),new Point(0,70));
		}
		
		@Override
		public void render (Context context) {
			super.render(context);
			// Render background
			Color bgcolor=new GSColor(background.getValue(),100);
			context.getInterface().fillRect(context.getRect(),bgcolor,bgcolor,bgcolor,bgcolor);
			// Render outline
			Color color=outline.getValue();
			context.getInterface().fillRect(new Rectangle(context.getPos(),new Dimension(context.getSize().width,1)),color,color,color,color);
			context.getInterface().fillRect(new Rectangle(context.getPos(),new Dimension(1,context.getSize().height)),color,color,color,color);
			context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x+context.getSize().width-1,context.getPos().y),new Dimension(1,context.getSize().height)),color,color,color,color);
			context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x,context.getPos().y+context.getSize().height-1),new Dimension(context.getSize().width,1)),color,color,color,color);
			// Render content
			if (mc.world != null && mc.player.ticksExisted >= 10) {
				EntityPlayer entityPlayer = (EntityPlayer) mc.world.loadedEntityList.stream()
						.filter(entity -> isValidEntity(entity))
						.map(entity -> (EntityLivingBase) entity)
						.min(Comparator.comparing(c -> mc.player.getDistance(c)))
						.orElse(null);
				if (entityPlayer!=null) {
					// Render player
					targetPlayer=entityPlayer;
					GameSenseGUI.renderEntity(entityPlayer,new Point(context.getPos().x+35,context.getPos().y+87-(entityPlayer.isSneaking()?10:0)));
					targetPlayer=null;
					// Render name
					String playerName = entityPlayer.getName();
					Color nameColor=getNameColor(playerName);
					context.getInterface().drawString(new Point(context.getPos().x+71,context.getPos().y+11),TextFormatting.BOLD+playerName,nameColor);
					// Render health
					int playerHealth = (int) (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount());
					Color healthColor=getHealthColor(playerHealth);
					context.getInterface().drawString(new Point(context.getPos().x+71,context.getPos().y+23),TextFormatting.WHITE+"Health: "+TextFormatting.RESET+playerHealth,healthColor);
					// Render distance
					context.getInterface().drawString(new Point(context.getPos().x+71,context.getPos().y+33),"Distance: "+((int)entityPlayer.getDistance(mc.player)),new Color(255, 255, 255));
					// Render ping and info
					String info;
					if (entityPlayer.inventory.armorItemInSlot(2).getItem().equals(Items.ELYTRA)) {
			            info=TextFormatting.LIGHT_PURPLE+"Wasp";
			        }
			        else if (entityPlayer.inventory.armorItemInSlot(2).getItem().equals(Items.DIAMOND_CHESTPLATE)) {
			            info=TextFormatting.RED+"Threat";
			        }
			        else if (entityPlayer.inventory.armorItemInSlot(3).getItem().equals(Items.AIR)) {
			            info=TextFormatting.GREEN+"NewFag";
			        }
			        else {
			            info=TextFormatting.WHITE+"None";
			        }
					context.getInterface().drawString(new Point(context.getPos().x+71,context.getPos().y+43),info+TextFormatting.WHITE+" | "+getPing(entityPlayer)+" ms",new Color(255, 255, 255));
					// Render status effects
					String status=null;
					Color statusColor=null;
					for (PotionEffect effect: entityPlayer.getActivePotionEffects()) {
						if (effect.getPotion()==MobEffects.WEAKNESS) {
							status="Weakness!";
							statusColor=new Color(135,0,25);
						} else if (effect.getPotion()==MobEffects.INVISIBILITY) {
							status="Invisible!";
							statusColor=new Color(90,90,90);
						} else if (effect.getPotion()==MobEffects.STRENGTH) {
							status="Strength!";
							statusColor=new Color(185,65,185);
						}
					}
					if (status!=null) context.getInterface().drawString(new Point(context.getPos().x+71,context.getPos().y+55),TextFormatting.WHITE+"Status: "+TextFormatting.RESET+status,statusColor);
					// Render items
					int xPos=context.getPos().x+150;
					for (ItemStack itemStack : entityPlayer.getArmorInventoryList()) {
						xPos-=20;
						GameSenseGUI.renderItem(itemStack,new Point(xPos,context.getPos().y+73));
					}
				}
			}
		}

		@Override
		public int getWidth (Interface inter) {
			return 162;
		}

		@Override
		public void getHeight (Context context) {
			context.setHeight(94);
		}
    }
}
