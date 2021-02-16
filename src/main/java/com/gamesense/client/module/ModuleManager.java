package com.gamesense.client.module;

import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.modules.combat.*;
import com.gamesense.client.module.modules.exploits.*;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.gui.HUDEditor;
import com.gamesense.client.module.modules.hud.*;
import com.gamesense.client.module.modules.misc.*;
import com.gamesense.client.module.modules.movement.*;
import com.gamesense.client.module.modules.render.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ModuleManager {

    public static ArrayList<Module> modules;

    public ModuleManager() {
        modules = new ArrayList<>();
        //Combat
		addMod(new AntiCrystal());
        addMod(new AutoAnvil());
        addMod(new AutoArmor());
        addMod(new AutoCrystalGS());
        addMod(new AutoTrap());
        addMod(new AutoWeb());
        addMod(new BedAura());
        addMod(new Blocker());
        addMod(new FastBow());
        addMod(new HoleFill());
        addMod(new KillAura());
		addMod(new OffHand());
        addMod(new PistonCrystal());
        addMod(new SelfTrap());
        addMod(new SelfWeb());
        addMod(new Surround());
        //Exploits
        addMod(new CoordExploit());
        addMod(new FastBreak());
        addMod(new LiquidInteract());
        addMod(new NoInteract());
        addMod(new NoSwing());
        addMod(new Reach());
		addMod(new PacketUse());
        addMod(new PacketXP());
        addMod(new PortalGodmode());
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
		addMod(new AutoGear());
        addMod(new AutoGG());
        addMod(new AutoReply());
		addMod(new AutoRespawn());
        addMod(new AutoTool());
        addMod(new ChatModifier());
        addMod(new ChatSuffix());
        addMod(new DiscordRPCModule());
        addMod(new FastPlace());
        addMod(new FakePlayer());
        addMod(new HoosiersDupe());
        addMod(new HotbarRefill());
        addMod(new MCF());
        addMod(new MultiTask());
        addMod(new NoEntityTrace());
        addMod(new NoKick());
		addMod(new PhysicsSpammer());
        addMod(new PvPInfo());
		addMod(new SortInventory());
        //Render
        addMod(new BlockHighlight());
        addMod(new BreakESP());
        addMod(new CapesModule());
        addMod(new Chams());
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
        addMod(new ArrayListModule());
        addMod(new CombatInfo());
        addMod(new InventoryViewer());
        addMod(new Notifications());
        addMod(new PotionEffects());
        addMod(new Radar());
        addMod(new TabGUIModule());
        addMod(new TargetHUD());
        addMod(new TargetInfo());
        addMod(new TextRadar());
        addMod(new Watermark());
        addMod(new Welcomer());
        //GUI
        addMod(new ClickGuiModule());
        addMod(new ColorMain());
        addMod(new HUDEditor());
    }

    public static void addMod(Module module) {
        modules.add(module);
    }

    public static void onBind(int key) {
        if (key == Keyboard.KEY_NONE) return;

        for (Module module : modules) {
            if (module.getBind() != key) continue;
            module.toggle();
        }
    }

    public static void onUpdate() {
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onUpdate();
        }
    }

    public static void onRender() {
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onRender();
        }
        GameSense.getInstance().gameSenseGUI.render();
    }

    public static void onWorldRender(RenderWorldLastEvent event) {
        Minecraft.getMinecraft().profiler.startSection("gamesense");
        Minecraft.getMinecraft().profiler.startSection("setup");
        RenderUtil.prepare();
        RenderEvent e = new RenderEvent(event.getPartialTicks());
        Minecraft.getMinecraft().profiler.endSection();

        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            Minecraft.getMinecraft().profiler.startSection(module.getName());
            module.onWorldRender(e);
            Minecraft.getMinecraft().profiler.endSection();
        }

        Minecraft.getMinecraft().profiler.startSection("release");
        RenderUtil.release();
        Minecraft.getMinecraft().profiler.endSection();
        Minecraft.getMinecraft().profiler.endSection();
    }

    public static ArrayList<Module> getModules() {
        return modules;
    }

    public static ArrayList<Module> getModulesInCategory(Module.Category category) {
        ArrayList<Module> list = new ArrayList<>();

        for (Module module : modules) {
            if (!module.getCategory().equals(category)) continue;
            list.add(module);
        }

        return list;
    }

    public static Module getModuleByName(String name) {
        for (Module module : modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }

        return null;
    }

    public static boolean isModuleEnabled(String name) {
        for (Module module : modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module.isEnabled();
        }

        return false;
    }

    public static boolean isModuleEnabled(Module module) {
        return module.isEnabled();
    }
}