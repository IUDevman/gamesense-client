package com.gamesense.client.command;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.Notifications;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public abstract class Command {
	static Minecraft mc = Minecraft.getMinecraft();

	public static String prefix = "-";

	public abstract String[] getAlias();
	public abstract String getSyntax();
	public abstract void onCommand(String command, String[] args) throws Exception;

	public static boolean MsgWaterMark = true;
	public static ChatFormatting cf = ChatFormatting.GRAY;

	public static void sendClientMessage(String message){
		Notifications.addMessage(new TextComponentString(cf + message));
		if (Notifications.disableChat.getValue() && ModuleManager.isModuleEnabled("Notifications")){
			return;
		}
		else {
			if (MsgWaterMark) {
				mc.player.sendMessage(new TextComponentString(ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + "Game" + ChatFormatting.DARK_GREEN + "Sense" + ChatFormatting.GRAY + "] " + ChatFormatting.RESET + cf + message));
			}
			else {
				mc.player.sendMessage(new TextComponentString(cf + message));
			}
		}
	}

	public static void sendRawMessage(String message){
		mc.player.sendMessage(new TextComponentString(message));
	}

	public static String getPrefix(){
		return prefix;
	}

	public static void setPrefix(String newPrefix){
		prefix = newPrefix;
	}
}