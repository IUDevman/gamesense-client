package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.BossbarEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.material.Material;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class NoRender extends Module {

	public NoRender() {
		super("NoRender", Category.Render);
	}

	public Setting.Boolean armor;
	Setting.Boolean fire;
	Setting.Boolean blind;
	Setting.Boolean nausea;
	public Setting.Boolean hurtCam;
	public Setting.Boolean noOverlay;
	Setting.Boolean noBossBar;
	public Setting.Boolean noSkylight;
	public static Setting.Boolean noCluster;
	public static Setting.Integer maxNoClusterRender;

	public static int currentClusterAmount = 0;

	public void setup() {
		armor = registerBoolean("Armor", false);
		fire = registerBoolean("Fire", false);
		blind = registerBoolean("Blind", false);
		nausea = registerBoolean("Nausea", false);
		hurtCam = registerBoolean("HurtCam", false);
		noSkylight = registerBoolean("Skylight", false);
		noOverlay = registerBoolean("No Overlay", false); //need to make sure this works better
		noBossBar = registerBoolean("No Boss Bar", false);
		noCluster = registerBoolean("No Cluster", false);
		maxNoClusterRender = registerInteger("No Cluster Max", 5, 1, 25);
	}

	public void onUpdate() {
		if (blind.getValue() && mc.player.isPotionActive(MobEffects.BLINDNESS)) mc.player.removePotionEffect(MobEffects.BLINDNESS);
		if (nausea.getValue() && mc.player.isPotionActive(MobEffects.NAUSEA)) mc.player.removePotionEffect(MobEffects.NAUSEA);
	}

	@Override
	public void onRender() {
		currentClusterAmount = 0;
	}

	@EventHandler
	public Listener<RenderBlockOverlayEvent> blockOverlayEventListener = new Listener<>(event -> {
		if (fire.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) event.setCanceled(true);
		if (noOverlay.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER) event.setCanceled(true);
		if (noOverlay.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK) event.setCanceled(true);
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
		event.setCanceled(true);
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

	public void onEnable() {
		GameSense.EVENT_BUS.subscribe(this);
	}

	public void onDisable() {
		GameSense.EVENT_BUS.unsubscribe(this);
	}

	// return whether to render or not
	public static boolean incrementNoClusterRender() {
		++currentClusterAmount;
		return currentClusterAmount <= maxNoClusterRender.getValue();
	}

	public static boolean getNoClusterRender() {
		return currentClusterAmount <= maxNoClusterRender.getValue();
	}
}