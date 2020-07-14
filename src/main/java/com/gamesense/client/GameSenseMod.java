package com.gamesense.client;

import com.gamesense.api.Stopper;
import com.gamesense.client.devgui.DevGUI;
import com.gamesense.client.command.CommandManager;
import com.gamesense.api.friends.Friends;
import com.gamesense.client.waypoint.WaypointManager;
import com.gamesense.api.enemy.Enemies;
import com.gamesense.api.settings.SettingsManager;
import com.gamesense.api.event.EventProcessor;
import com.gamesense.client.macro.MacroManager;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.api.util.CapeUtils;
import com.gamesense.api.util.ConfigUtils;
import com.gamesense.api.util.TpsUtils;
import com.gamesense.api.util.font.CFontRenderer;
import me.zero.alpine.EventBus;
import me.zero.alpine.EventManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.awt.*;

@Mod(modid = GameSenseMod.MODID, name = GameSenseMod.FORGENAME, version = GameSenseMod.MODVER, clientSideOnly = true)
public class GameSenseMod {
    public static final String MODID = "gamesense";
    public static String MODNAME = "GameSense";
    public static final String MODVER = "v2.0";
    public static final String FORGENAME = "GameSense";

    public static final Logger log = LogManager.getLogger(MODNAME);
    public DevGUI devGUI;
    public SettingsManager settingsManager;
    public Friends friends;
    public ModuleManager moduleManager;
    public ConfigUtils configUtils;
    public CapeUtils capeUtils;
    public MacroManager macroManager;
    EventProcessor eventProcessor;
    public WaypointManager waypointManager;
    public static CFontRenderer fontRenderer;
    public static Enemies enemies;

    public static final EventBus EVENT_BUS = new EventManager();

    @Mod.Instance
    private static GameSenseMod INSTANCE;

    public GameSenseMod(){
        INSTANCE = this;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        //log.info("PreInitialization complete!\n");

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        eventProcessor = new EventProcessor();
        eventProcessor.init();
        fontRenderer = new CFontRenderer(new Font("Verdana", Font.PLAIN, 20), true, false);
        TpsUtils tpsUtils = new TpsUtils();

        settingsManager = new SettingsManager();
        log.info("Settings initialized!");

        friends = new Friends();
        enemies = new Enemies();
        log.info("Friends and enemies initialized!");

        moduleManager = new ModuleManager();
        log.info("Modules initialized!");

        devGUI = new DevGUI();
        log.info("DevGUI initialized!");

        macroManager = new MacroManager();
        log.info("Macros initialized!");

        configUtils = new ConfigUtils();
        Runtime.getRuntime().addShutdownHook(new Stopper());
        log.info("Config loaded!");

        CommandManager.initCommands();
        log.info("Commands initialized!");

        waypointManager = new WaypointManager();
        log.info("Waypoints initialized!");

        log.info("Initialization complete!\n");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        Display.setTitle(MODNAME + " " + MODVER);

        capeUtils = new CapeUtils();
        log.info("Capes initialised!");

        //WelcomeWindow ww = new WelcomeWindow();
        //ww.setVisible(false);
        log.info("PostInitialization complete!\n");
    }

    public static GameSenseMod getInstance(){
        return INSTANCE;
    }

}
