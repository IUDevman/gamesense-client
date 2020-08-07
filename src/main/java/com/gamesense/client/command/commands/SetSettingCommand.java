package com.gamesense.client.command.commands;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

/**
 * Made by Hoosiers on 8/4/20 for GameSense
 */

public class SetSettingCommand extends Command {

    @Override
    public String[] getAlias() {
        return new String[]{
                "set"
        };
    }

    @Override
    public String getSyntax() {
        return "set <Module> <Setting> <Value>";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        for (Module m : ModuleManager.getModules()) {
            if (m.getName().equalsIgnoreCase(args[0])) {
                GameSenseMod.getInstance().settingsManager.getSettingsForMod(m).stream().filter(s -> s.getConfigName().equalsIgnoreCase(args[1])).forEach(s -> {
                    if (s.getType().equals(Setting.Type.BOOLEAN)) {
                            ((Setting.b) s).setValue(Boolean.parseBoolean(args[2]));
                            Command.sendClientMessage(s.getConfigName() + " set to " + ((Setting.b) s).getValue() + "!");
                    }
                    if (s.getType().equals(Setting.Type.INT)) {
                        if (Integer.parseInt(args[2]) > ((Setting.i) s).getMax()) {
                            ((Setting.i) s).setValue(((Setting.i) s).getMax());
                        }
                        if (Integer.parseInt(args[2]) < ((Setting.i) s).getMin()) {
                            ((Setting.i) s).setValue(((Setting.i) s).getMin());
                        }
                        if (Integer.parseInt(args[2]) < ((Setting.i) s).getMax() && Integer.parseInt(args[2]) > ((Setting.i) s).getMin()) {
                            ((Setting.i) s).setValue(Integer.parseInt(args[2]));
                        }
                        Command.sendClientMessage(s.getConfigName() + " set to " + ((Setting.i) s).getValue() + "!");
                    }
                    if (s.getType().equals(Setting.Type.DOUBLE)) {
                        if (Double.parseDouble(args[2]) > ((Setting.d) s).getMax()) {
                            ((Setting.d) s).setValue(((Setting.d) s).getMax());
                        }
                        if (Double.parseDouble(args[2]) < ((Setting.d) s).getMin()) {
                            ((Setting.d) s).setValue(((Setting.d) s).getMin());
                        }
                        if (Double.parseDouble(args[2]) < ((Setting.d) s).getMax() && Double.parseDouble(args[2]) > ((Setting.d) s).getMin()) {
                            ((Setting.d) s).setValue(Double.parseDouble(args[2]));
                        }
                        Command.sendClientMessage(s.getConfigName() + " set to " + ((Setting.d) s).getValue() + "!");
                    }
                    if (s.getType().equals(Setting.Type.MODE)) {
                        if (!((Setting.mode) s).getModes().contains(args[2])) {
                            Command.sendClientMessage("Invalid input!");
                        } else {
                            ((Setting.mode) s).setValue(args[2]);
                            Command.sendClientMessage(s.getConfigName() + " set to " + ((Setting.mode) s).getValue() + "!");
                        }
                    }
                });
            }
        }
    }
}
