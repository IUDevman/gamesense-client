package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.GameSenseTessellator;
import com.gamesense.api.util.GeometryMasks;
import com.gamesense.api.util.Rainbow;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.item.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class EntityESP extends Module {
    public EntityESP() {super("EntityESP", Category.Render);}

    public void setup() {

        ArrayList<String> Modes = new ArrayList<>();
        Modes.add("Box");
        Modes.add("Outline");
        Modes.add("Glow");

        exp = registerB("ExpBottles", false);
        epearls = registerB("EnderPearls", false);
        crystals = registerB("Crystals", false);
        items = registerB("Items", false);
        orbs = registerB("ExpOrbs", false);
        rainbow = registerB("Rainbow", false);
        r = registerI("Red", 255, 0, 255);
        g = registerI("Green", 255 ,0 ,255);
        b = registerI("Blue", 255, 0 ,255);
        a = registerI("Alpha", 50, 0 ,255);
        RenderMode = registerMode("Mode", Modes, "Box");
    }


    Setting.mode RenderMode;
    Setting.b exp;
    Setting.b epearls;
    Setting.b items;
    Setting.b orbs;
    Setting.b crystals;
    Color c;
    Setting.b rainbow;
    Setting.i r;
    Setting.i g;
    Setting.i b;
    Setting.i a;

    public void onWorldRender(RenderEvent event) {
        Color c = new Color(r.getValue(), g.getValue(), b.getValue(), a.getValue());
        if (rainbow.getValue())
            c = new Color(Rainbow.getColor().getRed(), Rainbow.getColor().getGreen(), Rainbow.getColor().getBlue(), a.getValue());
        Color finalC = c;
        if (RenderMode.getValue().equalsIgnoreCase("Box")) {
            mc.world.loadedEntityList.stream()
                    .filter(entity -> entity != mc.player)
                    .forEach(e -> {
                        GameSenseTessellator.prepare(GL11.GL_QUADS);
                        if (exp.getValue() && e instanceof EntityExpBottle) {
                            GameSenseTessellator.drawBox(e.getRenderBoundingBox(), finalC.getRGB(), GeometryMasks.Quad.ALL);
                        }
                        if (epearls.getValue() && e instanceof EntityEnderPearl) {
                            GameSenseTessellator.drawBox(e.getRenderBoundingBox(), finalC.getRGB(), GeometryMasks.Quad.ALL);
                        }
                        if (crystals.getValue() && e instanceof EntityEnderCrystal) {
                            GameSenseTessellator.drawBox(e.getRenderBoundingBox(), finalC.getRGB(), GeometryMasks.Quad.ALL);
                        }
                        if (items.getValue() && e instanceof EntityItem) {
                            GameSenseTessellator.drawBox(e.getRenderBoundingBox(), finalC.getRGB(), GeometryMasks.Quad.ALL);
                        }
                        if (orbs.getValue() && e instanceof EntityXPOrb) {
                            GameSenseTessellator.drawBox(e.getRenderBoundingBox(), finalC.getRGB(), GeometryMasks.Quad.ALL);
                        }
                        GameSenseTessellator.release();
                    });
        }
        if (RenderMode.getValue().equalsIgnoreCase("Outline")) {
            c = new Color(r.getValue(), g.getValue(), b.getValue(), a.getValue());
            if (rainbow.getValue())
                c = new Color(Rainbow.getColor().getRed(), Rainbow.getColor().getGreen(), Rainbow.getColor().getBlue(), a.getValue());
            Color finalC1 = c;
            mc.world.loadedEntityList.stream()
                    .filter(entity -> entity != mc.player)
                    .forEach(e -> {
                        GameSenseTessellator.prepareGL();
                        if (exp.getValue() && e instanceof EntityExpBottle) {
                            GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, finalC1.getRGB());
                        }
                        if (epearls.getValue() && e instanceof EntityEnderPearl) {
                            GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, finalC1.getRGB());
                        }
                        if (crystals.getValue() && e instanceof EntityEnderCrystal) {
                            GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, finalC.getRGB());
                        }
                        if (items.getValue() && e instanceof EntityItem) {
                            GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, finalC1.getRGB());
                        }
                        if (orbs.getValue() && e instanceof EntityXPOrb) {
                            GameSenseTessellator.drawBoundingBox(e.getRenderBoundingBox(), 1, finalC1.getRGB());
                        }
                        GameSenseTessellator.releaseGL();
                    });
        }
        if (RenderMode.getValue().equalsIgnoreCase("Glow")) {
            mc.world.loadedEntityList.stream()
                    .filter(e -> e != mc.player)
                    .forEach(e -> {
                        if (exp.getValue() && e instanceof EntityExpBottle) {
                            e.setGlowing(true);
                        }
                        if (epearls.getValue() && e instanceof EntityEnderPearl) {
                            e.setGlowing(true);
                        }
                        if (crystals.getValue() && e instanceof EntityEnderCrystal) {
                            e.setGlowing(true);
                        }
                        if (items.getValue() && e instanceof EntityItem) {
                            e.setGlowing(true);
                        }
                        if (orbs.getValue() && e instanceof EntityXPOrb) {
                            e.setGlowing(true);
                        }
                    });
        }
    }

    public void onUpdate(){
        mc.world.loadedEntityList.stream()
                .filter(e -> e != mc.player)
                .forEach(e -> {
        if (RenderMode.getValue().equalsIgnoreCase("Glow") == false){
                        if (e instanceof EntityExpBottle) {
                            e.setGlowing(false);
                        }
                        if (e instanceof EntityEnderPearl) {
                            e.setGlowing(false);
                        }
                        if (e instanceof EntityEnderCrystal) {
                            e.setGlowing(false);
                        }
                        if (e instanceof EntityItem) {
                            e.setGlowing(false);
                        }
                        if (e instanceof EntityXPOrb) {
                            e.setGlowing(false);
                        }
                    }
                    if (exp.getValue() == false && e instanceof EntityExpBottle) {
                        e.setGlowing(false);
                    }
                    if (epearls.getValue() == false && e instanceof EntityEnderPearl) {
                        e.setGlowing(false);
                    }
                    if (crystals.getValue() == false && e instanceof EntityEnderCrystal) {
                        e.setGlowing(false);
                    }
                    if (items.getValue() == false && e instanceof EntityItem) {
                        e.setGlowing(false);
                    }
                    if (orbs.getValue() == false && e instanceof EntityXPOrb) {
                        e.setGlowing(false);
                    }
        });
    }

    public void onDisable(){
        if (RenderMode.getValue().equalsIgnoreCase("Glow")) {
            mc.world.loadedEntityList.stream()
                    .filter(e -> e != mc.player)
                    .forEach(e -> {
                        if (e instanceof EntityExpBottle) {
                            e.setGlowing(false);
                        }
                        if (e instanceof EntityEnderPearl) {
                            e.setGlowing(false);
                        }
                        if (e instanceof EntityEnderCrystal) {
                            e.setGlowing(false);
                        }
                        if (e instanceof EntityItem) {
                            e.setGlowing(false);
                        }
                        if (e instanceof EntityXPOrb) {
                            e.setGlowing(false);
                        }
                    });
        }
    }

    public String getHudInfo() {
        String t = "";
        if(RenderMode.getValue().equalsIgnoreCase("Box")) {
            t = "[" + ChatFormatting.WHITE + "Box" + ChatFormatting.GRAY + "]";
        }
        if (RenderMode.getValue().equalsIgnoreCase("Outline")) {
            t = "[" + ChatFormatting.WHITE + "Outline" + ChatFormatting.GRAY + "]";
        }
        if (RenderMode.getValue().equalsIgnoreCase("Glow")) {
            t = "[" + ChatFormatting.WHITE + "Glow" + ChatFormatting.GRAY + "]";
        }
        return t;
    }
}