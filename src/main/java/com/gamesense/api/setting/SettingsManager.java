package com.gamesense.api.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gamesense.client.module.Module;

public class SettingsManager {

	private final List<Setting> settings;

	public SettingsManager() {
		this.settings = new ArrayList<>();
	}

	public List<Setting> getSettings() {
		return this.settings;
	}

	public void addSetting(final Setting setting) {
		this.settings.add(setting);
	}

	public Setting getSettingByNameAndMod(final String name, final Module parent) {
		return this.settings.stream().filter(s -> s.getParent().equals(parent)).filter(s -> s.getConfigName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public Setting getSettingByNameAndModConfig(final String configName, final Module parent) {
		return this.settings.stream().filter(s -> s.getParent().equals(parent)).filter(s -> s.getConfigName().equalsIgnoreCase(configName)).findFirst().orElse(null);
	}

	public List<Setting> getSettingsForMod(final Module parent) {
		return this.settings.stream().filter(s -> s.getParent().equals(parent)).collect(Collectors.toList());
	}

	public List<Setting> getSettingsByCategory(final Module.Category category) {
		return this.settings.stream().filter(s -> s.getCategory().equals(category)).collect(Collectors.toList());
	}

	public Setting getSettingByName(String name) {
		for (Setting set : getSettings()) {
			if (set.getName().equalsIgnoreCase(name)) {
				return set;
			}
		}
		return null;
	}
}