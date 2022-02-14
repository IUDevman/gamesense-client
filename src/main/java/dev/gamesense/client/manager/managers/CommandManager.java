package dev.gamesense.client.manager.managers;

import dev.gamesense.GameSense;
import dev.gamesense.client.Manifest;
import dev.gamesense.client.command.Command;
import dev.gamesense.client.manager.Manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author IUDevman
 * @since 02-09-2022
 */

public final class CommandManager implements Manager {

    private String prefix = "-";

    private final ArrayList<Command> commands = new ArrayList<>();

    @Override
    public void load() {
        GameSense.INSTANCE.LOGGER.info("CommandManager");

        Manifest.getCommandsToLoad().forEach(this::addCommand);

        postSortCommands();
    }

    public void postSortCommands() {
        this.commands.sort(Comparator.comparing(Command::getName));
    }

    public void addCommand(Command command) {
        this.commands.add(command);
    }

    public ArrayList<Command> getCommands() {
        return this.commands;
    }

    public Command getCommand(String name) {
        return this.commands.stream().filter(command -> command.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        if (prefix.length() > 1) return;

        this.prefix = prefix;
    }

    public void dispatchCommands(String message) {
        String[] splitMessage = message.split("\\s");

        AtomicBoolean foundMessage = new AtomicBoolean(false);

        getCommands().forEach(command -> {
            if (command.getName().equalsIgnoreCase(splitMessage[0])) {
                foundMessage.set(true);
                command.onCommand(splitMessage);
            }
        });

        if (!foundMessage.get()) {
            //todo: command error message
        }
    }
}
