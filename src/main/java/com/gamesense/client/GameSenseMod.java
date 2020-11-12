package com.gamesense.client;

import java.awt.Font;

import com.gamesense.client.command.CommandManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import com.gamesense.api.config.ConfigStopper;
import com.gamesense.api.config.LoadConfig;
import com.gamesense.api.config.SaveConfig;
import com.gamesense.api.event.EventProcessor;
import com.gamesense.api.settings.SettingsManager;
import com.gamesense.api.util.font.CFontRenderer;
import com.gamesense.api.util.players.enemy.Enemies;
import com.gamesense.api.util.players.friends.Friends;
import com.gamesense.api.util.render.CapeUtils;
import com.gamesense.client.clickgui.GameSenseGUI;
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
	public static final String FORGENAME = "GameSense";
	public static final String MODVER = "v2.2.2";

	public static final Logger log = LogManager.getLogger(MODNAME);

	public EventProcessor eventProcessor;
	public SaveConfig saveConfig;
	public LoadConfig loadConfig;
	public ModuleManager moduleManager;
	public SettingsManager settingsManager;
	public static CFontRenderer fontRenderer;
	public CapeUtils capeUtils;
	public GameSenseGUI clickGUI;
	public Friends friends;
	public Enemies enemies;

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
		log.info("Events initialized!");

		fontRenderer = new CFontRenderer(new Font("Verdana", Font.PLAIN, 18), true,true);
		log.info("Custom font initialized!");

		settingsManager = new SettingsManager();
		log.info("Settings initialized!");

		friends = new Friends();
		enemies = new Enemies();
		log.info("Friends and enemies initialized!");

		moduleManager = new ModuleManager();
		log.info("Modules initialized!");

		clickGUI = new GameSenseGUI();
		log.info("ClickGUI initialized!");

		CommandManager.registerCommands();
		log.info("Commands initialized!");

		saveConfig = new SaveConfig();
		loadConfig = new LoadConfig();
		Runtime.getRuntime().addShutdownHook(new ConfigStopper());
		log.info("Config initialized!");

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