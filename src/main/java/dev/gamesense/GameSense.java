package dev.gamesense;

import dev.gamesense.backend.event.handler.EventHandler;
import dev.gamesense.client.manager.Manager;
import dev.gamesense.client.manager.managers.CommandManager;
import dev.gamesense.client.manager.managers.ModuleManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author IUDevman
 * @since 02-09-2022
 */

@Mod(modid = GameSense.MOD_ID, name = GameSense.MOD_NAME, version = GameSense.MOD_VERSION)
public final class GameSense {

    public static final String MOD_NAME = "GameSense";
    public static final String MOD_ID = "gamesense";
    public static final String MOD_VERSION = "3.0.0-SNAPSHOT";

    @Mod.Instance
    public static GameSense INSTANCE;

    public GameSense() {
        INSTANCE = this;
    }

    public final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public EventHandler EVENT_HANDLER;

    public CommandManager COMMAND_MANAGER;
    public ModuleManager MODULE_MANAGER;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        long startTime = System.currentTimeMillis();
        this.LOGGER.info("Initializing " + MOD_NAME + " " + MOD_VERSION + "!");

        initClient();

        long finishTime = System.currentTimeMillis() - startTime;
        this.LOGGER.info("Finished initializing " + MOD_NAME + " " + MOD_VERSION + " (" + finishTime + "ms)!");
    }

    private void initClient() {
        this.EVENT_HANDLER = new EventHandler();

        this.COMMAND_MANAGER = returnLoadedManager(new CommandManager());

        this.MODULE_MANAGER = returnLoadedManager(new ModuleManager());
    }

    private <T extends Manager> T returnLoadedManager(T manager) {
        this.EVENT_HANDLER.register(manager);
        manager.load();

        return manager;
    }
}
