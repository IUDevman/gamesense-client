package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.PlayerJoinEvent;
import com.gamesense.api.event.events.PlayerLeaveEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.GameSenseTessellator;
import com.gamesense.api.util.Rainbow;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.ColorMain;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.world.WorldEvent;
import org.lwjgl.opengl.GL11;

public class LogoutSpots extends Module {
    public LogoutSpots() {super("LogoutSpots", Category.Render);}

    Map<Entity, String> loggedPlayers = new ConcurrentHashMap<>();
    List<Entity> lastTickEntities;

    @EventHandler
    private Listener<PlayerJoinEvent> listener1 = new Listener<>(event -> {
        loggedPlayers.forEach((e, s) -> {
            try {
                if (e.getName().equalsIgnoreCase(event.getName())) {
                    loggedPlayers.remove(e);
                    Command.sendClientMessage(event.getName() + " reconnected!");
                }
            } catch(ConcurrentModificationException ex){ex.printStackTrace();}
        });
    });

    @EventHandler
    private Listener<PlayerLeaveEvent> listener2 = new Listener<>(event -> {
        if (mc.world == null) return;
        lastTickEntities.forEach(e ->{
            if(e.getName().equalsIgnoreCase(event.getName())){
                String date = new SimpleDateFormat("k:mm").format(new Date());
                loggedPlayers.put(e, date);
                String pos = "x" + e.getPosition().getX() + " y" + e.getPosition().getY() + " z" + e.getPosition().getZ();
                Command.sendClientMessage(event.getName() + " disconnected at " + pos + "!");
            }
        });
    });

    public void onUpdate(){
        lastTickEntities = mc.world.loadedEntityList;
    }

    public void onWorldRender(RenderEvent event) {
        loggedPlayers.forEach((e, time) -> {
            if(mc.player.getDistance(e) < 500) {
                GL11.glPushMatrix();
                drawLogoutBox(e.getRenderBoundingBox(), 1, 0, 0, 0, 255);
                drawNametag(e, time);
                GL11.glPopMatrix();
            }
        });
    }

    public void drawLogoutBox(AxisAlignedBB bb, int width, int r, int b, int g, int a){
        ColorMain colorMain = ((ColorMain) ModuleManager.getModuleByName("Colors"));
        Color color;
        Color c = Rainbow.getColor();
        if(colorMain.Rainbow.getValue()) color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
        else color = new Color(colorMain.Red.getValue(), colorMain.Green.getValue(), colorMain.Blue.getValue(), 255);
        GameSenseTessellator.drawBoundingBox(bb, width, color.getRGB());
    }

    @EventHandler
    private Listener<WorldEvent.Unload> listener3 = new Listener<>(event -> {
        lastTickEntities.clear();
        if(mc.player == null)
            loggedPlayers.clear();
        else
        if(!mc.player.isDead)
            loggedPlayers.clear();
    });

    @EventHandler
    private Listener<WorldEvent.Load> listener4 = new Listener<>(event -> {
        lastTickEntities.clear();
        if (mc.player == null) {
            loggedPlayers.clear();
        }else {
            if (!mc.player.isDead) loggedPlayers.clear();
        }
    });

    public void onEnable(){
        lastTickEntities = new ArrayList<>();
        loggedPlayers.clear();
        GameSenseMod.EVENT_BUS.subscribe(this);
    }

    public void onDisable() {
        GameSenseMod.EVENT_BUS.unsubscribe(this);
    }

    private void drawNametag(Entity entityIn, String t) {
        GlStateManager.pushMatrix();

        float f = mc.player.getDistance(entityIn);
        float sc = f < 25 ? 0.5f : 2f;
        float m = (f / 20f) * (float) (Math.pow(1.2589254f, 0.1 / sc));
        if(m < 0.5f) m = 0.5f;
        if(m > 5f) m = 5f;

        Vec3d interp = getInterpolatedRenderPos(entityIn, mc.getRenderPartialTicks());
        float mm;
        if(m > 2)
            mm = m / 2;
        else
            mm = m;
        float yAdd = entityIn.height + mm;
        double x = interp.x;
        double y = interp.y + yAdd;
        double z = interp.z;

        float viewerYaw = mc.getRenderManager().playerViewY;
        float viewerPitch = mc.getRenderManager().playerViewX;
        boolean isThirdPersonFrontal = mc.getRenderManager().options.thirdPersonView == 2;
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);


        GlStateManager.scale(m, m, m);

        FontRenderer fontRendererIn = mc.fontRenderer;
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);

        String line1 = entityIn.getName() + "  (" + t + ")";
        String line2 = "x" + entityIn.getPosition().getX() + " y" + entityIn.getPosition().getY() + " z" + entityIn.getPosition().getZ();
        int i = fontRendererIn.getStringWidth(line1) / 2;
        int ii = fontRendererIn.getStringWidth(line2) / 2;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        GlStateManager.enableTexture2D();

        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        ColorMain colorMain = ((ColorMain) ModuleManager.getModuleByName("Colors"));
        Color color;
        Color c = Rainbow.getColor();
        if(colorMain.Rainbow.getValue()) color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
        else color = new Color(colorMain.Red.getValue(), colorMain.Green.getValue(), colorMain.Blue.getValue(), 255);
        fontRendererIn.drawStringWithShadow(line1, -i, 10, color.getRGB());
        fontRendererIn.drawStringWithShadow(line2, -ii, 20, color.getRGB());
        GlStateManager.glNormal3f(0.0F, 0.0F, 0.0F);
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedRenderPos(Entity entity, float ticks) {
        return getInterpolatedPos(entity, ticks).subtract(mc.getRenderManager().renderPosX,mc.getRenderManager().renderPosY,mc.getRenderManager().renderPosZ);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d(
                (entity.posX - entity.lastTickPosX) * x,
                (entity.posY - entity.lastTickPosY) * y,
                (entity.posZ - entity.lastTickPosZ) * z
        );
    }
    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }
}
