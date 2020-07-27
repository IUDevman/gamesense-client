package com.gamesense.client.module.modules.render;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.EntityUtil;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MobOwner extends Module {
    public MobOwner(){
        super("MobOwner", Category.Render);
    }

    private Setting.i requestTime;
    private Setting.b debug;

    private Map<String, String> cachedUUIDs = new HashMap<String, String>(){{ }};
    private int apiRequests = 0;
    private String invalidText = "Servers offline!";

    public void setup(){
        requestTime = this.registerI("Reset Ticks", "MOResetTicks", 10, 0, 20);
        debug = this.registerB("Debug", "MODebug", false);
    }

    public void onUpdate(){
        resetRequests();
        resetCache();
        for (final Entity entity : MobOwner.mc.world.loadedEntityList){
            if (entity instanceof EntityTameable){
                final EntityTameable entityTameable = (EntityTameable) entity;
                if (entityTameable.isTamed() && entityTameable.getOwner() != null){
                    entityTameable.setAlwaysRenderNameTag(true);
                    entityTameable.setCustomNameTag("Owner: " + entityTameable.getOwner().getDisplayName().getFormattedText());
                }
            }
            if (entity instanceof AbstractHorse){
               final AbstractHorse abstractHorse = (AbstractHorse) entity;
               if (!abstractHorse.isTame() || abstractHorse.getOwnerUniqueId() == null){
                   continue;
               }
               abstractHorse.setAlwaysRenderNameTag(true);
               abstractHorse.setCustomNameTag("Owner: " + getUsername(abstractHorse.getOwnerUniqueId().toString()));
            }
        }
    }

    private String getUsername(String uuid){
        for (Map.Entry<String, String> entries : cachedUUIDs.entrySet()){
            if (entries.getKey().equalsIgnoreCase(uuid)){
                return entries.getValue();
            }
        }
        try {
            if (apiRequests > 10){
                return "Too many API requests!";
            }
            cachedUUIDs.put(uuid, Objects.requireNonNull(EntityUtil.getNameFromUUID(uuid)).replace("\"", ""));
            apiRequests++;
        }
        catch (IllegalStateException illegal){
            cachedUUIDs.put(uuid, invalidText);
        }
        for (Map.Entry<String, String> entries : cachedUUIDs.entrySet()){
            if (entries.getKey().equalsIgnoreCase(uuid)){
                return entries.getValue();
            }
        }
        return invalidText;
    }

    public void onDisable(){
        cachedUUIDs.clear();
        for (final Entity entity : MobOwner.mc.world.loadedEntityList){
            if (!(entity instanceof EntityTameable)){
                if (!(entity instanceof AbstractHorse)){
                    continue;
                }
            }
            try {
                entity.setAlwaysRenderNameTag(false);
            }
            catch (Exception ignored){
            }
        }
    }

    private static long startTime1 = 0;
    private void resetRequests(){
        if (startTime1 == 0) startTime1 = System.currentTimeMillis();
        if (startTime1 + (10 * 1000) <= System.currentTimeMillis()){
            startTime1 = System.currentTimeMillis();
            if (apiRequests >= 2){
                apiRequests = 0;
                if (debug.getValue()) Command.sendClientMessage("Reset API requests counter!");
            }
        }
    }

    private static long startTime2 = 0;
    private void resetCache(){
        if (startTime2 == 0) startTime2 = System.currentTimeMillis();
        if (startTime2 + (requestTime.getValue() * 1000) <= System.currentTimeMillis()){
            startTime2 = System.currentTimeMillis();
            for (Map.Entry<String, String> entries : cachedUUIDs.entrySet()){
                if (entries.getKey().equalsIgnoreCase(invalidText)){
                    cachedUUIDs.clear();
                    if (debug.getValue()) Command.sendClientMessage("Reset cached UUIDs list!");
                    return;
                }
            }
        }
    }
}