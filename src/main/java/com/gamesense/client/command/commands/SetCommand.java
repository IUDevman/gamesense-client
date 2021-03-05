package com.gamesense.client.command.commands;

import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

/**
 * @Author Hoosiers on 8/4/2020
 * @Ported and modified on 11/05/2020
 */

@Command.Declaration(name = "Set", syntax = "set [module] [setting] value (no color support)", alias = {"set", "setmodule", "changesetting", "setting"})
public class SetCommand extends Command {

    //I should probably add an option for color settings in the future
    public void onCommand(String command, String[] message) throws Exception {
        String main = message[0];

        Module module = ModuleManager.getModule(main);

        if (module == null) {
            MessageBus.sendCommandMessage(this.getSyntax(), true);
            return;
        }

        SettingsManager.getSettingsForModule(module).stream().filter(setting -> setting.getConfigName().equalsIgnoreCase(message[1])).forEach(setting -> {
            if (setting instanceof BooleanSetting) {
                if (message[2].equalsIgnoreCase("true") || message[2].equalsIgnoreCase("false")) {
                    setting.setValue(Boolean.parseBoolean(message[2]));
                    MessageBus.sendCommandMessage(module.getName() + " " + setting.getConfigName() + " set to: " + setting.getValue() + "!", true);
                } else {
                    MessageBus.sendCommandMessage(this.getSyntax(), true);
                }
            } else if (setting instanceof IntegerSetting) {
                if (Integer.parseInt(message[2]) > ((IntegerSetting) setting).getMax()) {
                    setting.setValue(((IntegerSetting) setting).getMax());
                }
                if (Integer.parseInt(message[2]) < ((IntegerSetting) setting).getMin()) {
                    setting.setValue(((IntegerSetting) setting).getMin());
                }
                if (Integer.parseInt(message[2]) < ((IntegerSetting) setting).getMax() && Integer.parseInt(message[2]) > ((IntegerSetting) setting).getMin()) {
                    setting.setValue(Integer.parseInt(message[2]));
                }
                MessageBus.sendCommandMessage(module.getName() + " " + setting.getConfigName() + " set to: " + setting.getValue() + "!", true);
            } else if (setting instanceof DoubleSetting) {
                if (Double.parseDouble(message[2]) > ((DoubleSetting) setting).getMax()) {
                    setting.setValue(((DoubleSetting) setting).getMax());
                }
                if (Double.parseDouble(message[2]) < ((DoubleSetting) setting).getMin()) {
                    setting.setValue(((DoubleSetting) setting).getMin());
                }
                if (Double.parseDouble(message[2]) < ((DoubleSetting) setting).getMax() && Double.parseDouble(message[2]) > ((DoubleSetting) setting).getMin()) {
                    setting.setValue(Double.parseDouble(message[2]));
                }
                MessageBus.sendCommandMessage(module.getName() + " " + setting.getConfigName() + " set to: " + setting.getValue() + "!", true);
            } else if (setting instanceof ModeSetting) {
                if (!((ModeSetting) setting).getModes().contains(message[2])) {
                    MessageBus.sendCommandMessage(this.getSyntax(), true);
                } else {
                    setting.setValue(message[2]);
                    MessageBus.sendCommandMessage(module.getName() + " " + setting.getConfigName() + " set to: " + setting.getValue() + "!", true);
                }
            } else {
                MessageBus.sendCommandMessage(this.getSyntax(), true);
            }
        });
    }
}