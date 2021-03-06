package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.BossbarEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.material.Material;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

@Module.Declaration(name = "NoRender", category = Category.Render)
public class NoRender extends Module {

    public BooleanSetting armor = registerBoolean("Armor", false);
    BooleanSetting fire = registerBoolean("Fire", false);
    BooleanSetting blind = registerBoolean("Blind", false);
    BooleanSetting nausea = registerBoolean("Nausea", false);
    public BooleanSetting hurtCam = registerBoolean("HurtCam", false);
    public BooleanSetting noSkylight = registerBoolean("Skylight", false);
    public BooleanSetting noOverlay = registerBoolean("No Overlay", false);
    BooleanSetting noBossBar = registerBoolean("No Boss Bar", false);
    public BooleanSetting noCluster = registerBoolean("No Cluster", false);
    IntegerSetting maxNoClusterRender = registerInteger("No Cluster Max", 5, 1, 25);

    public int currentClusterAmount = 0;

    public void onUpdate() {
        if (blind.getValue() && mc.player.isPotionActive(MobEffects.BLINDNESS))
            mc.player.removePotionEffect(MobEffects.BLINDNESS);
        if (nausea.getValue() && mc.player.isPotionActive(MobEffects.NAUSEA))
            mc.player.removePotionEffect(MobEffects.NAUSEA);
    }

    @Override
    public void onRender() {
        currentClusterAmount = 0;
    }

    @EventHandler
    public Listener<RenderBlockOverlayEvent> blockOverlayEventListener = new Listener<>(event -> {
        if (fire.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE)
            event.setCanceled(true);
        if (noOverlay.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER)
            event.setCanceled(true);
        if (noOverlay.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK)
            event.setCanceled(true);
    });

    // Disable Water and lava fog
    @EventHandler
    private final Listener<EntityViewRenderEvent.FogDensity> fogDensityListener = new Listener<>(event -> {
        if (noOverlay.getValue()) {
            if (event.getState().getMaterial().equals(Material.WATER)
                    || event.getState().getMaterial().equals(Material.LAVA)) {
                event.setDensity(0);
                event.setCanceled(true);
            }
        }
    });

    // Disable screen overlays Overlays
    @EventHandler
    private final Listener<RenderBlockOverlayEvent> renderBlockOverlayEventListener = new Listener<>(event -> {
        if (noOverlay.getValue()) event.setCanceled(true);
    });

    @EventHandler
    private final Listener<RenderGameOverlayEvent> renderGameOverlayEventListener = new Listener<>(event -> {
        if (noOverlay.getValue()) {
            if (event.getType().equals(RenderGameOverlayEvent.ElementType.HELMET)) {
                event.setCanceled(true);
            }
            if (event.getType().equals(RenderGameOverlayEvent.ElementType.PORTAL)) {
                event.setCanceled(true);
            }
        }
    });

    // Bossbar
    @EventHandler
    private final Listener<BossbarEvent> bossbarEventListener = new Listener<>(event -> {
        if (noBossBar.getValue()) {
            event.cancel();
        }
    });

    // return whether to render or not
    public boolean incrementNoClusterRender() {
        ++currentClusterAmount;
        return currentClusterAmount <= maxNoClusterRender.getValue();
    }

    public boolean getNoClusterRender() {
        return currentClusterAmount <= maxNoClusterRender.getValue();
    }
}