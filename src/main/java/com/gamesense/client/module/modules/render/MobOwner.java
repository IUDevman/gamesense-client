package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;

public class MobOwner extends Module {
    public MobOwner(){
        super("MobOwner", Category.Render);
    }

    public void onUpdate() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityTameable) {
                EntityTameable entityTameable = (EntityTameable)entity;
                if (entityTameable.isTamed() && entityTameable.getOwner() != null) {
                    entityTameable.setAlwaysRenderNameTag(true);
                    entityTameable.setCustomNameTag("Owner: " + entityTameable.getOwner().getDisplayName().getFormattedText());
                }
            }
            if (entity instanceof AbstractHorse) {
                AbstractHorse abstractHorse = (AbstractHorse)entity;
                if (!abstractHorse.isTame() || abstractHorse.getOwnerUniqueId() == null) {
                    continue;
                }
                abstractHorse.setAlwaysRenderNameTag(true);
                abstractHorse.setCustomNameTag("Owner: " + (abstractHorse.getOwnerUniqueId().toString()));
            }
        }
    }

    @Override
    public void onDisable() {
        for (final Entity entity : MobOwner.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityTameable)) {
                if (!(entity instanceof AbstractHorse)) {
                    continue;
                }
            }
            try {
                entity.setAlwaysRenderNameTag(false);
            }
            catch (Exception ex) {}
        }
    }
}
