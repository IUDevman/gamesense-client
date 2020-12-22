package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.player.enemy.Enemies;
import com.gamesense.api.util.player.friends.Friends;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.*;

/**
 * @Author Hoosiers on 09/22/2020
 * todo: Create custom outlines for entities
 * todo: Basic wallhack/chams option???
 */


public class ESP extends Module {

    public ESP(){
        super("ESP", Category.Render);
    }

    Setting.Boolean playerRender;
    Setting.Boolean direction;
    Setting.Boolean mobRender;
    Setting.Boolean containerRender;
    Setting.Boolean itemRender;
    Setting.Boolean entityRender;
    Setting.Boolean glowCrystals;
    Setting.Boolean glowPlayer;
    Setting.Integer width;
    Setting.Integer range;
    Setting.ColorSetting mainColor;

    public void setup(){
        mainColor = registerColor("Color", "Color");
        range = registerInteger("Range", "Range", 100, 10, 260);
        direction = registerBoolean("Use Direction", "UseDirection", false);
        width = registerInteger("Line Width", "LineWidth", 2, 1, 5);
        playerRender = registerBoolean("Player", "Player", true);
        mobRender = registerBoolean("Mob", "Mob", false);
        entityRender = registerBoolean("Entity", "Entity", false);
        itemRender = registerBoolean("Item", "Item", true);
        containerRender = registerBoolean("Container", "Container", false);
        glowCrystals = registerBoolean("Glow Crystal", "GlowCrystal", false);
        glowPlayer = registerBoolean("Glow Player", "GlowPlayer", false);
    }

    GSColor playerColor;
    GSColor mobColor;
    GSColor mainIntColor;
    GSColor containerColor;
    int opacityGradient;

    public void onWorldRender(RenderEvent event){
        mc.world.loadedEntityList.stream().filter(entity -> entity != mc.player).filter(entity -> rangeEntityCheck(entity)).forEach(entity -> {
            defineEntityColors(entity);
            if (playerRender.getValue() && entity instanceof EntityPlayer){
                // If glowing
                if (glowPlayer.getValue()) {
                    entity.setGlowing(true);
                }
                else if (entity.isGlowing())
                    entity.setGlowing(false);
                // If the guy want to see the direction from the box
                if (direction.getValue())
                    GameSenseTessellator.drawBoxWithDirection(entity.getEntityBoundingBox(), playerColor, ((EntityPlayer) entity).rotationYawHead, width.getValue());
                else
                    GameSenseTessellator.drawBoundingBox(entity.getEntityBoundingBox(), width.getValue(), playerColor);
            }
            if (mobRender.getValue()){
                if (entity instanceof EntityCreature || entity instanceof EntitySlime || entity instanceof EntitySquid){
                    if (direction.getValue()) {
                        GameSenseTessellator.drawBoxWithDirection(entity.getEntityBoundingBox(), mobColor, entity.rotationYaw, width.getValue());
                    }
                    else GameSenseTessellator.drawBoundingBox(entity.getEntityBoundingBox(), width.getValue(), mobColor);
                }
            }
            if (itemRender.getValue() && entity instanceof EntityItem){
                GameSenseTessellator.drawBoundingBox(entity.getEntityBoundingBox(), width.getValue(), mainIntColor);
            }
            if (entityRender.getValue()){
                if (entity instanceof EntityEnderPearl || entity instanceof EntityXPOrb || entity instanceof EntityExpBottle || entity instanceof EntityEnderCrystal){
                    GameSenseTessellator.drawBoundingBox(entity.getEntityBoundingBox(), width.getValue(), mainIntColor);
                }
            }
            if (glowCrystals.getValue() && entity instanceof EntityEnderCrystal){
                entity.setGlowing(true);
            }

            if (!glowCrystals.getValue() && entity instanceof EntityEnderCrystal && entity.isGlowing()){
                entity.setGlowing(false);
            }
        });
        if (containerRender.getValue()) {
            mc.world.loadedTileEntityList.stream().filter(tileEntity -> rangeTileCheck(tileEntity)).forEach(tileEntity -> {
                if (tileEntity instanceof TileEntityChest){
                    containerColor = new GSColor(255, 255, 0, opacityGradient);
                    GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), width.getValue(), containerColor);
                }
                if (tileEntity instanceof TileEntityEnderChest){
                    containerColor = new GSColor(180, 70, 200, opacityGradient);
                    GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), width.getValue(), containerColor);
                }
                if (tileEntity instanceof TileEntityShulkerBox){
                    containerColor = new GSColor(255, 0, 0, opacityGradient);
                    GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), width.getValue(), containerColor);
                }
                if(tileEntity instanceof TileEntityDispenser || tileEntity instanceof TileEntityFurnace || tileEntity instanceof TileEntityHopper || tileEntity instanceof TileEntityDropper){
                    containerColor = new GSColor(150, 150, 150, opacityGradient);
                    GameSenseTessellator.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), width.getValue(), containerColor);
                }
            });
        }
    }

    public void onDisable(){
        mc.world.loadedEntityList.stream().forEach(entity -> {
            if ((entity instanceof EntityEnderCrystal || entity instanceof EntityPlayer) && entity.isGlowing()){
                entity.setGlowing(false);
            }
        });
    }

    private void defineEntityColors(Entity entity) {
        //should have everything covered here, mob categorizing is weird
        if (entity instanceof EntityPlayer){
            if (Friends.isFriend(entity.getName())){
                playerColor = ColorMain.getFriendGSColor();
            }
            else if (Enemies.isEnemy(entity.getName())){
                playerColor = ColorMain.getEnemyGSColor();
            }
            else {
                playerColor = new GSColor(mainColor.getValue(), opacityGradient);
            }
        }

        if (entity instanceof EntityMob){
            mobColor = new GSColor(255, 0, 0, opacityGradient);
        }
        else if (entity instanceof EntityAnimal || entity instanceof EntitySquid){
            mobColor = new GSColor(0, 255, 0, opacityGradient);
        }
        else {
            mobColor = new GSColor(255, 165, 0, opacityGradient);
        }

        if (entity instanceof EntitySlime){
            mobColor = new GSColor(255, 0, 0, opacityGradient);
        }

        if (entity != null) {
            mainIntColor = new GSColor(mainColor.getValue(), opacityGradient);
        }
    }

    //boolean range check and opacity gradient

    private boolean rangeEntityCheck(Entity entity) {
        if (entity.getDistance(mc.player) > range.getValue()){
            return false;
        }

        if (entity.getDistance(mc.player) >= 180){
            opacityGradient = 50;
        }
        else if (entity.getDistance(mc.player) >= 130 && entity.getDistance(mc.player) < 180){
            opacityGradient = 100;
        }
        else if (entity.getDistance(mc.player) >= 80 && entity.getDistance(mc.player) < 130){
            opacityGradient = 150;
        }
        else if (entity.getDistance(mc.player) >= 30 && entity.getDistance(mc.player) < 80){
            opacityGradient = 200;
        }
        else {
            opacityGradient = 255;
        }

        return true;
    }

    private boolean rangeTileCheck(TileEntity tileEntity) {
        //the range value has to be squared for this
        if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) > range.getValue() * range.getValue()){
            return false;
        }

        if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= 32400){
            opacityGradient = 50;
        }
        else if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= 16900 && tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) < 32400){
            opacityGradient = 100;
        }
        else if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= 6400 && tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) < 16900){
            opacityGradient = 150;
        }
        else if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= 900 && tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) < 6400){
            opacityGradient = 200;
        }
        else {
            opacityGradient = 255;
        }

        return true;
    }
}