package com.gamesense.client.command.commands;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

// Made by Hoosiers on 8/4/20 for GameSense

public class SetSettingCommand extends Command{

	@Override
	public String[] getAlias(){
		return new String[]{
				"set"
		};
	}

	@Override
	public String getSyntax(){
		return "set <module> <setting> <value>";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception{
		for (Module m : ModuleManager.getModules()){
			if (m.getName().equalsIgnoreCase(args[0])){
				GameSenseMod.getInstance().settingsManager.getSettingsForMod(m).stream().filter(s -> s.getConfigName().equalsIgnoreCase(args[1])).forEach(s -> {
					if (s.getType().equals(Setting.Type.BOOLEAN)){
							((Setting.Boolean) s).setValue(java.lang.Boolean.parseBoolean(args[2]));
							Command.sendClientMessage(s.getConfigName() + " set to " + ((Setting.Boolean) s).getValue() + "!");
					}
					if (s.getType().equals(Setting.Type.INTEGER)){
						if (java.lang.Integer.parseInt(args[2]) > ((Setting.Integer) s).getMax()){
							((Setting.Integer) s).setValue(((Setting.Integer) s).getMax());
						}
						if (java.lang.Integer.parseInt(args[2]) < ((Setting.Integer) s).getMin()){
							((Setting.Integer) s).setValue(((Setting.Integer) s).getMin());
						}
						if (java.lang.Integer.parseInt(args[2]) < ((Setting.Integer) s).getMax() && java.lang.Integer.parseInt(args[2]) > ((Setting.Integer) s).getMin()){
							((Setting.Integer) s).setValue(java.lang.Integer.parseInt(args[2]));
						}
						Command.sendClientMessage(s.getConfigName() + " set to " + ((Setting.Integer) s).getValue() + "!");
					}
					if (s.getType().equals(Setting.Type.DOUBLE)){
						if (java.lang.Double.parseDouble(args[2]) > ((Setting.Double) s).getMax()){
							((Setting.Double) s).setValue(((Setting.Double) s).getMax());
						}
						if (java.lang.Double.parseDouble(args[2]) < ((Setting.Double) s).getMin()){
							((Setting.Double) s).setValue(((Setting.Double) s).getMin());
						}
						if (java.lang.Double.parseDouble(args[2]) < ((Setting.Double) s).getMax() && java.lang.Double.parseDouble(args[2]) > ((Setting.Double) s).getMin()){
							((Setting.Double) s).setValue(java.lang.Double.parseDouble(args[2]));
						}
						Command.sendClientMessage(s.getConfigName() + " set to " + ((Setting.Double) s).getValue() + "!");
					}
					if (s.getType().equals(Setting.Type.MODE)){
						if (!((Setting.Mode) s).getModes().contains(args[2])){
							Command.sendClientMessage("Invalid input!");
						} else{
							((Setting.Mode) s).setValue(args[2]);
							Command.sendClientMessage(s.getConfigName() + " set to " + ((Setting.Mode) s).getValue() + "!");
						}
					}
				});
			}
		}
	}
}