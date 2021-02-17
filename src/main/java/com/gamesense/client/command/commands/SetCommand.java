package com.gamesense.client.command.commands;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.GameSense;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

/**
 * @Author Hoosiers on 8/4/2020
 *
 * @Ported and modified on 11/05/2020
 */

public class SetCommand extends Command {

    public SetCommand() {
        super("Set");

        setCommandSyntax(Command.getCommandPrefix() + "set [module] [setting] value (no color support)");
        setCommandAlias(new String[]{
                "set", "setmodule", "changesetting", "setting"
        });
    }

    //I should probably add an option for color settings in the future
    public void onCommand(String command, String[] message) throws Exception {
        String main = message[0];

        Module module = ModuleManager.getModule(main);

        if (module == null) {
            MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
            return;
        }

        GameSense.getInstance().settingsManager.getSettingsForMod(module).stream().filter(setting -> setting.getConfigName().equalsIgnoreCase(message[1])).forEach(setting -> {
            if (setting.getType().equals(Setting.Type.BOOLEAN)) {
                if (message[2].equalsIgnoreCase("true") || message[2].equalsIgnoreCase("false")) {
                    ((Setting.Boolean) setting).setValue(Boolean.parseBoolean(message[2]));
                    MessageBus.sendCommandMessage(module.getName() + " " + setting.getConfigName() + " set to: " + ((Setting.Boolean) setting).getValue() + "!", true);
                }
                else {
                    MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
                }
            }
            else if (setting.getType().equals(Setting.Type.INTEGER)) {
                if (Integer.parseInt(message[2]) > ((Setting.Integer) setting).getMax()) {
                    ((Setting.Integer) setting).setValue(((Setting.Integer) setting).getMax());
                }
                if (Integer.parseInt(message[2]) < ((Setting.Integer) setting).getMin()) {
                    ((Setting.Integer) setting).setValue(((Setting.Integer) setting).getMin());
                }
                if (Integer.parseInt(message[2]) < ((Setting.Integer) setting).getMax() && Integer.parseInt(message[2]) > ((Setting.Integer) setting).getMin()) {
                    ((Setting.Integer) setting).setValue(Integer.parseInt(message[2]));
                }
                MessageBus.sendCommandMessage(module.getName() + " " + setting.getConfigName() + " set to: " + ((Setting.Integer) setting).getValue() + "!", true);
            }
            else if (setting.getType().equals(Setting.Type.DOUBLE)) {
                if (Double.parseDouble(message[2]) > ((Setting.Double) setting).getMax()) {
                    ((Setting.Double) setting).setValue(((Setting.Double) setting).getMax());
                }
                if (Double.parseDouble(message[2]) < ((Setting.Double) setting).getMin()) {
                    ((Setting.Double) setting).setValue(((Setting.Double) setting).getMin());
                }
                if (Double.parseDouble(message[2]) < ((Setting.Double) setting).getMax() && Double.parseDouble(message[2]) > ((Setting.Double) setting).getMin()) {
                    ((Setting.Double) setting).setValue(Double.parseDouble(message[2]));
                }
                MessageBus.sendCommandMessage(module.getName() + " " + setting.getConfigName() + " set to: " + ((Setting.Double) setting).getValue() + "!", true);
            }
            else if (setting.getType().equals(Setting.Type.MODE)) {
                if (!((Setting.Mode) setting).getModes().contains(message[2])) {
                    MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
                }
                else {
                    ((Setting.Mode) setting).setValue(message[2]);
                    MessageBus.sendCommandMessage(module.getName() + " " + setting.getConfigName() + " set to: " + ((Setting.Mode) setting).getValue() + "!", true);
                }
            }
            else {
                MessageBus.sendCommandMessage(this.getCommandSyntax(), true);
            }
        });
    }
}