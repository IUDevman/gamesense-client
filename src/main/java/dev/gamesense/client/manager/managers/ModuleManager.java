package dev.gamesense.client.manager.managers;

import dev.gamesense.GameSense;
import dev.gamesense.client.Manifest;
import dev.gamesense.client.manager.Manager;
import dev.gamesense.client.module.Category;
import dev.gamesense.client.module.Module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author IUDevman
 * @since 02-09-2022
 */

public final class ModuleManager implements Manager {

    private final ArrayList<Module> modules = new ArrayList<>();

    @Override
    public void load() {
        GameSense.INSTANCE.LOGGER.info("ModuleManager");

        Manifest.getModulesToLoad().forEach(this::addModule);

        postSortModules();
    }

    public void postSortModules() {
        this.modules.sort(Comparator.comparing(Module::getName));
    }

    public void addModule(Module module) {
        this.modules.add(module);
    }

    public ArrayList<Module> getModules() {
        return this.modules;
    }

    public ArrayList<Module> getModulesInCategory(Category category) {
        return this.modules.stream().filter(module -> module.getCategory().equals(category)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Module> getEnabledModules() {
        return this.modules.stream().filter(Module::isEnabled).collect(Collectors.toCollection(ArrayList::new));
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> aClass) {
        return (T) this.modules.stream().filter(module -> module.getClass().equals(aClass)).findFirst().orElse(null);
    }

    public Module getModule(String name) {
        return this.modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
