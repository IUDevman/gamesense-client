package com.gamesense.client;

import com.gamesense.api.config.ConfigStopper;
import com.gamesense.api.config.LoadConfig;
import com.gamesense.api.config.SaveConfig;
import com.gamesense.api.event.EventProcessor;
import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.util.font.CFontRenderer;
import com.gamesense.api.util.misc.VersionChecker;
import com.gamesense.api.util.player.enemy.Enemies;
import com.gamesense.api.util.player.friends.Friends;
import com.gamesense.api.util.render.CapeUtils;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.module.ModuleManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import me.zero.alpine.EventBus;
import me.zero.alpine.EventManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.awt.Font;

@Mod(modid = GameSense.MODID, name = GameSense.MODNAME, version = GameSense.MODVER)
public class GameSense {

	public static final String MODNAME = "GameSense";
	public static final String MODID = "gamesense";
	public static final String MODVER = "d2.2.6";

	public static final Logger LOGGER = LogManager.getLogger(MODNAME);
	public static final EventBus EVENT_BUS = new EventManager();

	@Mod.Instance
	private static GameSense INSTANCE;

	public GameSense() {
		INSTANCE = this;
	}

	public static GameSense getInstance() {
		return INSTANCE;
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		Display.setTitle(MODNAME + " " + MODVER);

		LOGGER.info("Starting up " + MODNAME + " " + MODVER + "!");
		startClient();
		LOGGER.info("Finished initialization for " + MODNAME + " " + MODVER + "!");
	}

	public VersionChecker versionChecker;
	public EventProcessor eventProcessor;
	public CFontRenderer cFontRenderer;
	public SettingsManager settingsManager;
	public Friends friends;
	public Enemies enemies;
	public ModuleManager moduleManager;
	public GameSenseGUI gameSenseGUI;
	public SaveConfig saveConfig;
	public LoadConfig loadConfig;
	public CapeUtils capeUtils;

	private void startClient() {
		versionChecker = new VersionChecker();
		LOGGER.info("Version checked!");

		eventProcessor = new EventProcessor();
		eventProcessor.init();
		LOGGER.info("Events initialized!");

		cFontRenderer = new CFontRenderer(new Font("Verdana", Font.PLAIN, 18), true,true);
		LOGGER.info("Custom font initialized!");

		settingsManager = new SettingsManager();
		LOGGER.info("Settings initialized!");

		friends = new Friends();
		enemies = new Enemies();
		LOGGER.info("Friends and enemies initialized!");

		moduleManager = new ModuleManager();
		LOGGER.info("Modules initialized!");

		gameSenseGUI = new GameSenseGUI();
		LOGGER.info("GameSenseGUI initialized!");

		CommandManager.registerCommands();
		LOGGER.info("Commands initialized!");

		saveConfig = new SaveConfig();
		loadConfig = new LoadConfig();
		Runtime.getRuntime().addShutdownHook(new ConfigStopper());
		LOGGER.info("Config initialized!");

		capeUtils = new CapeUtils();
		LOGGER.info("Capes initialized!");
	}
}