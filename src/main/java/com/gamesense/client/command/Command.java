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
}