package com.gamesense.client.module;

import com.gamesense.client.module.modules.combat.AntiCrystal;
import com.gamesense.client.module.modules.combat.AutoAnvil;
import com.gamesense.client.module.modules.combat.AutoArmor;
import com.gamesense.client.module.modules.combat.AutoCrystalGS;
import com.gamesense.client.module.modules.combat.AutoTrap;
import com.gamesense.client.module.modules.combat.AutoWeb;
import com.gamesense.client.module.modules.combat.BedAura;
import com.gamesense.client.module.modules.combat.Blocker;
import com.gamesense.client.module.modules.combat.CevBreaker;
import com.gamesense.client.module.modules.combat.FastBow;
import com.gamesense.client.module.modules.combat.HoleFill;
import com.gamesense.client.module.modules.combat.KillAura;
import com.gamesense.client.module.modules.combat.OffHand;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import com.gamesense.client.module.modules.combat.SelfTrap;
import com.gamesense.client.module.modules.combat.SelfWeb;
import com.gamesense.client.module.modules.combat.Surround;
import com.gamesense.client.module.modules.exploits.FastBreak;
import com.gamesense.client.module.modules.exploits.HoosiersDupe;
import com.gamesense.client.module.modules.exploits.LiquidInteract;
import com.gamesense.client.module.modules.exploits.NoInteract;
import com.gamesense.client.module.modules.exploits.NoSwing;
import com.gamesense.client.module.modules.exploits.PacketUse;
import com.gamesense.client.module.modules.exploits.PacketXP;
import com.gamesense.client.module.modules.exploits.PortalGodmode;
import com.gamesense.client.module.modules.exploits.Reach;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.gui.HUDEditor;
import com.gamesense.client.module.modules.hud.ArmorHUD;
import com.gamesense.client.module.modules.hud.ArrayListModule;
import com.gamesense.client.module.modules.hud.CombatInfo;
import com.gamesense.client.module.modules.hud.Coordinates;
import com.gamesense.client.module.modules.hud.InventoryViewer;
import com.gamesense.client.module.modules.hud.Notifications;
import com.gamesense.client.module.modules.hud.PotionEffects;
import com.gamesense.client.module.modules.hud.Radar;
import com.gamesense.client.module.modules.hud.Speedometer;
import com.gamesense.client.module.modules.hud.TabGUIModule;
import com.gamesense.client.module.modules.hud.TargetHUD;
import com.gamesense.client.module.modules.hud.TargetInfo;
import com.gamesense.client.module.modules.hud.TextRadar;
import com.gamesense.client.module.modules.hud.Watermark;
import com.gamesense.client.module.modules.hud.Welcomer;
import com.gamesense.client.module.modules.misc.Announcer;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.gamesense.client.module.modules.misc.AutoGear;
import com.gamesense.client.module.modules.misc.AutoReply;
import com.gamesense.client.module.modules.misc.AutoRespawn;
import com.gamesense.client.module.modules.misc.AutoTool;
import com.gamesense.client.module.modules.misc.ChatModifier;
import com.gamesense.client.module.modules.misc.ChatSuffix;
import com.gamesense.client.module.modules.misc.DiscordRPCModule;
import com.gamesense.client.module.modules.misc.FakePlayer;
import com.gamesense.client.module.modules.misc.FastPlace;
import com.gamesense.client.module.modules.misc.HotbarRefill;
import com.gamesense.client.module.modules.misc.MCF;
import com.gamesense.client.module.modules.misc.MultiTask;
import com.gamesense.client.module.modules.misc.NoEntityTrace;
import com.gamesense.client.module.modules.misc.NoKick;
import com.gamesense.client.module.modules.misc.PhysicsSpammer;
import com.gamesense.client.module.modules.misc.PvPInfo;
import com.gamesense.client.module.modules.misc.SortInventory;
import com.gamesense.client.module.modules.misc.XCarry;
import com.gamesense.client.module.modules.movement.Anchor;
import com.gamesense.client.module.modules.movement.Blink;
import com.gamesense.client.module.modules.movement.HoleTP;
import com.gamesense.client.module.modules.movement.PlayerTweaks;
import com.gamesense.client.module.modules.movement.ReverseStep;
import com.gamesense.client.module.modules.movement.Speed;
import com.gamesense.client.module.modules.movement.Sprint;
import com.gamesense.client.module.modules.movement.Step;
import com.gamesense.client.module.modules.render.BlockHighlight;
import com.gamesense.client.module.modules.render.BreakESP;
import com.gamesense.client.module.modules.render.Bucked;
import com.gamesense.client.module.modules.render.CapesModule;
import com.gamesense.client.module.modules.render.Chams;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;

public class ModuleManager {

    private static LinkedHashMap<Class<? extends Module>, Module> modulesClassMap;
    private static LinkedHashMap<String, Module> modulesNameMap;

    public static void init() {
        modulesClassMap = new LinkedHashMap<>();
        modulesNameMap = new LinkedHashMap<>();

        //Combat
        addMod(new AntiCrystal());
        addMod(new AutoAnvil());
        addMod(new AutoArmor());
        addMod(new AutoCrystalGS());
        addMod(new AutoTrap());
        addMod(new AutoWeb());
        addMod(new BedAura());
        addMod(new Blocker());
        addMod(new CevBreaker());
        addMod(new FastBow());
        addMod(new HoleFill());
        addMod(new KillAura());
        addMod(new OffHand());
        addMod(new PistonCrystal());
        addMod(new SelfTrap());
        addMod(new SelfWeb());
        addMod(new Surround());
        //Exploits
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
        addMod(new XCarry());
        //Render
        addMod(new BlockHighlight());
        addMod(new BreakESP());
        addMod(new Bucked());
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
        addMod(new Coordinates());
        addMod(new InventoryViewer());
        addMod(new Notifications());
        addMod(new PotionEffects());
        addMod(new Radar());
        addMod(new Speedometer());
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
        modulesClassMap.put(module.getClass(), module);
        modulesNameMap.put(module.getName().toLowerCase(Locale.ROOT), module);
    }

    public static Collection<Module> getModules() {
        return modulesClassMap.values();
    }

    public static ArrayList<Module> getModulesInCategory(Category category) {
        ArrayList<Module> list = new ArrayList<>();

        for (Module module : modulesClassMap.values()) {
            if (!module.getCategory().equals(category)) continue;
            list.add(module);
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T getModule(Class<T> clazz) {
        return (T) modulesClassMap.get(clazz);
    }

    public static Module getModule(String name) {
        if (name == null) return null;
        return modulesNameMap.get(name.toLowerCase(Locale.ROOT));
    }

    public static boolean isModuleEnabled(Class<? extends Module> clazz) {
        Module module = getModule(clazz);
        return module != null && module.isEnabled();
    }

    public static boolean isModuleEnabled(String name) {
        Module module = getModule(name);
        return module != null && module.isEnabled();
    }
}