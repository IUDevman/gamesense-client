package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
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

import java.util.Arrays;

/**
 * @author Hoosiers on 09/22/2020
 * @author Techale on 12/19/2020
 */

@Module.Declaration(name = "ESP", category = Category.Render)
public class ESP extends Module {

    ColorSetting mainColor = registerColor("Color");
    IntegerSetting range = registerInteger("Range", 100, 10, 260);
    IntegerSetting width = registerInteger("Line Width", 2, 1, 5);
    ModeSetting playerESPMode = registerMode("Player", Arrays.asList("None", "Glowing", "Box", "Direction"), "Box");
    ModeSetting mobESPMode = registerMode("Mob", Arrays.asList("None", "Glowing", "Box", "Direction"), "Box");
    BooleanSetting entityRender = registerBoolean("Entity", false);
    BooleanSetting itemRender = registerBoolean("Item", true);
    BooleanSetting containerRender = registerBoolean("Container", false);
    BooleanSetting glowCrystals = registerBoolean("Glow Crystal", false);

    GSColor playerColor;
    GSColor mobColor;
    GSColor mainIntColor;
    GSColor containerColor;
    int opacityGradient;

    public void onWorldRender(RenderEvent event) {
        mc.world.loadedEntityList.stream().filter(entity -> entity != mc.player).filter(entity -> rangeEntityCheck(entity)).forEach(entity -> {
            defineEntityColors(entity);

            if ((!playerESPMode.getValue().equals("None")) && entity instanceof EntityPlayer) {

                if (!playerESPMode.getValue().equals("None")) {

                    if (playerESPMode.getValue().equals("Glowing")) {
                        entity.setGlowing(true);

                    } else if (entity.isGlowing()) {
                        entity.setGlowing(false);
                    } else {
                        switch (playerESPMode.getValue()) {
                            case "Direction":
                                RenderUtil.drawBoxWithDirection(entity.getEntityBoundingBox(), playerColor, entity.rotationYaw, width.getValue(), 0);
                                break;
                            case "Box":
                                RenderUtil.drawBoundingBox(entity.getEntityBoundingBox(), width.getValue(), playerColor);
                                break;
                        }
                    }
                }
            }

            if (!mobESPMode.getValue().equals("None")) {

                if (entity instanceof EntityCreature || entity instanceof EntitySlime || entity instanceof EntitySquid) {

                    if (mobESPMode.getValue().equals("Glowing")) {
                        entity.setGlowing(true);

                    } else if (entity.isGlowing()) {
                        entity.setGlowing(false);
                    } else if (mobESPMode.getValue().equals("Direction")) {
                        RenderUtil.drawBoxWithDirection(entity.getEntityBoundingBox(), mobColor, entity.rotationYaw, width.getValue(), 0);
                    } else {
                        RenderUtil.drawBoundingBox(entity.getEntityBoundingBox(), width.getValue(), mobColor);
                    }
                }
            }


            if (itemRender.getValue() && entity instanceof EntityItem) {
                RenderUtil.drawBoundingBox(entity.getEntityBoundingBox(), width.getValue(), mainIntColor);
            }
            if (entityRender.getValue()) {
                if (entity instanceof EntityEnderPearl || entity instanceof EntityXPOrb || entity instanceof EntityExpBottle || entity instanceof EntityEnderCrystal) {
                    RenderUtil.drawBoundingBox(entity.getEntityBoundingBox(), width.getValue(), mainIntColor);
                }
            }
            if (glowCrystals.getValue() && entity instanceof EntityEnderCrystal) {
                entity.setGlowing(true);
            }

            if (!glowCrystals.getValue() && entity instanceof EntityEnderCrystal && entity.isGlowing()) {
                entity.setGlowing(false);
            }
        });


        if (containerRender.getValue()) {
            mc.world.loadedTileEntityList.stream().filter(tileEntity -> rangeTileCheck(tileEntity)).forEach(tileEntity -> {
                if (tileEntity instanceof TileEntityChest) {
                    containerColor = new GSColor(255, 255, 0, opacityGradient);
                    RenderUtil.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), width.getValue(), containerColor);
                }
                if (tileEntity instanceof TileEntityEnderChest) {
                    containerColor = new GSColor(180, 70, 200, opacityGradient);
                    RenderUtil.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), width.getValue(), containerColor);
                }
                if (tileEntity instanceof TileEntityShulkerBox) {
                    containerColor = new GSColor(255, 0, 0, opacityGradient);
                    RenderUtil.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), width.getValue(), containerColor);
                }
                if (tileEntity instanceof TileEntityDispenser || tileEntity instanceof TileEntityFurnace || tileEntity instanceof TileEntityHopper || tileEntity instanceof TileEntityDropper) {
                    containerColor = new GSColor(150, 150, 150, opacityGradient);
                    RenderUtil.drawBoundingBox(mc.world.getBlockState(tileEntity.getPos()).getSelectedBoundingBox(mc.world, tileEntity.getPos()), width.getValue(), containerColor);
                }
            });
        }
    }

    public void onDisable() {
        mc.world.loadedEntityList.stream().forEach(entity -> {
            if ((entity instanceof EntityEnderCrystal || entity instanceof EntityPlayer || entity instanceof EntityCreature || entity instanceof EntitySlime || entity instanceof EntitySquid) && entity.isGlowing()) {
                entity.setGlowing(false);
            }
        });
    }

    private void defineEntityColors(Entity entity) {
        if (entity instanceof EntityPlayer) {
            if (SocialManager.isFriend(entity.getName())) {
                playerColor = ModuleManager.getModule(ColorMain.class).getFriendGSColor();
            } else if (SocialManager.isEnemy(entity.getName())) {
                playerColor = ModuleManager.getModule(ColorMain.class).getEnemyGSColor();
            } else {
                playerColor = new GSColor(mainColor.getValue(), opacityGradient);
            }
        }

        if (entity instanceof EntityMob) {
            mobColor = new GSColor(255, 0, 0, opacityGradient);
        } else if (entity instanceof EntityAnimal || entity instanceof EntitySquid) {
            mobColor = new GSColor(0, 255, 0, opacityGradient);
        } else {
            mobColor = new GSColor(255, 165, 0, opacityGradient);
        }

        if (entity instanceof EntitySlime) {
            mobColor = new GSColor(255, 0, 0, opacityGradient);
        }

        if (entity != null) {
            mainIntColor = new GSColor(mainColor.getValue(), opacityGradient);
        }
    }

    private boolean rangeEntityCheck(Entity entity) {
        if (entity.getDistance(mc.player) > range.getValue()) {
            return false;
        }

        if (entity.getDistance(mc.player) >= 180) {
            opacityGradient = 50;
        } else if (entity.getDistance(mc.player) >= 130 && entity.getDistance(mc.player) < 180) {
            opacityGradient = 100;
        } else if (entity.getDistance(mc.player) >= 80 && entity.getDistance(mc.player) < 130) {
            opacityGradient = 150;
        } else if (entity.getDistance(mc.player) >= 30 && entity.getDistance(mc.player) < 80) {
            opacityGradient = 200;
        } else {
            opacityGradient = 255;
        }

        return true;
    }

    private boolean rangeTileCheck(TileEntity tileEntity) {
        if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) > range.getValue() * range.getValue()) {
            return false;
        }

        if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= 32400) {
            opacityGradient = 50;
        } else if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= 16900 && tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) < 32400) {
            opacityGradient = 100;
        } else if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= 6400 && tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) < 16900) {
            opacityGradient = 150;
        } else if (tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= 900 && tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) < 6400) {
            opacityGradient = 200;
        } else {
            opacityGradient = 255;
        }

        return true;
    }
}