package com.gamesense.client;

import java.awt.Font;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import com.gamesense.api.Stopper;
import com.gamesense.api.event.EventProcessor;
import com.gamesense.api.players.enemy.Enemies;
import com.gamesense.api.players.friends.Friends;
import com.gamesense.api.settings.SettingsManager;
import com.gamesense.api.util.config.LoadConfiguration;
import com.gamesense.api.util.config.LoadModules;
import com.gamesense.api.util.config.SaveConfiguration;
import com.gamesense.api.util.config.SaveModules;
import com.gamesense.api.util.font.CFontRenderer;
import com.gamesense.api.util.render.CapeUtils;
import com.gamesense.client.clickgui.ClickGUI;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.macro.MacroManager;
import com.gamesense.client.module.ModuleManager;

import me.zero.alpine.EventBus;
import me.zero.alpine.EventManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = GameSenseMod.MODID, name = GameSenseMod.FORGENAME, version = GameSenseMod.MODVER, clientSideOnly = true)
public class GameSenseMod{
	public static final String MODID = "gamesense";
	public static String MODNAME = "GameSense";
	public static final String MODVER = "v2.1.5";
	public static final String FORGENAME = "GameSense";

	public static final Logger log = LogManager.getLogger(MODNAME);

	public ClickGUI clickGUI;
	public SettingsManager settingsManager;
	public Friends friends;
	public ModuleManager moduleManager;
	public SaveConfiguration saveConfiguration;
	public LoadConfiguration loadConfiguration;
	public SaveModules saveModules;
	public LoadModules loadModules;
	public CapeUtils capeUtils;
	public MacroManager macroManager;
	public EventProcessor eventProcessor;
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
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event){
		eventProcessor = new EventProcessor();
		eventProcessor.init();

		fontRenderer = new CFontRenderer(new Font("Ariel", Font.PLAIN, 18), true,true);

		//TpsUtils tpsUtils = new TpsUtils();

		settingsManager = new SettingsManager();
		log.info("Settings initialized!");

		friends = new Friends();
		enemies = new Enemies();
		log.info("Friends and enemies initialized!");

		moduleManager = new ModuleManager();
		log.info("Modules initialized!");

		clickGUI = new ClickGUI();
		log.info("ClickGUI initialized!");

		macroManager = new MacroManager();
		log.info("Macros initialized!");

		saveConfiguration = new SaveConfiguration();
		saveModules = new SaveModules();
		Runtime.getRuntime().addShutdownHook(new Stopper());
		log.info("Config Saved!");

		loadConfiguration = new LoadConfiguration();
		loadModules = new LoadModules();
		log.info("Config Loaded!");

		CommandManager.initCommands();
		log.info("Commands initialized!");

		log.info("Initialization complete!\n");
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event){
		Display.setTitle(MODNAME + " " + MODVER);

		capeUtils = new CapeUtils();
		log.info("Capes initialised!");

		log.info("PostInitialization complete!\n");
	}

	public static GameSenseMod getInstance(){
		return INSTANCE;
	}
}