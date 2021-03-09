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
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

/**
 * @author Hoosiers
 * @since 8/12/20
 * some GL from Osiris/KAMI was referenced.
 */

@Module.Declaration(name = "Tracers", category = Category.Render)
public class Tracers extends Module {

    IntegerSetting renderDistance = registerInteger("Distance", 100, 10, 260);
    ModeSetting pointsTo = registerMode("Draw To", Arrays.asList("Head", "Feet"), "Feet");
    BooleanSetting colorType = registerBoolean("Color Sync", true);
    ColorSetting nearColor = registerColor("Near Color", new GSColor(255, 0, 0, 255));
    ColorSetting midColor = registerColor("Middle Color", new GSColor(255, 255, 0, 255));
    ColorSetting farColor = registerColor("Far Color", new GSColor(0, 255, 0, 255));

    GSColor tracerColor;

    public void onWorldRender(RenderEvent event) {
        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        mc.world.loadedEntityList.stream()
                .filter(e -> e instanceof EntityPlayer)
                .filter(e -> e != mc.player)
                .forEach(e -> {
                    if (mc.player.getDistance(e) > renderDistance.getValue()) {
                        return;
                    } else {
                        if (SocialManager.isFriend(e.getName())) {
                            tracerColor = colorMain.getFriendGSColor();
                        } else if (SocialManager.isEnemy(e.getName())) {
                            tracerColor = colorMain.getEnemyGSColor();
                        } else {
                            if (mc.player.getDistance(e) < 20) {
                                tracerColor = nearColor.getValue();
                            }
                            if (mc.player.getDistance(e) >= 20 && mc.player.getDistance(e) < 50) {
                                tracerColor = midColor.getValue();
                            }
                            if (mc.player.getDistance(e) >= 50) {
                                tracerColor = farColor.getValue();
                            }

                            if (colorType.getValue()) {
                                tracerColor = getDistanceColor((int) mc.player.getDistance(e));
                            }
                        }
                    }
                    drawLineToEntityPlayer(e, tracerColor);
                });
    }

    public void drawLineToEntityPlayer(Entity e, GSColor color) {
        double[] xyz = interpolate(e);
        drawLine1(xyz[0], xyz[1], xyz[2], e.height, color);
    }

    public static double[] interpolate(Entity entity) {
        double posX = interpolate(entity.posX, entity.lastTickPosX);
        double posY = interpolate(entity.posY, entity.lastTickPosY);
        double posZ = interpolate(entity.posZ, entity.lastTickPosZ);
        return new double[]{posX, posY, posZ};
    }

    public static double interpolate(double now, double then) {
        return then + (now - then) * mc.getRenderPartialTicks();
    }

    public void drawLine1(double posx, double posy, double posz, double up, GSColor color) {
        Vec3d eyes = ActiveRenderInfo.getCameraPosition().add(mc.getRenderManager().viewerPosX, mc.getRenderManager().viewerPosY, mc.getRenderManager().viewerPosZ);
        RenderUtil.prepare();
        if (pointsTo.getValue().equalsIgnoreCase("Head")) {
            RenderUtil.drawLine(eyes.x, eyes.y, eyes.z, posx, posy + up, posz, color);
        } else {
            RenderUtil.drawLine(eyes.x, eyes.y, eyes.z, posx, posy, posz, color);
        }
        RenderUtil.release();
    }

    private GSColor getDistanceColor(int distance) {
        if (distance > 50) {
            distance = 50;
        }

        int red = (int) (255 - (distance * 5.1));
        int green = 255 - red;

        return new GSColor(red, green, 0, 255);
    }
}