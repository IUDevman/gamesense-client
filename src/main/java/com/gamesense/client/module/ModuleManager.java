package com.gamesense.client.module;

import java.util.ArrayList;
import java.util.stream.Collectors;
import org.lwjgl.input.Keyboard;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.client.module.modules.combat.AutoArmor;
import com.gamesense.client.module.modules.combat.AutoCrystal;
import com.gamesense.client.module.modules.combat.AutoTotem;
import com.gamesense.client.module.modules.combat.AutoTrap;
import com.gamesense.client.module.modules.combat.AutoWeb;
import com.gamesense.client.module.modules.combat.FastBow;
import com.gamesense.client.module.modules.combat.HoleFill;
import com.gamesense.client.module.modules.combat.KillAura;
import com.gamesense.client.module.modules.combat.OffhandCrystal;
import com.gamesense.client.module.modules.combat.OffhandGap;
import com.gamesense.client.module.modules.combat.SelfTrap;
import com.gamesense.client.module.modules.combat.SelfWeb;
import com.gamesense.client.module.modules.combat.Surround;
import com.gamesense.client.module.modules.exploits.CoordExploit;
import com.gamesense.client.module.modules.exploits.FastBreak;
import com.gamesense.client.module.modules.exploits.LiquidInteract;
import com.gamesense.client.module.modules.exploits.NoInteract;
import com.gamesense.client.module.modules.exploits.NoSwing;
import com.gamesense.client.module.modules.exploits.PortalGodMode;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.hud.ArmorHUD;
import com.gamesense.client.module.modules.hud.CombatInfo;
import com.gamesense.client.module.modules.hud.InventoryViewer;
import com.gamesense.client.module.modules.hud.ModuleArrayList;
import com.gamesense.client.module.modules.hud.Notifications;
import com.gamesense.client.module.modules.hud.Overlay;
import com.gamesense.client.module.modules.hud.PotionEffects;
import com.gamesense.client.module.modules.hud.TargetHUD;
import com.gamesense.client.module.modules.hud.TextRadar;
import com.gamesense.client.module.modules.misc.Announcer;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.gamesense.client.module.modules.misc.AutoReply;
import com.gamesense.client.module.modules.misc.AutoTool;
import com.gamesense.client.module.modules.misc.ChatModifier;
import com.gamesense.client.module.modules.misc.ChatSuffix;
import com.gamesense.client.module.modules.misc.FakePlayer;
import com.gamesense.client.module.modules.misc.FastPlace;
import com.gamesense.client.module.modules.misc.HoosiersDupe;
import com.gamesense.client.module.modules.misc.HotbarRefill;
import com.gamesense.client.module.modules.misc.MCF;
import com.gamesense.client.module.modules.misc.MultiTask;
import com.gamesense.client.module.modules.misc.NoEntityTrace;
import com.gamesense.client.module.modules.misc.NoKick;
import com.gamesense.client.module.modules.misc.PvPInfo;
import com.gamesense.client.module.modules.movement.Anchor;
import com.gamesense.client.module.modules.movement.Blink;
import com.gamesense.client.module.modules.movement.HoleTP;
import com.gamesense.client.module.modules.movement.PlayerTweaks;
import com.gamesense.client.module.modules.movement.ReverseStep;
import com.gamesense.client.module.modules.movement.Speed;
import com.gamesense.client.module.modules.movement.Sprint;
import com.gamesense.client.module.modules.movement.Step;
import com.gamesense.client.module.modules.render.BlockHighlight;
import com.gamesense.client.module.modules.render.CapesModule;
import com.gamesense.client.module.modules.render.CityESP;
import com.gamesense.client.module.modules.render.ESP;
import com.gamesense.client.module.modules.render.Freecam;
import com.gamesense.client.module.modules.render.Fullbright;
import com.gamesense.client.module.modules.render.HitSpheres;
import com.gamesense.client.module.modules.render.HoleESP;
import com.gamesense.client.module.modules.render.LogoutSpots;
import com.gamesense.client.module.modules.render.Nametags;
import com.gamesense.client.module.modules.render.NoRender;
import com.gamesense.client.module.modules.render.RenderTweaks;
import com.gamesense.client.module.modules.render.ShulkerViewer;
import com.gamesense.client.module.modules.render.SkyColor;
import com.gamesense.client.module.modules.render.Tracers;
import com.gamesense.client.module.modules.render.ViewModel;
import com.gamesense.client.module.modules.render.VoidESP;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ModuleManager {
	public static ArrayList<Module> modules;

	public ModuleManager(){
		modules = new ArrayList<>();
		//Combat
		addMod(new AutoArmor());
		addMod(new AutoCrystal());
		addMod(new AutoTotem());
		addMod(new AutoTrap());
		addMod(new AutoWeb());
		addMod(new FastBow());
		addMod(new HoleFill());
		addMod(new KillAura());
		addMod(new OffhandCrystal());
		addMod(new OffhandGap());
		addMod(new SelfTrap());
		addMod(new SelfWeb());
		addMod(new Surround());
		//Exploits
		addMod(new CoordExploit());
		addMod(new FastBreak());
		addMod(new LiquidInteract());
		addMod(new NoInteract());
		addMod(new NoSwing());
		addMod(new PortalGodMode());
		//Movement
		addMod(new Anchor());
		addMod(new Blink());
		addMod(new HoleTP());
		addMod(new PlayerTweaks());
		addMod(new ReverseStep());
		addMod(new Speed());
		addMod(new Sprint());
		addMod(new Step());
		//Misc
		addMod(new Announcer());
		addMod(new AutoGG());
		addMod(new AutoReply());
		addMod(new AutoTool());
		addMod(new ChatModifier());
		addMod(new ChatSuffix());
		addMod(new FastPlace());
		addMod(new FakePlayer());
		addMod(new HoosiersDupe());
		addMod(new HotbarRefill());
		addMod(new MCF());
		addMod(new MultiTask());
		addMod(new NoEntityTrace());
		addMod(new NoKick());
		addMod(new PvPInfo());
		//Render
		addMod(new BlockHighlight());
		addMod(new CapesModule());
		addMod(new CityESP());
		addMod(new ESP());
		addMod(new Freecam());
		addMod(new Fullbright());
		addMod(new HitSpheres());
		addMod(new HoleESP());
		addMod(new LogoutSpots());
		addMod(new Nametags());
		addMod(new NoRender());
		addMod(new RenderTweaks());
		addMod(new ShulkerViewer());
		addMod(new SkyColor());
		addMod(new Tracers());
		addMod(new ViewModel());
		addMod(new VoidESP());
		//HUD
		addMod(new ArmorHUD());
		addMod(new ModuleArrayList());
		addMod(new CombatInfo());
		addMod(new InventoryViewer());
		addMod(new Notifications());
		addMod(new Overlay());
		addMod(new PotionEffects());
		addMod(new TargetHUD());
		addMod(new TextRadar());
		//GUI
		addMod(new ClickGuiModule());
		addMod(new ColorMain());
	}

	public static void addMod(Module m){
		modules.add(m);
	}

	public static void onUpdate() {
		modules.stream().filter(Module::isEnabled).forEach(Module::onUpdate);
	}

	public static void onRender() {
		modules.stream().filter(Module::isEnabled).forEach(Module::onRender);
	}

	public static void onWorldRender(RenderWorldLastEvent event) {
		Minecraft.getMinecraft().profiler.startSection("gamesense");
		Minecraft.getMinecraft().profiler.startSection("setup");
		GameSenseTessellator.prepare();
		RenderEvent e = new RenderEvent(event.getPartialTicks());
		Minecraft.getMinecraft().profiler.endSection();

		modules.stream().filter(module -> module.isEnabled()).forEach(module -> {
			Minecraft.getMinecraft().profiler.startSection(module.getName());
			module.onWorldRender(e);
			Minecraft.getMinecraft().profiler.endSection();
		});

		Minecraft.getMinecraft().profiler.startSection("release");
		GameSenseTessellator.release();
		Minecraft.getMinecraft().profiler.endSection();
		Minecraft.getMinecraft().profiler.endSection();
	}

	public static ArrayList<Module> getModules() {
		return modules;
	}

	public static ArrayList<Module> getModulesInCategory(Module.Category c){
		ArrayList<Module> list = (ArrayList<Module>) getModules().stream().filter(m -> m.getCategory().equals(c)).collect(Collectors.toList());
		return list;
	}

	public static void onBind(int key) {
		if (key == 0 || key == Keyboard.KEY_NONE) return;
		modules.forEach(module -> {
			if(module.getBind() == key){
				module.toggle();
			}
		});
	}

	public static Module getModuleByName(String name){
		Module m = getModules().stream().filter(mm->mm.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		return m;
	}

	public static boolean isModuleEnabled(String name){
		Module m = getModules().stream().filter(mm->mm.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		return m.isEnabled();
	}

	public static boolean isModuleEnabled(Module m){
		return m.isEnabled();
	}

	public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
		return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
	}

	public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
		return getInterpolatedAmount(entity, ticks, ticks, ticks);
	}

	public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
		return new Vec3d(
				(entity.posX - entity.lastTickPosX) * x,
				(entity.posY - entity.lastTickPosY) * y,
				(entity.posZ - entity.lastTickPosZ) * z
		);
	}
}